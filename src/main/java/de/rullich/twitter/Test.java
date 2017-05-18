package de.rullich.twitter;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class Test {
	
	private Twitter twitter;
	
    public static final String API_KEY_PROPERTY = "API_KEY";
    public static final String API_SECRET_PROPERTY = "API_SECRET";
    public static final String TOKEN_PROPERTY = "TOKEN";
    public static final String TOKEN_SECRET_PROPERTY = "TOKEN_SECRET";

    // security values needed for connecting to the twitter account (defined in config.properties)
    private String apiKey;
    private String apiSecret;
    private String token;
    private String tokenSecret;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*
		JerseyClient jc = new JerseyClient();
		List<String> titles = jc.getTitles();
		for (String s:titles){
			System.out.println(s);
		}
		*/
		
		TestBot bot = new TestBot();
		bot.run();
	}
	
	 private boolean initConnection() {
	        twitter = TwitterFactory.getSingleton();
	        twitter.setOAuthConsumer(apiKey, apiSecret);
	        AccessToken accessToken = new AccessToken(token, tokenSecret);
	        twitter.setOAuthAccessToken(accessToken);

	        try {
	            final String screenName = twitter.getScreenName();
	            System.out.println("Successfully connected to twitter account " + screenName);

	            return true;
	        } catch (TwitterException e) {
	            System.out.println("could not connect to twitter account");

	            return false;
	        }
	    }

}
