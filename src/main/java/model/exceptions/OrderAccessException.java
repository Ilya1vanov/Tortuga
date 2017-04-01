package model.exceptions;

import model.office.lables.Carrier;

/**
 * Thrown if {@link Carrier Carrier} tries to get products from the
 * {@link model.stock.Stock Stock}, when order was not taken or it was taken
 * not by specified {@code Carrier}.
 * @author Ilya Ivanov
 */
public class OrderAccessException extends Exception {
    public OrderAccessException(String message) {
        super(message);
    }
}
