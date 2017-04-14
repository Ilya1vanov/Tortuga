package model.client.interfaces;

import model.client.ship.Ship;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.production.Transportable;
import model.server.pdcsystem.contracts.Importance;
import model.server.pdcsystem.order.Order;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Class that is able to carry {@link Transportable} by the sea.
 * @param <I> transportable item(s)
 * @author Ilya Ivanov
 */
public interface MaritimeCarrier<I extends Transportable> extends Carrier<I>, Assessable, Remote {
    /**
     * Compare carriers first by importance of transporting production, then by rating.
     * @return same as in {@link Comparable} interface
     */
    default int compareTo(MaritimeCarrier o) throws RemoteException {
        final MaritimeCarrier other = o;
        final MaritimeCarrier self = this;

        final Order otherOrder = other.getOrder();
        final Order selfOrder = self.getOrder();

        if ((selfOrder != null) == (otherOrder != null))
            if (selfOrder == null)
                return self.getRating().compareTo(other.getRating());
            else {
                final Importance selfImportance = selfOrder.getContract().getImportance();
                final Importance otherImportance = otherOrder.getContract().getImportance();
                return selfImportance.compareTo(otherImportance);
            }
        else
            return (selfOrder == null ? -1 : 1);
    }
}
