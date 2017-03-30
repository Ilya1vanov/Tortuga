package model.rating;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static model.rating.Stars.*;

/**
 * Rating JUnit test.
 * Created by Илья on 30.03.2017.
 */
@RunWith(JUnitParamsRunner.class)
public class RatingTest {
    /* data-providing */
    private static Object[] getValidRateChainAndExpectedValue() {
        return new Object[][]{
                {new Stars[]{TWO, THREE}, 3.}, // 3.0
                {new Stars[]{TWO, THREE, FOUR, THREE}, 3.2}, // 3.2
                {new Stars[]{THREE, FIVE, ONE}, 3.3}, // 3.25
                {new Stars[]{THREE, FOUR, THREE}, 3.5}, // 3.5
                {new Stars[]{TWO, FIVE}, 3.7}, // 3.66
                {new Stars[]{ONE, FIVE, FIVE}, 3.8}, // 3.75
                {new Stars[]{TWO, FIVE, FIVE, THREE}, 3.8} // 3.8
        };
    }

    private Rating rating;

    @Before
    public void setUp() {
        rating = new Rating();
    }

    @Test
    public void defaultConstructorTestShouldSetInitialRateTo4() {
        assertEquals(4.0, rating.getCurrentRate(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rateShouldThrowIAEForInvalidRate(int rate) {
        rating.rate(null);
    }

    @Test
    @Parameters(method = "getValidRateChainAndExpectedValue")
    public void rateAfterRateChain(Stars[] rates, double expected) {
        for (Stars rate : rates) {
            rating.rate(rate);
        }
        assertEquals(expected, rating.getCurrentRate(), 0);
    }

    @Test
    public void compareTo() {
        // x.compareTo(y)) == -sgn(y.compareTo(x)
        // x.compareTo(y)>0 && y.compareTo(z)>0) implies x.compareTo(z)>0
        // x.compareTo(y)==0 implies that sgn(x.compareTo(z)) == sgn(y.compareTo(z)
        // x.compareTo(y)==0) == (x.equals(y)
        Rating cmpTo = new Rating();
    }
}