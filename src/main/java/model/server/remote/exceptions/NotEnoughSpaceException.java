package model.server.remote.exceptions;

import model.server.warehouse.Warehouse;

import java.rmi.RemoteException;

/**
 * Thrown when there are no available free space in the {@link Warehouse}.
 * @author Ilya Ivanov
 */
public class NotEnoughSpaceException extends RemoteException {
    public NotEnoughSpaceException(String message) {
        super(message);
    }
}
