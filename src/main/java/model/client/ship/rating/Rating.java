package model.client.ship.rating;

//import model.client.ship.Ship;
import com.google.gson.annotations.Expose;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * <p>Class that represents {@code rating} of the {@link model.client.ship.Ship Ship}. rating measures
 * in {@link Stars} from one to five. Rounded to the one decimal place.</p>
 * <p>Class implements {@code Comparable} interface, so comparison occur by the actual, not rounded rate
 * (i.g., {@code rate1.currentRank == 4.567} and {@code rate2.currentRank == 4.59}, so
 * {@code rate1.getCurrentRank() == rate2.getCurrentRank()} is true, but {@code rate1.equals(rate2)} is false and
 * {@code rate1.compareTo(rate2) < 0} is true.</p>
 * @author Ilya Ivanov
 */
public class Rating implements Comparable<Rating>, Serializable {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(Rating.class);

    /** current rating */
    private double currentRank = 4.0;

    /** number of rates */
    private int timesRated = 1;

    /** Constructor provides {@code currentRank} defaults to 4.0 and {@code timesRate} to 1.*/
    public Rating() {}

    /**
     * Get rank and calculate new average.
     * @param rank new assessment; null values are ignored
     */
    public void rate(Stars rank) {
        if (rank == null)
            return;
        currentRank = (currentRank * timesRated + rank.getValue()) / ++timesRated;
    }

    /**
     * Returns rounded current rating.
     * @return {@code currentRank} rounded to the one decimal place.
     */
    double getCurrentRank() {
        // precision is 1
        return roundWithPrecision(currentRank, 1);
    }

    /**
     * Rounds given number to the {@code decimalPlaces}.
     * @param number number to round
     * @param decimalPlaces number of decimal places
     * @return rounded number
     */
    private double roundWithPrecision(double number, int decimalPlaces) {
        int precision = (int) Math.pow(10, decimalPlaces);
        return (double) Math.round(number * precision) / precision;
    }

    @Override
    public int compareTo(Rating o) {
        if (currentRank == o.currentRank)
            return 0;
        return currentRank > o.currentRank ? 1 : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rating rating = (Rating) o;

        return Double.compare(rating.currentRank, currentRank) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(currentRank);
        return (int) (temp ^ (temp >>> 32));
    }
}
