package de.rullich.twitter.tweets;

import java.time.LocalDateTime;

/**
 * A tweet that is timed (may or may not be recurring)
 */
public interface TimedTweet {

    /**
     * Returns whether <code>dateTime</code> is the right time for this tweet to be tweeted
     *
     * @param dateTime
     * @return <code>true</code> if <code>getTweet()</code> should be used to update the status, <code>false</code>
     * otherwise
     */
    boolean isApplicable(LocalDateTime dateTime);

    /**
     * Constructs and returns the tweet
     *
     * @return tweet to be used to update status
     */
    String getTweet();
}
