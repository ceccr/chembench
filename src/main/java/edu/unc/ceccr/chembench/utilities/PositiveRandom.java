package edu.unc.ceccr.chembench.utilities;

import java.util.Random;

/**
 * A random number generator that returns only positive integers.
 *
 * This class is necessary in order to utilize Random#next(), which is a protected method.
 * Random#nextInt() is unsatisfactory because it only generates integers in the interval [0, Integer.MAX_VALUE),
 * meaning it will never return Integer.MAX_VALUE as a possible integer.
 */
public class PositiveRandom extends Random {
    public PositiveRandom() {
        super();
    }

    public PositiveRandom(int seed) {
        super(seed);
    }

    /**
     * Returns a random, positive integer.
     *
     * @return a random integer between 0 and Integer.MAX_VALUE, inclusive
     */
    public int nextPositiveInt() {
        return next(Integer.SIZE - 1);
    }
}
