package model.client.ship;

import model.cargo2.Cargo;
import model.client.Client;
import model.client.interfaces.Assessable;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Performer;
import model.server.interfaces.remote.ArrivalService;
import model.server.interfaces.remote.DepartService;
import model.server.interfaces.targetareas.OrdersExchangeArea;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.pdcsystem.order.Order;
import org.jscience.physics.amount.Amount;
import org.junit.Before;
import org.junit.Test;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.Volume;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import java.rmi.ConnectException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Ship class test.
 * Created by Илья on 30.03.2017.
 */
public class ShipTest {
    private final Client client = mock(Client.class);
    private final ArrivalService<Cargo> arrivalService = mock(ArrivalService.class);
    private final DepartService<Cargo> departService = mock(DepartService.class);
    private final OrdersExchangeArea<Cargo> area = mock(OrdersExchangeArea.class);

    private final Order<Cargo> order = mock(Order.class);
    private final TransportContract contract = mock(TransportContract.class);

    private final Amount<Volume> displacement = Amount.valueOf(100, SI.CUBIC_METRE);
    private final Amount<Velocity> velocity = Amount.valueOf(10, SI.METERS_PER_SECOND);
    private final Amount<Volume> volume = Amount.valueOf(1000, SI.CUBIC_METRE);
    private final Amount<Mass> carrying = Amount.valueOf(500, NonSI.TON_US);

    private Ship SUT;

    @Before
    public void setUp() throws RemoteException {
        when(arrivalService.moor(eq(SUT), anyLong(), any())).thenReturn(departService).thenThrow(ConnectException.class);
        when(departService.getOrdersExchangeArea()).thenReturn(area).thenThrow(ConnectException.class);
        when(area.takeOrder()).thenReturn(order);
        when(order.getContract()).thenReturn(contract);


        SUT = new Ship(client, arrivalService,"Nname", displacement, velocity, volume, carrying);
    }

    @Test
    public void shipShouldBeAssessable() {
        assertThat("rating should implement Assessable interface", SUT, instanceOf(Assessable.class));
    }

    @Test
    public void shipShouldBeRunnable() {
        assertThat("rating should implement Runnable interface", SUT, instanceOf(Runnable.class));
    }

    @Test
    public void shipShouldBeCarrier() {
        assertThat("rating should implement Carrier interface", SUT, instanceOf(Carrier.class));
    }

    @Test
    public void shipShouldBeRemote() {
        assertThat("rating should implement , Remote interface", SUT, instanceOf(Remote.class));
    }

    @Test
    public void shouldMoorAndUnmoor() throws Exception {
        when(arrivalService.moor(eq(SUT), anyLong(), any())).thenReturn(departService).thenThrow(ConnectException.class);
        SUT.run();
        verify(arrivalService, times(2)).moor(eq(SUT), anyLong(), any());
        verify(departService).unmoor();
    }

    @Test
    public void shouldShutdownOnConnectionException() throws Exception {
        when(arrivalService.moor(eq(SUT), anyLong(), any())).thenThrow(ConnectException.class);
        SUT.run();
        verify(client).shutdown();
    }

    @Test
    public void shouldTakeOrder() throws Exception {
        when(arrivalService.moor(eq(SUT), anyLong(), any())).thenReturn(departService).thenThrow(ConnectException.class);
        SUT.run();
        verify(area).takeOrder();
    }
}