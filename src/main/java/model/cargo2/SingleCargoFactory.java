package model.cargo2;

import model.procuring.factories.SingleFactory;
import org.apache.log4j.Logger;

/**
 * @author Ilya Ivanov
 */
public class SingleCargoFactory implements SingleFactory<Cargo> {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(SingleCargoFactory.class);


    @Override
    public Cargo create(String product) {
        return Cargo.valueOf(product);
    }
}
