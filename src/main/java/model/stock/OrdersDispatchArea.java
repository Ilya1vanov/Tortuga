package model.stock;

import model.exceptions.OrderAccessException;
import model.office.TransportContract;
import model.office.lables.Carrier;
import model.office.lables.Storable;

import java.util.Collection;

/**
 * Place from which orders can be received.
 * @author Ilya Ivanov
 */
public interface OrdersDispatchArea {
    /** @return collection of available orders */
    Collection<TransportContract> getOrders();

    /**
     * Unloads products from this {@code Stock} according to the transportContract.
     * transportContract must be already taken by specified {@link Carrier Carrier<cargo>}.
     * @param transportContract transportContract
     * @return products according to the transportContract
     * @throws OrderAccessException if transportContract was not taken or it was taken not by
     * specified {@code carrier}
     * @see Contract#take take()
     */
    Collection<? extends Storable> takeOrder(Carrier carrier, TransportContract transportContract)
            throws OrderAccessException;
}
