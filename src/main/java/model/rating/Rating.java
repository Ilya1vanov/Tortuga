package model.rating;

//import model.ship.Ship;
import org.apache.log4j.Logger;

/**
 * <p>Class that represents {@code Rating} of the {@link model.ship.Ship Ship}. Rating measures
 * in stars from one to five. Rounded to the nearest half or whole.</p>
 * @author Ilya Ivanov
 */
public class Rating implements Comparable<Rating> {
    /* log4j logger */
    private static final Logger log = Logger.getLogger(Rating.class);

    /* current rating */
    private double currentRate = 4.0;

    /* number of rates */
    private int timesRated = 1;

    /** Constructor provides {@code currentRate} defaults to 4.0 and {@code timesRate} to 1.*/
    public Rating() {}

    /**
     * Get rate and calculate new average.
     * @param rate new rate
     * @throws IllegalArgumentException if rate is null
     */
    public void rate(Stars rate) throws IllegalArgumentException {
        if (rate == null)
            throw new IllegalArgumentException("Rate must not be null: " + "[" + rate + "]");
        currentRate = (currentRate * timesRated + rate.getValue()) / ++timesRated;
    }

    /**
     * Returns rounded current rating.
     * @return {@code currentRate} rounded to the one decimal place.
     */
    public double getCurrentRate() {
        // precision is 1
        return (double)Math.round(currentRate * 10d) / 10d;
    }

    @Override
    public int compareTo(Rating o) {
        if (getCurrentRate() == o.getCurrentRate())
            return 0;
        return getCurrentRate() > o.getCurrentRate() ? 1 : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rating rating = (Rating) o;

        return Double.compare(rating.getCurrentRate(), getCurrentRate()) == 0;

    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(getCurrentRate());
        return (int) (temp ^ (temp >>> 32));
    }

    //    /**
//     * Returns rounded current rating.
//     * @return rounded to the nearest half or whole rating.
//     */
//    public double getCurrentRate() {
//        final long roundedWhole = Math.round(currentRate);
//        final double roundedPlusHalf = roundedWhole + 0.5;
//        final double roundedMinusHalf = roundedWhole - 0.5;
//
//        if (roundedWhole <= currentRate)
//            // first half
//            if (Math.round(currentRate + 0.25) > currentRate)
//                // first quoter
//                return roundedPlusHalf;
//            else
//                // second quoter
//                return roundedWhole;
//        else
//            // second half
//            if (Math.round(currentRate - 0.25) < currentRate)
//                // third quoter
//                return roundedMinusHalf;
//            else
//                // fourth quoter
//                return roundedWhole;
//    }
}
