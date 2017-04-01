package model.port;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import model.cargo.Storable;
import model.office.TransportContract;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import model.ship.Ship;
import model.pier.Pier;
import model.stock.Stock;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Port has a several {@link Pier Piers} and {@link Stock Stocks}. Able to moor
 * {@link Ship Ships} to the {@link Pier Pier}. And receive {@link TransportContract Orders}.
 * @author Ilya Ivanov
 */
public class Port implements Runnable {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Port.class);

    /** thread-safe id counter */
    private static final AtomicInteger idCounter = new AtomicInteger(0);

    /** unique ship id */
    private int id;

    /** name of this port */
    private String name;

    /** port ship queue */
    private PriorityBlockingQueue<Ship> shipsQueue;

    /**  */
    private Queue<Pair<TransportContract, Collection<? extends Storable>>> ordersBuffer;

    /** list of {@link Pier Piers} of this {@code Port} */
    private List<Pier> piers;

    /**
     *
     * @param name name of the port
     */
    public Port(String name) {
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

    }

    public void moor(Ship ship) {
        if (!shipsQueue.isEmpty())
            shipsQueue.add(ship);
    }

    @Override
    public String toString() {
        return "Port{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
