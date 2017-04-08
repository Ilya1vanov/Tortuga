package model.server.warehouse;

import javafx.util.Pair;
import model.cargo2.Cargo;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.targetareas.OrdersDeliveryArea;
import model.server.interfaces.targetareas.OrdersDispatchArea;
import model.server.interfaces.targetareas.OrdersExchangeArea;
import model.server.pdcs.contracts.TransportContract;
import model.server.remote.exceptions.NotEnoughSpaceException;
import model.server.remote.exceptions.OrderAccessException;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Store {@link Cargo Storable}.
 * @author Ilya Ivanov
 */
public class Warehouse implements OrdersExchangeArea<Cargo>, Serializable {
    /** log4j logger */
    private static final Logger log = Logger.getLogger(Warehouse.class);

    /** total capacity */
    private int capacity;

    /** number of currently storing items */
    private int nowStore;

    /** orders list &lt;order, collection of products&gt; */
    private List<Pair<TransportContract<Client, ? extends Carrier<Cargo>, Cargo>, Collection<? extends Cargo>>> newOrders;

    /**
     * {@inheritDoc}
     * @see OrdersDeliveryArea
     */
    @Override
    public void putOrder(TransportContract<Client, ? extends Carrier<Cargo>, Cargo> TransportContract, Collection<? extends Cargo> products)
            throws NotEnoughSpaceException {
        synchronized(this) {
            newOrders.add(new Pair<>(TransportContract, products));
            this.notifyAll();
        }
    }

    /**
     * {@inheritDoc}
     * @see OrdersDispatchArea
     */
    @Override
    public Pair<TransportContract<Client, Carrier<Cargo>, Cargo>, Collection<? extends Cargo>> takeOrder() throws OrderAccessException {
        synchronized(this) {
            // while (!hasSuitable()) this.wait();
            // return suitable
        }
        return null;
    }
}
