package model.server.interfaces.targetareas;

import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.pdcs.contracts.CommodityContract;
import model.server.pdcs.contracts.TransportContract;

import java.util.Collection;

/**
 * Place where new (not {@link CommodityContract#isAccepted() isAccepted()}) orders are supplied.
 * May be implemented as bounded or unbounded storage.
 * @param <TS> {@link Transportable} and {@link Storable} production
 * @see CommodityContract#isAccepted()
 * @author Ilya Ivanov
 */
public interface SupplyingArea<TS extends Transportable & Storable> {
    /**
     * Supply new (not {@link CommodityContract#isAccepted() isAccepted()}) order.
     * @param transportContract new transport contract
     * @param products products according to the {@code transportContract}.
     * @see CommodityContract#isAccepted()
     */
    void supply(TransportContract<Client, ? extends Carrier<TS>, TS> transportContract, Collection<? extends TS> products);

    /**
     * Ask area if it needs supply.
     * @return true if this area needs supply.
     */
    boolean isSupplyingRequired();
}
