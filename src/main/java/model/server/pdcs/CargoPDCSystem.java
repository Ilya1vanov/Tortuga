package model.server.pdcs;

import model.cargo2.Cargo;
import model.cargo2.CargoHandbook;
import model.cargo2.SingleCargoFactory;
import model.server.interfaces.targetareas.CollectingArea;
import model.server.interfaces.targetareas.SupplyingArea;
import org.apache.log4j.Logger;

/**
 * Stub for PDCSystem, specialized for producing {@link Cargo}
 * @author Ilya Ivanov
 */
public class CargoPDCSystem extends PDCSystem<Cargo> {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(CargoPDCSystem.class);

    public CargoPDCSystem(SupplyingArea<Cargo> supplyingArea, CollectingArea<Cargo> collectingArea) {
        super(supplyingArea, collectingArea, new SingleCargoFactory(), new CargoHandbook());
    }
}
