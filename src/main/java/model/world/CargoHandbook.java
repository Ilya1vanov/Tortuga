package model.world;

import org.apache.log4j.Logger;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

/**
 * @author Ilya Ivanov
 */
public class CargoHandbook {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(CargoHandbook.class);

    private static CargoHandbook ourInstance = new CargoHandbook();

    public static CargoHandbook getInstance() {
        return ourInstance;
    }

    private CargoHandbook() {}


    public Amount<Mass> weighCargo(String cargo) {
        // stub
        return null;
    }

    public Amount<Volume> measureCargoVolume(String cargo) {
        // stub
        return null;
    }
}
