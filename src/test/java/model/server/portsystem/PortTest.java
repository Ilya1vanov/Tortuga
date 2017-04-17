package model.server.portsystem;

import model.cargo2.Cargo;
import model.client.interfaces.MaritimeCarrier;
import model.server.interfaces.remote.DepartService;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.pdcsystem.order.Order;
import org.jscience.physics.amount.Amount;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Before;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Ilya Ivanov
 */
//@RunWith(JUnitParamsRunner.class)
public class PortTest  {
    MaritimeCarrier carrier = mock(MaritimeCarrier.class);
    TransportContract contract = mock(TransportContract.class);
    Order<Cargo> order = mock(Order.class);

    private DepartService<Cargo> departService = mock(DepartService.class);

    Warehouse warehouse1 = mock(Warehouse.class);
    Warehouse warehouse2 = mock(Warehouse.class);

    Pier pier1 = mock(Pier.class);
    Pier pier2 = mock(Pier.class);
    Port SUT;

    @Rule public RetryTestRule retryTestRule = new RetryTestRule();
//    @Rule public ConcurrentRule concurrently = new ConcurrentRule();
//    @Rule public RepeatingRule repeatedly = new RepeatingRule();

    @Before
    public void setUp() throws RemoteException {
        when(pier1.getWarehouse()).thenReturn(warehouse1);
        when(pier1.getId()).thenReturn(0);
        when(pier1.isFree()).thenCallRealMethod();
        when(pier1.moor(eq(carrier), anyLong(), any())).thenCallRealMethod();
        when(pier1.getMaritimeCarrier()).thenCallRealMethod();

//        when(warehouse2.isSupplyingRequired()).thenCallRealMethod();
//        when(warehouse2.isCollectingRequired()).thenCallRealMethod();
        when(pier2.getWarehouse()).thenReturn(warehouse1);
        when(pier2.getId()).thenReturn(1);
        when(pier2.isFree()).thenCallRealMethod();
        when(pier2.moor(eq(carrier), anyLong(), any())).thenCallRealMethod();
        when(pier2.getMaritimeCarrier()).thenCallRealMethod();


        when(carrier.getName()).thenReturn("Nameee");
        when(carrier.getCarrying()).thenReturn(Amount.valueOf(100, NonSI.TON_US));
        when(carrier.getVolume()).thenReturn(Amount.valueOf(1000, SI.CUBIC_METRE));
        when(carrier.getOrder()).thenReturn(order);

        when(order.getContract()).thenReturn(contract);

        when(contract.getTotalItems()).thenReturn(1);
        when(contract.getTotalWeight()).thenReturn(Amount.valueOf(5, SI.KILOGRAM));
        when(contract.getTotalVolume()).thenReturn(Amount.valueOf(5, SI.CUBIC_METRE));

        try {
            SUT = new Port("Nname", Arrays.asList(pier1, pier2));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = RuntimeException.class)
    public void doubleMooringShouldThrowRTException() throws Exception {
        SUT.moor(carrier ,5, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(1);
        org.junit.Assert.assertThat(pier1.isFree() && pier2.isFree(), is(false));
    }


    @Test
    public void isCollectingRequired() throws Exception {
        assertFalse(SUT.isCollectingRequired());
    }

    @Test
    public void isSupplyingRequired() throws Exception {
        when(warehouse1.isSupplyingRequired()).thenReturn(true);
        assertTrue(SUT.isSupplyingRequired());
        verify(warehouse1, times(2)).isSupplyingRequired();
    }

    @Test
    public void collect() throws Exception {
        final Collection<Order<Cargo>> collect = SUT.collect();
        assertThat(collect).isEmpty();

        verify(warehouse1, times(2)).collect();
    }

    @Test
    public void supply() throws Exception {
        when(pier2.getWarehouse()).thenReturn(warehouse2);
        when(warehouse1.isSupplyingRequired()).thenReturn(true);
        SUT.supply(order);

        verify(warehouse1).supply(order);
    }
}