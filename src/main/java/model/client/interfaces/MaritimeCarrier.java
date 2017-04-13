package model.client.interfaces;

import model.server.interfaces.parties.Carrier;
import model.server.interfaces.production.Transportable;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * Class that is able to carry {@link Transportable} by the sea.
 * @param <I> transportable item(s)
 * @param <T> the type the carrier
 * @author Ilya Ivanov
 */
public interface MaritimeCarrier<I extends Transportable, T extends Carrier<I>> extends Carrier<I>, Assessable<T>, Remote {
}
