package model.server.interfaces.remote;

import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.targetareas.OrdersExchangeArea;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @param <TS> {@link Transportable} and {@link Storable} production
 * @author Ilya Ivanov
 */
public interface DepartService<TS extends Transportable & Storable> extends Remote {
    /** Unmoor and get rated. */
    void unmoor() throws RemoteException;

     /** @return interface for handling goods */
    OrdersExchangeArea<TS> getOrdersExchangeArea() throws RemoteException;
}
