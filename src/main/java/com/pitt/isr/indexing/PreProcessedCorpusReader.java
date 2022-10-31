package com.pitt.isr.indexing
;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pitt.isr.config.Path;

public class PreProcessedCorpusReader {

	//reader to read the results file
	private BufferedReader bufferedReader;

	public PreProcessedCorpusReader(String type) throws IOException {

		//validating the type of file
		if (type.equals("trectext")) {
			bufferedReader = new BufferedReader(new FileReader(Path.ResultHM1 + type));
		} else {
			throw new IOException("Wrong type error");
		}
	}


	public Map<String, Object> NextDocument() throws IOException {


		//map for storing document id and content
		Map<String, Object> docIdContentMap = new HashMap<>();

		//line to be read from bufferedReader
		String line = "";
		//variable for reading document id
		String docId ="";
		//variable for reading document content
		String content = "";

		line = bufferedReader.readLine(); // initialize , get ID

		if( line != null) {
			docId = line;

			//reading content
			content = bufferedReader.readLine();

			docIdContentMap.put(docId, content.toCharArray());

			return docIdContentMap;
		}

		bufferedReader.close();
		return null;
	}
}