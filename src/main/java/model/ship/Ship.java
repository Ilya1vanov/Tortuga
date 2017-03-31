package model.ship;

import model.order.Order;
import org.apache.log4j.Logger;
import model.port.Port;
import model.pier.Pier;
import model.cargo.Cargo;
import model.rating.Rating;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.Volume;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Transport ship. Able to transport {@link Cargo Cargos} according to the {@link Order Order}.
 * Also can come to the {@link Port Port}, moor to the {@link Pier Pier}. Load or unload {@code Cargos}.
 * If there is some orders ship can take if or wait otherwise.</p>
 * <p>When ship moors to the {@code Pier}, it specifies time of halt. If this
 * time was exceeded, {@link Rating rating} of this {@code Ship} falls.</p>
 * @author Ilya Ivanov
 */
public class Ship implements Runnable, Comparable<Ship>, Carrier {
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
    private List<Cargo> transportingCargo = new ArrayList<>(50);

    /* current order */
    Order order;

    /* current pier */
    Pier pier;

    /* current ship status */
    ShipStatus status;

    /** @param name {@code Ship} name */
    public Ship(String name) {
        this.id = idCounter.incrementAndGet();
        this.name = name;
    }

    /** @return current rating*/
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

    private void takeOrder() {
        final List<Order> orders = pier.getPort().getOrders();
        synchronized (orders) {
            while(getSuitableOrder(orders) == null)
                try {
                    orders.wait();
                } catch (InterruptedException e) {
                    // suppress and wait again
                    e.printStackTrace();
                }
            Order order = getSuitableOrder(orders);
            order.take(this);
        }
    }

    /**
     * Finds first suitable order and returns. Returns null if nothing
     * found.
     * @param orders list of orders
     * @return first suitable order; null if absent
     */
    private Order getSuitableOrder(List<Order> orders) {
        for (Order order : orders) {
            if (order.getTotalVolume().compareTo(volume) <= 0 && order.getTotalWeight().compareTo(carrying) <= 0)
                return order;
        }
        return null;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            // 1. try to take order
            takeOrder();
            // 2.
        }
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
