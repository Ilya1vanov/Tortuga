package model.order;

import model.port.Port;
import org.apache.log4j.Logger;
import model.ship.Ship;
import model.cargo.Cargo;

import java.util.List;

/**
 * <p>Order for transportation. User by {@link Ship Ships} to deliver {@link Cargo Cargos}.</p>
 * @author Ilya Ivanov
 */
public class Order {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(Order.class);

    /* ship, that executes this order*/
    Ship transporter;

    /* point of departure port */
    Port pointOfDeparture;

    /* destination port */
    Port destination;

    /* cargo to transport */
    List<Cargo> cargo;

    /* is order was taken flag */
    boolean taken;

    /* done flag */
    boolean done;

    public void take(Ship ship) {
        transporter = ship;
    }

    public void done() {
        done = true;
    }
}
