package model.server.pdcsystem.customer;

import com.google.common.collect.ImmutableCollection;
import javafx.util.Pair;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Producible;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.targetareas.SupplyingArea;
import model.server.pdcsystem.contracts.CommodityContract;
import model.server.pdcsystem.contracts.ProductionContract;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.interfaces.production.SingleFactory;
import model.server.pdcsystem.order.Order;
import model.server.pdcsystem.producer.Producer;
import model.server.pdcsystem.provider.Provider;
import model.server.pdcsystem.handbook.Handbook;
import org.apache.log4j.Logger;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>Entity that produces {@link TransportContract Orders} and start producing-
 * delivery chain.</p>
 * @param <PTS> {@link Producible}, {@link Transportable} and {@link Storable} production
 * @author Ilya Ivanov
 */
public class Customer<PTS extends Producible & Transportable & Storable> implements Client {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Customer.class);

    /** class that is able to produce items according to {@link CommodityContract} */
    private transient final Producer<PTS> producer;

    /** class that is able to deliver {@code Producible} according to the {@code TransportContract} */
    private transient final Provider<PTS> provider;

    /** handbook to learn about implementations of {@code Producible} */
    private transient final Handbook<PTS> handbook;

    /**
     * <p>{@code producer} is defaults to {@code new Producer<>(new MultipleFactory<>(factory))}</p>
     * <p>{@code provider} is defaults to {@code new Provider<>(deliveryArea)}</p>
     * @see #Customer(Producer, Provider, Handbook)
     */
    public Customer(Handbook<PTS> handbook, SingleFactory<PTS> factory, SupplyingArea<PTS> supplyingArea) {
        this(new Producer<>(factory), new Provider<>(supplyingArea), handbook);
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

        final Map<String, Integer> map = new HashMap<>();

        map.putAll(names.stream().map(name -> new Pair<>(name, 4)).collect(Collectors.toMap(Pair::getKey, Pair::getValue)));

        Amount<Mass> totalWeight = Amount.valueOf(0, Mass.UNIT);
        Amount<Volume> totalVolume = Amount.valueOf(0, Volume.UNIT);

        for (Map.Entry<String, Integer> notation : map.entrySet()) {
            totalWeight = totalWeight.plus(handbook.measureWeight(notation.getKey()).times(notation.getValue()));
            totalVolume = totalVolume.plus(handbook.measureVolume(notation.getKey()).times(notation.getValue()));
        }

        final CommodityContract.Notations notations = new CommodityContract.Notations(map);

        ProductionContract<Customer<PTS>, ? extends Producer<PTS>, PTS> order;
        order = new ProductionContract<>(this, producer, notations, totalWeight, totalVolume);

        log.info("The order was made");
        final ImmutableCollection<? extends PTS> production = producer.produce(order);

        TransportContract<Client, Carrier<PTS>, PTS> contract;
        contract = new TransportContract<>(this, notations, totalWeight, totalVolume,"Tortuga", "Minsk");

        provider.deliver(new Order<>(contract, production));
    }
}
