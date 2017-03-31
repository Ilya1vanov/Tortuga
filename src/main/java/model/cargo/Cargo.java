package model.cargo;

import model.ship.Carrier;
import model.ship.Ship;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

/**
 * <p>Cargo, that is transporting by {@link Carrier}.</p>
 * @author Ilya Ivanov
 */
public abstract class Cargo {
    /* cargo's name */
    private String name;

    /* cargo's volume */
    private Amount<Volume> volume;

    /* cargo's mass */
    private Amount<Mass> mass;

    public String getName() {
        return name;
    }

    public Amount<Volume> getVolume() {
        return volume;
    }

    public Amount<Mass> getMass() {
        return mass;
    }

    Cargo(Amount<Volume> volume, Amount<Mass> mass) {
        this.volume = volume;
        this.mass = mass;
    }
}
