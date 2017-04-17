package model.server.pdcsystem.producer;

import com.google.common.collect.ImmutableCollection;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import model.server.interfaces.parties.Client;
import model.server.interfaces.parties.Performer;
import model.server.interfaces.production.Producible;
import model.server.pdcsystem.contracts.CommodityContract;
import model.server.pdcsystem.contracts.ProductionContract;
import model.server.interfaces.production.SingleFactory;
import org.jscience.physics.amount.Amount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.measure.quantity.Mass;
import javax.measure.quantity.Volume;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Ilya Ivanov
 */
@RunWith(JUnitParamsRunner.class)
public class ProducerTest {
    private SingleFactory<Producible> factory = mock(SingleFactory.class);
    private Producible producible = mock(Producible.class);

    private ProductionContract<Client, Producer<Producible>, Producible> contract;
    private Client client = mock(Client.class);
    private final Map<String, Integer> map = new HashMap<>();
    private final Amount<Mass> totalWeight = Amount.valueOf(1, Mass.UNIT);
    private final Amount<Volume> totalVolume = Amount.valueOf(2, Volume.UNIT);

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
        map.put(name, num);
        contract = new ProductionContract<>(client, SUT, new CommodityContract.Notations(map), totalWeight, totalVolume);
        // act
        SUT.produce(contract);
    }

    @Test
    @Parameters(method = "getSimpleNotations")
    public void producerShouldProduce(String name, Integer num) throws Exception {
        // arrange
        map.put(name, num);
        when(factory.create(name)).thenReturn(producible);
        contract = new ProductionContract<>(client, SUT, new CommodityContract.Notations(map), totalWeight, totalVolume);

        // act
        final Collection<Producible> produced = SUT.produce(contract);

        // assert
        verify(factory, times(num)).create(name);
        assertThat("Transmitted and returned collections sizes are different", produced.size(), is(num));
        assertThat("Items in returned collection are not instance of transmitted", produced.iterator().next(), instanceOf(Producible.class));
    }

    @Test
    @Parameters(method = "getRepeatedNotations")
    public void producerShouldProduceRepeatedOrders(String[] names, Integer[] nums) throws Exception {
        factory = mock(SingleFactory.class, withSettings().useConstructor().defaultAnswer(CALLS_REAL_METHODS));
        SUT = new Producer<>(factory);
        // arrange
        int totalNum = nums[0];

        final CommodityContract.Notations notations = new CommodityContract.Notations(names[0], nums[0]);
        for (int i = 1; i < names.length; i++) {
            notations.put(names[i], nums[i]);
            totalNum += nums[i];
        }
        when(factory.create(anyString())).thenReturn(producible);

        contract = new ProductionContract<>(client, SUT, notations, totalWeight, totalVolume);
        // act
        final ImmutableCollection<Producible> produced = SUT.produce(contract);
        // assert
        assertThat("Returned collections size doesn't satisfy contract", produced.size(), is(totalNum));
    }
}