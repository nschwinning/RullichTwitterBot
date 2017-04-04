# RullichTwitterBot

A bot that updates a twitter account's status based on simple rules.

## Compile

* `git clone https://github.com/nschwinning/RullichTwitterBot.git`
* place a file named `config.properties` in `src/main/resources`. The file must contain the following properties:
  * API_KEY
  * API_SECRET
  * TOKEN_KEY
  * TOKEN_SECRET
* `mvn clean package`

## Run

Simply run `java -jar RullichTwitterBot.jar`.

You might want to edit the templates on which the bot's tweets are based on. In that case, place one or more of the following files into the jar's directory:

* `sayings.txt` (full tweets for SayingsRule)
* `derwesten.txt` (templates for DerWestenRule)
* `derwesten_nouns.txt` (names and nouns for DerWestenRule)

The file format is one tweet/template/name/whatever per line.

Have fun!