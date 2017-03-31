package model.pier;

import model.order.Order;
import model.port.Port;
import model.stock.Stock;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * <p>Pier, that {@link Port Port} has.</p>
 * @author Ilya Ivanov
 */
public class Pier {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(Pier.class);

    /* port that owns this pier */
    Port port;

    /* owned stock */
    Stock stock;

    /** @return {@link Port} than owns this {@code Pier} */
    public Port getPort() {
        return port;
    }
}
