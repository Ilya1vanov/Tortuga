package model.cargo2;

import model.server.pdcs.factories.SingleFactory;
import org.apache.log4j.Logger;

/**
 * @author Ilya Ivanov
 */
public class SingleCargoFactory implements SingleFactory<Cargo> {
    /**
     * {@inheritDoc}
     * @see SingleFactory
     */
    @Override
    public Cargo create(String product) {
        return Cargo.valueOf(product);
    }
}
