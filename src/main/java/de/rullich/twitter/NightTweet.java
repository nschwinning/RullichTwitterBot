package de.rullich.twitter;

import java.util.Random;

public class NightTweet {

	final static String[] NIGHT_TWEET = new String[]{"Noch einer im Bier?", "Hat noch jemand Lust auf ein Stauder?", "Jetzt noch ein leckeres Pilsken inner Gaststätte?", "Abschlussstauder jetzt!", "Wer könnte jetzt Nein zu einem leckeren Stauder sagen?", "4:23, Zeit für ein Stauder!", "Könnt ihr auch nicht schlafen? Dann lasst uns noch ein Bierchen trinken :)"};
	
	public static String getTweet(){
		Random r = new Random();
		int n = NIGHT_TWEET.length;
		int index = r.nextInt(n);
		return NIGHT_TWEET[index];
	}
}
