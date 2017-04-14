package model.client.interfaces;

import model.client.ship.logbook.Logbook;
import model.client.ship.rating.Rating;

import java.rmi.RemoteException;

/**
 * Describes entity, that can be estimated.
 * @author Ilya Ivanov
 */
public interface Assessable {
    /** @return current rating */
    Rating getRating() throws RemoteException;

    /** @return logbook */
    Logbook getLogBook() throws RemoteException;
}
