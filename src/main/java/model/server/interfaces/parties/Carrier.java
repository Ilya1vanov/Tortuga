package model.server.interfaces.parties;

import model.server.interfaces.production.Transportable;
import org.jscience.physics.amount.Amount;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

/**
 * Class that is able to carry {@link Transportable}.
 * @param <T> transportable item(s)
 * @author Ilya Ivanov
 */
public interface Carrier<T extends Transportable> extends Performer {
    /** @return volume limit for transport */
    default Amount<Volume> getVolume() {
        throw new NotImplementedException();
    }

    /** @return deadweight for transport */
    default Amount<Mass> getCarrying() {
        throw new NotImplementedException();
    }
}
