package de.rullich.twitter.rules;

import java.util.Optional;
import java.util.Random;

public class SayingsRule extends Rule {

    private final String[] TWEETS = new String[]{
            "Es ist nicht alles Bier, was glänzt.",
            "Wer in der Gaststätte Rullich sitzt, sollte nicht mit Steinen werfen.",
            "Alle Wege führen in die Gaststätte Rullich.",
            "Auch ein blindes Huhn findet mal einen Korn.",
            "Besser ein Bier in der Hand als eine Taube auf dem Dach.",
            "Das Bier im Haus erspart den Zimmermann.",
            "Dienst ist Dienst und Schnaps ist Schnaps.",
            "Die Würfel sind gefallen (Schock Out im Ersten!).",
            "Erlaubt ist, was Hopfen enthält.",
            "In der Not säuft der Teufel Fiege.",
            "Lieber dem Wirt was schenken als den Magen verrenken."
    };

    private final Random RANDOM = new Random();

    public SayingsRule() {
        super(RuleCategory.SAYINGS);
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
        final int index = RANDOM.nextInt(TWEETS.length);
        final String tweet = TWEETS[index];

        return new RuleApplication(this, tweet, Integer.toString(index));
    }
}
