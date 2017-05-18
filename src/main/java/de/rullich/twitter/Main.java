package de.rullich.twitter;

/**
 * The application's entry point
 */
public class Main {

    private static final String DEBUG_FLAG = "-debug";

    public static void main(String[] args) {
        final TwitterBot twitterBot;

        if(args.length > 0 && args[0].equals("-debug")) {
            twitterBot = TwitterBot.newDebugInstance();
        } else {
            twitterBot = TwitterBot.newInstance();
        }

        final Thread thread = new Thread(twitterBot);
        thread.start();
    }
}
