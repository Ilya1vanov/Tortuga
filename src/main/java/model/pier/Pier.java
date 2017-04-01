package model.pier;

import model.port.Port;
import model.stock.Stock;
import org.apache.log4j.Logger;

/**
 * <p>Pier, that {@link Port Port} has.</p>
 * @author Ilya Ivanov
 */
public class Pier {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Pier.class);

    /** unique {@code Pier} id */
    private int id;

    /** port that owns this pier */
    private Port port;

    /** owned stock */
    private Stock stock;

    /** @return {@link Port} than owns this {@code Pier} */
    public Port getPort() {
        return port;
    }

    /** @return owned {@link Stock} */
    public Stock getStock() {
        return stock;
    }
}
