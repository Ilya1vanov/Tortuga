package model.procuring.factories;

import model.office.lables.Producible;

import java.util.Collection;

/**
 * Single product factory.
 * @param <P> production
 * @author Ilya Ivanov
 */
public interface SingleFactory<P extends Producible> {
    /**
     * Create a new single product.
     * @param product string representation of product class
     * @return single product
     */
    P create(String product);
}
