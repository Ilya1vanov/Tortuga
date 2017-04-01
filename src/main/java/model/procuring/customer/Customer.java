package model.procuring.customer;

import model.office.TransportContract;
import model.office.lables.Producible;
import model.office.lables.Client;
import model.office.Contract;
import model.office.lables.Transportable;
import model.procuring.producer.Producer;
import model.procuring.provider.Provider;
import model.world.handbook.Handbook;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *  <p>Entity that make {@link Contract Orders} for {@link Producible} and send in to {@link Producer}.</p>
 * @param <PT> production
 * @author Ilya Ivanov
 */
public abstract class Customer<PT extends Producible & Transportable> implements Client {
    /** class that is able to produce items according to {@link Contract} */
    Producer<PT> producer;

    /** class that is able to deliver {@code Producible} according to the {@code CargoTransportContract} */
    Provider<PT> provider;

    /** handbook to learn about implementations of {@code Producible} */
    Handbook<PT> handbook;

    /**
     * @param producer class that is able to produce {@code Producible} items.
     * @param provider class that is able to deliver production
     * @param handbook entity that store information about implementations of the given
     * {@code Producible}
     */
    public Customer(Producer<PT> producer, Provider<PT> provider, Handbook<PT> handbook) {
        this.producer = producer;
        this.provider = provider;
        this.handbook = handbook;
    }

    /**
     * Make a new order for producing and transporting cargo.
     */
    public void makeAnOrder() {
        final Map<String, Integer> notations = new HashMap<>();

        for (String name : handbook.getNames())
            notations.put(name, 1);

        final Collection<PT> production = producer.produce(notations);

        TransportContract contract;
        contract = new TransportContract(this, notations, "ToTo", "FromFrom");

        provider.deliver(contract, production);
    }
}
