package model.office;

import model.office.lables.Client;
import model.office.lables.Performer;
import model.office.lables.Carrier;

import java.util.Date;

/**
 * Executive contract between {@link Client} and {@link Performer}.
 * @author Ilya Ivanov
 */
public abstract class Contract<C extends Client, P extends Performer> {
    /** client (customer) */
    C client;

    /** performer */
    P performer;

    /** time and date when {@code CargoTransportContract} was taken */
    private Date taken;

    /** time and date when {@code CargoTransportContract} was completed */
    private Date completed;

    /**
     * Create new contract with specified customer
     * @param client customer
     */
    public Contract(C client) {
        this.client = client;
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
     * @see #getDateOfTake()
     * @see #take(Performer) take()
     */
    public boolean isTaken() {
        return !(taken == null);
    }

    /**
     * @return time and date, then this contract was taken; null if it has been not taken yet
     * @see #isTaken()
     * @see #take(Performer) take()
     */
    public Date getDateOfTake() {
        return taken;
    }

    /**
     * @param performer {@link Carrier} that take this contract
     * @see #isTaken()
     * @see #getDateOfTake()
     */
    public void take(P performer) {
        taken = new Date();
        this.performer = performer;
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
    public Date getDateOfCompletion() {
        return completed;
    }

    /**
     * Mark contract as 'completed' and set completion time.
     * @see #isCompleted()
     * @see #getDateOfCompletion()
     */
    public void complete() {
        completed = new Date();
    }

    @Override
    public String toString() {
        return "Contract{" +
                "client=" + client +
                ", performer=" + performer +
                '}';
    }
}
