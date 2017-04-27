package de.rullich.twitter.rules;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Creates tweets based on classic German sayings
 */
public class SayingsRule extends Rule implements Serializable {

    // file containing the tweets
    private static final String TEMPLATE_FILE = "sayings.txt";

    private final Random RANDOM = new Random();

    private List<String> tweets;

    public SayingsRule() {
        super(RuleCategory.SAYINGS);

        tweets = getLinesFromFile(TEMPLATE_FILE);
    }

    @Override
    Optional<RuleApplication> apply() {
        final int index = RANDOM.nextInt(tweets.size());
        final String tweet = tweets.get(index);

        return Optional.of(new RuleApplication(this, tweet, Integer.toString(index)));
    }
}
