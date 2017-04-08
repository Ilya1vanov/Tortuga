package model.server.port;

import model.cargo2.Cargo;
import model.client.rating.Stars;
import model.client.ship.MaritimeCarrier;
import model.client.ship.Ship;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.targetareas.OrdersExchangeArea;
import model.server.interfaces.targetareas.SupplyingCollectingArea;
import model.server.pdcs.contracts.TransportContract;
import model.server.pier.Pier;
import model.server.remote.ArrivalService;
import model.server.warehouse.Warehouse;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Port has a several {@link Pier Piers} and {@link Warehouse Stocks}. Able to moor
 * {@link Ship Ships} to the {@link Pier Pier}. And receive {@link TransportContract Orders}.
 * @author Ilya Ivanov
 */
public class Port extends UnicastRemoteObject
        implements Runnable,
        ArrivalService<Cargo>,
        SupplyingCollectingArea<Cargo> {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Port.class);

    /** thread-safe id counter */
    private static final AtomicInteger idCounter = new AtomicInteger(0);

    /** unique ship id */
    private final int id;

    /** name of this port */
    private final String name;

    /** input products buffer */
    BlockingQueue<Cargo> productsInputBuf;

    /** output products buffer */
    BlockingQueue<Cargo> productsOutputBuf;

    /** ships queue */
    private final PriorityBlockingQueue<MaritimeCarrier<Cargo>> shipsQueue = new PriorityBlockingQueue<>();

    /** list of {@link Pier Piers} of this {@code Port} */
    private List<Pier> piers;

    /**
     *
     * @param name name of the port
     */
    public Port(String name) throws RemoteException {
        super();
        this.id = idCounter.incrementAndGet();
        this.name = name;
    }

    /** @return name of this port */
    public String getName() {
        return name;
    }

    /** @return list of piers of this port */
    public List<Pier> getPiers() {
        return piers;
    }

    @Override
    public void run() {
        // wait for connections
    }

    /**
     * {@inheritDoc}
     * @see ArrivalService#moor(MaritimeCarrier, long, TimeUnit)
     */
    @Override
    public <S extends MaritimeCarrier<Cargo>> OrdersExchangeArea<Cargo> moor(S carrier, long estimatedDuration, TimeUnit unit) {
        synchronized (shipsQueue) {
            final long numberOfFreePiers = piers.stream().map(Pier::isFree).count();
            if (    // there is some free pier but queue is not empty
                    numberOfFreePiers > 0 && !shipsQueue.isEmpty() ||
                    // or no free pier
                    numberOfFreePiers == 0) {
                try {
                    // then add carrier to the queue
                    shipsQueue.add(carrier);
                    while(  // no free pier
                            piers.stream().map(Pier::isFree).count() == 0 ||
                            // or carrier is not the first in the queue
                            shipsQueue.peek() != carrier)
                        // then sleep
                        shipsQueue.wait();
                } catch (InterruptedException e) {
                    // suppress
                    e.printStackTrace();
                }
            }
            final MaritimeCarrier<Cargo> peek = shipsQueue.poll();
            assert peek == carrier : "Bug report: carrier is not first in the queue, when it should";
        }

        final Pier pier = piers.stream().filter(Pier::isFree).findFirst().get();
        try {
            return pier.moor(carrier, estimatedDuration, unit);
        } catch (RemoteException e) {
            // fail fast
            // report bug
            e.printStackTrace();
            throw new IllegalStateException("Bug report: Problems with concurrency - empty queue, when it shouldn't be");
        }
    }

    @Override
    public Stars unmoor() {
        synchronized (shipsQueue) {
            shipsQueue.notifyAll();
        }
        return null;
    }

    @Override
    public boolean isCollectingRequired() {
        return false;
    }

    @Override
    public boolean isSupplyingRequired() {
        return false;
    }

    @Override
    public Collection<Cargo> collect() {
        return null;
    }

    @Override
    public void supply(TransportContract<Client, ? extends Carrier<Cargo>, Cargo> transportContract, Collection<? extends Cargo> products) {

    }

    @Override
    public String toString() {
        return "Port{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
