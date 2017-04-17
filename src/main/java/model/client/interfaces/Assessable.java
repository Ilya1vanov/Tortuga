package model.client.interfaces;

import model.client.ship.logbook.Logbook;
import model.client.ship.rating.Rating;
import model.client.ship.rating.Stars;

import java.rmi.RemoteException;

/**
 * Describes entity, that can be estimated.
 * @author Ilya Ivanov
 */
public interface Assessable {
    /** @return current rating */
    Rating getRating() throws RemoteException;

    /** @param stars new assessment; null values are ignored */
    void rate(Stars stars) throws RemoteException;

    /** @return logbook */
    Logbook getLogBook() throws RemoteException;

    /** @param record new record in logbook */
    void log(String author, String record) throws RemoteException;
}
