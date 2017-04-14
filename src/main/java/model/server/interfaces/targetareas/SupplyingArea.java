package model.server.interfaces.targetareas;

import model.server.exceptions.CapacityViolationException;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.pdcsystem.contracts.CommodityContract;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.pdcsystem.order.Order;

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
     * @param order new order
     * @see CommodityContract#isAccepted()
     */
    void supply(Order<TS> order) throws CapacityViolationException;

    /**
     * Ask area if it needs supply.
     * @return true if this area needs supply.
     */
    boolean isSupplyingRequired();
}
