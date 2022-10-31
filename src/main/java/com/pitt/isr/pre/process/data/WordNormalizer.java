package com.pitt.isr.pre.process.data;
import com.pitt.isr.config.Stemmer;

public class WordNormalizer {

	public char[] lowercase( char[] chars ) {

		//converting all chars to lowercase
		for(int i = 0; i<chars.length; i++) {
			chars[i] = Character.toLowerCase(chars[i]);

		}
		return chars;
	}

	public String stem(char[] chars)
	{
		Stemmer stemmer = new Stemmer();
		stemmer.add(chars, chars.length);
		stemmer.stem();

		return stemmer.toString();
	}

}
