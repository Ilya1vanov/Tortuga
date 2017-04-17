package model.server.pdcsystem.order;

import com.google.common.collect.ImmutableCollection;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Producible;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.pdcsystem.contracts.TransportContract;

import java.io.Serializable;

/**
 * Abstract data type, that represents pair of contract and production,
 * related with it.
 * @param <TS> {@link Producible}, {@link Transportable} and {@link Storable} production
 * @author Ilya Ivanov
 */
public class Order<TS extends  Transportable & Storable> implements Serializable {
    private TransportContract<Client, Carrier<TS>, TS> contract;
    private ImmutableCollection<? extends TS> production;

    // Rep invariant:
    //      production is never changed collection of goods
    //      contract is a contract between client and carrier for delivering related production
    // Abstract function:
    //      represents an order with the given contract and related production
    // Safety from rep exposure:
    //      all fields are private
    //      production is immutable
    //      contract is mutable, cause it has some states (as completed, accepted)

    public Order(TransportContract<Client, Carrier<TS>, TS> contract, ImmutableCollection<? extends TS> production) {
        this.contract = contract;
        this.production = production;
    }

    public TransportContract<Client, Carrier<TS>, TS> getContract() {
        return contract;
    }

    public ImmutableCollection<? extends TS> getProduction() {
        return production;
    }
}
