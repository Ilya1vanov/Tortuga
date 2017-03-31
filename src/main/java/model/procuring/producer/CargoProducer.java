package model.procuring.producer;

import model.cargo.Cargo;
import model.cargo.MultipleFactory;
import model.order.Order;
import model.procuring.provider.Provider;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Someone who is able to produce {@link Cargo} according to the {@link Order}.
 * @author Ilya Ivanov
 */
public class CargoProducer extends Producer<Order, Cargo> {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(CargoProducer.class);

    /**
     * {@inheritDoc}
     * @see Producer
     */
    public CargoProducer(Provider provider, MultipleFactory factory) {
        super(provider, factory);
    }

    /**
     * {@inheritDoc}
     * @see Producer
     */
    @Override
    public void produceAndSend(Order order) {
        final Map<String, Integer> notation = order.getNotation();
        Integer accumulated = notation.keySet().stream().map(s -> notation.get(s)).mapToInt(Integer::intValue).sum();
        List<Cargo> implementation = new ArrayList<>(accumulated);

        for (Map.Entry<String, Integer> entry : notation.entrySet()) {
            final Collection collection = factory.create(entry.getKey(), entry.getValue());
            implementation.addAll(collection);
            collection.clear();
        }

        provider.deliver(order, implementation);
    }
}
