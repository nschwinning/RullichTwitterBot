package de.rullich.twitter;

import org.junit.Test;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;

import java.lang.reflect.Field;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TwitterBotTest {

    static class TrendMock implements Trend {

        private final String name;

        public TrendMock(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getURL() {
            return null;
        }

        @Override
        public String getQuery() {
            return null;
        }
    }

    @Test
    public void testProvideTrendNormal() throws Exception {
        final Trend[] transmittedTrends = new Trend[]{
                new TrendMock("test"),
                new TrendMock("tost"),
                new TrendMock("tust"),
                new TrendMock("#nice")
        };

        final TwitterBot bot = setupTwitterBot(transmittedTrends);

        final String expectedTrend = "#nice";
        final String actualTrend = bot.provideTrend();

        assertEquals(expectedTrend, actualTrend);
    }

    @Test
    public void testProvideTrendNoHashtagTrend() throws Exception {
        final Trend[] transmittedTrends = new Trend[]{
                new TrendMock("test"),
                new TrendMock("tost"),
                new TrendMock("tust"),
                new TrendMock("nice")
        };

        final TwitterBot bot = setupTwitterBot(transmittedTrends);

        final String expectedTrend = "";
        final String actualTrend = bot.provideTrend();

        assertEquals(expectedTrend, actualTrend);
    }

    @Test
    public void testProvideTrendNoTrends() throws Exception {
        final Trend[] transmittedTrends = new Trend[]{};

        final TwitterBot bot = setupTwitterBot(transmittedTrends);

        final String expectedTrend = "";
        final String actualTrend = bot.provideTrend();

        assertEquals(expectedTrend, actualTrend);
    }

    private TwitterBot setupTwitterBot(final Trend[] transmittedTrends) throws Exception {
        final TwitterBot bot = TwitterBot.newInstance();

        Twitter twitter = mock(Twitter.class);
        Trends trends = mock(Trends.class);
        when(twitter.getPlaceTrends(TwitterBot.WOEID_ESSEN)).thenReturn(trends);
        when(trends.getTrends()).thenReturn(transmittedTrends);

        final Field twitterField = TwitterBot.class.getDeclaredField("twitter");

        twitterField.setAccessible(true);
        twitterField.set(bot, twitter);
        twitterField.setAccessible(false);

        return bot;
    }

}
