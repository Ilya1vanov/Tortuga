package model.procuring.provider;

import model.office.TransportContract;
import model.office.lables.Carrier;
import model.office.lables.Transportable;

import java.util.Collection;

/**
 * Someone who is able to deliver given products to destination.
 * @param <T> production
 * @author Ilya Ivanov
 */
public abstract class Provider<T extends Transportable>
        implements Carrier<T> {

    /**
     * Delivers products to the {@code from} point of departure.
     * @param contract contract
     * @param products produced products
     */
    public void deliver(TransportContract contract, Collection<T> products) {
        System.out.println("Delivered");
    }
}
