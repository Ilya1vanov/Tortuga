package model.procuring.customer;

import model.cargo2.Cargo;
//import model.office.contracts.CargoTransportContract;
import model.office.lables.Carrier;
import model.procuring.producer.CargoProducer;
import model.procuring.producer.Producer;
import model.procuring.provider.CargoProvider;
import model.procuring.provider.Provider;
import model.world.handbook.CargoHandbook;
import model.world.handbook.Handbook;
import org.apache.log4j.Logger;

/**
 * <p>Entity that make {@link CargoTransportContract Orders} for {@link Cargo} and send in to {@link Producer}.</p>
 * @author Ilya Ivanov
 */
public class CargoCustomer extends Customer<Cargo> {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(CargoCustomer.class);

    /**
     * {@code provider} defaults to {@link CargoProducer}
     * {@code handbook} defaults to {@link CargoProvider}
     * {@code producer} defaults to {@link CargoHandbook}
     * @see #CargoCustomer(Producer, Provider, Handbook)
     */
    public CargoCustomer() {
        super(new CargoProducer(), new CargoProvider(), new CargoHandbook());
    }

    /**
     * @param producer class that is able to produce items according to the {@link ProductionOrder}.
     * @param provider class that is able to deliver production
     * @param handbook entity that store information about implementations of the given
     * {@code Producible}
     */
    public CargoCustomer(Producer<Cargo> producer, Provider<Cargo> provider, Handbook<Cargo> handbook) {
        super(producer, provider, handbook);
    }
}
