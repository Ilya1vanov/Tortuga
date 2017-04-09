package model.server.portsystem;

import model.cargo2.Cargo;
import model.client.logbook.Logbook;
import model.client.rating.Rating;
import model.client.rating.Stars;
import model.client.interfaces.MaritimeCarrier;
import model.server.exceptions.PierIsNotFreeException;
import model.server.interfaces.remote.ArrivalService;
import model.server.interfaces.targetareas.OrdersExchangeArea;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
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
    private Port port;

    /** owned warehouse */
    @XmlElement
    private Warehouse warehouse;

    /** currently moored maritimeCarrier */
    private MaritimeCarrier<Cargo, ?> maritimeCarrier;

    /** park timer */
    private Timer timer = new Timer();

    /** stop watch to count mooring time exceeding */
    private StopWatch stopWatch = new StopWatch();

    /** task, that runs when estimated park duration was exceeded */
    private final TimerTask onTimeExceeded = new TimerTask() {
        @Override
        public void run() {
            stopWatch.start();
        }
    };

    /** correct defining id */
    {
        this.id = idCounter.incrementAndGet();
    }

    /** Sets up the parent pointer correctly */
    public void afterUnmarshal(Unmarshaller u, Object parent) {
        this.port = (Port)parent;
    }

    /** @return owned {@link Warehouse} */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    /** @return currently moored maritimeCarrier */
    public MaritimeCarrier<Cargo, ?> getMaritimeCarrier() {
        return maritimeCarrier;
    }

    /** @return true if pier is able to accept mooring */
    public boolean isFree(){
        return maritimeCarrier == null;
    }

    /**
     * {@inheritDoc}
     * @throws PierIsNotFreeException on attempt to moor, when previous didn't unmoor
     * @see ArrivalService#moor(MaritimeCarrier, long, TimeUnit)
     */
    @Override
    public <S extends MaritimeCarrier<Cargo, ?>> OrdersExchangeArea<Cargo> moor(S carrier, long estimatedDuration, TimeUnit unit) {
        if (maritimeCarrier != null)
            throw new PierIsNotFreeException(id + " pier is not free");
        maritimeCarrier = carrier;
        timer.schedule(onTimeExceeded, TimeUnit.MILLISECONDS.convert(estimatedDuration, unit));
        return warehouse;
    }

    /**
     * {@inheritDoc}
     * @see ArrivalService#unmoor
     */
    @Override
    public void unmoor() {
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
    }

//    /**
//     * Constructs new pier.
//     * @param port port, that owns this pier
//     * @param capacityOfWarehouse capacity of owned warehouse
//     */
//    public Pier(Port port, int capacityOfWarehouse) {
//        this.port = port;
//        this.warehouse = new Warehouse(capacityOfWarehouse, this);
//        this.id = idCounter.incrementAndGet();
//    }
}
