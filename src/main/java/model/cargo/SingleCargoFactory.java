package model.cargo;


import model.procuring.factories.SingleFactory;

/**
 * @author Ilya Ivanov
 */
public class SingleCargoFactory implements SingleFactory<Storable> {
    /**
     * {@inheritDoc}
     * @see SingleFactory
     */
    @Override
    public Storable create(String product) {
        return null;
    }
}
