package model.server.portsystem;

import gson.GSONExclude;
import model.cargo2.Cargo;
import model.client.ship.logbook.Logbook;
import model.client.ship.rating.Rating;
import model.client.ship.rating.Stars;
import model.client.interfaces.MaritimeCarrier;
import model.server.interfaces.remote.ArrivalService;
import model.server.interfaces.remote.DepartService;
import model.server.interfaces.targetareas.OrdersExchangeArea;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Pier, that {@link Port Port} has.</p>
 * @author Ilya Ivanov
 */
public class Pier extends RemoteClass
        implements ArrivalService<Cargo>, DepartService<Cargo> {
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
    private MaritimeCarrier<Cargo> maritimeCarrier;

    /** park timer */
    @GSONExclude
    private Timer timer;

    /** stop watch to count mooring time exceeding */
    @GSONExclude
    private StopWatch stopWatch = new StopWatch();

    /** default constructor for JAXB */
    public Pier() {
        super();
    }

    /**
     * Constructs new pier.
     * @param port port, that owns this pier
     * @param capacityOfWarehouse capacity of owned warehouse
     */
    Pier(Port port, int capacityOfWarehouse) {
        this.port = port;
        this.warehouse = new Warehouse(capacityOfWarehouse, this);
    }

    /** correct defining id */
    {
        this.id = idCounter.incrementAndGet();
    }

    /** Sets up the parent pointer correctly */
    public void afterUnmarshal(Unmarshaller u, Object parent) {
        this.port = (Port) parent;
        super.setRemotes(Collections.singletonList(warehouse));
    }

    /** @return id of this pier */
    public int getId() {
        return id;
    }

    /** @return owned {@link Warehouse} */
    Warehouse getWarehouse() {
        return warehouse;
    }

    /** @return currently moored maritimeCarrier */
    MaritimeCarrier<Cargo> getMaritimeCarrier() {
        return maritimeCarrier;
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
    public <S extends MaritimeCarrier<Cargo>> DepartService<Cargo> moor(S carrier, long estimatedDuration, TimeUnit unit)
            throws RemoteException {
        maritimeCarrier = carrier;

        timer  = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopWatch.start();
            }
        }, TimeUnit.MILLISECONDS.convert(estimatedDuration, unit));
        log.info(maritimeCarrier.getName() + " now moored to " + id + " pier");
        return this;
    }

    /**
     * {@inheritDoc}
     * @see DepartService#unmoor
     */
    @Override
    public void unmoor() throws RemoteException {
        timer.cancel();
        long delay = 0;
        if (stopWatch.isStarted()) {
            stopWatch.stop();
            delay = stopWatch.getTime(); // in millis
        }

        if (delay == 0)
            maritimeCarrier.rate(Stars.FIVE);
        else if (delay < 100)
            maritimeCarrier.rate(Stars.FOUR);
        else if (delay < 200)
            maritimeCarrier.rate(Stars.THREE);
        else if (delay < 300)
            maritimeCarrier.rate(Stars.TWO);
        else
            maritimeCarrier.rate(Stars.ONE);

        if (delay != 0)
            maritimeCarrier.log(port.getName() + " - " + id + " pier","Mooring time was exceeded on " + delay);

        stopWatch.reset();
        timer.purge();
        log.info(maritimeCarrier.getName() + " now unmoored from " + id + " pier");
        maritimeCarrier = null;

        // notify everyone who is in queue
        synchronized (port.shipsQueue) {
            port.shipsQueue.notifyAll();
        }
    }

    @Override
    public OrdersExchangeArea<Cargo> getOrdersExchangeArea() throws RemoteException {
        return warehouse;
    }

    private void forceUnmoor() {
        timer.cancel();
        if (stopWatch.isStarted())
            stopWatch.stop();
        stopWatch.reset();
        timer.purge();
        maritimeCarrier = null;
    }

    @Override
    public String toString() {
        try {
            return "\n\tPier " + id + '\n' +
                    "\tWarehouse: " + warehouse +
                    "\n\tMoored carrier: " + (maritimeCarrier == null ? "free" : maritimeCarrier.getName());
        } catch (RemoteException e) {
            e.printStackTrace();
            return "";
        }
    }
}
