package model.server.pdcsystem.contracts;

import javafx.util.Pair;
import model.server.interfaces.parties.Client;
import model.server.interfaces.parties.Performer;
import model.server.interfaces.production.Producible;
import model.server.pdcsystem.producer.Producer;
import org.apache.log4j.Logger;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
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
     * @see CommodityContract#CommodityContract(Client, Performer, Collection, Amount, Amount)
     */
    public ProductionOrder(Cu client, Pe producer, Collection<Pair<String, Integer>> notations, Amount<Mass> totalWeight, Amount<Volume> totalVolume) {
        super(client, producer, notations, totalWeight, totalVolume);
    }
}
