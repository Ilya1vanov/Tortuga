package model.cargo;

import model.office.lables.Producible;
import model.office.lables.Transportable;
import model.office.lables.Carrier;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

/**
 * <p>Storable, that is transporting by {@link Carrier}.</p>
 * @author Ilya Ivanov
 */
public abstract class Storable implements Producible, Transportable {
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

    Storable() {
        Material material = getClass().getAnnotation(Material.class);
        name = material.getName();
        volume = (Amount<Volume>) Amount.valueOf(material.getVolume());
        mass = (Amount<Mass>) Amount.valueOf(material.getMass());
    }
}
