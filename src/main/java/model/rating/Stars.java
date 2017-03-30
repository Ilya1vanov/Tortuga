package model.rating;

/**
 * Represents rate from one star to five.
 * Created by Илья on 30.03.2017.
 */
public enum Stars {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);

    private int value;

    Stars(int num) {
        this.value = num;
    }

    public int getValue() {
        return value;
    }
}
