package model.server.portsystem;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import model.client.interfaces.MaritimeCarrier;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import javax.validation.constraints.AssertTrue;

import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class PierTest {
    MaritimeCarrier carrier = mock(MaritimeCarrier.class);
    Port port = mock(Port.class);
    Pier SUT = new Pier(port, 10);

    @Before
    public void setUp() throws RemoteException {
        when(carrier.getName()).thenReturn("Nameee");

    }

    @Test
    public void shouldBeFreeWhenCreated() throws Exception {
        assertTrue(SUT.isFree());
    }

    @Test
    public void shouldAcceptCarriers() throws Exception {
        SUT.moor(carrier, 1, TimeUnit.SECONDS);
        assertFalse(SUT.isFree());
    }

    @Test
    public void shouldRate() throws Exception {
        SUT.moor(carrier, 1, TimeUnit.SECONDS);
        SUT.unmoor();

        verify(carrier).rate(any());
        verify(carrier, never()).log(anyString(), anyString());
    }

    @Test
    public void shouldLogAndRateOnTimeExceeding() throws Exception {
        SUT.moor(carrier, 1, TimeUnit.MILLISECONDS);
        TimeUnit.MILLISECONDS.sleep(3);
        SUT.unmoor();

        verify(carrier).rate(any());
        verify(carrier).log(anyString(), anyString());
    }
}