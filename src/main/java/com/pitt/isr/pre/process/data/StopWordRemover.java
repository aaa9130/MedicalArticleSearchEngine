package com.pitt.isr.pre.process.data;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

import com.pitt.isr.config.Path;
import com.pitt.isr.config.Stemmer;


public class StopWordRemover {

	//declaring and initializing scanner object
	private Scanner scanner = null;
	private HashSet<String> stopWordsSet = null;

	public StopWordRemover( ) {

		File file = new File(Path.StopwordDir);//reading filePath
		try {
			scanner = new Scanner(file);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		//set for stop words
		stopWordsSet = new HashSet<String>();
		while (scanner.hasNext()) {
			char[] charArray = scanner.nextLine().toCharArray();

			//stemming stop words
			Stemmer stemmer = new Stemmer(); 
			stemmer.add(charArray, charArray.length);
			stemmer.stem();

			//adding stopWords to hashset
			stopWordsSet.add(stemmer.toString()); 
		}
	}

	// method to filter out stopwords, returns true if stopword
	public boolean isStopword( char[] word ) {

		String wordString = new String(word);

		if (stopWordsSet.contains(wordString))
			return true;
		return false;
	}
}
