package model.procuring.producer;

import model.cargo.Cargo;
import model.cargo.MultipleFactory;
import model.order.Order;
import model.procuring.provider.Provider;

import java.util.Collection;

/**
 * Someone who is able to produce products according to the order.
 * @author Ilya Ivanov
 * @param <O> order
 * @param <O> product
 */
public abstract class Producer<O extends Order, P extends Cargo> {
    /** someone who is able to deliver production according */
    Provider provider;

    /* factory that is able to produce collections of objects*/
    MultipleFactory factory;

    protected Producer(Provider provider, MultipleFactory factory) {
        this.provider = provider;
        this.factory = factory;
    }

    /**
     * Produces products and send them to {@link Provider}
     * @param order order
     */
    public abstract void produceAndSend(O order);
}
