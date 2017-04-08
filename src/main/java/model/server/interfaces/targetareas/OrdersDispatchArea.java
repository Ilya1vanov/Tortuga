package model.server.interfaces.targetareas;

import javafx.util.Pair;
import model.server.remote.exceptions.OrderAccessException;
import model.server.pdcs.contracts.CommodityContract;
import model.server.pdcs.contracts.TransportContract;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;

import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Place which from new (not {@link CommodityContract#isAccepted() isAccepted()}) orders can be received.
 * @param <TS> {@link Transportable} and {@link Storable} production
 * @see CommodityContract#isAccepted()
 * @author Ilya Ivanov
 */
public interface OrdersDispatchArea<TS extends Transportable & Storable> {
    /**
     * Take new (not {@link CommodityContract#isAccepted() isAccepted()}) order.
     * <p><b>Note: </b>Implementation must be thread-safe.</p>
     * @return pair of suitable order and
     * @throws RemoteException if some remote exception occurs
     * @see CommodityContract#accept accept()
     */
    Pair<TransportContract<Client, Carrier<TS>, TS>, Collection<? extends TS>> takeOrder()
            throws RemoteException;
}
