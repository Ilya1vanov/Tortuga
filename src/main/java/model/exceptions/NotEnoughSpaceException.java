package model.exceptions;

import model.stock.OrdersDeliveryArea;
import model.stock.Stock;
import org.apache.log4j.Logger;

/**
 * Thrown when there are no available free space in the {@link Stock}.
 * @author Ilya Ivanov
 */
public class NotEnoughSpaceException extends Exception{
    /* indignant stock */
    OrdersDeliveryArea area;

    /**
     * Throws by {@link OrdersDeliveryArea} when attempt was made to place an order,
     * but there is not enough space.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param area indignant.
     * @since 1.4
     */
    public NotEnoughSpaceException(String message, OrdersDeliveryArea area) {
        super(message);
        this.area = area;
    }
}
