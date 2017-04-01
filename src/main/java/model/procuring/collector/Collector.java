package model.procuring.collector;

import model.office.lables.Producible;

/**
 * <p>Entity that cares about </p>
 * @author Ilya Ivanov
 */
public interface Collector<P extends Producible> {
    void collect();
}
