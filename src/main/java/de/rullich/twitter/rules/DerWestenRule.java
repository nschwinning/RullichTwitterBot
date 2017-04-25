package de.rullich.twitter.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.rullich.twitter.JerseyClient;

public class DerWestenRule extends Rule {

    // file containing the tweets
    private static final String TEMPLATE_FILE = "derwesten.txt";
    private static final String NOUNS_FILE = "derwesten_nouns.txt";
    
    private static final List<String> SIGNAL_WORDS = new ArrayList<String>();
    
    private static final HashMap<String,String> REPLACEMENTS = new HashMap<String,String>();

    private final Random RANDOM = new Random();
    private final JerseyClient jc = new JerseyClient();

    private List<String> tweets;
    private List<String> nouns;

    public DerWestenRule() {
        super(RuleCategory.DER_WESTEN);

        SIGNAL_WORDS.add("Polizei");
        SIGNAL_WORDS.add("Essen");
        SIGNAL_WORDS.add("Bier");
        SIGNAL_WORDS.add("Rüttenscheid");
        
        REPLACEMENTS.put("Polizei", "Narrenzunft");
        REPLACEMENTS.put("Essen", "Stauderstadt");
        REPLACEMENTS.put("Bier", "Stauder");
        REPLACEMENTS.put("Rüttenscheid", "Rüsselscheid");
    }

    @Override
    RuleApplication apply() {
    	
    	/*
        String tweet = tweets.get(RANDOM.nextInt(tweets.size()));

        while(tweet.contains("[]")) {
            tweet = tweet.replaceFirst("\\[\\]", nouns.get(RANDOM.nextInt(nouns.size())));
        }
        */
    	List<String> tweets = jc.getTitles();
    	Collections.shuffle(SIGNAL_WORDS);
    	for (int i=0;i<SIGNAL_WORDS.size();i++){
    		for (int j=0;j<tweets.size();j++){
    			if (tweets.get(j).contains(SIGNAL_WORDS.get(i))){
    				String tweet = tweets.get(j).replaceAll(SIGNAL_WORDS.get(i), REPLACEMENTS.get(SIGNAL_WORDS.get(i)));
    				return new RuleApplication(this, tweet, "1");
    			}
    		}
    	}
    	

        return null;
    }
}
