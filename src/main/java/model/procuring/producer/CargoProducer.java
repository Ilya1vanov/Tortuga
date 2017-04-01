package model.procuring.producer;

import model.cargo2.Cargo;
import model.cargo2.MultipleCargoFactory;
import model.procuring.factories.MultipleFactory;
//import model.office.contracts.CargoTransportContract;
import org.apache.log4j.Logger;


/**
 * Someone who is able to produce {@link Cargo} according to the {@link CargoTransportContract}.
 * @author Ilya Ivanov
 */
public class CargoProducer extends Producer<Cargo> {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(CargoProducer.class);

    /**
     * {@code MultipleFactory} defaults to {@link MultipleCargoFactory}.
     * @see #CargoProducer(MultipleFactory)
     */
    public CargoProducer() {
        this(new MultipleCargoFactory());
    }

    /**
     * {@inheritDoc}
     * @see Producer
     */
    public CargoProducer(MultipleFactory<Cargo> factory) {
        super(factory);
    }
}
