package model.procuring.customer;

import model.cargo.Cargo;
import model.order.Order;
import model.stock.Stock;
import model.port.Port;
import org.apache.log4j.Logger;

/**
 * <p>Entity that produce {@link Order Orders} and supply {@link Cargo Cargos} to {@link Stock Stocks}
 * of the {@link Port Ports}.</p>
 * @author Ilya Ivanov
 */
public class CargoCustomer extends Customer<Order> {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(CargoCustomer.class);

    /**
     * {@inheritDoc}
     * @see Customer
     */
    @Override
    public void makeAnOrder() {
        Order order = new Order();


        producer.produceAndSend(order);
    }
}
