package model.server.interfaces.targetareas;

import model.server.exceptions.CapacityViolationException;
import model.server.pdcsystem.contracts.CommodityContract;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Place where completed orders are delivered. Implementation may have
 * bounded buffer size.
 * @param <TS> {@link Transportable} and {@link Storable} production
 * @see CommodityContract#isCompleted()
 * @author Ilya Ivanov
 */
public interface OrdersDeliveryArea<TS extends Transportable & Storable> extends Serializable {
    /**
     * Puts completed TransportContract and products to this {@code OrdersDeliveryArea}.
     * <p><b>Note: </b>Implementation must be thread-safe.</p>
     * @param transportContract new transport contract
     * @param products products according to the {@code transportContract}.
     * @throws CapacityViolationException if capacity of storage doesn't allow to place required order
     * but there is not enough space, was made.
     * @see CommodityContract#isCompleted()
     */
    void putOrder(TransportContract<Client, Carrier<TS>, TS> transportContract, Collection<? extends TS> products)
            throws RemoteException, CapacityViolationException;
}
