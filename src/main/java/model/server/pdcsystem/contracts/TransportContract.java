package model.server.pdcsystem.contracts;

import com.google.gson.annotations.Expose;
import javafx.util.Pair;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Transportable;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.util.Collection;

/**
 * CommodityContract for transportation.
 * @param <Cl> client
 * @param <Ca> carrier
 * @param <T> {@link Transportable} production
 * @author Ilya Ivanov
 */
public class TransportContract<Cl extends Client, Ca extends Carrier<T>, T extends Transportable>
        extends CommodityContract<Cl, Ca> {
    /** name of point of departure */
    private String from;

    /** name of destination */
    private String to;

    /**
     * @param from point of dispatch
     * @param to destination
     * @see CommodityContract#CommodityContract(Client, Notations, Amount, Amount)
     */
    public TransportContract(Cl client, Notations notations, Amount<Mass> totalWeight, Amount<Volume> totalVolume, String from, String to) {
        super(client, notations, totalWeight, totalVolume);
        this.from = from;
        this.to = to;
    }

    /** @return name of point of departure */
    public String getFrom() {
        return from;
    }

    /** @return name of destination */
    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "TransportContract{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                "} " + super.toString();
    }
}
