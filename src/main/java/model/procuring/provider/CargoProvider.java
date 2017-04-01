package model.procuring.provider;

import model.cargo2.Cargo;
//import model.office.contracts.CargoTransportContract;
import model.procuring.customer.Customer;
import model.procuring.producer.Producer;
import org.apache.log4j.Logger;

/**
 * <p>Someone who is able to deliver given products to destination.</p>
 * <p>Provider, {@link Producer} and {@link Customer} are old buddies, so the respect and trust each other.
 * Ans no extra contracts and orders are needed.</p>
 * @author Ilya Ivanov
 */
public class CargoProvider extends Provider<Cargo> {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(CargoProvider.class);
}
