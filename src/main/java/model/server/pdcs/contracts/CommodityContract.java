package model.server.pdcs.contracts;

import javafx.util.Pair;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.parties.Performer;
import org.jscience.physics.amount.Amount;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Executive contract between {@link Client} and {@link Performer}.
 * @author Ilya Ivanov
 */
public abstract class CommodityContract<C extends Client, P extends Performer> implements Serializable {
    /** client (customer) */
    private C client;

    /** performer */
    private P performer;

    /** time and date when {@code CargoTransportContract} was taken */
    private LocalDateTime taken;

    /** time and date when {@code CargoTransportContract} was completed */
    private LocalDateTime completed;

    /** total weight of product */
    private Amount<Mass> totalWeight;

    /** total volume of product */
    private Amount<Volume> totalVolume;

    /** total number of items */
    private int totalItems;

    /** pairs like: name, number of items */
    private Collection<Pair<String, Integer>> notations;

    /**
     * Create new contract with specified customer. This contract
     * has open type.
     * @param client customer
     * @param notations pairs like: name, number of items
     * @throws IllegalArgumentException if one of the pair contains null or
     * key is empty string or value <= 0
     */
    CommodityContract(C client, Collection<Pair<String, Integer>> notations) {
        this.client = client;
        this.notations = notations;

        for (Pair<String, Integer> notation : notations) {
            final String key = notation.getKey();
            final Integer value = notation.getValue();
            if (key == null || value == null || key.isEmpty() || value <= 0)
                throw new IllegalArgumentException("Invalid pair <" + key + ", " + value + ">");
            totalItems += value;
        }
    }

    /**
     * Create new contract with specified customer. This contract
     * has closed type.
     * @see CommodityContract#CommodityContract(Client, Collection)
     */
    CommodityContract(C client, P performer, Collection<Pair<String, Integer>> notations) {
        this(client, notations);
        accept(performer);
    }

    /** @return client (customer) */
    public C getClient() {
        return client;
    }

    /** @return performer */
    public P getPerformer() {
        return performer;
    }

    /**
     * @return true if and only if this contract was taken
     * @see #getDateOfAcceptance()
     * @see #accept(Performer) take()
     */
    public boolean isAccepted() {
        return !(taken == null);
    }

    /**
     * @return time and date, then this contract was taken; null if it has been not taken yet
     * @see #isAccepted()
     * @see #accept(Performer) take()
     */
    public LocalDateTime getDateOfAcceptance() {
        return taken;
    }

    /**
     * Accept contract and sign. Repeated call has no effect.
     * @param performer {@link Carrier} that accept this contract
     * @see #isAccepted()
     * @see #getDateOfAcceptance()
     */
    public void accept(P performer) {
        if (!isAccepted()) {
            taken = LocalDateTime.now();
            this.performer = performer;
        }
    }

    /**
     * @return true if and only if this contract was completed
     * @see #getDateOfCompletion()
     * @see #complete()
     */
    public boolean isCompleted() {
        return !(completed == null);
    }

    /**
     * @return time and date, then this contract was completed; null if it has been not completed yet
     * @see #isCompleted()
     * @see #complete()
     */
    public LocalDateTime getDateOfCompletion() {
        return completed;
    }

    /**
     * Mark contract as 'completed' and set completion time.
     * Repeated call has no effect.
     * @see #isCompleted()
     * @see #getDateOfCompletion()
     */
    public void complete() {
        if (!isCompleted()) {
            completed = LocalDateTime.now();
        }
    }

    /** @return pairs like: name, number of items */
    public Collection<Pair<String, Integer>> getNotations() {
        return notations;
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
        return "CommodityContract{" +
                "client=" + client +
                ", performer=" + performer +
                '}';
    }
}
