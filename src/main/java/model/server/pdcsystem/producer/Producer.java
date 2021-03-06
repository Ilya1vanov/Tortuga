package model.server.pdcsystem.producer;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import model.server.interfaces.parties.Performer;
import model.server.interfaces.production.Producible;
import model.server.pdcsystem.contracts.CommodityContract;
import model.server.pdcsystem.contracts.ProductionContract;
import model.server.pdcsystem.factory.MultipleFactory;
import model.server.interfaces.production.SingleFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Class that is able to produce items according to the {@code notations}.
 * @author Ilya Ivanov
 * @param <P> {@code Producible} production
 */
public class Producer<P extends Producible> implements Performer {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Producer.class);

    /* factory that is able to produce collections of objects */
    private final MultipleFactory<P> factory;

    /** @param factory class that is able to produce production from their name */
    public Producer(SingleFactory<P> factory) {
        this.factory = new MultipleFactory<>(factory);
    }

    /**
     * Produces {@link Producible} according to notation.
     * @param order notation
     * @throws NullPointerException on null argument
     * @throws IllegalArgumentException if one of the pair contains null or
     * key is empty string or value <= 0
     */
    public ImmutableCollection<P> produce(ProductionContract<?, ? extends  Producer<P>, P> order) {
        final CommodityContract.Notations notations = order.getNotations();
        Collection<P> implementation = new ArrayList<>(order.getTotalItems());

        for (Map.Entry<String, Integer> entry : notations.entrySet()) {
            final String key = entry.getKey();
            final Integer value = entry.getValue();
            if (key == null || value == null || key.isEmpty() || value <= 0)
                throw new IllegalArgumentException("Invalid pair <" + key + ", " + value + ">");
            final Collection<? extends P> collection = factory.create(key, value);
            implementation.addAll(collection);
            collection.clear();
        }

        log.info("Goods produced");

        return ImmutableList.copyOf(implementation);
    }
}
