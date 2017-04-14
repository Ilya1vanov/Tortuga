package model.server.interfaces.parties;

import model.server.interfaces.production.Transportable;
import model.server.pdcsystem.order.Order;
import org.jscience.physics.amount.Amount;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.rmi.RemoteException;

/**
 * Class that is able to carry {@link Transportable}.
 * @param <T> transportable item(s)
 * @author Ilya Ivanov
 */
public interface Carrier<T extends Transportable> extends Performer {
    /** @return volume limit for transport */
    default Amount<Volume> getVolume() throws RemoteException {
        throw new NotImplementedException();
    }

    /** @return deadweight for transport */
    default Amount<Mass> getCarrying() throws RemoteException {
        throw new NotImplementedException();
    }

    /** @return order, that is currently transporting by this carrier; null if no orders is carrying */
    default Order getOrder() throws RemoteException {
        throw new NotImplementedException();
    }
}
