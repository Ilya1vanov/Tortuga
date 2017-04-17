package model.server.portsystem;

import gson.GSONExclude;
import model.cargo2.Cargo;
import model.client.interfaces.MaritimeCarrier;
import model.client.ship.Ship;
import model.server.Server;
import model.server.exceptions.CapacityViolationException;
import model.server.interfaces.remote.ArrivalService;
import model.server.interfaces.remote.DepartService;
import model.server.interfaces.targetareas.CollectingArea;
import model.server.interfaces.targetareas.SupplyingArea;
import model.server.interfaces.targetareas.SupplyingCollectingArea;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.pdcsystem.order.Order;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotEmpty;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.rmi.RemoteException;
import java.rmi.server.Unreferenced;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Port has a several {@link Pier Piers} and {@link Warehouse Stocks}. Able to moor
 * {@link Ship Ships} to the {@link Pier Pier}. And receive {@link TransportContract Orders}.
 * @author Ilya Ivanov
 */
@XmlRootElement
public class Port extends RemoteClass
        implements Runnable,
        Unreferenced,
        ArrivalService<Cargo>,
        SupplyingCollectingArea<Cargo> {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Port.class);

    /** parent ref */
    @GSONExclude
    @XmlTransient
    private Server server;

    /** name of this port */
    @XmlAttribute
    private String name;

    /** ships queue */
    final PriorityBlockingQueue<MaritimeCarrier<Cargo>> shipsQueue
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
    private Collection<Pier> piers = new ArrayList<>();

    /** default constructor for JAXB */
    public Port() {
        super();
    }

    /** for manual instantiation */
    public Port(String name, @NotEmpty Collection<Pier> piers) throws RemoteException {
        super(piers);
        this.name = name;
        this.piers = piers;
    }

    /** Sets up the parent pointer correctly */
    public void afterUnmarshal(Unmarshaller u, Object parent) {
        this.server = (Server) parent;
        super.setRemotes(piers);
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
    public <S extends MaritimeCarrier<Cargo>> DepartService<Cargo> moor(S carrier, long estimatedDuration, TimeUnit unit) throws RemoteException {
        synchronized (shipsQueue) {
            log.info(carrier.getName() + " arrived in port");
            Pier freePier = getFreePier();
            // add carrier to the queue
            shipsQueue.put(carrier);
            while(  // no free pier
                    freePier == null ||
                    // or carrier is not the first in the queue
                    shipsQueue.peek() != carrier) {
                try {
                    log.info(carrier.getName() + " waiting in the queue");
                    shipsQueue.wait();
                } catch (InterruptedException e) {
                    // suppress
                    e.printStackTrace();
                }
                freePier = getFreePier();
            }
            final MaritimeCarrier<Cargo> poll = shipsQueue.poll();
            assert poll == carrier : "Bug report: Problems with concurrency - carrier is not first in the queue, when it should; or queue was empty";

            log.info(carrier.getName() + " will be moored on " + freePier.getId() + " pier");
            return freePier.moor(carrier, estimatedDuration, unit);
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
        final List<Pier> pierList = piers.stream().filter(pier -> pier.getWarehouse().isSupplyingRequired()).collect(Collectors.toList());
        final Random random = new Random();
        final PrimitiveIterator.OfInt iterator = random.ints(pierList.size(), 0, pierList.size()).iterator();

        while (iterator.hasNext()) {
            try {
                pierList.get(iterator.next()).getWarehouse().supply(order);
            } catch (CapacityViolationException e) {
                e.printStackTrace();
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
        StringBuilder s = new StringBuilder("Port name: '").append(name).append('\'').append('\n').append("Piers: ");
        for (Pier pier : piers) {
            s.append(pier.toString());
        }
        return s.toString();
    }

    public PriorityBlockingQueue<MaritimeCarrier<Cargo>> getShipsQueue() {
        return shipsQueue;
    }

    @Override
    public void unreferenced() {
        server.shutdown();
    }
}
