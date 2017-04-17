package model.server.portsystem;

import com.google.common.collect.ImmutableCollection;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import gson.GSONExclude;
import model.cargo2.Cargo;
import model.client.interfaces.MaritimeCarrier;
import model.server.exceptions.CapacityViolationException;
import model.server.exceptions.NoSuitableOrder;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.targetareas.*;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.pdcsystem.order.Order;
import org.apache.log4j.Logger;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Store {@link Cargo Storable}.
 * @author Ilya Ivanov
 */
public class Warehouse extends RemoteClass
        implements OrdersExchangeArea<Cargo>, SupplyingCollectingArea<Cargo> {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Warehouse.class);

    /** total capacity */
    @XmlAttribute(required = true)
    private int capacity;

    /** number of currently storing items */
    private final IntegerProperty nowStore = new SimpleIntegerProperty(this, "nowStore",0);

    /** pier, that own this warehouse */
    @XmlTransient
    @GSONExclude
    private Pier pier;

    /** list that contains new orders list &lt;order, collection of products&gt; */
    private final ObservableList<Order<Cargo>> newOrders
            = FXCollections.observableArrayList();

    /** list that contains completed orders list &lt;order, collection of products&gt;  */
    private final ObservableList<Order<Cargo>> completedOrders
            = FXCollections.observableArrayList();

    /** default constructor for JAXB */
    public Warehouse() {
        super();
    }

    /**
     * @param capacity warehouse capacity
     * @param pier pier, that own this warehouse
     */
    Warehouse(int capacity, Pier pier) {
        this.capacity = capacity;
        this.pier = pier;
    }

    {
        final IntegerBinding newOrdersSize = Bindings.size(newOrders);
        final IntegerBinding completedOrdersSize = Bindings.size(completedOrders);
        // recalculate
        newOrdersSize.addListener(observable -> newOrdersSize.get());
        completedOrdersSize.addListener(observable -> completedOrdersSize.get());
        nowStore.bind(
                Bindings.createIntegerBinding(() ->  newOrders.stream().mapToInt(order -> order.getContract().getTotalItems()).sum(), newOrdersSize)
                .add(Bindings.createIntegerBinding(() -> completedOrders.stream().mapToInt(order -> order.getProduction().size()).sum(), completedOrdersSize))
        );
    }

    /** Sets up the parent pointer correctly */
    public void afterUnmarshal(Unmarshaller u, Object parent) {
        this.pier = (Pier)parent;
    }

    /**
     * {@inheritDoc}
     * @see OrdersDeliveryArea
     */
    @Override
    public void putOrder(Order<Cargo> order)
            throws RemoteException {
        synchronized (completedOrders) {
            log.info(pier.getMaritimeCarrier().getName() + " from " + pier.getId() + " is trying to put order");

            final TransportContract<Client, Carrier<Cargo>, Cargo> contract = order.getContract();
            if (contract.getTotalItems() > capacity)
                throw new CapacityViolationException("Warehouse is unable to place production");

            while (contract.getTotalItems() > capacity - nowStore.get()) {
                if (!isCollectingRequired())
                    throw new CapacityViolationException("Warehouse is unable to place this production now. Come back later.");
                try {
                    completedOrders.wait();
                    log.info(pier.getMaritimeCarrier().getName() + " from " + pier.getId() + " is some free space to put order");
                } catch (InterruptedException ignored) {}
            }
            completedOrders.add(order);
            log.info(pier.getMaritimeCarrier().getName() + " from " + pier.getId() + " put order");
        }
    }

    /**
     * {@inheritDoc}
     * @see OrdersDispatchArea
     */
    @Override
    public Order<Cargo> takeOrder() throws RemoteException {
        synchronized(newOrders) {
            log.info(pier.getMaritimeCarrier().getName() + " from " + pier.getId() + " is trying to take order");
            Order<Cargo> suitable = getSuitableOrder();

            while (suitable == null) {
                if (!isSupplyingRequired())
                    throw new NoSuitableOrder("There are no suitable order for your. Come back later");
                try {
                    log.info(pier.getMaritimeCarrier().getName() + " from " + pier.getId() + " is waiting for suitable order");
                    newOrders.wait();
                } catch (InterruptedException e) {
                    // suppress
                    e.printStackTrace();
                }
                suitable = getSuitableOrder();
            }

            newOrders.remove(suitable);
            newOrders.notifyAll();
            log.info(pier.getMaritimeCarrier().getName() + " from " + pier.getId() + " took order");
            return suitable;
        }
    }

    /**
     * Find suitable, to the currently moored carrier, order, if present.
     * @return suitable, to the currently moored carrier, order; null if no suitable
     */
    private Order<Cargo> getSuitableOrder() throws RemoteException {
        if (newOrders.isEmpty())
            return null;

        final MaritimeCarrier<Cargo> maritimeCarrier = pier.getMaritimeCarrier();
        final Amount<Mass> carrying = maritimeCarrier.getCarrying();
        final Amount<Volume> volume = maritimeCarrier.getVolume();

        Order<Cargo> suitable = null;
        final Optional<Order<Cargo>> any =
                newOrders.stream()
                        .filter(order ->
                                order.getContract().getTotalVolume().compareTo(volume) <= 0 &&
                                        order.getContract().getTotalWeight().compareTo(carrying) <= 0)
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
    public Collection<Order<Cargo>> collect() {
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
     * @see model.server.interfaces.targetareas.SupplyingArea#supply
     */
    @Override
    public void supply(Order<Cargo> order)
            throws CapacityViolationException {
        synchronized (newOrders) {
            final TransportContract<Client, Carrier<Cargo>, Cargo> contract = order.getContract();

            if (contract.getTotalItems() > capacity)
                throw new CapacityViolationException("Warehouse is unable to place production");
            synchronized (newOrders) {
                while (contract.getTotalItems() > capacity - nowStore.get())
                    try {
                        newOrders.wait();
                    } catch (InterruptedException e) {
                        // suppress
                        e.printStackTrace();
                    }
                newOrders.add(order);
                newOrders.notifyAll();
            }
        }
    }

    /**
     * {@inheritDoc}
     * @see SupplyingArea#isSupplyingRequired()
     */
    @Override
    public boolean isSupplyingRequired() {
        return newOrders.isEmpty() || (capacity / nowStore.get() >= 2);
    }

    @Override
    public String toString() {
        return  "\n\t\tCapacity: " + capacity +
                "\n\t\tNow storing: " + nowStore.get() +
                "\n\t\tNumber of new orders: " + newOrders.size() +
                "\n\t\tNumber of completed: " + completedOrders.size();
    }
}
