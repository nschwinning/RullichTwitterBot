package de.rullich.twitter;

import de.rullich.twitter.rules.DerWestenRule;
import de.rullich.twitter.rules.RuleApplication;
import de.rullich.twitter.rules.RuleEngine;
import de.rullich.twitter.rules.SayingsRule;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

/**
 * The real bot. Runs in a thread, wakes up from time to time and updates the twitter status (or not).
 */
public class TwitterBot implements Runnable {

    private static final Logger logger = Logger.getLogger(TwitterBot.class.getName());

    // the amount of time the bot will sleep
    private static final int ONE_MINUTE = 60 * 1000; // 60 seconds times 1.000 milliseconds

    // twitter Where On Earth ID for Essen, Germany
    public static final int WOEID_ESSEN = 648820;

    // keys for config.properties
    public static final String API_KEY_PROPERTY = "API_KEY";
    public static final String API_SECRET_PROPERTY = "API_SECRET";
    public static final String TOKEN_PROPERTY = "TOKEN";
    public static final String TOKEN_SECRET_PROPERTY = "TOKEN_SECRET";

    // security values needed for connecting to the twitter account (defined in config.properties)
    private String apiKey;
    private String apiSecret;
    private String token;
    private String tokenSecret;

    // connection to twitter
    private Twitter twitter;

    // used in run() loop to determine whether to continue or not
    private boolean running;

    private JerseyClient jc;

    // managing of rules and their application
    private final RuleEngine ruleEngine = new RuleEngine();

    private final Random random = new Random();

    @Override
    public void run() {
        try {
            readConfigFile();

            if (initConnection()) {
                running = true;
            }

            ruleEngine.registerRule(new SayingsRule());
            ruleEngine.registerRule(new DerWestenRule());

            jc = new JerseyClient();
        } catch (IOException e) {
            logger.severe("error while starting TwitterBot: " + e.getMessage());
        }

        while (running) {
            final LocalDateTime now = LocalDateTime.now();
            int hour = now.getHour();
            int minute = now.getMinute();
            if (minute == 23 && hour == 4) {
                try {
                    final String trend = getRandomTrend();
                    twitter.updateStatus(NightTweet.getTweet() + " " + trend);
                } catch (TwitterException e) {
                    logger.warning("unable to update status: " + e.getMessage());
                }
            } else {
                if (fireTweet()) {
                    final Optional<RuleApplication> optionalRuleApplication = ruleEngine.fireNextRule();

                    if (optionalRuleApplication.isPresent()) {
                        // could successfully apply a rule
                        final String tweet = optionalRuleApplication.get().getTweet();

                        try {
                            twitter.updateStatus(tweet);
                            logger.info("updated status: " + tweet);
                        } catch (TwitterException e) {
                            logger.warning("unable to update status. reason: " + e.getMessage());
                        }
                    } else {
                        // no rule could be applied
                        logger.warning("tried to apply a rule but no rule was applicable right now");
                    }
                }
            }
            try {
                Thread.sleep(ONE_MINUTE);
            } catch (InterruptedException e) {
                logger.warning("whoops! My thread got interrupted!");
            }
        }
    }

    /**
     * Initialises the connection to twitter. Returns if a connection could be established
     *
     * @return <code>true</code> if a connection to the twitter account could be established,
     * <code>false</code> otherwise
     */
    private boolean initConnection() {
        twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(apiKey, apiSecret);
        AccessToken accessToken = new AccessToken(token, tokenSecret);
        twitter.setOAuthAccessToken(accessToken);

        try {
            final String screenName = twitter.getScreenName();
            logger.info("Successfully connected to twitter account " + screenName);

            return true;
        } catch (TwitterException e) {
            logger.severe("could not connect to twitter account");

            return false;
        }
    }

    /**
     * Reads and returns a random trending keyword for Essen
     *
     * @return a random trend
     * @throws TwitterException when Twitter service or network is unavailable
     */
    private String getRandomTrend() throws TwitterException {
        final Trends trends = twitter.getPlaceTrends(WOEID_ESSEN);
        final int n = random.nextInt(trends.getTrends().length);
        final String result = trends.getTrends()[n].getName();

        return result;
    }

    /**
     * Returns whether a status update should be done or not
     *
     * @return <code>if a status update should be done</code>, <code>false</code> otherwise
     */
    private boolean fireTweet() {
        return random.nextInt(300) > 297;
    }

    /**
     * Reads the config.properties file
     *
     * @throws IOException if there was an error while reading <tt>config.properties</tt>
     */
    private void readConfigFile() throws IOException {
        // Read properties file
        final Properties prop = new Properties();

        try (final InputStream input = TwitterBot.class.getResourceAsStream("/config.properties")) {
            // load a properties file
            prop.load(input);

            // get the property values
            apiKey = prop.getProperty(API_KEY_PROPERTY);
            apiSecret = prop.getProperty(API_SECRET_PROPERTY);
            token = prop.getProperty(TOKEN_PROPERTY);
            tokenSecret = prop.getProperty(TOKEN_SECRET_PROPERTY);
        }
    }
}