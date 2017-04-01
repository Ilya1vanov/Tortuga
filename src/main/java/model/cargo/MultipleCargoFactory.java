package model.cargo;

import model.procuring.factories.MultipleFactory;
import model.procuring.factories.SingleFactory;

/**
 * @author Ilya Ivanov
 */
public class MultipleCargoFactory extends MultipleFactory<Storable> {
    /**
     * {@code factory} defaults to
     * @see MultipleCargoFactory
     */
    public MultipleCargoFactory() {
        super(new SingleCargoFactory());
    }

    /**
     * {@inheritDoc}
     * @see MultipleFactory
     */
    public MultipleCargoFactory(SingleFactory<Storable> factory) {
        super(factory);
    }
}
