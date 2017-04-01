package model.cargo2;

import model.procuring.factories.MultipleFactory;
import model.procuring.factories.SingleFactory;

/**
 * @author Ilya Ivanov
 */
public class MultipleCargoFactory extends MultipleFactory<model.cargo2.Cargo> {
    /**
     * {@code factory} defaults to
     * @see model.cargo2.MultipleCargoFactory
     */
    public MultipleCargoFactory() {
        super(new SingleCargoFactory());
    }

    /**
     * {@inheritDoc}
     * @see MultipleFactory
     */
    public MultipleCargoFactory(SingleFactory<Cargo> factory) {
        super(factory);
    }
}
