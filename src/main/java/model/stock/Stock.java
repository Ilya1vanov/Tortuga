package model.stock;

import model.cargo2.Cargo;
import model.office.TransportContract;
import model.exceptions.NotEnoughSpaceException;
import model.exceptions.OrderAccessException;
import model.office.lables.Carrier;
import model.office.lables.Storable;
import org.apache.commons.configuration2.sync.ReadWriteSynchronizer;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;

/**
 * Store {@link Cargo Storable}.
 * @author Ilya Ivanov
 */
public class Stock implements OrdersDeliveryArea, OrdersDispatchArea {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Stock.class);

    ReadWriteSynchronizer synchronizer = new ReadWriteSynchronizer();

    /** total capacity */
    private int capacity;

    /** number of currently storing items */
    private int nowStore;

    /** orders map: order -> collection of products */
    private Map<TransportContract, Collection<? extends Storable>> orders;

    /**
     * {@inheritDoc}
     * @see OrdersDeliveryArea
     */
    @Override
    public boolean hasAvailableSpaceFor(TransportContract transportContract) {
        return transportContract.getTotalItems() <= capacity - nowStore;
    }

    /**
     * {@inheritDoc}
     * @see OrdersDeliveryArea
     */
    @Override
    public void putOrder(TransportContract TransportContract, Collection<? extends Storable> products)
            throws NotEnoughSpaceException {
        if (!hasAvailableSpaceFor(TransportContract))
            throw new NotEnoughSpaceException("", this);
        orders.put(TransportContract, products);
        this.notifyAll();
    }

    /**
     * {@inheritDoc}
     * @see OrdersDispatchArea
     */
    @Override
    public Collection<TransportContract> getOrders() {
        return orders.keySet();
    }

    /**
     * {@inheritDoc}
     * @see OrdersDispatchArea
     */
    @Override
    public Collection<? extends Storable> takeOrder(Carrier carrier, TransportContract transportContract)
            throws OrderAccessException {
        if (!transportContract.isTaken() || !transportContract.getPerformer().equals(carrier))
            throw new OrderAccessException("Illegal access attempt " + carrier + " to " + transportContract);
        Collection<? extends Storable> products = orders.get(transportContract);
        orders.remove(transportContract);
        return products;
    }
}
