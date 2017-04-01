package model.office.lables;

import model.office.lables.Producible;
import model.office.lables.Transportable;
import model.office.lables.Performer;

/**
 * Class that is able to carry {@link Producible}.
 * @param <T> transportable item(s)
 * @author Ilya Ivanov
 */
public interface Carrier<T extends Transportable> extends Performer {
}
