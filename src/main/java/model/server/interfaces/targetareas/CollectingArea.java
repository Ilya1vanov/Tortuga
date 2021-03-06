package model.server.interfaces.targetareas;

import javafx.util.Pair;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.pdcsystem.contracts.CommodityContract;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.pdcsystem.order.Order;

import java.util.Collection;

/**
 * This area may have completed contracts. So, it's possible
 * to collect them.
 * @param <TS> {@link Transportable} and {@link Storable} production
 * @see CommodityContract#isCompleted()
 * @author Ilya Ivanov
 */
public interface CollectingArea<TS extends Transportable & Storable> {
    /**
     * Returns collections of unwanted completed orders. Returns empty
     * collection if there are no unwanted contracts.
     * @return collection of orders
     * @see CommodityContract#isCompleted()
     */
    Collection<Order<TS>> collect();

    /**
     * Ask area if it has unwanted completed orders to collect.
     * @return true if this area needs to be collected
     * @see CommodityContract#isCompleted()
     */
    boolean isCollectingRequired();
}
