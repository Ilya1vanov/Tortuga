package model.procuring.provider;

import model.cargo.Cargo;
import model.order.Order;

import java.util.Collection;

/**
 * Someone who is able to deliver given products to destination.
 * @param <O> order
 * @param <P> product
 * @author Ilya Ivanov
 */
public abstract class Provider<O extends Order, P extends Cargo> {
    /**
     * Delivers products to the {@code from} point of departure.
     * @param order order
     * @param products produced products
     */
    public abstract void deliver(O order, Collection<P> products);
}
