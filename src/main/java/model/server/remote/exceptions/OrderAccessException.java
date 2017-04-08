package model.server.remote.exceptions;

import model.server.interfaces.parties.Carrier;
import model.server.warehouse.Warehouse;

import java.rmi.RemoteException;

/**
 * Thrown if {@link Carrier Carrier} tries to get products from the
 * {@link Warehouse Warehouse}, when order was not taken or it was taken
 * not by specified {@code Carrier}.
 * @author Ilya Ivanov
 */
public class OrderAccessException extends RemoteException {
    public OrderAccessException(String message) {
        super(message);
    }
}
