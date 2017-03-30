package model.rating;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static model.rating.Stars.*;

/**
 * Rating JUnit test.
 * Created by Илья on 30.03.2017.
 */
@RunWith(JUnitParamsRunner.class)
public class RatingTest {
    /* data-provider method */
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

    /* data-provider method */
    private static Object[] getAllThreeStarsCombinations() {
        return new Object[][] {
                // x < y < z
                {THREE, FOUR, FIVE},
                // x < y > z
                {THREE, FIVE, FOUR},
                {THREE, FIVE, TWO},
                // x > y < z
                {FIVE, THREE, FOUR},
                {FOUR, THREE, FIVE},
                // x > y > z
                {FIVE, FOUR, THREE},
                // two equals
                // and third more
                {FOUR, FOUR, FIVE},
                {FOUR, FIVE, FOUR},
                {FIVE, FOUR, FOUR},
                // and third less
                {FOUR,  FOUR,  THREE},
                {FOUR,  THREE, FOUR},
                {THREE, FOUR,  FOUR},
                // three equals
                {FIVE, FIVE, FIVE}
        };
    }

    private static final double DEFAULT_RANK = 4.0;

    private Rating sut;

    @Before
    public void setUp() {
        sut = new Rating();
    }

    @Test
    public void ratingShouldBeComparable() {
        assertThat("sut should implement Comparable interface", sut, instanceOf(Comparable.class));
    }

    @Test
    public void defaultConstructorTestShouldSetInitialRateTo4() {
        assertEquals(
                "currentRank is defaults to " + DEFAULT_RANK + " in constructor, but getter returned " + sut.getCurrentRank(),
                DEFAULT_RANK,
                sut.getCurrentRank(),
                0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rateShouldThrowIAEWhenRateIsNull() {
        sut.rate(null);
    }

    @Test
    @Parameters(method = "getValidRateChainAndExpectedValue")
    public void rateAfterRateChain(Stars[] rates, double expected) {
        for (Stars rate : rates) {
            sut.rate(rate);
        }
        assertEquals("rate method calculates average in the wrong way", expected, sut.getCurrentRank(), 0);
    }

    @Test
    @Parameters(method = "getAllThreeStarsCombinations")
    public void compareToImplementationTest(Stars X, Stars Y, Stars Z) {
        //
        Rating x = sut;
        Rating y = new Rating();
        Rating z = new Rating();

        // act
        x.rate(X);
        y.rate(Y);
        z.rate(Z);

        // assert
        // compareTo should be transitive
        if (x.compareTo(y) > 0 && y.compareTo(z) > 0)
            assertThat("compareTo is not transitive", true, is(x.compareTo(z) > 0));
        // sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
        assertThat("compareTo has reverse sign mismatch", Math.signum(x.compareTo(y)), is(Math.signum(y.compareTo(x) * -1)));
        // x.compareTo(y)==0 implies that sgn(x.compareTo(z)) == sgn(y.compareTo(z))
        if (x.compareTo(y) == 0)
            assertThat("compareTo has sign mismatch" , x.compareTo(z), is(y.compareTo(z)));
        // x.compareTo(y)==0) == (x.equals(y))
        assertThat("compareTo and equals works different", x.compareTo(y) == 0, is(x.equals(y)));
    }
}