package model.server.pdcs.collector;

import model.server.interfaces.parties.Carrier;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.targetareas.CollectingArea;
import model.server.pdcs.contracts.TransportContract;

/**
 * <p>Entity that cares about collecting of completed orders.</p>
 * @see TransportContract
 * @see TransportContract#isCompleted()
 * @author Ilya Ivanov
 */
public class Collector<ST extends Storable & Transportable> implements Carrier<ST> {
    /** place to completed orders accept from */
    private final CollectingArea<ST> collectingArea;

    /** @param collectingArea place to completed orders accept from */
    public Collector(CollectingArea<ST> collectingArea) {
        this.collectingArea = collectingArea;
    }

    /** Perform collection. Thread-safe method. */
    public final void collect() {
        synchronized (collectingArea) {
            collectingArea.collect();
        }
    }
}
