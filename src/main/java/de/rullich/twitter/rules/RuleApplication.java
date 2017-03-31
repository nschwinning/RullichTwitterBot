package de.rullich.twitter.rules;

import java.time.LocalDateTime;

public final class RuleApplication {

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
