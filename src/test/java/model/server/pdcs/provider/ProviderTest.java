package model.server.pdcs.provider;

import junitparams.JUnitParamsRunner;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Producible;
import model.server.interfaces.production.Transportable;
import model.server.interfaces.targetareas.OrdersDeliveryArea;
import model.server.interfaces.targetareas.SupplyingArea;
import model.server.pdcs.contracts.TransportContract;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class ProviderTest {
    private SupplyingArea supplyingArea = mock(SupplyingArea.class);
    private Collection<Producible> collection = mock(Collection.class);
    private TransportContract<Client, Carrier<Transportable>, Transportable> contract = mock(TransportContract.class);
    Provider SUT;

    @Before
    public void setUp() {
        SUT = new Provider(supplyingArea);
    }

    /* data-provider method */
    public Object[] data() {
        return new Object[][] {
                {null, collection},
                {contract, null},
                {null, null}
        };
    }

    @Test
    public void providerShouldImplementCarrier() throws Exception {
        assertThat("Provider should implement Carrier", SUT, is(instanceOf(Carrier.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void providerShouldThrowIAEForNullInput(TransportContract t, Collection<Producible> c) throws Exception {
        SUT.deliver(t, c);
    }

    @Test
    public void providerShouldDeliver() throws Exception {
        // act
        SUT.deliver(contract, collection);
        // assert
        verify(supplyingArea).supply(contract, collection);
    }
}