package model.client.interfaces;

import model.client.ship.logbook.Logbook;
import model.client.ship.rating.Rating;

import java.rmi.RemoteException;

/**
 * @author Ilya Ivanov
 * @param <T> the type of objects that this object may be compared to
 */
public interface Assessable<T> {
    /** @return current rating */
    Rating getRating() throws RemoteException;

    /** @return logbook */
    Logbook getLogBook() throws RemoteException;

    /** @return same as in {@link Comparable} interface*/
    int compareTo(T o) throws RemoteException;
}
