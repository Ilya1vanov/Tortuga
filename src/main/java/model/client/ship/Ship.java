package model.client.ship;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.util.Pair;
import model.cargo2.Cargo;
import model.client.interfaces.MaritimeCarrier;
import model.client.logbook.Logbook;
import model.client.rating.Rating;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.remote.ArrivalService;
import model.server.interfaces.targetareas.OrdersExchangeArea;
import model.server.pdcs.contracts.TransportContract;
import model.server.portsystem.Pier;
import model.server.portsystem.Port;
import org.apache.log4j.Logger;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.Volume;
import java.rmi.RemoteException;
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
public class Ship implements Runnable, MaritimeCarrier<Cargo, Ship> {
    /** final log4j logger */
    private Logger log = Logger.getLogger(getClass());

    private static final String CLASS_LOG_PATH = Ship.class.getSimpleName().toLowerCase();

    /** thread-safe id counter */
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    /** unique ship id */
    private final int id;

    /** name of the ship */
    private final String name;

    /** ship's displacement */
    private final Amount<Volume> displacement;

    /** ship's velocity */
    private final Amount<Velocity> velocity;

    /** ship's volume */
    private final Amount<Volume> volume;

    /** ship's carrying */
    private final Amount<Mass> carrying;

    /** rating of the ship */
    private final Rating rating = new Rating();

    /** contract */
    private TransportContract<Client, Carrier<Cargo>, Cargo> contract;

    /** transported goods. defaults to 50*/
    private final List<Cargo> goods = new ArrayList<>(50);

    /** remote interface */
    private final ArrivalService<Cargo> service;

    /** current ship status */
    private final ObjectProperty<ShipStatus> status = new SimpleObjectProperty<>();

    /** logbook */
    private final Logbook logbook = new Logbook();

    /**
     * @param service arrival service
     * @param name {@code Ship} name
     */
    public Ship(ArrivalService service, String name, Amount<Volume> displacement, Amount<Velocity> velocity, Amount<Volume> volume, Amount<Mass> carrying) {
        this.id = idCounter.incrementAndGet();
        this.name = name;
        this.displacement = displacement;
        this.velocity = velocity;
        this.volume = volume;
        this.carrying = carrying;

        this.status.set(ShipStatus.ON_WAY);
        this.service = service;
        status.addListener(observable -> log.info(String.valueOf(id) + name + " status: " + status.get().getMessage()));
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

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                // 1. try to moor
                status.set(ShipStatus.IDLE);
                final OrdersExchangeArea<Cargo> exchangeArea = service.moor(this, calculateMooringTime(), TimeUnit.MILLISECONDS);
                // 2. unload
                if (contract != null) {
                    status.set(ShipStatus.UNLOADING);
                    contract.complete();
                    exchangeArea.putOrder(contract, new ArrayList<>(goods));
                    goods.clear();
                    contract = null;
                    TimeUnit.MILLISECONDS.sleep(calculateLoadingTime());
                }
                // 3. take a new order
                status.set(ShipStatus.LOADING);
                final Pair<TransportContract<Client, Carrier<Cargo>, Cargo>, Collection<? extends Cargo>> order = exchangeArea.takeOrder();
                // 3. accept order
                final TransportContract<Client, Carrier<Cargo>, Cargo> contract = order.getKey();
                contract.accept(this);
                // 4. load
                this.contract = contract;
                final Collection<? extends Cargo> cargos = order.getValue();
                goods.addAll(cargos);
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

    /** @return estimated mooring time */
    private int calculateMooringTime() {
        final int i = calculateLoadingTime();
        int coefficient = 0;
        if (i == 0)
            coefficient = 200;
        return 2 * i + 500 + coefficient;
    }

    /** @return estimated loading time */
    private int calculateLoadingTime() {
        int estimatedTime = 0;
        if (contract != null) {
            contract.getTotalItems();
        }
        return estimatedTime;
    }

    /* comparison by rating */
    @Override
    public int compareTo(Ship o) {
        return rating.compareTo(o.rating);
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
