package model.server.portsystem;

import gson.GSONExclude;
import model.cargo2.Cargo;
import model.client.ship.logbook.Logbook;
import model.client.ship.rating.Rating;
import model.client.ship.rating.Stars;
import model.client.interfaces.MaritimeCarrier;
import model.server.interfaces.remote.ArrivalService;
import model.server.interfaces.targetareas.OrdersExchangeArea;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Pier, that {@link Port Port} has.</p>
 * @author Ilya Ivanov
 */
public class Pier implements ArrivalService<Cargo> {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Pier.class);

    /** thread-safe id counter */
    private static final AtomicInteger idCounter = new AtomicInteger(0);

    /** unique {@code Pier} id */
    private final int id;

    /** port that owns this pier */
    @XmlTransient
    @GSONExclude
    private Port port;

    /** owned warehouse */
    @XmlElement(required = true)
    private Warehouse warehouse;

    /** currently moored maritimeCarrier */
    private MaritimeCarrier<Cargo, ?> maritimeCarrier;

    /** id of thread, that process this mooring */
    @GSONExclude
    Long serviceThreadID;

    /** park timer */
    @GSONExclude
    private Timer timer = new Timer();

    /** stop watch to count mooring time exceeding */
    @GSONExclude
    private StopWatch stopWatch = new StopWatch();

    /** task, that runs when estimated park duration was exceeded */
    @GSONExclude
    private final TimerTask onTimeExceeded = new TimerTask() {
        @Override
        public void run() {
            stopWatch.start();
        }
    };

    /** default constructor for JAXB */
    public Pier() {}

    /**
     * Constructs new pier.
     * @param port port, that owns this pier
     * @param capacityOfWarehouse capacity of owned warehouse
     */
    public Pier(Port port, int capacityOfWarehouse) {
        this.port = port;
        this.warehouse = new Warehouse(capacityOfWarehouse, this);
    }

    /** correct defining id */
    {
        this.id = idCounter.incrementAndGet();
    }

    /** Sets up the parent pointer correctly */
    public void afterUnmarshal(Unmarshaller u, Object parent) {
        this.port = (Port)parent;
    }

    void exportRemote() throws RemoteException {
        UnicastRemoteObject.exportObject(warehouse, 0);
    }

    /** @return owned {@link Warehouse} */
    Warehouse getWarehouse() {
        return warehouse;
    }

    /** @return currently moored maritimeCarrier */
    MaritimeCarrier<Cargo, ?> getMaritimeCarrier() {
        return maritimeCarrier;
    }

    /** @return id of thread, that process current mooring; null if {@code this.isFree == true} */
    long getServiceThreadID() {
        return serviceThreadID;
    }

    /** @return true if pier is able to accept mooring */
    boolean isFree(){
        return maritimeCarrier == null;
    }

    /**
     * {@inheritDoc}
     * @see ArrivalService#moor(MaritimeCarrier, long, TimeUnit)
     */
    @Override
    public <S extends MaritimeCarrier<Cargo, ?>> OrdersExchangeArea<Cargo> moor(S carrier, long estimatedDuration, TimeUnit unit) {
        assert maritimeCarrier != null : "Concurrency bug: " + id + " pier is not free";

        maritimeCarrier = carrier;
        serviceThreadID = Thread.currentThread().getId();
        timer.schedule(onTimeExceeded, TimeUnit.MILLISECONDS.convert(estimatedDuration, unit));
        return warehouse;
    }

    /**
     * {@inheritDoc}
     * @see ArrivalService#unmoor
     */
    @Override
    public void unmoor() throws RemoteException {
        timer.cancel();
        long delay;
        if (stopWatch.isStarted()) {
            stopWatch.stop();
            delay = stopWatch.getTime(); // in millis
        }
        else
            delay = 0;

        final Rating rating = maritimeCarrier.getRating();
        if (delay == 0)
            rating.rate(Stars.FIVE);
        else if (delay < 100)
            rating.rate(Stars.FOUR);
        else if (delay < 200)
            rating.rate(Stars.THREE);
        else if (delay < 300)
            rating.rate(Stars.TWO);
        else
            rating.rate(Stars.ONE);

        final Logbook logBook = maritimeCarrier.getLogBook();
        // write something

        stopWatch.reset();
        timer.purge();
        maritimeCarrier = null;
        serviceThreadID = null;
    }

    @Override
    public String toString() {
        return "Pier {" +
                "id = " + id +
                ", warehouse =" + warehouse +
                ", maritimeCarrier=" + (maritimeCarrier == null ? "free" : maritimeCarrier) +
                '}';
    }
}
