package de.rullich.twitter.rules;

public abstract class Rule {

    final RuleCategory ruleCategory;
    RuleEngine ruleEngine;

    public Rule(RuleCategory ruleCategory) {
        this.ruleCategory = ruleCategory;
    }

    public RuleCategory getRuleCategory() {
        return ruleCategory;
    }

    public void setRuleEngine(final RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    abstract int getWeight();
    abstract RuleApplication apply();
}
