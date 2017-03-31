package de.rullich.twitter;

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
import java.util.Properties;
import java.util.Random;

public class TwitterBot {

    private static String API_KEY;
    private static String API_SECRET;
    private static String TOKEN;
    private static String TOKEN_SECRET;
    private static Twitter twitter;
    private static boolean connected;
    private static boolean run = true;
    private static JerseyClient jc;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        readConfigFile();
        init();

        final RuleEngine ruleEngine = new RuleEngine();
        ruleEngine.registerRule(new SayingsRule());

        jc = new JerseyClient();
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
            	if (fireTweet()){
            		//Tweet here!
            	}
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
    
    private static boolean fireTweet(){
    	int n = (new Random()).nextInt(300);
    	if (n>297){
    		return true;
    	}
    	return false;
    }

}
