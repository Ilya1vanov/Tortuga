package model.ship;

import javafx.beans.value.ObservableObjectValue;
import model.cargo.Storable;
import model.office.TransportContract;
import model.office.lables.Carrier;
import model.procuring.customer.Customer;
import model.stock.Stock;
import org.apache.log4j.Logger;
import model.port.Port;
import model.pier.Pier;
import model.rating.Rating;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.Volume;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Transport ship. Able to transport {@link Storable Cargos} according to the {@link TransportContract}.
 * Also can come to the {@link Port Port}, moor to the {@link Pier Pier}. Load or unload {@code Cargos}.
 * If there is some orders ship can take if or wait otherwise.</p>
 * <p>When ship moors to the {@code Pier}, it specifies time of halt. If this
 * time was exceeded, {@link Rating rating} of this {@code Ship} falls.</p>
 * @author Ilya Ivanov
 */
public class Ship implements Runnable, Comparable<Ship>, Carrier<Storable> {
    /* log4j logger */
    private Logger log = Logger.getLogger(getClass());

    private static final String CLASS_LOG_PATH = Ship.class.getSimpleName().toLowerCase();

    /* thread-safe id counter */
    private static final AtomicInteger idCounter = new AtomicInteger(0);

    /* unique ship id */
    private int id;

    /* name of the ship */
    private String name;

    /* ship's displacement */
    private Amount<Volume> displacement;

    /* ship's velocity */
    private Amount<Velocity> velocity;

    /* ship's volume */
    private Amount<Volume> volume;

    /* ship's carrying */
    private Amount<Mass> carrying;

    /* rating of the ship */
    private Rating rating = new Rating();

    /* transported cargo. defaults to 50*/
    private List<Storable> transportingStorable = new ArrayList<>(50);

    /* current TransportContract */
    TransportContract<Customer<Storable>, Ship, Storable> transportContract;

    /* current pier */
    Pier pier;

    /* current ship status */
    ObservableObjectValue<ShipStatus> status;

    /** @param name {@code Ship} name */
    public Ship(String name) {
        this.id = idCounter.incrementAndGet();
        this.name = name;
    }

    /** @return current rating */
    public Rating getRating() {
        return rating;
    }

    /* tune logger */
//    {
//        try {
//            final File tempDir = Model.getInstance().getLogSessionDir();
//            final PatternLayout layout = new PatternLayout("%d{HH:mm:ss} SHIP: %m%n");
//            final String logFilename = Paths.get(tempDir.getPath(), CLASS_LOG_PATH, String.valueOf(id) + name).toString();
//            log.addAppender(new FileAppender(layout, logFilename, true));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            // 1. try to take TransportContract
            takeOrder();
            // 2.
        }
    }

    private void takeOrder() {
        Stock stock = pier.getStock();
        synchronized (stock) {
            final Collection<TransportContract> TransportContracts = stock.getOrders();
            while(getSuitableOrder(TransportContracts) == null)
                try {
                    log.info("Waiting for suitable orders");
                    stock.wait();
                } catch (InterruptedException e) {
                    // suppress and wait again
                    log.trace(e);
                }
            TransportContract transportContract = getSuitableOrder(TransportContracts);

            assert transportContract != null : "Concurrency bug: getSuitableOrder returned null";
            transportContract.take(this);
            log.info("Order was taken: " + transportContract.toString());
        }
    }

    /**
     * Finds first suitable TransportContract and returns. Returns null if nothing
     * found.
     * @param transportContracts list of TransportContracts
     * @return first suitable TransportContract; null if absent
     */
    private TransportContract getSuitableOrder(Collection<TransportContract> transportContracts) {
        for (TransportContract transportContract : transportContracts) {
            if (transportContract.getTotalVolume().compareTo(volume) <= 0 && transportContract.getTotalWeight().compareTo(carrying) <= 0)
                return transportContract;
        }
        return null;
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
