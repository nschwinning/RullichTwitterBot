package de.rullich.twitter;

import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Random;

public class TwitterBot {

    static String API_KEY;
    static String API_SECRET;
    static String TOKEN;
    static String TOKEN_SECRET;
    static Twitter twitter;
    static boolean connected;
    static boolean run = true;
    final static String NIGHT_TWEET = "Noch einer im Bier?";

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        readConfigFile();
        init();
        while (run) {
            LocalDateTime now = LocalDateTime.now();
            int hour = now.getHour();
            int minute = now.getMinute();
            if (minute == 23 && hour == 4) {
                try {
                    String trend = getRandomTrend();
                    if (connected) {
                        twitter.updateStatus(NightTweet.getTweet() + " " + trend);
                    }
                } catch (TwitterException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                System.out.println(hour + ":" + minute);
            }
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static void init() {

        twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(API_KEY, API_SECRET);
        AccessToken accessToken = new AccessToken(TOKEN, TOKEN_SECRET);
        twitter.setOAuthAccessToken(accessToken);

        try {
            twitter.getScreenName();
            connected = true;
            System.out.println("Successfully connected to twitter account " + twitter.getScreenName());
        } catch (TwitterException e) {
            connected = false;
        }
    }

    private static void readConfigFile() {
        // Read properties file
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = TwitterBot.class.getResourceAsStream("/config.properties");

            // load a properties file
            prop.load(input);

            // get the property values
            API_KEY = prop.getProperty("API_KEY");
            API_SECRET = prop.getProperty("API_SECRET");
            TOKEN = prop.getProperty("TOKEN");
            TOKEN_SECRET = prop.getProperty("TOKEN_SECRET");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static String getRandomTrend() throws TwitterException {
        Trends trends = twitter.getPlaceTrends(648820);
        Random random = new Random();
        int n = random.nextInt(trends.getTrends().length);
        String result = trends.getTrends()[n].getName();
        return result;
    }

}
