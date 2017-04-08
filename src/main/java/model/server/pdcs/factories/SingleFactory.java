package model.server.pdcs.factories;

import model.server.interfaces.production.Producible;

/**
 * Single product factory.
 * @param <P> production
 * @author Ilya Ivanov
 */
public interface SingleFactory<P extends Producible> {
    /**
     * Create a new single product.
     * @param <E> instance of &lt;P&gt;
     * @param product string representation of product class
     * @return single product
     */
    <E extends P> E create(String product);
}
