package de.rullich.twitter.rules;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a 'twitter rule', i.e.
 */
public abstract class Rule implements Serializable {

    final RuleCategory ruleCategory;

    // engine that is used to apply this rule
    transient RuleEngine ruleEngine;

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

    static List<String> readTemplatesFromFile(final String fileName) throws IOException {
        return FileUtils.readLines(new File(fileName), "UTF-8");
    }
}
