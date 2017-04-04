package de.rullich.twitter.tweets;

import de.rullich.twitter.TrendProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class NightTweetTest {

    NightTweet tweet;

    TrendProvider trendProvider;

    @Before
    public void initTest() {
        trendProvider = Mockito.mock(TrendProvider.class);
        tweet = new NightTweet(trendProvider);
    }

    @Test
    public void testIsApplicableTrueBegin() {
        final LocalDateTime dateTime = LocalDateTime.of(2017, 4, 4, 4, 23, 0);

        final boolean actual = tweet.isApplicable(dateTime);

        assertEquals(true, actual);
    }

    @Test
    public void testIsApplicableTrueEnd() {
        final LocalDateTime dateTime = LocalDateTime.of(2017, 4, 4, 4, 23, 59);

        final boolean actual = tweet.isApplicable(dateTime);

        assertEquals(true, actual);
    }

    @Test
    public void testIsApplicableFalseBegin() {
        final LocalDateTime dateTime = LocalDateTime.of(2017, 4, 4, 4, 24, 0);

        final boolean actual = tweet.isApplicable(dateTime);

        assertEquals(false, actual);
    }

    @Test
    public void testIsApplicableFalseEnd() {
        final LocalDateTime dateTime = LocalDateTime.of(2017, 4, 4, 4, 22, 59);

        final boolean actual = tweet.isApplicable(dateTime);

        assertEquals(false, actual);
    }

    @Test
    public void testGetTweet() throws Exception {
        // verifies that TrendProvider.provideTrend() is called to append a trend

        tweet.getTweet();
        verify(trendProvider).provideTrend();
    }
}
