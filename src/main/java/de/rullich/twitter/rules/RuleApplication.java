package de.rullich.twitter.rules;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Contains information about the application of a rule, i.e. the rule that was applied, the
 * resulting tweet, additional information as well as a date and time of application. The
 * additional information may be used to provide information a rule needs to be flexible (e.g.
 * an index to avoid repeating tweets).
 */
public final class RuleApplication implements Serializable {

    private final Rule rule;
    private final String tweet;
    private final String additionalInformation;
    private final LocalDateTime creationDateTime;

    public RuleApplication(Rule rule, String tweet, String additionalInformation) {
        this.rule = rule;
        this.tweet = tweet;
        this.additionalInformation = additionalInformation;
        this.creationDateTime = LocalDateTime.now();
    }

    public Rule getRule() {
        return rule;
    }

    public String getTweet() {
        return tweet;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }
}
