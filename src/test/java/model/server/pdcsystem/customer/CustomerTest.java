package model.server.pdcsystem.customer;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Producible;
import model.server.interfaces.production.Storable;
import model.server.interfaces.production.Transportable;
import model.server.pdcsystem.producer.Producer;
import model.server.pdcsystem.provider.Provider;
import model.server.pdcsystem.handbook.Handbook;
import org.jscience.physics.amount.Amount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class CustomerTest<PTS extends Producible & Transportable & Storable> {
    private Customer SUT;

    private Producer<PTS> producer = mock(Producer.class);
    private Provider<PTS> provider = mock(Provider.class);
    private Handbook<PTS> handbook = mock(Handbook.class);

    @Before
    public void setUp() {
        SUT = new Customer(producer, provider, handbook);
        when(handbook.getNames()).thenReturn(Arrays.asList("a", "b"));
        when(handbook.measureVolume(any())).thenReturn(Amount.valueOf(1, Volume.UNIT));
        when(handbook.measureWeight(any())).thenReturn(Amount.valueOf(1, Mass.UNIT));

        when(producer.produce(any())).thenReturn(null);
    }

    @Test
    public void providerShouldImplementClient() throws Exception {
        assertThat("Customer should implement Client", SUT, is(instanceOf(Client.class)));
    }

    @Test
    public void customerShouldLookAtTheHandbook() throws Exception {
        // act
        SUT.makeAnOrder();
        // assert
        verify(handbook).getNames();
    }

    @Test
    public void customerShouldAskProducer() throws Exception {
        // act
        SUT.makeAnOrder();
        // assert
        verify(producer).produce(any());
    }

    @Test
    public void customerShouldAskProvider() throws Exception {
        // act
        SUT.makeAnOrder();
        // assert
        verify(provider).deliver(any());
    }

    /* data-provider method */
    public Object[] getNullInput() {
        return new Object[][] {
                {null, provider, handbook},
                {producer, null, handbook},
                {producer, provider, null}
        };
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "getNullInput")
    public void customerShouldThrowIAEForNullInput(Producer producer, Provider provider, Handbook handbook) throws Exception {
        new Customer(producer, provider, handbook);
    }
}