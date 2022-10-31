package com.pitt.isr.MedicalArticleSearchEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.pitt.isr.config.Path;
import com.pitt.isr.indexing.MyIndexReader;
import com.pitt.isr.indexing.MyIndexWriter;
import com.pitt.isr.indexing.PreProcessedCorpusReader;
import com.pitt.isr.pre.process.data.DocumentCollection;
import com.pitt.isr.pre.process.data.StopWordRemover;
import com.pitt.isr.pre.process.data.TrectextCollection;
import com.pitt.isr.pre.process.data.WordNormalizer;
import com.pitt.isr.pre.process.data.WordTokenizer;

public class DocumentReader {

	public static void main(String[] args) throws Exception {
		// main entrance
		long startTime=System.currentTimeMillis(); //star time of running code
		DocumentReader hm1 = new DocumentReader();
		hm1.PreProcess("trectext");//1.96min
		long endTime=System.currentTimeMillis(); //end time of running code
		System.out.println("Index text corpus, running time: "+(endTime-startTime)/60000.0+" min");

		createIndex();
	}

	private static void createIndex() throws IOException {
		
		long startTime=System.currentTimeMillis();
		long endTime=System.currentTimeMillis();

		startTime=System.currentTimeMillis();
		
			WriteIndex("trectext");
		
		endTime=System.currentTimeMillis();
		System.out.println("index text corpus running time: "+(endTime-startTime)/60000.0+" min");
		startTime=System.currentTimeMillis();
		//indexAndDictionaryFileCreation.ReadIndex("trectext", "yhoo");
		endTime=System.currentTimeMillis();
		System.out.println("load index & retrieve running time: "+(endTime-startTime)/60000.0+" min");
	
		
	}

	private static void WriteIndex(String dataType) throws IOException {

		// Initiate pre-processed collection file reader
		PreProcessedCorpusReader corpus=new PreProcessedCorpusReader(dataType);

		// initiate the output object
		MyIndexWriter output=new MyIndexWriter(dataType);

		// initiate a doc object, which will hold document number and document content
		Map<String, Object> doc = null;

		int count=0;
		// build index of corpus document by document
		while ((doc = corpus.NextDocument()) != null) {
			// load document number and content of the document
			String docno = doc.keySet().iterator().next();
			char[] content = (char[]) doc.get(docno);

			// index this document
			output.IndexADocument(docno, content);

			count++;
			if(count%30000==0)
				System.out.println("finish "+count+" docs");
		}
		System.out.println("totaly document count:  "+count);
		output.Close();
			
	}

	public void ReadIndex(String dataType, String token) throws Exception {
		// Initiate the index file reader
		MyIndexReader idxreader=new MyIndexReader(dataType);

		// conduct retrieval
		int df = idxreader.GetDocFreq(token);
		long ctf = idxreader.GetCollectionFreq(token);
		System.out.println(" >> the token \""+token+"\" appeared in "+df+" documents and "+ctf+" times in total");
		if(df>0){
			int[][] posting = idxreader.GetPostingList(token);
			for(int ix=0;ix<posting.length;ix++){
				int docid = posting[ix][0];
				int freq = posting[ix][1];
				String docno = idxreader.GetDocno(docid);
				System.out.printf("    %20s    %6d    %6d\n", docno, docid, freq);
			}
		}
		idxreader.Close();
	}
	public void PreProcess(String collectionType) throws Exception {
		// Loading the collection file and initiate the DocumentCollection class
		DocumentCollection corpus = null;

		corpus = new TrectextCollection();


		// loading stopword list and initiate the StopWordRemover and WordNormalizer class
		StopWordRemover stopwordRemoverObj = new StopWordRemover();
		WordNormalizer normalizerObj = new WordNormalizer();

		// initiate the BufferedWriter to output result
		FileWriter wr = new FileWriter(Path.ResultHM1 + collectionType);

		// initiate a doc object, which can hold document number and document content of a document

		// process the corpus, document by document, iteractively
		int count=0;
		Map<String, String>  doc = corpus.nextDocument();
		for (Entry<String, String> entry : doc.entrySet()) {
			//while ((doc = corpus.nextDocument()) != null) {
			// load document number of the document
			String docno = entry.getKey();
			if(docno!=null) {

				// load document content
				String content =  entry.getValue();
				System.out.println("doc no:  "+docno.toString() +" content: " +content.toString());
				// write docno into the result file
				wr.append(docno + "\n");

				// initiate the WordTokenizer class
				WordTokenizer tokenizer = new WordTokenizer(content);

				// initiate a word object, which can hold a word
				char[] word = null;

				// process the document word by word iteratively
				while ((word = tokenizer.nextWord()) != null) {
					// each word is transformed into lowercase
					word = normalizerObj.lowercase(word);

					// filter out stopword, and only non-stopword will be written
					// into result file
					if (!stopwordRemoverObj.isStopword(word))
						wr.append(normalizerObj.stem(word) + " ");
					//stemmed format of each word is written into result file
				}

				wr.append("\n");// finish processing one document
				count++;
				if(count%10000==0)
					System.out.println("Finish "+count+" docs");
			}
		}
		System.out.println("Totaly document count:  "+count);
		wr.close();
	}
}
