package model.procuring.customer;

import model.order.Order;
import model.procuring.producer.Producer;

/**
 * Customer, that has a one known producer.
 * @param <O> order
 * @author Ilya Ivanov
 */
public abstract class Customer<O extends Order> {
    /** someone who is able to produce items according to {@link Order} */
    Producer producer;

    /**
     * Make a new order for producing and transporting cargo.
     */
    public abstract void makeAnOrder();
}
