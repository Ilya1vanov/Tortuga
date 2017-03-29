package model.ship;

import org.apache.log4j.Logger;
import model.port.Port;
import model.pier.Pier;
import model.order.Order;
import model.cargo.Cargo;
import model.rating.Rating;

/**
 * <p>Transport ship. Able to transport {@link Cargo Cargos} according to the {@link Order Order}.
 * Also can come to the {@link Port Port}, moor to the {@link Pier Pier}. Load or unload {@code Cargos}.
 * If there is some orders ship can take if or wait otherwise.</p>
 * <p>When ship moors to the {@code Pier}, it specifies time of halt. If this
 * time was exceeded, {@link Rating Rating} of this {@code Ship} falls.</p>
 * @author Ilya Ivanov
 */
public class Ship implements Runnable {
    private static final Logger log = Logger.getLogger(Ship.class);



    @Override
    public void run() {

    }
}
