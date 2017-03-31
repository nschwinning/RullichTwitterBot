package de.rullich.twitter.rules;

import com.sun.imageio.plugins.jpeg.JPEG;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Creates tweets based on classic German sayings
 */
public class SayingsRule extends Rule implements Serializable {

    private static final Logger logger = Logger.getLogger(SayingsRule.class.getName());

    // file containing the tweets
    private static final String TEMPLATE_FILE = "sayings.txt";

    private final Random RANDOM = new Random();

    private List<String> tweets;

    public SayingsRule() {
        super(RuleCategory.SAYINGS);

        try {
            tweets = readTemplatesFromFile(TEMPLATE_FILE);
            logger.info(String.format("template file '%s' found. Importing %d entries...", TEMPLATE_FILE, tweets.size()));
        } catch (IOException e) {
            logger.warning("unable to read template file " + TEMPLATE_FILE);
            tweets = new LinkedList<>();
        }
    }

    @Override
    int getWeight() {
        Optional<RuleApplication> optionalApplication = ruleEngine.getRuleApplications().stream()
                .filter(application ->
                        application.getRule().getRuleCategory() == this.ruleCategory)
                .findAny();

        if (optionalApplication.isPresent()) {
            return 1;
        } else {
            return 100;
        }
    }

    @Override
    RuleApplication apply() {
        final int index = RANDOM.nextInt(tweets.size());
        final String tweet = tweets.get(index);

        return new RuleApplication(this, tweet, Integer.toString(index));
    }
}
