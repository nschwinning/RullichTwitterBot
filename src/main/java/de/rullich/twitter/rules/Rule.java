package de.rullich.twitter.rules;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Represents a 'twitter rule', i.e.
 */
public abstract class Rule implements Serializable {

    private transient Logger logger = Logger.getLogger(Rule.class.getName());

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

    /**
     * Applies the rule. The return value contains the resulting tweet (among other information)
     *
     * @return
     */
    abstract RuleApplication apply();

    /**
     * Reads tweet templates from a file <code>fileName</code> in the current directory. If <code>fileName</code> is not
     * present, it tries to get it from the packaged jar. If this is also not successful, an empty list is returned.
     *
     * @param fileName file containing tweet templates
     * @return
     */
    protected List<String> getTemplatesFromFile(final String fileName) {
        List<String> lines;

        try {
            // try to read it from the current directory
            lines = readTemplatesFromFile(fileName);
            logger.info(String.format("template file '%s' found. %d entries imported.", fileName, lines.size()));
        } catch (IOException e) {
            logger.warning(String.format("unable to read template file '%s'. Falling back to packaged '%s'...", fileName, fileName));

            try {
                // try to read it from the packed jar
                lines = readTemplatesFromClasspathFile(fileName);
                logger.info(String.format("packaged template file '%s' found. %d entries imported.", fileName, lines.size()));
            } catch (IOException | URISyntaxException ee) {
                // everything went wrong. we just return an empty list
                logger.warning(String.format("unable to read packaged template file '%s'", fileName));
                lines = new LinkedList<>();
            }
        }

        return lines;
    }

    private static List<String> readTemplatesFromFile(final String fileName) throws IOException {
        return FileUtils.readLines(new File(fileName), "UTF-8");
    }

    private static List<String> readTemplatesFromClasspathFile(final String fileName) throws IOException, URISyntaxException {
        return IOUtils.readLines(Rule.class.getResource("/" + fileName).openStream(), "UTF-8");
    }
}
