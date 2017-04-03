package de.rullich.twitter;

/**
 * The application's entry point
 */
public class Main {

    public static void main(String[] args) {
        final Thread thread = new Thread(new TwitterBot());
        thread.start();
    }
}
