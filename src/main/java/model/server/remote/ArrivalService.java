package model.server.remote;

import model.client.rating.Stars;
import model.client.ship.MaritimeCarrier;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.targetareas.OrdersExchangeArea;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

/**
 * Remote arrival interface.
 *
 * @author Ilya Ivanov
 */
public interface ArrivalService<TS extends Transportable & Storable> extends Remote {
    /**
     * Send request for mooring.
     * @param carrier sea transport that is able to carry production
     * @param estimatedDuration estimated duration of mooring
     * @param unit time unit of {@code estimatedDuration}
     * @param <S> sea transport
     * @return interface for handling goods
     */
    <S extends MaritimeCarrier<TS>>
    OrdersExchangeArea<TS> moor(S carrier, long estimatedDuration, TimeUnit unit) throws RemoteException;

    /**
     *
     * @return
     */
    Stars unmoor() throws RemoteException;
}
