package model.cargo2;

import model.server.interfaces.production.SingleFactory;

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
