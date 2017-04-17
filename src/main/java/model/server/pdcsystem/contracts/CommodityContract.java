package model.server.pdcsystem.contracts;

import com.google.gson.annotations.Expose;
import gson.GSONExclude;
import javafx.util.Pair;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.parties.Performer;
import org.hibernate.validator.constraints.NotEmpty;
import org.jscience.physics.amount.Amount;
import org.mockito.internal.matchers.Not;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Executive contract between {@link Client} and {@link Performer}.
 * @author Ilya Ivanov
 */
public abstract class CommodityContract<C extends Client, P extends Performer> implements Serializable {
    /** client (customer) */
    @GSONExclude
    private C client;

    /** performer */
    @GSONExclude
    private P performer;

    /** importance of production delivering */
    private Importance importance;

    /** time and date when {@code CargoTransportContract} was taken */
    @GSONExclude
    private LocalDateTime taken;

    /** time and date when {@code CargoTransportContract} was completed */
    @GSONExclude
    private LocalDateTime completed;

    /** total weight of product */
    private Amount<Mass> totalWeight;

    /** total volume of product */
    private Amount<Volume> totalVolume;

    /** total number of items */
    private int totalItems;

    /** pairs like: name, number of items */
    @GSONExclude
    private Notations notations;

    // Rep invariant:
    //      notations is immutable, not empty map of entries: key is a name of production, value is amount of productions units
    // Abstract function:
    //      represents a contract between client and performer
    // Rep exposure safety:
    //      All fields are private
    //      taken and completed dates are immutable LocalDateTime type
    //      importance is immutable

    /**
     * {@code importance} is defaults to {@link Importance Importance.NORMAL}.
     * @see #CommodityContract(Client, Notations, Amount, Amount, Importance)
     */
    public CommodityContract(C client, Notations notations, Amount<Mass> totalWeight, Amount<Volume> totalVolume) {
        this(client, notations, totalWeight, totalVolume, Importance.NORMAL);
    }

    /**
     * Create new contract with specified customer. This contract
     * has open type.
     * @param client customer
     * @param notations pairs like: name, number of items
     * @param totalWeight summary weight of production
     * @param totalVolume summary volume of production
     * @param importance importance of transportation
     * @throws IllegalArgumentException if one of the pair contains null or
     * key is empty string or value <= 0
     */
    public CommodityContract(C client, Notations notations, Amount<Mass> totalWeight, Amount<Volume> totalVolume, Importance importance) {
        this.client = client;
        this.notations = notations;
        this.totalWeight = totalWeight;
        this.totalVolume = totalVolume;
        this.importance = importance;

        for (Map.Entry<String, Integer> notation : notations.entrySet()) {
            final String key = notation.getKey();
            final Integer value = notation.getValue();
            if (key == null || value == null || key.isEmpty() || value <= 0)
                throw new IllegalArgumentException("Invalid pair <" + key + ", " + value + ">");
            totalItems += value;
        }
        checkRep();
    }

    /**
     * Create new contract with specified customer. This contract
     * has closed type.
     * @see CommodityContract#CommodityContract(Client, Notations, Amount, Amount, Importance)
     */
    public CommodityContract(C client, P performer, Notations notations, Amount<Mass> totalWeight, Amount<Volume> totalVolume, Importance importance) {
        this(client, notations, totalWeight, totalVolume, importance);
        accept(performer);
    }

    /**
     * {@code importance} is defaults to {@link Importance Importance.NORMAL}.
     * @see CommodityContract#CommodityContract(Client, Performer, Notations, Amount, Amount, Importance)
     */
    public CommodityContract(C client, P performer, Notations notations, Amount<Mass> totalWeight, Amount<Volume> totalVolume) {
        this(client, performer, notations, totalWeight, totalVolume, Importance.NORMAL);
    }

    /** always check representation invariant after construction */ {
//        checkRep();
    }

    /** @return client (customer) */
    public C getClient() {
        return client;
    }

    /** @return performer */
    public P getPerformer() {
        return performer;
    }

    public Importance getImportance() {
        return importance;
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
    public Notations getNotations() {
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

    /** check representation */
    private void checkRep() {
        assert client != null;
        assert notations != null;
        assert totalWeight != null;
        assert totalVolume != null;
        assert importance != null;
    }

    static public class Notations extends HashMap<String, Integer> {
        public Notations(String key, Integer value) {
            super();
            super.put(key, value);
            checkRep();
        }

        public Notations(@NotEmpty Map<? extends String, ? extends Integer> m) {
            super(m);
            checkRep();
        }

        @Override
        public Integer put(String key, Integer value) {
            if (this.containsKey(key)) {
                final Integer integer = get(key);
                super.put(key, integer + value);
                return integer;
            }
            final Integer put = super.put(key, value);
            checkRep();
            return put;
        }

        @Override
        public void clear() {}

        @Override
        public Integer remove(Object key) {
            final Integer remove = super.remove(key);
            checkRep();
            return remove;
        }

        private void checkRep() {
            assert !isEmpty() : "Notations is empty";
        }
    }
}
