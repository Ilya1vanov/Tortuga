package model.cargo;

import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * @author Ilya Ivanov
 */
public class MultipleCargoFactory implements MultipleFactory<Cargo> {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(MultipleCargoFactory.class);

    /**
     * {@inheritDoc}
     * @see MultipleFactory
     */
    @Override
    public Collection<Cargo> create(String product, int amount) {

        return null;
    }
}
