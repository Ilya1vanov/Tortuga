package model.procuring.factories;

import model.office.lables.Producible;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Factory that is able to produce collections of objects
 * @author Ilya Ivanov
 * @param <P> product base class
 */
public abstract class MultipleFactory<P extends Producible> {
    private SingleFactory<P> factory;

    public MultipleFactory(SingleFactory<P> factory) {
        this.factory = factory;
    }

    /**
     * Create a new collections of products.
     * @param product string representation of product class
     * @param amount amount of instantiating products
     * @return collection of the products
     */
    public Collection<P> create(String product, int amount) {
        final ArrayList<P> list = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            list.add(factory.create(product));
        }

        return list;
    }
}
