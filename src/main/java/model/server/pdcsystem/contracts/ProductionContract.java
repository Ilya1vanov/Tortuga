package model.server.pdcsystem.contracts;

import model.server.interfaces.parties.Client;
import model.server.interfaces.parties.Performer;
import model.server.interfaces.production.Producible;
import model.server.pdcsystem.producer.Producer;
import org.apache.log4j.Logger;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;

/**
 * Contract between {@link Client} and {@link Producer}, that obliges {@code Producer}
 * to supply {@link Producible} production back to {@code Client}.
 * @param <Cu> {@code Client}
 * @param <Pe> {@code Producer} of &lt;P&gt; production
 * @param <P> {@code Producible} production
 * @author Ilya Ivanov
 */
public class ProductionContract<Cu extends Client, Pe extends Producer<P>, P extends Producible> extends CommodityContract<Cu, Pe> {
    /**
     * {@inheritDoc}
     * @see CommodityContract#CommodityContract(Client, Performer, Notations, Amount, Amount)
     */
    public ProductionContract(Cu client, Pe producer, Notations notations, Amount<Mass> totalWeight, Amount<Volume> totalVolume) {
        super(client, producer, notations, totalWeight, totalVolume);
    }
}
