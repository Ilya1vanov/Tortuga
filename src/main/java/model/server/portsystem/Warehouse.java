package model.server.portsystem;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import gson.GSONExclude;
import model.cargo2.Cargo;
import model.client.interfaces.MaritimeCarrier;
import model.server.exceptions.CapacityViolationException;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.targetareas.*;
import model.server.pdcsystem.contracts.TransportContract;
import org.apache.log4j.Logger;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.*;

/**
 * Store {@link Cargo Storable}.
 * @author Ilya Ivanov
 */
public class Warehouse implements OrdersExchangeArea<Cargo>, SupplyingCollectingArea<Cargo>, Serializable {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Warehouse.class);

    /** total capacity */
    @XmlAttribute
    private int capacity;

    /** number of currently storing items */
    private final IntegerProperty nowStore = new SimpleIntegerProperty(this, "nowStore",0);

    /** pier, that own this warehouse */
    @XmlTransient
    @GSONExclude
    private Pier pier;

    /** list that contains new orders list &lt;order, collection of products&gt; */
    private final ObservableList<Pair<TransportContract<Client, Carrier<Cargo>, Cargo>, Collection<? extends Cargo>>> newOrders
            = FXCollections.observableArrayList();

    /** list that contains completed orders list &lt;order, collection of products&gt;  */
    private final ObservableList<Pair<TransportContract<Client, Carrier<Cargo>, Cargo>, Collection<? extends Cargo>>> completedOrders
            = FXCollections.observableArrayList();

    /** Sets up the parent pointer correctly */
    public void afterUnmarshal(Unmarshaller u, Object parent) {
        this.pier = (Pier)parent;
    }

    /**
     * {@inheritDoc}
     * @see OrdersDeliveryArea
     */
    @Override
    public void putOrder(TransportContract<Client, Carrier<Cargo>, Cargo> transportContract, Collection<? extends Cargo> products)
            throws CapacityViolationException {
        if (transportContract.getTotalItems() > capacity)
            throw new CapacityViolationException("Warehouse is unable to place production");
        synchronized (completedOrders) {
            while (transportContract.getTotalItems() > capacity - nowStore.get())
                try {
                    completedOrders.wait();
                } catch (InterruptedException e) {
                    // suppress
                    e.printStackTrace();
                }
            completedOrders.add(new Pair<>(transportContract, products));
        }
    }

    /**
     * {@inheritDoc}
     * @see OrdersDispatchArea
     */
    @Override
    public Pair<TransportContract<Client, Carrier<Cargo>, Cargo>, Collection<? extends Cargo>> takeOrder() {
        synchronized(newOrders) {
            Pair<TransportContract<Client, Carrier<Cargo>, Cargo>, Collection<? extends Cargo>> suitable = getSuitableOrder();

            while (newOrders.isEmpty() || suitable == null) {
                try {
                    newOrders.wait();
                } catch (InterruptedException e) {
                    // suppress
                    e.printStackTrace();
                }
                suitable = getSuitableOrder();
            }
            newOrders.remove(suitable);
            newOrders.notifyAll();
            return suitable;
        }
    }

    /**
     * Find suitable, to the currently moored carrier, order, if present.
     * @return suitable, to the currently moored carrier, order; null if no suitable
     */
    private Pair<TransportContract<Client, Carrier<Cargo>, Cargo>, Collection<? extends Cargo>> getSuitableOrder() {
        final MaritimeCarrier<Cargo, ?> maritimeCarrier = pier.getMaritimeCarrier();
        final Amount<Mass> carrying = maritimeCarrier.getCarrying();
        final Amount<Volume> volume = maritimeCarrier.getVolume();

        Pair<TransportContract<Client, Carrier<Cargo>, Cargo>, Collection<? extends Cargo>> suitable = null;
        final Optional<Pair<TransportContract<Client,Carrier<Cargo>, Cargo>, Collection<? extends Cargo>>> any =
                newOrders.stream()
                        .filter(pair ->
                                pair.getKey().getTotalVolume().compareTo(volume) <= 0 &&
                                        pair.getKey().getTotalWeight().compareTo(carrying) <= 0)
                        .findAny();
        if (any.isPresent())
            suitable = any.get();
        return suitable;
    }

    /**
     * {@inheritDoc}
     * @see CollectingArea#collect()
     */
    @Override
    public Collection<Pair<TransportContract, Collection<? extends Cargo>>> collect() {
        synchronized (completedOrders) {
            final ArrayList arrayList = new ArrayList<>(completedOrders);
            completedOrders.clear();
            completedOrders.notifyAll();
            return arrayList;
        }
    }

    /**
     * {@inheritDoc}
     * @see CollectingArea#isCollectingRequired()
     */
    @Override
    public boolean isCollectingRequired() {
        return !completedOrders.isEmpty();
    }

    /**
     * {@inheritDoc}
     * @see model.server.interfaces.targetareas.SupplyingArea#supply(TransportContract, Collection)
     */
    @Override
    public void supply(TransportContract<Client, Carrier<Cargo>, Cargo> transportContract, Collection<? extends Cargo> products)
            throws CapacityViolationException {
        if (transportContract.getTotalItems() > capacity)
            throw new CapacityViolationException("Warehouse is unable to place production");
        synchronized(newOrders) {
            while (transportContract.getTotalItems() > capacity - nowStore.get())
                try {
                    newOrders.wait();
                } catch (InterruptedException e) {
                    // suppress
                    e.printStackTrace();
                }
            newOrders.add(new Pair<>(transportContract, products));
            newOrders.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     * @see SupplyingArea#isSupplyingRequired()
     */
    @Override
    public boolean isSupplyingRequired() {
        return newOrders.isEmpty();
    }

    @Override
    public String toString() {
        return "Warehouse {" +
                "capacity = " + capacity +
                ", now storing = " + nowStore.get() +
                ", number of new orders = " + newOrders.size() +
                ", number of completed = " + completedOrders.size() +
                '}';
    }

    //    /**
//     * @param capacity warehouse capacity
//     * @param pier pier, that own this warehouse
//     */
//    public Warehouse(int capacity, Pier pier) {
//        nowStore.bind(Bindings.size(completedOrders).add(Bindings.size(newOrders)));
//        this.capacity = capacity;
//        this.pier = pier;
//    }
}
