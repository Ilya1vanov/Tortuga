package model.procuring.producer;

import model.procuring.factories.MultipleFactory;
import model.office.lables.Producible;
import model.office.lables.Performer;
import model.procuring.provider.Provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Class that is able to produce items according to the {@code notations}.
 * @author Ilya Ivanov
 * @param <R> production
 */
public abstract class Producer<R extends Producible> implements Performer {
    /* factory that is able to produce collections of objects */
    private MultipleFactory<R> factory;

    /**
     * @param factory class that is able
     */
    Producer(MultipleFactory<R> factory) {
        this.factory = factory;
    }

    /**
     * Produces products and send them to {@link Provider}
     * @param notation notation
     */
    public Collection<R> produce(Map<String, Integer> notation) {
        Integer accumulated = notation.keySet().stream().map(notation::get).mapToInt(Integer::intValue).sum();
        List<R> implementation = new ArrayList<>(accumulated);

        for (Map.Entry<String, Integer> entry : notation.entrySet()) {
            final Collection<R> collection = factory.create(entry.getKey(), entry.getValue());
            implementation.addAll(collection);
            collection.clear();
        }
        return implementation;
    }
}
