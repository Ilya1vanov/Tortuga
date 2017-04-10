package model.server.pdcsystem;

import model.server.interfaces.production.Producible;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.targetareas.CollectingArea;
import model.server.interfaces.targetareas.SupplyingArea;
import model.server.pdcsystem.collector.Collector;
import model.server.pdcsystem.customer.Customer;
import model.server.pdcsystem.factories.SingleFactory;
import model.server.pdcsystem.handbook.Handbook;
import org.apache.log4j.Logger;

/**
 * Production, delivery and Collection system. System has no inner states, excluding
 * constructor params, so, it can be reused without instantiating a new object.
 * @param <PTS> {@link Producible}, {@link Transportable} and {@link Storable}
 * @author Ilya Ivanov
 */
public class PDCSystem<PTS extends Producible & Transportable & Storable> implements Runnable {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(PDCSystem.class);

    /** supplying area */
    private final SupplyingArea<PTS> supplyingArea;

    /** collecting area */
    private final CollectingArea<PTS> collectingArea;

    /** customer */
    private final Customer<PTS> customer;

    /** collector */
    private final Collector<PTS> collector;

    /**
     * <p>{@code customer} is defaults to {@link Customer Customer&lt;PTS&gt;}</p>
     * <p>{@code collector} is defaults to {@link Collector Collector&lt;PTS&gt;}</p>
     * @see #PDCSystem(SupplyingArea, CollectingArea, Customer, Collector)
     */
    public PDCSystem(SupplyingArea<PTS> supplyingArea, CollectingArea<PTS> collectingArea, SingleFactory<PTS> factory, Handbook<PTS> handbook) {
        this(supplyingArea,
                collectingArea,
                new Customer<PTS>(handbook, factory, supplyingArea), new Collector<>(collectingArea));
    }

    public PDCSystem(SupplyingArea<PTS> supplyingArea, CollectingArea<PTS> collectingArea, Customer<PTS> customer, Collector<PTS> collector) {
        this.supplyingArea = supplyingArea;
        this.collectingArea = collectingArea;
        this.customer = customer;
        this.collector = collector;
    }

    @Override
    public final void run() {
        log.info("PDC cycle was started!");
        if (collectingArea.isCollectingRequired()) {
            log.info("Perform collecting");
            collector.collect();
            System.runFinalization();
            System.gc();
        }
        else
            log.info("Collecting canceled by the client");
        if (supplyingArea.isSupplyingRequired()) {
            log.info("Perform supplying");
            customer.makeAnOrder();
        }
        else
            log.info("Supplying canceled by the client");
        log.info("PDC cycle is over\n");
    }
}
