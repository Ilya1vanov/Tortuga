package model.server.interfaces.production;

import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

/**
 * Object is suitable for storing.
 * @author Ilya Ivanov
 */
public interface Storable {
    /** @return name */
    String getName();

    /** @return volume */
    Amount<Volume> getVolume();

    /** @return mass */
    Amount<Mass> getMass();
}
