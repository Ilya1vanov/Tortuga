package model.cargo;

import org.apache.log4j.Logger;
import model.ship.Ship;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

/**
 * <p>Cargo, that is transporting by {@link Ship Ships}.</p>
 * @author Ilya Ivanov
 */
public abstract class Cargo {
    /* cargo's volume */
    private Amount<Volume> volume;

    /* cargo's carrying */
    private Amount<Mass> carrying;

    protected Cargo(Amount<Volume> volume, Amount<Mass> carrying) {
        this.volume = volume;
        this.carrying = carrying;
    }
}
