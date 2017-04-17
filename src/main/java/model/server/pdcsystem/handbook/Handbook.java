package model.server.pdcsystem.handbook;

import model.server.interfaces.production.Producible;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.io.Serializable;
import java.util.Collection;

/**
 * Entity that store information about implementations of the given
 * {@code Producible}.
 * @param <P> {@link Producible} production
 * @author Ilya Ivanov
 */
public interface Handbook<P extends Producible> {
    /** @return unmodifiable, non empty collection of production names */
    Collection<String> getNames();

    /** @return weight of specified production */
    Amount<Mass> measureWeight(String name);

    /** @return volume of specified production */
    Amount<Volume> measureVolume(String name);
}
