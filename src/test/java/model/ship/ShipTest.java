package model.ship;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Ship class test.
 * Created by Илья on 30.03.2017.
 */
public class ShipTest {
    private Ship sut;

    @Before
    public void setUp() {
//        sut = new Ship();
    }

    @Test
    public void shipShouldBeComparable() {
        assertThat("rating should implement Comparable interface", sut, instanceOf(Comparable.class));
    }

    @Test
    public void shipShouldBeRunnable() {
        assertThat("rating should implement Runnable interface", sut, instanceOf(Runnable.class));
    }

    @Test
    public void run() throws Exception {

    }

    @Test
    public void getRating() throws Exception {

    }

    @Test
    public void compareTo() throws Exception {

    }
}