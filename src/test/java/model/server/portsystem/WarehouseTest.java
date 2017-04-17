package model.server.portsystem;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import model.cargo2.Cargo;
import model.client.interfaces.MaritimeCarrier;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.pdcsystem.order.Order;
import org.jscience.physics.amount.Amount;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import java.rmi.RemoteException;
import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class WarehouseTest {
    Order<Cargo> order = mock(Order.class);
    MaritimeCarrier carrier = mock(MaritimeCarrier.class);
    TransportContract contract = mock(TransportContract.class);
    Pier pier = mock(Pier.class);
    Warehouse SUT = new Warehouse(50, pier);

    @Before
    public void setUp() throws RemoteException {
        when(pier.getId()).thenReturn(1);
        when(pier.getMaritimeCarrier()).thenReturn(carrier);

        when(carrier.getName()).thenReturn("Nameee");
        when(carrier.getCarrying()).thenReturn(Amount.valueOf(100, NonSI.TON_US));
        when(carrier.getVolume()).thenReturn(Amount.valueOf(1000, SI.CUBIC_METRE));

        when(order.getContract()).thenReturn(contract);

        when(contract.getTotalItems()).thenReturn(1);
        when(contract.getTotalWeight()).thenReturn(Amount.valueOf(5, SI.KILOGRAM));
        when(contract.getTotalVolume()).thenReturn(Amount.valueOf(5, SI.CUBIC_METRE));
    }

    @Test
    public void shouldAcceptOrders() throws Exception {
        SUT.putOrder(order);
        assertTrue(SUT.isCollectingRequired());
    }

    @Test
    public void shouldBetrayOrders() throws Exception {
        SUT.supply(order);
        final Order<Cargo> cargoOrder = SUT.takeOrder();
        org.junit.Assert.assertThat("", cargoOrder, is(order));
        assertTrue(SUT.isSupplyingRequired());
    }

    @Test
    public void shouldReturnEmptyListWhenEmpty() throws Exception {
        final Collection<Order<Cargo>> collection = SUT.collect();
        assertThat(collection).isNotNull().isEmpty();
    }

    @Test
    public void shouldRefuseCollectingWhenEmpty() throws Exception {
        assertFalse(SUT.isCollectingRequired());
    }

    @Test
    public void shouldAcceptSupplyingWhenEmpty() throws Exception {
        // no exception thrown
        SUT.supply(order);
    }

    @Test
    public void shouldRequireSupplyingWhenEmpty() throws Exception {
        assertTrue(SUT.isSupplyingRequired());
    }

}