package model.client.ship;

import model.server.interfaces.parties.Carrier;
import model.server.interfaces.production.Transportable;

/**
 * Class that is able to carry {@link Transportable} by the sea.
 * @param <T> transportable item(s)
 * @author Ilya Ivanov
 */
public interface MaritimeCarrier<T extends Transportable> extends Carrier<T> {
}
