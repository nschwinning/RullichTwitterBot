package de.rullich.twitter.rules;

import java.util.*;

public class RuleEngine {

    private static final int HISTORY_SIZE = 50;

    private Set<Rule> rules = new HashSet<>();

    private List<RuleApplication> ruleApplications = new ArrayList<>();

    private final Random RANDOM = new Random();

    private static class WeightedRule {
        private final Rule rule;
        private final int fromWeigh;
        private final int toWeight;

        public WeightedRule(Rule rule, int fromWeight, int toWeight) {
            this.rule = rule;
            this.fromWeigh = fromWeight;
            this.toWeight = toWeight;
        }
    }

    public void registerRule(final Rule rule) {
        Objects.requireNonNull(rule);

        rule.setRuleEngine(this);
        this.rules.add(rule);
    }

    public Optional<RuleApplication> fireNextRule() {
        final List<WeightedRule> weightedRules = new LinkedList<>();

        int sum = 0;

        for (final Rule rule : rules) {
            if (rule.getWeight() > 0) {
                final int fromWeight = sum + 1;
                final int toWeight = sum + rule.getWeight();
                weightedRules.add(new WeightedRule(rule, fromWeight, toWeight));
                sum = toWeight;
            }
        }

        final int randomValue;

        if (sum > 1) {
            randomValue = RANDOM.nextInt(sum - 1) + 1;
        } else {
            randomValue = 1;
        }

        // randfall
        Optional<WeightedRule> optionalWeigthedRule = weightedRules.stream()
                .filter(wr -> wr.fromWeigh <= randomValue && randomValue <= wr.toWeight)
                .findFirst();

        if (optionalWeigthedRule.isPresent()) {
            final Rule rule = optionalWeigthedRule.get().rule;
            final RuleApplication result = rule.apply();
            ruleApplications.add(result);

            if (ruleApplications.size() > HISTORY_SIZE) {
                ruleApplications.remove(0);
            }

            return Optional.of(result);
        } else {
            // log error
            return Optional.empty();
        }
    }

    public List<RuleApplication> getRuleApplications() {
        return Collections.unmodifiableList(ruleApplications);
    }
}
