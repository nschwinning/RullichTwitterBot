package de.rullich.twitter.tweets;

import de.rullich.twitter.TrendProvider;
import twitter4j.TwitterException;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Logger;

public class NightTweet implements TimedTweet {

    private Logger logger = Logger.getLogger(NightTweet.class.getName());

    private static final String[] TWEETS = new String[]{
            "Noch einer im Bier?",
            "Hat noch jemand Lust auf ein Stauder?",
            "Jetzt noch ein leckeres Pilsken inner Gaststätte?",
            "Abschlussstauder jetzt!",
            "Wer könnte jetzt Nein zu einem leckeren Stauder sagen?",
            "4:23, Zeit für ein Stauder!",
            "Könnt ihr auch nicht schlafen? Dann lasst uns noch ein Bierchen trinken :)",
            "4:23 Uhr und alle reden nur noch Stauderwelsch!"
    };

    private static final int HOUR = 4;
    private static final int MINUTE = 23;

    private final TrendProvider trendProvider;

    private final Random random = new Random();

    public NightTweet(final TrendProvider trendProvider) {
        this.trendProvider = trendProvider;
    }

    @Override
    public boolean isApplicable(final LocalDateTime dateTime) {
        return dateTime.getHour() == HOUR && dateTime.getMinute() == MINUTE;
    }

    public String getTweet() {
        String trend = "";

        try {
            trend = " " + trendProvider.provideTrend();
        } catch (TwitterException e) {
            logger.warning("unable to request trend: " + e.getMessage());
        }

        return TWEETS[random.nextInt(TWEETS.length)] + trend;
    }
}
