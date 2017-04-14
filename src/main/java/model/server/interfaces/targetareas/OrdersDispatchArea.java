package model.server.interfaces.targetareas;

import javafx.util.Pair;
import model.server.pdcsystem.contracts.CommodityContract;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.pdcsystem.order.Order;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Place which from new (not {@link CommodityContract#isAccepted() isAccepted()}) orders can be received.
 * @param <TS> {@link Transportable} and {@link Storable} production
 * @see CommodityContract#isAccepted()
 * @author Ilya Ivanov
 */
public interface OrdersDispatchArea<TS extends Transportable & Storable> extends Serializable {
    /**
     * Take new (not {@link CommodityContract#isAccepted() isAccepted()}) order.
     * <p><b>Note: </b>Implementation must be thread-safe.</p>
     * @return suitable order
     * @throws RemoteException if some remote exception occurs
     * @see CommodityContract#accept accept()
     */
    Order<TS> takeOrder()
            throws RemoteException;
}
