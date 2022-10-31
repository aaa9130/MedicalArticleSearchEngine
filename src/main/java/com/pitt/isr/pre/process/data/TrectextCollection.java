package com.pitt.isr.pre.process.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.pitt.isr.config.Path;

public class TrectextCollection implements DocumentCollection {

	//declaring and initializing buffer reader object
	private BufferedReader bufferReader = null;

	public TrectextCollection() throws IOException {


		FileInputStream fis = new FileInputStream(Path.DataTextDir);//reading filePath
		bufferReader = new BufferedReader(new InputStreamReader(fis));

	}

	public Map<String, String> nextDocument() throws IOException {

		//Declaring and initializing variables


		Map<String, String> docIdTextMap = new HashMap<String, String>(); //map for docId and docText 
		String line = ""; 

		while ((line = bufferReader.readLine())!=null && !line.equals("</PubmedArticleSet>")) {

			String pmId = ""; 
			String docText = ""; 

			// reading lines till <PMID> tag
			while (line!=null && !line.contains("<PMID")) { 
				line = bufferReader.readLine();
			}

			//Extract PMID once <PMID> tag found
			if (line!=null && line.contains("<PMID")) { 
				line = line.trim();
				//Extracting PMID
				pmId = line.substring(18, line.indexOf("/")-1); 

			}

			// reading lines till <Title> tag
			while (line!=null && !line.contains("<Title>")) { 
				line = bufferReader.readLine();
			}

			//append Title 
			if (line!=null && line.contains("<Title>")) {
				line = line.trim();
				docText += line.substring(7, line.indexOf("/")-1);


			}
			// reading lines till <ArticleTitle> tag
			while (line!=null && !line.contains("<ArticleTitle>")) { 
				line = bufferReader.readLine();
			}

			//append ArticleTitle
			if (line!=null && line.contains("<ArticleTitle>")) {
				line = line.trim();
				docText += " " + line.substring(14, line.indexOf("/")-1); 

			}


			// reading lines till <NameOfSubstance tag and extracting name
			while(line!=null && !line.trim().equals("</ChemicalList>")) {

				while (line!=null && !line.contains("<NameOfSubstance")) { 
					line = bufferReader.readLine();
					if(line.trim().equals("</ChemicalList>")) {
						break;
					}
				}

				if (line!=null && line.contains("<NameOfSubstance")) {
					line = line.trim();
					line = line.substring(30, line.indexOf("/")-1);
					docText += " " + line; 

				} 

			}
			if(!pmId.isEmpty()) {
				docIdTextMap.put(pmId, docText); //populating values in map, PMID and CONTENT
			}
		}

		return docIdTextMap;
	}
}