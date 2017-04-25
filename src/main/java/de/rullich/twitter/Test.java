package de.rullich.twitter;

import java.util.List;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*
		JerseyClient jc = new JerseyClient();
		List<String> titles = jc.getTitles();
		for (String s:titles){
			System.out.println(s);
		}
		*/
		
		TestBot bot = new TestBot();
		bot.run();
	}

}
