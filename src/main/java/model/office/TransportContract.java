package model.office;

import model.office.lables.Producible;
import model.office.lables.Transportable;
import model.office.Contract;
import model.procuring.customer.Customer;
import model.office.lables.Carrier;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.util.Map;

/**
 * Contract for transportation.
 * @param <Cu> customer
 * @param <Ca> carrier
 * @param <T> transportable and producible
 * @author Ilya Ivanov
 */
public class TransportContract<Cu extends Customer<T>, Ca extends Carrier<T>, T extends Transportable & Producible>
        extends Contract<Cu, Ca> {
    /** name of point of departure */
    private String from;

    /** name of destination */
    private String to;

    /** product to transport
     * mapping: name -> number of items */
    private Map<String, Integer> notation;

    /** total weight of product */
    private Amount<Mass> totalWeight;

    /** total volume of product */
    private Amount<Volume> totalVolume;

    /** total number of items */
    private int totalItems;

    /**
     * Create new contract with specified customer
     * @param client customer
     * @param notation
     * @param from
     * @param to
     */
    public TransportContract(Cu client, Map<String, Integer> notation, String from, String to) {
        super(client);
        this.notation = notation;
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

    /** @return products to transport
     * mapping: name -> number of items */
    public Map<String, Integer> getNotation() {
        return notation;
    }

    /** @return total weight of product */
    public Amount<Mass> getTotalWeight() {
        return totalWeight;
    }

    /** @return total volume of product */
    public Amount<Volume> getTotalVolume() {
        return totalVolume;
    }

    /** @return total number of items */
    public int getTotalItems() {
        return totalItems;
    }

    @Override
    public String toString() {
        return "TransportContract{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                "} " + super.toString();
    }
}
