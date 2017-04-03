package de.rullich.twitter.rules;

import java.util.List;
import java.util.Random;

public class DerWestenRule extends Rule {

    // file containing the tweets
    private static final String TEMPLATE_FILE = "derwesten.txt";
    private static final String NOUNS_FILE = "derwesten_nouns.txt";

    private final Random RANDOM = new Random();

    private List<String> tweets;
    private List<String> nouns;

    public DerWestenRule() {
        super(RuleCategory.DER_WESTEN);

        tweets = getLinesFromFile(TEMPLATE_FILE);
        nouns = getLinesFromFile(NOUNS_FILE);
    }

    @Override
    RuleApplication apply() {
        String tweet = tweets.get(RANDOM.nextInt(tweets.size()));

        while(tweet.contains("[]")) {
            tweet = tweet.replaceFirst("\\[\\]", nouns.get(RANDOM.nextInt(nouns.size())));
        }

        return new RuleApplication(this, tweet, "1");
    }
}
