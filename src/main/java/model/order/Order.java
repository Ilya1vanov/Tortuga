package model.order;

import model.ship.Carrier;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.util.Date;
import java.util.Map;

/**
 * @author Ilya Ivanov
 */
public class Order {
    /* ship, that executes this order */
    private Carrier carrier;

    /* name of point of departure */
    private String from;

    /* name of destination */
    private String to;

    /* product to transport
    * mapping: name -> number of items */
    private Map<String, Integer> notation;

    /* total weight of product */
    private Amount<Mass> totalWeight;

    /* total volume of product */
    private Amount<Volume> totalVolume;

    /* time and date when order was taken */
    private Date taken;

    /* time and date when order was completed */
    private Date completed;

    /**
     * @return true if and only if this order was taken
     * @see #getTaken()
     */
    public boolean isTaken() {
        return !(taken == null);
    }

    /**
     * @return time and date, then this order was taken; null if it has been not taken yet
     * @see #isTaken()
     */
    public Date getTaken() {
        return taken;
    }

    /**@param carrier {@link Carrier} that take this order */
    public void take(Carrier carrier) {
        taken = new Date();
        this.carrier = carrier;
    }

    /**
     * @return true if and only if this order was completed
     * @see #getCompleted()
     */
    public boolean isCompleted() {
        return !(completed == null);
    }

    /**
     * @return time and date, then this order was completed; null if it has been not completed yet
     * @see #isCompleted()
     */
    public Date getCompleted() {
        return completed;
    }

    /** Mark order as 'completed' and set completion time. */
    public void complete() {
        completed = new Date();
    }

    /** @return {@link Carrier}, that executes this order */
    public Carrier getCarrier() {
        return carrier;
    }

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

    /** @return name of point of departure */
    public String getFrom() {
        return from;
    }

    /** @return name of destination */
    public String getTo() {
        return to;
    }
}
