package model.client.ship;

import gson.GSONExclude;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import model.cargo2.Cargo;
import model.client.Client;
import model.client.interfaces.MaritimeCarrier;
import model.client.ship.logbook.Logbook;
import model.client.ship.rating.Rating;
import model.client.ship.rating.Stars;
import model.server.exceptions.CapacityViolationException;
import model.server.exceptions.NoSuitableOrder;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.remote.ArrivalService;
import model.server.interfaces.remote.DepartService;
import model.server.interfaces.targetareas.OrdersExchangeArea;
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
import javax.xml.bind.annotation.XmlTransient;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
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
    private static final AtomicInteger idCounter = new AtomicInteger(0);

    /** ref to the parent object */
    @GSONExclude
    @XmlTransient
    private model.client.Client client;

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
    public Ship(Client client, ArrivalService<Cargo> service, String name, Amount<Volume> displacement, Amount<Velocity> velocity, Amount<Volume> volume, Amount<Mass> carrying) {
        this.client = client;
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
        final model.client.Client client = (model.client.Client) parent;
        this.client = client;
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

    @Override
    public void rate(Stars stars) throws RemoteException {
        rating.rate(stars);
    }

    /** {@inheritDoc}*/
    @Override
    public Logbook getLogBook() {
        return logbook;
    }

    @Override
    public void log(String author, String record) throws RemoteException {
        logbook.log(author, record);
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
            DepartService<Cargo> departService = null;
            try {
                // 1. try to moor
                status.set(ShipStatus.IDLE);
                departService = service.moor(this, calculateMooringTime(), TimeUnit.MILLISECONDS);
                final OrdersExchangeArea<Cargo> exchangeArea = departService.getOrdersExchangeArea();

                try {
                    if (order != null) {
                        // 2. unload
                        status.set(ShipStatus.UNLOADING);
                        final TransportContract<model.server.interfaces.parties.Client, Carrier<Cargo>, Cargo> contract = order.getContract();
                        contract.complete();
                        exchangeArea.putOrder(order);
                        order = null;
                        TimeUnit.MILLISECONDS.sleep(calculateLoadingTime());
                    }

                    if (order == null) {
                        // 3. take a new order
                        status.set(ShipStatus.LOADING);
                        final Order<Cargo> order = exchangeArea.takeOrder();
                        assert order != null : "takeOrder() returned null";

                        // 4. accept order and load
                        order.getContract().accept(this);
                        this.order = order;
                        TimeUnit.MILLISECONDS.sleep(calculateLoadingTime());
                    }
                } catch (NoSuitableOrder | CapacityViolationException ignored) {}

                // 6. unmoor and take a trip
                departService.unmoor();
                status.set(ShipStatus.ON_WAY);
                TimeUnit.SECONDS.sleep(6);
            } catch (ConnectException | UnmarshalException e) {
                log.error("Connection to the server is lost");
                Thread.currentThread().interrupt();
            } catch (InterruptedException e) {
                log.error("Thread was interrupted by the client");
                Thread.currentThread().interrupt();
            } catch (RemoteException e) {
                log.fatal("Unknown exception", e);
                Thread.currentThread().interrupt();
            }
        }
        client.shutdown();
    }

    /** @return estimated mooring time in millis */
    private int calculateMooringTime() {
        final int i = calculateLoadingTime();
        int coefficient = 0;
        if (i == 0)
            coefficient = 150;
        return 2 * i + 800 + coefficient;
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

    @Override
    public String getName() throws RemoteException {
        return name;
    }
}
