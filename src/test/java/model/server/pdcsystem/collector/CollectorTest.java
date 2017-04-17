package model.server.pdcsystem.collector;

import junitparams.JUnitParamsRunner;
import model.server.interfaces.parties.Carrier;
import model.server.interfaces.targetareas.CollectingArea;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class CollectorTest {
    Collector SUT;

    CollectingArea collectingArea = mock(CollectingArea.class);

    @Before
    public void setUp() {
        SUT = new Collector(collectingArea);
    }

    @Test
    public void collectorShouldImplementCarrier() throws Exception {
        assertThat("Collector should implement Carrier", SUT, is(instanceOf(Carrier.class)));
    }

    @Test
    public void collectorShouldCollect() throws Exception {
        when(collectingArea.collect()).thenReturn(Arrays.asList("a", "b"));

        SUT.collect();

        verify(collectingArea).collect();
    }
}