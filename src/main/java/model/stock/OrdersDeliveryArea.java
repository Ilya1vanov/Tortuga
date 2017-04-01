package model.stock;

import model.exceptions.NotEnoughSpaceException;
import model.office.TransportContract;
import model.office.lables.Storable;

import java.util.Collection;

/**
 * Place where orders are delivered.
 * @author Ilya Ivanov
 */
public interface OrdersDeliveryArea {
    /**
     * Checks if {@link Storable}, specified in
     * {@link TransportContract}, can be placed in this {@code OrdersDeliveryArea}.
     * @param transportContract contract for transporting
     * @return true if and only if {@link Storable}, specified in
     * {@link TransportContract}, can be placed in this {@code OrdersDeliveryArea}.
     */
    boolean hasAvailableSpaceFor(TransportContract transportContract);

    /**
     * Puts TransportContract and products to this {@code Stock}.
     * @param TransportContract TransportContract to put
     * @param products products according to the {@code TransportContract}.
     */
    void putOrder(TransportContract TransportContract, Collection<? extends Storable> products) throws NotEnoughSpaceException;
}
