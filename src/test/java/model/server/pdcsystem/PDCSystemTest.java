package model.server.pdcsystem;

import junitparams.JUnitParamsRunner;
import model.server.exceptions.CapacityViolationException;
import model.server.interfaces.production.Producible;
import model.server.interfaces.targetareas.CollectingArea;
import model.server.interfaces.targetareas.SupplyingArea;
import model.server.interfaces.production.SingleFactory;
import model.server.pdcsystem.handbook.Handbook;
import org.jscience.physics.amount.Amount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.util.Arrays;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class PDCSystemTest {
    CollectingArea collectingArea = mock(CollectingArea.class);
    SupplyingArea supplyingArea = mock(SupplyingArea.class);
    SingleFactory singleFactory = mock(SingleFactory.class);
    Handbook handbook = mock(Handbook.class);
    PDCSystem SUT = new PDCSystem(supplyingArea, collectingArea, singleFactory, handbook);

    @Before
    public void setUp() {
        try {
            when(collectingArea.collect()).thenReturn(Arrays.asList("a", "b"));
            when(collectingArea.isCollectingRequired()).thenReturn(true);
            doNothing().when(supplyingArea).supply(any());
            when(supplyingArea.isSupplyingRequired()).thenReturn(true);
            when(singleFactory.create(anyString())).thenReturn(null);

            when(handbook.getNames()).thenReturn(Arrays.asList("a", "b"));
            when(handbook.measureVolume(any())).thenReturn(Amount.valueOf(1, Volume.UNIT));
            when(handbook.measureWeight(any())).thenReturn(Amount.valueOf(1, Mass.UNIT));
        } catch (CapacityViolationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldAskCollectibleArea() throws Exception {
        SUT.run();
        verify(collectingArea).isCollectingRequired();
    }



}