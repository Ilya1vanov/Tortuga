package model.client.ship;

import gson.GSONExclude;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.util.Pair;
import model.cargo2.Cargo;
import model.client.interfaces.MaritimeCarrier;
import model.client.ship.logbook.Logbook;
import model.client.ship.rating.Rating;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.remote.ArrivalService;
import model.server.interfaces.targetareas.OrdersExchangeArea;
import model.server.pdcsystem.contracts.Importance;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.pdcsystem.order.Order;
import model.server.portsystem.Pier;
import model.server.portsystem.Port;
import org.apache.log4j.Logger;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.Volume;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Transport ship. Able to transport {@link Transportable} according to the {@link TransportContract}.
 * Also can come to the {@link Port Port}, moor to the {@link Pier Pier}. Load or unload {@code Cargos}.
 * If there is some orders ship can accept if or wait otherwise.</p>
 * <p>When ship moors to the {@code Pier}, it specifies time of halt. If this
 * time was exceeded, {@link Rating rating} of this {@code Ship} falls.</p>
 * @author Ilya Ivanov
 */
public class Ship implements Runnable, MaritimeCarrier<Cargo> {
    /** final log4j logger */
    private static Logger log = Logger.getLogger(Ship.class);

    /** thread-safe id counter */
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    /** unique ship id */
    private final int id;

    /** name of the ship */
    @XmlAttribute
    private String name;

    /** ship's displacement */
    @GSONExclude
    private Amount<Volume> displacement;

    /** ship's velocity */
    @GSONExclude
    private Amount<Velocity> velocity;

    /** ship's volume */
    @GSONExclude
    private Amount<Volume> volume;

    /** ship's carrying */
    @GSONExclude
    private Amount<Mass> carrying;

    /** rating of the ship */
    private final Rating rating = new Rating();

    /** executing order */
    private Order<Cargo> order;

    /** remote interface */
    @GSONExclude
    private ArrivalService<Cargo> service;

    /** current ship status */
    @GSONExclude
    private final ObjectProperty<ShipStatus> status = new SimpleObjectProperty<>();

    /** logbook */
    @GSONExclude
    private final Logbook logbook = new Logbook();

    /** Set up client settings */
    public Ship() {}

    /**
     * Manual constructor.
     * @param service arrival service
     * @param name {@code Ship} name
     */
    public Ship(ArrivalService service, String name, Amount<Volume> displacement, Amount<Velocity> velocity, Amount<Volume> volume, Amount<Mass> carrying) {
        this.service = service;
        this.name = name;
        this.displacement = displacement;
        this.velocity = velocity;
        this.volume = volume;
        this.carrying = carrying;
        tuneListeners();
    }

    /** Set up ship settings */
    public void afterUnmarshal(Unmarshaller u, Object parent) {
        tuneListeners();
    }

    {
        this.id = idCounter.incrementAndGet();
    }

    private void tuneListeners() {
        status.addListener(observable -> log.info(String.valueOf(id) + " - " +  name + " status: " + status.get().getMessage()));
        this.status.set(ShipStatus.ON_WAY);
    }

    /** {@inheritDoc}*/
    @Override
    public Rating getRating() {
        return rating;
    }

    /** {@inheritDoc}*/
    @Override
    public Logbook getLogBook() {
        return logbook;
    }

    /** {@inheritDoc}*/
    @Override
    public Order<Cargo> getOrder() {
        return order;
    }

    /** @return current ship status */
    public ShipStatus getStatus() {
        return status.get();
    }

    /** @return current ship status property */
    public ObservableObjectValue<ShipStatus> statusProperty() {
        return status;
    }

    @Override
    public Amount<Volume> getVolume() {
        return volume;
    }

    @Override
    public Amount<Mass> getCarrying() {
        return carrying;
    }

    /** @param service arrival service */
    public void setService(ArrivalService<Cargo> service) {
        this.service = service;
    }

    /** private suit of setter for JAXB */
    @XmlElement(name = "displacement", required = true)
    private void setDisplacement_(String displacement) {
        this.displacement = (Amount<Volume>) Amount.valueOf(displacement);
    }

    @XmlElement(name = "velocity", required = true)
    private void setVelocity_(String velocity) {
        this.velocity = (Amount<Velocity>) Amount.valueOf(velocity);
    }

    @XmlElement(name = "volume", required = true)
    private void setVolume_(String v) {
        this.volume = (Amount<Volume>) Amount.valueOf(v);
    }

    @XmlElement(name = "carrying", required = true)
    private void setCarrying_(String carrying) {
        this.carrying = (Amount<Mass>) Amount.valueOf(carrying);
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                // 1. try to moor
                status.set(ShipStatus.IDLE);
                final OrdersExchangeArea<Cargo> exchangeArea = service.moor(this, calculateMooringTime(), TimeUnit.MILLISECONDS);
                // 2. unload
                if (order != null) {
                    status.set(ShipStatus.UNLOADING);
                    final TransportContract<Client, Carrier<Cargo>, Cargo> contract = order.getContract();
                    contract.complete();
                    exchangeArea.putOrder(order);
                    order = null;
                    TimeUnit.MILLISECONDS.sleep(calculateLoadingTime());
                }
                // 3. take a new order
                status.set(ShipStatus.LOADING);
                final Order<Cargo> order = exchangeArea.takeOrder();
                // 3. accept order
                order.getContract().accept(this);
                // 4. load
                this.order = order;
                TimeUnit.MILLISECONDS.sleep(calculateLoadingTime());
                // 5. unmoor
                service.unmoor();
                // 6. rake a trip
                status.set(ShipStatus.ON_WAY);

            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /** @return estimated mooring time in millis */
    private int calculateMooringTime() {
        final int i = calculateLoadingTime();
        int coefficient = 0;
        if (i == 0)
            coefficient = 100;
        return 2 * i + 200 + coefficient;
    }

    /** @return estimated loading time in millis */
    private int calculateLoadingTime() {
        int estimatedTime = 0;
        if (order != null) {
            final int totalItems = order.getContract().getTotalItems();
            estimatedTime = 2 * totalItems;
        }
        return estimatedTime;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
