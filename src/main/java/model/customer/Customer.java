package model.customer;

import model.order.Order;
import model.cargo.Cargo;
import model.ship.Ship;
import model.stock.Stock;
import model.port.Port;
import org.apache.log4j.Logger;

/**
 * <p>Entity that produce {@link Order Orders} and supply {@link Cargo Cargos} to {@link Stock Stocks}
 * of the {@link Port Ports}.</p>
 * @author Ilya Ivanov
 */
public class Customer {
    private static final Logger log = Logger.getLogger(Customer.class);
}
