package model.server.interfaces.targetareas;

import model.server.interfaces.parties.Carrier;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;

import java.rmi.Remote;

/**
 * Place where orders are delivered or from which can be received. Implementation may have
 * bounded buffer size.
 * @param <TS> {@link Transportable} and {@link Storable} production
 * @author Ilya Ivanov
 */
public interface OrdersExchangeArea<TS extends Transportable & Storable>
        extends OrdersDeliveryArea<TS>,
        OrdersDispatchArea<TS>,
        Remote {
}
