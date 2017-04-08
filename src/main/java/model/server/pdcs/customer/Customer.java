package model.server.pdcs.customer;

import javafx.util.Pair;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Producible;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.targetareas.OrdersDeliveryArea;
import model.server.interfaces.targetareas.SupplyingArea;
import model.server.pdcs.contracts.CommodityContract;
import model.server.pdcs.contracts.ProductionOrder;
import model.server.pdcs.contracts.TransportContract;
import model.server.pdcs.factories.MultipleFactory;
import model.server.pdcs.factories.SingleFactory;
import model.server.pdcs.producer.Producer;
import model.server.pdcs.provider.Provider;
import model.server.pdcs.handbook.Handbook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * <p>Entity that produces {@link TransportContract Orders} and start producing-
 * delivery chain.</p>
 * @param <PTS> {@link Producible}, {@link Transportable} and {@link Storable} production
 * @author Ilya Ivanov
 */
public class Customer<PTS extends Producible & Transportable & Storable> implements Client {
    /** class that is able to produce items according to {@link CommodityContract} */
    private final Producer<PTS> producer;

    /** class that is able to deliver {@code Producible} according to the {@code TransportContract} */
    private final Provider<PTS> provider;

    /** handbook to learn about implementations of {@code Producible} */
    private final Handbook<PTS> handbook;

    /**
     * <p>{@code producer} is defaults to {@code new Producer<>(new MultipleFactory<>(factory))}</p>
     * <p>{@code provider} is defaults to {@code new Provider<>(deliveryArea)}</p>
     * @see #Customer(Producer, Provider, Handbook)
     */
    public Customer(Handbook<PTS> handbook, SingleFactory<PTS> factory, SupplyingArea<PTS> supplyingArea) {
        this(new Producer<>(new MultipleFactory<>(factory)), new Provider<>(supplyingArea), handbook);
    }

    /**
     * @param producer class that is able to produce {@code Producible} items.
     * @param provider class that is able to deliver production
     * @throws IllegalArgumentException on null argument
     * @param handbook entity that store information about implementations of the given
     * {@code Producible}
     */
    public Customer(Producer<PTS> producer, Provider<PTS> provider, Handbook<PTS> handbook) {
        if (producer == null || provider == null || handbook == null)
            throw new IllegalArgumentException("Illegal null argument: " + producer + ", " + provider + ", " + handbook);
        this.producer = producer;
        this.provider = provider;
        this.handbook = handbook;
    }

    /**
     * Make a new order for producing and transporting cargo.
     */
    public final void makeAnOrder() {
        final Collection<String> names = handbook.getNames();

        final Collection<Pair<String, Integer>> notations = new ArrayList<>(names.size());

        notations.addAll(names.stream().map(name -> new Pair<>(name, 1)).collect(Collectors.toList()));

        ProductionOrder<Customer<PTS>, ? extends Producer<PTS>, PTS> order;
        order = new ProductionOrder<>(this, producer, notations);
        final Collection<? extends PTS> production = producer.produce(order);

        TransportContract<Client, ? extends Carrier<PTS>, PTS> contract;
        contract = new TransportContract<>(this, notations, "ToTo", "FromFrom");

        provider.deliver(contract, production);
    }
}
