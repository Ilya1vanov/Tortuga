package model.server.pdcsystem.provider;

import com.google.common.collect.ImmutableList;
import junitparams.JUnitParamsRunner;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Producible;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.targetareas.SupplyingArea;
import model.server.pdcsystem.contracts.TransportContract;
import model.server.pdcsystem.order.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class ProviderTest {
    private final SupplyingArea supplyingArea = mock(SupplyingArea.class);
    private final TransportContract<Client, Carrier<Transportable>, Transportable> contract = mock(TransportContract.class);
    private final Collection<Producible> collection = new ArrayList<>();
    private final Producible producible = mock(Producible.class);
    Provider SUT;

    @Before
    public void setUp() {
        SUT = new Provider(supplyingArea);
        collection.add(producible);
    }

    /* data-provider method */
    public Object[] data() {
        return new Object[][] {
                {null, collection},
                {contract, null}
//                {null, null}
        };
    }

    @Test
    public void providerShouldImplementCarrier() throws Exception {
        assertThat("Provider should implement Carrier", SUT, is(instanceOf(Carrier.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void providerShouldThrowIAEForNullInput(TransportContract t, Collection<Producible> c) throws Exception {
        SUT.deliver(any());
    }

    @Test
    public void providerShouldDeliver() throws Exception {
        // act
        final Order order = new Order(contract, ImmutableList.copyOf(collection));
        SUT.deliver(order);
        // assert
        verify(supplyingArea).supply(order);
    }
}