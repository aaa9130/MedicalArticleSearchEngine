package com.pitt.isr.pre.process.data;

import java.util.ArrayList;
import java.util.List;

public class WordTokenizer {

	private List<String> list = null;
	private int index = 0;


	public WordTokenizer( String content ) {

		final String eliminateSymbols = "([[^a-z]&&[^A-Z] && [^0-9]]+)";
		final String replaceComma = "(\\.)|(\\')|(\t)";

		list = new ArrayList<String>();

		//Eliminating symbols and whitespaces
		String text = new String(content).replaceAll(eliminateSymbols, " ")
				.replaceAll(replaceComma, " ").replaceAll("( )+", " ");

		String[] tokens = text.split(" "); // splitting text

		//store into list
		for (int i = 0; i < tokens.length; i++) { 
			list.add(tokens[i]);
		}
		//System.out.println(list.toString());
	}

	public char[] nextWord() {

		if (index >= list.size())
			return null;
		else {
			//get the tokens using index and convert to CharArray
			return list.get(index++).toCharArray(); 
		}
	}

}
