package de.rullich.twitter;

import twitter4j.TwitterException;

public interface TrendProvider {

    /**
     * Reads and returns a random trending keyword for Essen
     *
     * @return a random trend
     * @throws TwitterException when Twitter service or network is unavailable
     */
    String provideTrend() throws TwitterException;
}
