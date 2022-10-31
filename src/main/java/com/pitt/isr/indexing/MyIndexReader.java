package com.pitt.isr.indexing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.pitt.isr.config.Path;

public class MyIndexReader {

	//declaring class variables for document, dictionary and postings file
	private String documentFile;
	private String dictionaryFile;
	private String postingsFile;
	private BufferedReader bufferedReader;	

	public MyIndexReader(String type) throws IOException {

		if(type.equals("trectext")) {

			documentFile = Path.DataTextDir;
			dictionaryFile = "data//textDict.txt";
			postingsFile = Path.IndexTextDir+".txt";
		}

	}


	public int GetDocid(String docno) {

		return -1;
	}

	public String GetDocno(int docid) {

		try {
			bufferedReader = new BufferedReader(new FileReader(documentFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String line = "";
		String [] tempArray;

		try {
			while((line=bufferedReader.readLine() )!= null) {
				tempArray = line.split(": ");  //document set file in format [docid, doc number]

				if( docid == Integer.valueOf(tempArray[0])) {  

					return tempArray[0]; 

				}

			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		try {

			bufferedReader.close();

		} catch (IOException e) {

			e.printStackTrace();
		} 
		return null;
	}

	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList(String token) throws IOException {

		int temp[][] = readPostingsFile(token);

		if(temp != null) {

			return temp;
		}

		return null;
	}

	public int GetDocFreq(String token) throws IOException {

		String[] temp = readDictionaryFile(token);
		if(temp != null) {    

			return Integer.parseInt(temp[3]);
		}

		return 0;
	}

	public long GetCollectionFreq(String token) throws IOException {

		String[] temp = readDictionaryFile(token); 
		if(temp != null) {    

			return Integer.parseInt(temp[2]);
		}

		return 0;
	}

	public void Close() throws IOException {

		bufferedReader.close();

	}

	/**
	 * Method for reading dictionary file
	 * @param term
	 * @throws IOException
	 */	
	public String[] readDictionaryFile(String term) throws IOException {

		bufferedReader = new BufferedReader(new FileReader(dictionaryFile));

		String line = "";
		String[] tempArr;

		while((line=bufferedReader.readLine())!= null && line.length()!=0) {

			tempArr = line.split(",");  //termId, term, idf, df

			if(tempArr[1].equals(term)) {   
				return tempArr;  
			}

		}
		bufferedReader.close();
		return null;
	}

	/**
	 * Method for reading postings file
	 * @param term
	 * @throws IOException
	 */	
	public int[][] readPostingsFile(String term) throws IOException{

		int result[][] = null;

		String termId;

		String[] tempArray = readDictionaryFile(term);  //termId, term, idf, df

		String line;

		String[] pointer;

		if(tempArray!= null) {

			termId = tempArray[0]; 

			bufferedReader = new BufferedReader(new FileReader(postingsFile));

			while((line = bufferedReader.readLine())!= null && line.length()!= 0) {

				pointer = line.split(","); 


				if(pointer[0].equals(termId)) {// find Id in posting file

					result = new int[pointer.length-1][2];

					for(int i =1; i<pointer.length; i++) {

						// split into [id, freq]  array
						String[] docIdFreqArray = pointer[i].split("_");

						result [i-1][0] = Integer.valueOf(docIdFreqArray[0]);  //documentId
						result [i-1][1] = Integer.valueOf(docIdFreqArray[1]);  //frequency
					}
				}
			}
			bufferedReader.close();
			return result;
		}

		return null;
	}

}