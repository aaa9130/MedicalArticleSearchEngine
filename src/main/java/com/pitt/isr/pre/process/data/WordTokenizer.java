package com.pitt.isr.pre.process.data;

import java.util.ArrayList;
import java.util.List;

public class WordTokenizer {

	private List<String> list = null;
	private int index = 0;

	final String eliminateSymbols = "([[^a-z]&&[^A-Z] && [^0-9]]+)";
	final String replaceComma = "(\\.)|(\\')|(\t)";
	

// YOU MUST IMPLEMENT THIS METHOD
public WordTokenizer(char[] content) {
    // this constructor will tokenize the input texts (usually it is a char array for a whole
    // document)
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


	public WordTokenizer( String content ) {

		

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
