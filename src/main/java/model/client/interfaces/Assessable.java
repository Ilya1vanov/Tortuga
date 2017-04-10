package model.client.interfaces;

import model.client.ship.logbook.Logbook;
import model.client.ship.rating.Rating;

/**
 * @author Ilya Ivanov
 * @param <T> the type of objects that this object may be compared to
 */
public interface Assessable<T> extends Comparable<T> {
    /** @return current rating */
    Rating getRating();

    /** @return logbook */
    Logbook getLogBook();
}
