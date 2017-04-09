package model.cargo2;

import model.server.interfaces.production.Producible;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

import java.io.Serializable;

import static javax.measure.unit.SI.*;
import static javax.measure.unit.NonSI.*;

/**
 * @author Ilya Ivanov
 */
public enum Cargo implements Producible, Transportable, Storable, Serializable {
    GOLD("gold",  Amount.valueOf(622, CENTI(CUBIC_METRE)),  Amount.valueOf(12, KILOGRAM )),
    COAL("coal", Amount.valueOf(20, CUBIC_METRE),  Amount.valueOf(27600, KILOGRAM )),
    DRUGS("drugs", Amount.valueOf(20, CENTI(CUBIC_METRE)),  Amount.valueOf(700, GRAM )),
    WOOD("wood", Amount.valueOf(30, CUBIC_METRE),  Amount.valueOf(27600, KILOGRAM )),
    HERBS("herbs", Amount.valueOf(5, CUBIC_METRE),  Amount.valueOf(19800, KILOGRAM ));

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



//    static Cargo produce(String cargo) {
//        return Cargo.valueOf(cargo);
//    }
}
