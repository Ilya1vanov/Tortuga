package model.server.pdcs.contracts;

import javafx.util.Pair;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Transportable;

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
     * @see CommodityContract#CommodityContract(Client, Collection)
     */
    public TransportContract(Cl client, Collection<Pair<String, Integer>> notations, String from, String to) {
        super(client, notations);
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
