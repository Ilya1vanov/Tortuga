package model.ship;

import org.apache.log4j.Logger;
import model.port.Port;
import model.pier.Pier;
import model.order.Order;
import model.cargo.Cargo;
import model.rating.Rating;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.Volume;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Transport ship. Able to transport {@link Cargo Cargos} according to the {@link Order Order}.
 * Also can come to the {@link Port Port}, moor to the {@link Pier Pier}. Load or unload {@code Cargos}.
 * If there is some orders ship can take if or wait otherwise.</p>
 * <p>When ship moors to the {@code Pier}, it specifies time of halt. If this
 * time was exceeded, {@link Rating rating} of this {@code Ship} falls.</p>
 * @author Ilya Ivanov
 */
public class Ship implements Runnable, Comparable<Ship> {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(Ship.class);

    /* name of the ship */
    private String name;

    /* ship's displacement */
    private Amount<Volume> displacement;

    /* ships velocity */
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

    /** @return current rating*/
    public Rating getRating() {
        return rating;
    }

    @Override
    public void run() {

    }

    /* comparison by rating */
    @Override
    public int compareTo(Ship o) {
        return rating.compareTo(o.rating);
    }
}
