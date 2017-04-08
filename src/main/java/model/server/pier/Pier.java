package model.server.pier;

import model.cargo2.Cargo;
import model.client.rating.Stars;
import model.client.ship.MaritimeCarrier;
import model.client.ship.Ship;
import model.server.interfaces.targetareas.OrdersExchangeArea;
import model.server.port.Port;
import model.server.remote.ArrivalService;
import model.server.warehouse.Warehouse;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;
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
    private final Port port;

    /** owned warehouse */
    private final Warehouse warehouse;

    /** currently moored ship */
    private MaritimeCarrier<Cargo> ship;

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

    /** Constructs new pier */
    public Pier(Port port, Warehouse warehouse) {
        this.port = port;
        this.warehouse = warehouse;
        this.id = idCounter.incrementAndGet();
    }

    /** @return owned {@link Warehouse} */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    public boolean isFree(){
        return ship == null;
    }

    @Override
    public <S extends MaritimeCarrier<Cargo>> OrdersExchangeArea<Cargo> moor(S carrier, long estimatedDuration, TimeUnit unit) throws RemoteException {
        ship = carrier;
        // start timer
        return warehouse;
    }

    @Override
    public Stars unmoor() throws RemoteException {
        stopWatch.stop();
        final long time = stopWatch.getTime(); // in millis

        // rate ship

        timer.purge();
        ship = null;
        return null;
    }
}
