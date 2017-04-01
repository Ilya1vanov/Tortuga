package model.cargo2;

import model.office.lables.Producible;
import model.office.lables.Storable;
import model.office.lables.Transportable;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

import static javax.measure.unit.SI.*;

/**
 * @author Ilya Ivanov
 */
public enum Cargo implements Producible, Transportable, Storable {
    GOLD("gold",  Amount.valueOf(200, CUBIC_METRE),  Amount.valueOf(20, KILOGRAM ));

    Cargo(String name, Amount<Volume> volume, Amount<Mass> mass) {
        this.name = name;
        this.volume = volume;
        this.mass = mass;
    }

    /** cargo's name */
    private final String name;

    /** cargo's volume */
    private final Amount<Volume> volume;

    /** cargo's mass */
    private final Amount<Mass> mass;

    /** @return cargo's name */
    public String getName() {
        return name;
    }

    /** @return cargo's volume */
    public Amount<Volume> getVolume() {
        return volume;
    }

    /** @return cargo's mass */
    public Amount<Mass> getMass() {
        return mass;
    }

    static Cargo produce(String cargo) {
        return Cargo.valueOf(cargo);
    }
}
