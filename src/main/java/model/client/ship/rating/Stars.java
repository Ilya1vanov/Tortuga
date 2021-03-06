package model.client.ship.rating;

import java.io.Serializable;

/**
 * Represents rate from one star to five.
 * Created by Илья on 30.03.2017.
 */
public enum Stars implements Serializable {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);

    private int value;

    Stars(int num) {
        this.value = num;
    }

    /** @return the integer equivalent to this enum */
    public int getValue() {
        return value;
    }
}
