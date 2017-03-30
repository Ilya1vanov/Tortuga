package model.port;

import org.apache.log4j.Logger;
import model.ship.Ship;
import model.pier.Pier;
import model.stock.Stock;
import model.order.Order;

/**
 * Port has a several {@link Pier Piers} and {@link Stock Stocks}. Able to moor
 * {@link Ship Ships} to the {@link Pier Pier}. And receive {@link Order Orders}.
 * @author Ilya Ivanov
 */
public class Port implements Runnable {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(Port.class);

    @Override
    public void run() {

    }
}
