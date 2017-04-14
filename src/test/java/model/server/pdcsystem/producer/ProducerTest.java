package model.server.pdcsystem.producer;

import javafx.util.Pair;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import model.server.interfaces.parties.Client;
import model.server.interfaces.parties.Performer;
import model.server.interfaces.production.Producible;
import model.server.pdcsystem.contracts.ProductionOrder;
import model.server.interfaces.production.SingleFactory;
import org.jscience.physics.amount.Amount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class ProducerTest {
    private Client client = mock(Client.class);
    private Producible producible = mock(Producible.class);
    private SingleFactory factory = mock(SingleFactory.class);
    private final Collection<Pair<String, Integer>> notations = new ArrayList<>();
    private ProductionOrder<Client, Producer<Producible>, Producible> order;
    private Producer<Producible> SUT;

    @Before
    public void setUp() {
        SUT = new Producer<>(factory);
    }

    /* data-provider method */
    public static Object[] getInvalidNotations() {
        return new Object[][] {
                {"prod", 0},
                {"prod", -4},
                {"", 5},
                {null, 3},
                {"df", null}
        };
    }

    /* data-provider method */
    public static Object[] getSimpleNotations() {
        return new Object[][] {
                {"prod", 15},
                {"gold", 2},
                {"silver", 1000}
        };
    }

    /* data-provider method */
    public static Object[] getRepeatedNotations() {
        return new Object[][] {
                {new String[]{"prod", "prod", "prod"}, new Integer[] {2, 2, 4}}
        };
    }

    @Test
    public void providerShouldImplementCarrier() throws Exception {
        assertThat("Producer should implement Performer", SUT, is(instanceOf(Performer.class)));
    }

    @Test(expected = NullPointerException.class)
    public void producerShouldThrowNPEForNullInput() throws Exception {
        SUT.produce(null);
    }

    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "getInvalidNotations")
    public void producerShouldThrowIAEForInvalidInput(String name, Integer num) throws Exception {
        // arrange
        notations.add(new Pair<>(name, num));
        order = new ProductionOrder<>(client, SUT, notations, Amount.valueOf(1, Mass.UNIT), Amount.valueOf(2, Volume.UNIT));
        // act
        SUT.produce(order);
    }

    @Test
    @Parameters(method = "getSimpleNotations")
    public void producerShouldProduce(String name, Integer num) throws Exception {
        // arrange
        notations.add(new Pair<>(name, num));
        when(factory.create(name)).thenReturn(producible);
        order = new ProductionOrder<>(client, SUT, notations, Amount.valueOf(1, Mass.UNIT), Amount.valueOf(2, Volume.UNIT));

        // act
        final Collection<Producible> produced = SUT.produce(order);

        // assert
        verify(factory).create(name);
        assertThat("Transmitted and returned collections sizes are different", produced.size(), is(num));
        assertThat("Items in returned collection are not instance of transmitted", produced.iterator().next(), instanceOf(Producible.class));
    }

    @Test
    @Parameters(method = "getRepeatedNotations")
    public void producerShouldProduceRepeatedOrders(String[] names, Integer[] nums) throws Exception {
        factory = mock(SingleFactory.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
        SUT = new Producer<>(factory);
        // arrange
        int totalNum = 0;
        for (int i = 0; i < names.length; i++) {
            notations.add(new Pair<>(names[i], nums[i]));
            totalNum += nums[i];
        }

        order = new ProductionOrder<>(client, SUT, notations, Amount.valueOf(1, Mass.UNIT), Amount.valueOf(2, Volume.UNIT));
        // act
        final Collection<Producible> produced = SUT.produce(order);
        // assert
        assertThat("Returned collections size doesn't satisfy order", produced.size(), is(totalNum));
    }
}