package model.server.pdcsystem.provider;

import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.targetareas.SupplyingArea;
import model.server.pdcsystem.contracts.TransportContract;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Someone who is able to deliver given products to destination.
 * @param <TS> {@link Transportable} and {@link Storable} production
 * @author Ilya Ivanov
 */
public class Provider<TS extends Transportable & Storable>
        implements Carrier<TS> {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(Provider.class);

    /** supplying area */
    private final SupplyingArea<TS> supplyingArea;

    /** @param supplyingArea place, where orders will be delivered */
    public Provider(SupplyingArea<TS> supplyingArea) {
        this.supplyingArea = supplyingArea;
    }

    /**
     * Delivers products to the {@code from} point of departure.
     * @param contract contract
     * @param products produced products
     */
    public void deliver(TransportContract<Client, Carrier<TS>, TS> contract, Collection<? extends TS> products) {
        if (contract == null || products == null)
            throw new IllegalArgumentException("Null arguments passed: " + contract + products);
        try {
            supplyingArea.supply(contract, products);
        } catch (Exception e) {
            log.warn("Unbounded delivery area thrown exception", e);
            assert false : "Unbounded delivery area thrown exception" ;
        }
        log.info("Order was delivered");
    }
}
