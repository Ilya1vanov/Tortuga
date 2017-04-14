package model.server.portsystem;

import com.google.common.collect.ImmutableCollection;
import javafx.util.Pair;
import model.cargo2.Cargo;
import model.client.interfaces.MaritimeCarrier;
import model.client.ship.Ship;
import model.server.exceptions.CapacityViolationException;
import model.server.exceptions.NotInServiceException;
import model.server.interfaces.remote.ArrivalService;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.targetareas.CollectingArea;
import model.server.interfaces.targetareas.OrdersExchangeArea;
import model.server.interfaces.targetareas.SupplyingArea;
import model.server.interfaces.targetareas.SupplyingCollectingArea;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.pdcsystem.order.Order;
import org.apache.log4j.Logger;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Port has a several {@link Pier Piers} and {@link Warehouse Stocks}. Able to moor
 * {@link Ship Ships} to the {@link Pier Pier}. And receive {@link TransportContract Orders}.
 * @author Ilya Ivanov
 */
@XmlRootElement
public class Port implements Runnable,
        ArrivalService<Cargo>,
        SupplyingCollectingArea<Cargo> {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Port.class);

    /** name of this port */
    @XmlAttribute
    private String name;
//    /** input products buffer */
//    BlockingQueue<Cargo> productsInputBuf = new ArrayBlockingQueue<Cargo>(5, true);
//
//    /** output products buffer */
//    BlockingQueue<Cargo> productsOutputBuf = new ArrayBlockingQueue<Cargo>(5, true);
    /** ships queue */
    private final PriorityBlockingQueue<MaritimeCarrier<Cargo>> shipsQueue
            = new PriorityBlockingQueue<>(50, (o1, o2) -> {
                try {
                    return o1.compareTo(o2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return 0;
                }
            });

    /** list of {@link Pier Piers} of this {@code Port} */
    @XmlElement(name="pier", required = true)
    private List<Pier> piers = new ArrayList<>();

    /** default constructor for JAXB */
    public Port() {}

    /** for manual instantiation */
    public Port(String name, Pier first, Pier... piers) throws RemoteException {
        this.name = name;
        this.piers.add(first);
        for (Pier pier : piers)
            this.piers.add(pier);
    }

    {
        shipsQueue.comparator();
    }

    /** Sets up the parent pointer correctly */
    public void afterUnmarshal(Unmarshaller u, Object parent) {
        for (Pier pier : piers)
            try {
                pier.exportRemote();
            } catch (RemoteException e) {
                log.fatal("Port failed while exporting remotes", e);
                throw new RuntimeException("Port failed while exporting remotes", e);
            }
    }

    /** @return name of this port */
    public String getName() {
        return name;
    }

    @Override
    public void run() {
        log.info("Port was started");
        // wait for connections
    }

    /**
     * {@inheritDoc}
     * @see ArrivalService#moor(MaritimeCarrier, long, TimeUnit)
     */
    @Override
    public <S extends MaritimeCarrier<Cargo>> OrdersExchangeArea<Cargo> moor(S carrier, long estimatedDuration, TimeUnit unit) {
        synchronized (shipsQueue) {
            Pier freePier = getFreePier();
            // add carrier to the queue
            shipsQueue.put(carrier);
            while(  // no free pier
                    freePier == null ||
                    // or carrier is not the first in the queue
                    shipsQueue.peek() != carrier) {
                try {
                    shipsQueue.wait();
                } catch (InterruptedException e) {
                    // suppress
                    e.printStackTrace();
                }
                freePier = getFreePier();
            }
            final MaritimeCarrier<Cargo> poll = shipsQueue.poll();
            assert poll == carrier : "Bug report: Problems with concurrency - carrier is not first in the queue, when it should; or queue was empty";

            return freePier.moor(carrier, estimatedDuration, unit);
        }
    }

    /**
     * {@inheritDoc}
     * @see ArrivalService#unmoor
     */
    @Override
    public void unmoor() throws RemoteException {
        synchronized (shipsQueue) {
            final Stream<Pier> pierStream = piers.stream().filter(pier -> pier.getServiceThreadID() == Thread.currentThread().getId());
            if (pierStream.count() == 0)
                throw new NotInServiceException("Your carrier was not in service. Moor first.");
            if (pierStream.count() > 1) {
                log.fatal("More than one threads where created on one connection");
                throw new RuntimeException("More than one threads where created on one connection");
            }
            pierStream.findFirst().get().unmoor();
            shipsQueue.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     * @see CollectingArea#isCollectingRequired()
     */
    @Override
    public boolean isCollectingRequired() {
        return piers.stream().anyMatch(pier -> pier.getWarehouse().isCollectingRequired());
    }

    /**
     * {@inheritDoc}
     * @see SupplyingArea#isSupplyingRequired()
     */
    @Override
    public boolean isSupplyingRequired() {
        return piers.stream().anyMatch(pier -> pier.getWarehouse().isSupplyingRequired());
    }

    /**
     * {@inheritDoc}
     * @see CollectingArea#collect()
     */
    @Override
    public Collection<Order<Cargo>> collect() {
        Collection<Order<Cargo>> collected = new ArrayList<>();
        for (Pier pier : piers)
            collected.addAll(pier.getWarehouse().collect());
        return collected;
    }

    /**
     * {@inheritDoc}
     * @see model.server.interfaces.targetareas.SupplyingArea#supply
     */
    @Override
    public void supply(Order<Cargo> order) {
        for (Pier pier : piers) {
            final Warehouse warehouse = pier.getWarehouse();
            if (warehouse.isSupplyingRequired()) {
                try {
                    warehouse.supply(order);
                } catch (CapacityViolationException e) {
                    continue;
                }
                break;
            }
        }
    }

    /** @return any free pier */
    private Pier getFreePier() {
        Pier pier = null;
        final Optional<Pier> any = piers.stream().filter(Pier::isFree).findAny();
        if (any.isPresent())
            pier = any.get();
        return pier;
    }

    @Override
    public String toString() {
        return "Port {" +
                "name = '" + name + '\'' +
                ", shipsQueue = " + shipsQueue +
                ", piers = " + piers +
                '}';
    }
}
