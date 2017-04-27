package de.rullich.twitter.rules;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

/**
 * The rule engine manages to apply (execute) {@link Rule}s. The rule engine keeps track of the rules that were applied
 * and provides this information to the rules. A rule defines a way to create tweets and may use this information to
 * avoid repeating the same tweets over and over again.
 * <p><strong>Example usage:</strong></p>
 * <code>final RuleEngine ruleEngine = new RuleEngine();
 * ruleEngine.registerRule(new SayingsRule());
 * String tweet = ruleEngine.fireNextRule().get().getTweet();</code>
 */
public class RuleEngine {

    private final Logger logger = Logger.getLogger(RuleEngine.class.getName());

    // number of rule applications that are stored
    private static final int HISTORY_SIZE = 20;

    // file that stores the state of ruleApplications variable
    private static final String HISTORY_FILE = "rules.history";

    private final Path HISTORY_FILE_PATH = Paths.get(HISTORY_FILE);

    // the rules that have been bound to the engine
    private Set<Rule> rules = new HashSet<>();

    // rule application history
    private List<RuleApplication> ruleApplications = new ArrayList<>();

    private final Random RANDOM = new Random();

    private static class WeightedRule {
        private final Rule rule;
        private final int fromWeight;
        private final int toWeight;

        public WeightedRule(Rule rule, int fromWeight, int toWeight) {
            this.rule = rule;
            this.fromWeight = fromWeight;
            this.toWeight = toWeight;
        }
    }

    public RuleEngine() {
        // try to load ruleApplications
        loadRuleApplications();
    }

    public void registerRule(final Rule rule) {
        Objects.requireNonNull(rule);

        rule.setRuleEngine(this);
        this.rules.add(rule);

        logger.info(String.format("Rule %s registered.", rule.getClass().getSimpleName()));
    }

    /**
     * Determines the rules that are applicable right now, weights them and randomly selects one of them to be applied.
     * The application's result is then returned.
     *
     * @return
     */
    public Optional<RuleApplication> fireNextRule() {
        // general idea: first, sort out rules that are not applicable. Then, define a value interval for each rule
        // based on its weights. Finally, get a random value and determine the rule to apply using the intervals.

        // applicable rules and their weights are stored in this list
        final List<WeightedRule> weightedRules = new LinkedList<>();

        // sum of the rules' weights
        int sum = 0;

        // iterate through all rules. If a rule is applicable (weight > 0) then store their 'value interval' in
        // [fromWeight; toWeight]
        for (final Rule rule : rules) {
            if (rule.getWeight() > 0) {
                final int fromWeight = sum + 1;
                final int toWeight = sum + rule.getWeight();
                weightedRules.add(new WeightedRule(rule, fromWeight, toWeight));
                sum = toWeight;
            }
        }

        // select a random value
        final int randomValue;

        // ...only possible to use the RNG if the weight sum is greater than 1
        if (sum > 1) {
            randomValue = RANDOM.nextInt(sum - 1) + 1;
        } else {
            // ...otherwise, just use 1
            randomValue = 1;
        }

        // find the rule for that randomValue is between fromWeight and toWeight
        Optional<WeightedRule> optionalWeigthedRule = weightedRules.stream()
                .filter(wr -> wr.fromWeight <= randomValue && randomValue <= wr.toWeight)
                .findFirst();

        // if there is such a rule, apply it
        if (optionalWeigthedRule.isPresent()) {
            final Rule rule = optionalWeigthedRule.get().rule;
            final Optional<RuleApplication> optionalResult = rule.apply();

            if(optionalResult.isPresent()) {
                final RuleApplication result = optionalResult.get();

                ruleApplications.add(result);

                if (ruleApplications.size() > HISTORY_SIZE) {
                    ruleApplications.remove(0);
                }

                saveRuleApplications();

                return Optional.of(result);
            } else {
                return Optional.empty();
            }
        } else {
            // there seems to be an error here.
            logger.warning("could not determine rule to apply");

            return Optional.empty();
        }
    }

    public List<RuleApplication> getRuleApplications() {
        return Collections.unmodifiableList(ruleApplications);
    }

    /**
     * Loads the ruleApplications list from HISTORY_FILE
     */
    private void loadRuleApplications() {
        if (Files.exists(HISTORY_FILE_PATH)) {
            logger.info(String.format("history file found"));
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(HISTORY_FILE_PATH.toFile()))) {
                ruleApplications = (List<RuleApplication>) in.readObject();

                // set all the Rule instances' ruleEngine field to this rule engine
                ruleApplications.stream()
                        .map(ra -> ra.getRule())
                        .forEach(rule -> rule.setRuleEngine(this));

                logger.info(String.format("history loaded (%d RuleApplication items)", ruleApplications.size()));
            } catch (IOException | ClassNotFoundException e) {
                logger.warning("error while loading history: " + e.getMessage());
            }
        }
    }

    /**
     * Stores the ruleApplications list in HISTORY_FILE
     */
    private void saveRuleApplications() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE_PATH.toFile()))) {
            out.writeObject(ruleApplications);
        } catch (IOException e) {
            logger.warning("error while writing history file: " + e.getMessage());
        }
    }
}
