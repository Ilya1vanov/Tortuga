package model.stock;

import model.cargo.Cargo;
import model.order.Order;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;

/**
 * Store {@link Cargo Cargo}.
 * @author Ilya Ivanov
 */
public class Stock {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(Stock.class);

    /* total capacity */
    int capacity;

    /* number of currently storing items */
    int nowStore;

    /* orders mapping: order -> collection of cargo */
    private Map<Order, Collection<Cargo>> orders;

    /** @return collection of available orders */
    public Collection<Order> getOrders() {
        return orders.keySet();
    }
}
