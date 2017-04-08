package model.server.pdcs.handbook;

import model.server.interfaces.production.Producible;

import java.util.Collection;

/**
 * Entity that store information about implementations of the given
 * {@code Producible}.
 * @param <P> {@link Producible} production
 * @author Ilya Ivanov
 */
public interface Handbook<P extends Producible> {
    /** @return unmodifiable collection of production names */
    Collection<String> getNames();
}
