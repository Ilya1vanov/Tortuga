package model.world.handbook;

import model.cargo2.Cargo;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 *
 * @author Ilya Ivanov
 */
public class CargoHandbook extends Handbook<Cargo> {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(CargoHandbook.class);

    @Override
    public Collection<String> getNames() {
        return Arrays.stream(Cargo.values()).map(Cargo::toString).collect(Collectors.toList());
    }
}
