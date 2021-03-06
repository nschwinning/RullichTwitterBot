package de.rullich.twitter;

import de.rullich.twitter.rules.DerWestenRule;
import de.rullich.twitter.rules.RuleApplication;
import de.rullich.twitter.rules.RuleEngine;
import de.rullich.twitter.rules.SayingsRule;
import de.rullich.twitter.tweets.NightTweet;
import de.rullich.twitter.tweets.TimedTweet;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The real bot. Runs in a thread, wakes up from time to time and updates the twitter status (or not).
 */
public class TestBot implements Runnable, TrendProvider {

    private static final Logger logger = Logger.getLogger(TwitterBot.class.getName());

    // the amount of time the bot will sleep
    private static final int ONE_MINUTE = 10; // 60 seconds times 1.000 milliseconds

    // twitter Where On Earth ID for Essen, Germany
    public static final int WOEID_ESSEN = 648820;
    
    public static final int NUMBER_OF_RUNS = 100000;

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

    // fixed-time tweets
    private final List<TimedTweet> timedTweets = new LinkedList<>();

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

            timedTweets.add(new NightTweet(this));

            jc = new JerseyClient();
        } catch (IOException e) {
            logger.severe("error while starting TwitterBot: " + e.getMessage());
        }

        int tweets=0;
        for (int i=0;i<NUMBER_OF_RUNS;i++) {
            final LocalDateTime now = LocalDateTime.now();

            boolean timedTweetFired = false;

            for (final TimedTweet timedTweet : timedTweets) {
                if (timedTweet.isApplicable(now)) {
                    System.out.println(timedTweet.getTweet());

                    timedTweetFired = true;
                }
            }

            if (!timedTweetFired) {
                if (fireTweet()) {
                	tweets++;
                    final Optional<RuleApplication> optionalRuleApplication = ruleEngine.fireNextRule();

                    if (optionalRuleApplication!=null){
	                    if (optionalRuleApplication.isPresent()) {
	                        // could successfully apply a rule
	                        final String tweet = optionalRuleApplication.get().getTweet();
	
	                        System.out.println(tweet);
							logger.info("updated status: " + tweet);
	                    } else {
	                        // no rule could be applied
	                        logger.warning("tried to apply a rule but no rule was applicable right now");
	                    }
                    }
                }
            }
            try {
                Thread.sleep(ONE_MINUTE);
            } catch (InterruptedException e) {
                logger.warning("whoops! My thread got interrupted!");
            }
        }
        System.out.println(tweets + " tweets fired in " + NUMBER_OF_RUNS + " minutes.");
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

    @Override
    public String provideTrend() throws TwitterException {
        final Trends trends = twitter.getPlaceTrends(WOEID_ESSEN);

        final List<String> hashtagTrends = Arrays.stream(trends.getTrends())
                .map(trend -> trend.getName())
                .filter(name -> name.startsWith("#"))
                .collect(Collectors.toList());

        if (hashtagTrends.size() == 0) {
            // if there is no trend starting with a hashtag...
            return "";
        } else {
            return hashtagTrends.get(random.nextInt(hashtagTrends.size()));
        }
    }

    /**
     * Returns whether a status update should be done or not
     *
     * @return <code>if a status update should be done</code>, <code>false</code> otherwise
     */
    private boolean fireTweet() {
        return random.nextInt(300) > 295;
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