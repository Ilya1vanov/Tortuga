package model.cargo;

import java.util.Collection;

/**
 * Factory that is able to produce collections of objects
 * @author Ilya Ivanov
 * @param <P> product base class
 */
public interface MultipleFactory<P> {
    /**
     * Create a new collections of products.
     * @param product string representation of product class
     * @param amount amount of instantiating products
     * @return collection of the products
     */
    Collection<P> create(String product, int amount);
}
