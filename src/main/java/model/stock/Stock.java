package model.stock;

import model.cargo.Cargo;
import model.port.Port;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Store {@link Cargo Cargo}.
 * @author Ilya Ivanov
 */
public class Stock {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(Stock.class);

    /* total capacity */
    int capacity;

    /* cargo stored in this stoke */
    List<Cargo> cargo = new ArrayList<>(250);
}
