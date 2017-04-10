package model.server.interfaces.targetareas;

import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.pdcsystem.contracts.CommodityContract;

/**
 * Place where new (not {@link CommodityContract#isAccepted() isAccepted()}) orders are supplied,
 * and completed orders can be collected from.
 * @param <TS> {@link Transportable} and {@link Storable} production
 * @see CommodityContract#isAccepted()
 * @see CommodityContract#isCompleted()
 * @author Ilya Ivanov
 */
public interface SupplyingCollectingArea<TS extends Transportable & Storable>
        extends CollectingArea<TS>, SupplyingArea<TS> {
}
