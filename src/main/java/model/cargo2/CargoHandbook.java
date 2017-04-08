package model.cargo2;

import model.server.pdcs.handbook.Handbook;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Ilya Ivanov
 */
public class CargoHandbook implements Handbook<Cargo> {
    /** cached result */
    private final Collection<String> names;

    /** provides immutability */
    {
        final List<String> collect = Arrays.stream(Cargo.values()).map(Cargo::toString).collect(Collectors.toList());
        names = Collections.unmodifiableList(collect);
    }

    /**
     * {@inheritDoc}
     * @see Handbook
     */
    @Override
    public Collection<String> getNames() {
        return names;
    }
}
