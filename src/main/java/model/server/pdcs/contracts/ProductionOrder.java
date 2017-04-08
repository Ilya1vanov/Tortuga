package model.server.pdcs.contracts;

import javafx.util.Pair;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Producible;
import model.server.pdcs.producer.Producer;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Contract between {@link Client} and {@link Producer}, that obliges {@code Producer}
 * to supply {@link Producible} production back to {@code Client}.
 * @param <Cu> {@code Client}
 * @param <Pe> {@code Producer} of &lt;P&gt; production
 * @param <P> {@code Producible} production
 * @author Ilya Ivanov
 */
public class ProductionOrder<Cu extends Client, Pe extends Producer<P>, P extends Producible> extends CommodityContract<Cu, Pe> {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(ProductionOrder.class);

    /**
     * {@inheritDoc}
     * @see CommodityContract#CommodityContract(Client, Collection)
     */
    public ProductionOrder(Cu client, Pe producer, Collection<Pair<String, Integer>> notations) {
        super(client, producer, notations);
    }
}
