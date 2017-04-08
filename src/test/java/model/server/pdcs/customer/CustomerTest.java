package model.server.pdcs.customer;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import model.server.interfaces.parties.Client;
import model.server.interfaces.production.Producible;
import model.server.pdcs.producer.Producer;
import model.server.pdcs.provider.Provider;
import model.server.pdcs.handbook.Handbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class CustomerTest {
    private Customer SUT;

    private Producer<Producible> producer = mock(Producer.class);
    private Provider provider = mock(Provider.class);
    private Handbook<Producible> handbook = mock(Handbook.class);

    @Before
    public void setUp() {
        SUT = new Customer(producer, provider, handbook);
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
        verify(provider).deliver(any(), anyCollection());
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