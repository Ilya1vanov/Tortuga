package model.world.handbook;

import model.office.lables.Producible;

import java.util.Collection;

/**
 * Entity that store information about implementations of the given
 * {@code Producible}.
 * @param <P> production
 * @author Ilya Ivanov
 */
public abstract class Handbook<P extends Producible> {
    public abstract Collection<String> getNames();
}
