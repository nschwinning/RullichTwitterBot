package de.rullich.twitter.rules;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.rullich.twitter.JerseyClient;

public class DerWestenRule extends Rule {

    // file containing the tweets
    private static final String TEMPLATE_FILE = "derwesten.txt";
    private static final String NOUNS_FILE = "derwesten_nouns.txt";
    
    private static final HashMap<String,String> REPLACEMENTS = new HashMap<String,String>();

    private final Logger logger = Logger.getLogger(DerWestenRule.class.getName());

    private final Random RANDOM = new Random();
    private final JerseyClient jc = new JerseyClient();

    static {
        REPLACEMENTS.put("Polizei", "Narrenzunft");
        REPLACEMENTS.put("Essen", "Stauderstadt");
        REPLACEMENTS.put("Bier", "Stauder");
        REPLACEMENTS.put("Rüttenscheid", "Rüsselscheid");
    }

    private List<String> tweets;
    private List<String> nouns;

    public DerWestenRule() {
        super(RuleCategory.DER_WESTEN);
    }

    @Override
    Optional<RuleApplication> apply() {
    	List<String> tweets = jc.getTitles();

    	final List<String> signalWords = REPLACEMENTS.keySet().stream().collect(Collectors.toList());

    	Collections.shuffle(signalWords);

    	for(final String signalWord : signalWords) {
    	    for(final String tweet : tweets) {
    	        if(tweet.contains(signalWord)) {
    	            final String newTweet = tweet.replaceAll(signalWord, REPLACEMENTS.get(signalWord));
    	            return Optional.of(new RuleApplication(this, newTweet, "1"));
                }
            }
        }

        logger.warning("trying to apply DerWestenRule, but no tweet containing a singal word found");

        return Optional.empty();
    }
}
