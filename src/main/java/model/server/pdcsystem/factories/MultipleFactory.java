package model.server.pdcsystem.factories;

import model.server.interfaces.production.Producible;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Factory that is able to produce collections of objects
 * @author Ilya Ivanov
 * @param <P> product base class
 */
public class MultipleFactory<P extends Producible> {
    /** */
    private final SingleFactory<P> factory;

    /**
     * Create multiple factory with spec {@code SingleFactory}
     * @param factory factory that is able to produce single production
     */
    public MultipleFactory(SingleFactory<P> factory) {
        this.factory = factory;
    }

    /**
     * Create a new collections of products.
     * @param product string representation of product class
     * @param amount amount of instantiating products
     * @return collection of the products
     */
    public Collection<? extends P> create(String product, int amount) {
        final ArrayList<? extends P> list = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            list.add(factory.create(product));
        }

        return list;
    }
}
