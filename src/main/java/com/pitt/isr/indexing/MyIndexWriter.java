package com.pitt.isr.indexing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.pitt.isr.config.Path;


public class MyIndexWriter {

	//max chunk to be read at once
	private static final int MAX_IN_BLOCK = 30000;

	//index string
	private String indexStr;

	//file for temp index
	private File tempIndexFile;

	//identifier for block
	private int blockId;

	//process index for posting list, it contains(term, docId, frequency)
	private Map<String, Map<Integer, Integer>> postingsMap;

	//variable for document id integer value
	private int docId;

	//variable to read results file->doc id- doc no file
	private String documentSet;

	//writer for writing temp file
	private BufferedWriter tempBufferedWriter;

	//writer for writing document set
	private BufferedWriter documentSetWriter;


	//file reading for merging temp files
	private BufferedReader tempIndexFileReader;

	//file object for dictionary
	private File dictionaryFile;

	//writer for writing dictionary file
	private BufferedWriter dictWriter;

	//writer for postings list
	private BufferedWriter postingsWriter;

	public MyIndexWriter(String type) throws IOException {

		postingsMap = new TreeMap<>();

		//initialising variables
		blockId = 0;  
		docId = 0; 
		tempIndexFile = null; 
		tempBufferedWriter = null;


		//reading file path for text or web corpus
		if(type.equals("trectext")) {

			documentSet = Path.DataTextDir;
			indexStr = Path.IndexTextDir;
		}

		//for document set
		documentSetWriter = new BufferedWriter(new FileWriter(documentSet, true)); 


	}

	public void IndexADocument(String docno, char[] content) throws IOException {

		Map<Integer, Integer> tempMap = null;  //map for docId and term frequency

		String contentString = String.valueOf(content);  //converting char[] to string
		String[] wordsArray = contentString.split("\\s+");  //splitting the string by whitespace to obtain string array

		docId++;  //increment doc count

		documentSetWriter.write(docId + ": "+docno+"\n");  //writing in document set file in format as [count: document number] 

		// ----------------------section:  content( posting list)--------------------------------------------

		for(String word: wordsArray) {

			if(word.equals(""))
				continue;  //skipping for blank spaces 

			if(postingsMap.containsKey(word)) {
				tempMap = postingsMap.get(word);  //fetching the word and frequency if already existing

				if(tempMap.containsKey(docId)) {  
					tempMap.put(docId, tempMap.get(docId)+1);  //if word already present in postings, increment frequency
				} else {
					tempMap.put(docId, 1);  //adding new word 
				}
				postingsMap.put(word, tempMap);
			} else {

				tempMap = new TreeMap<>(); //initializing again

				tempMap.put(docId, 1);  
				postingsMap.put(word, tempMap);	

			}   
		}

		if(docId % MAX_IN_BLOCK ==0) {

			writeTemp();  //writing in disk
			postingsMap.clear();  //clearing map of postings
		}
	}


	public void Close() throws IOException {

		writeTemp();

		documentSetWriter.flush();
		documentSetWriter.close();

		merge();  //calling fusing method to merge all temp file into one
	}




	/**
	 * Method for creating word dictionary files
	 * @param 
	 * @throws IOException
	 */
	public void writeTemp() throws IOException {

		blockId++;  //incrementing block id

		tempIndexFile = new File(indexStr +blockId+".txt");  //initialising temp file

		if(!tempIndexFile.exists()) {
			tempIndexFile.createNewFile(); 
		}

		tempBufferedWriter = new BufferedWriter(new FileWriter(tempIndexFile));

		for(Entry<String, Map<Integer, Integer>> entrySet : postingsMap.entrySet() ){ //Entry in map will be in form [Term, docId and frequency]

			tempBufferedWriter.write(entrySet.getKey().trim()+" "); 

			for(Entry<Integer, Integer> frequencyEntrySet: entrySet.getValue().entrySet()){  //one term can be present in multiple docs and many times

				tempBufferedWriter.write(frequencyEntrySet.getKey()+"_"+frequencyEntrySet.getValue()+",");
			}

			tempBufferedWriter.write("\n"); 
		}
		tempBufferedWriter.flush();
		tempBufferedWriter.close();
	}

	/**
	 * Method for merging all documents
	 * @param 
	 * @throws IOException
	 */
	public void merge() throws IOException{

		Map<String, String> mergedPostingsMap = null; //merged postings map

		for(int i=1; i<=blockId; i++) {

			tempIndexFileReader = new BufferedReader(new FileReader(indexStr+i+".txt"));

			String line = "";
			Map<String, String> tempIndexMap = new TreeMap<>();

			while((line = tempIndexFileReader.readLine())!= null && line.length() != 0) {

				String[] indexArray = line.split(" ");  
				tempIndexMap.put(indexArray[0], indexArray[1]); 
			}

			if(mergedPostingsMap == null)    
				mergedPostingsMap = new TreeMap<>(tempIndexMap);    // put temp into final postings list

			else {
				for(Entry<String, String> entrySet: tempIndexMap.entrySet()) {

					String term = entrySet.getKey();    

					if(mergedPostingsMap.containsKey(term)){    
						mergedPostingsMap.put(term, mergedPostingsMap.get(term)+entrySet.getValue());
					} else {
						mergedPostingsMap.put(term, entrySet.getValue());
					}
				}
			}

			tempIndexFileReader.close(); // closing once first block is read
			tempIndexFile = new File(indexStr +i+".txt"); 

			tempIndexFile.delete();
		}

		createDictionaryFile(mergedPostingsMap); //calling dictionary writer and postings writer method
	}

	/**
	 * Method for writing dictionary and postings
	 * @param mergedPostingsMap
	 * @throws IOException
	 */		

	private void createDictionaryFile(Map<String, String> mergedPostingsMap) throws IOException {


		dictionaryFile = new File("data//textDict.txt");

		dictWriter = new BufferedWriter(new FileWriter(dictionaryFile,true));  //writes file in format [termId, term, whole collection freq, document freq]

		postingsWriter = new BufferedWriter(new FileWriter(indexStr+".txt", true));  //writes file in format [termId, docId_freq]

		int termId = 0;
		for(Entry<String, String> list: mergedPostingsMap.entrySet()) {

			int frequency = 0;

			String[] freqArray = list.getValue().split(","); 

			for(String temp: freqArray) {//generating total document freq

				String[] freq = temp.split("_");
				frequency= frequency+ Integer.parseInt(freq[1]);
			}

			termId++;
			dictWriter.write(termId+","+list.getKey()+","+frequency+","+freqArray.length); 

			dictWriter.write("\n");

			postingsWriter.write(termId+","+list.getValue());  
			postingsWriter.write("\n");
		}

		dictWriter.flush();
		dictWriter.close();

		postingsWriter.flush();
		postingsWriter.close();
	}
}
