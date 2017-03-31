package de.rullich.twitter.rules;

/**
 * Represents a 'twitter rule', i.e.
 */
public abstract class Rule {

    final RuleCategory ruleCategory;

    // engine that is used to apply this rule
    RuleEngine ruleEngine;

    public Rule(RuleCategory ruleCategory) {
        this.ruleCategory = ruleCategory;
    }

    public RuleCategory getRuleCategory() {
        return ruleCategory;
    }

    /**
     * Sets the rule engine this rule is bound to. Should only be called by a rule engine itself
     *
     * @param ruleEngine rule engine to bind this rule to
     */
    void setRuleEngine(final RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    /**
     * Calculates and returns the rule's weight. The higher the weight the more likely it it that
     * this rule will be executed
     *
     * @return the rule's weight (can be based on the previously applied rules)
     */
    abstract int getWeight();

    /**
     * Applies the rule. The return value contains the resulting tweet (among other information)
     *
     * @return
     */
    abstract RuleApplication apply();
}
