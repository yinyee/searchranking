package ranking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Main {
	
	static double averageDocLength;
	
	public static void main (String[] args) {
		
		// DOCUMENT OBJECT
		// String docNo
		// long sumOfTermFrequency (aka length of document)
		// ArrayList<term, term frequency>
		// ArrayList<topicID, bm25score, bm25rank, ndcgscore, mmrscore>
		
		// PRE-PROCESS DOCUMENTS
		// create a Dictionary called DocumentCollection
		// read document term vectors file
		// for each line,
		//		create an ArrayList<term, term frequency>
		//		create a Document object
		//		add key = docNo and value = Document object
		
		Hashtable<String, Document> documentCollection = new Hashtable<String, Document>();
		
		try {			
			BufferedReader reader = new BufferedReader(new FileReader("../data/document_term_vectors.dat"));
			
			String line;
			Document doc;
			String docNo;
			long docLength;
			double totalDocLength = 0;
			int totalDocCount = 0;
			Term term;
			int termID, termFrequency;
			Hashtable<Integer,Term> terms = new Hashtable<Integer, Term>();
			
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokeniser = new StringTokenizer(line);
				docNo = tokeniser.nextToken();
				docLength = 0;
				while (tokeniser.hasMoreTokens()) {
					termID = Integer.parseInt(tokeniser.nextToken("\\s"));
					termFrequency = Integer.parseInt(tokeniser.nextToken(":"));
					docLength += termFrequency;
					term = new Term(termID, termFrequency);
					terms.put(termID, term);	
				}
				doc = new Document(docNo, docLength, terms);
				documentCollection.put(docNo, doc);	
				totalDocLength += docLength;
				totalDocCount += 1;
			}
			averageDocLength = totalDocLength / totalDocCount;
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// PRE-PROCESS QUERIES
		// create a Dictionary called QueryCollection
		// read query term vectors file
		// for each line, add key = topicID and value = ArrayList<term, term frequency>
		
		Hashtable<Integer, Topic> topicCollection = new Hashtable<Integer, Topic>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("../data/query_term_vectors.dat"));
			
			String line;
			Topic query;
			int topicID;
			ArrayList<Term> terms = new ArrayList<Term>();
			Term term;
			int termID;
			
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokeniser = new StringTokenizer(line);
				topicID = Integer.parseInt(tokeniser.nextToken());
				while (tokeniser.hasMoreTokens()) {
					termID = Integer.parseInt(tokeniser.nextToken("\\s"));
					term = new Term(termID, 1);
					terms.add(term);
				}
				query = new Topic(topicID, terms);
				topicCollection.put(topicID, query);
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// CREATE INVERTED INDEX
		InvertedIndex index = InvertedIndex.getInstance(documentCollection, topicCollection);
		
		// CALCULATE BM25 SCORES
		
		 
		
		
		// PRE-PROCESS RELEVANCE JUDGEMENTS
		// read qrels adhoc file
		// create a Dictionary called IDCG with key = topicID
		// for each topicID from QueryCollection, create an ArrayList called Unsorted
		// for each line,
		//		use docNo to retrieve the document from DocumentCollection
		//		if negative, adjust relevance
		//		retrieve rank from document
		//		compute partial dcg
		// find max for Unsorted and compute IDCG
		// add value = IDCG to IDCG
		
		// CALCULATE NORMALISED DISCOUNTED CUMULATIVE GAIN
		// create a Dictionary called NDCG with key = topicID and value = null
		// for each k, create an ArrayList called K
		// create a Dictionary called NDCGK with key = k and value = K
		// for each topicID from QueryCollection,
		//		float idcg
		// 		float cumulative
		// 		for each rank from Results,
		// 			retrieve document
		//			retrieve partial dcg
		//			compute ndcgscore
		//			if rank = {k}, add ndcgscore to K
		
		// CALCULATE COSINE SIMILARITY AND PEARSON'S COEFFICIENT
		// create a Dictionary called Similarities with key = doc_1 || doc_2
		// for each pair of documents in DocumentCollection,
		//		for each term in common,
		//			compute product of frequencies and squares of individual frequencies (cosine similarity)
		//			compute product of (frequency - mean term frequency) and squares (Pearson's coefficient)
		//		compute cosine similarity
		//		compute Pearson's coefficient
		//		create a Similarity object to store cosine similarity and Pearson's coefficient
		// add value = Similarity object to Similarities
		
		// CALCULATE MAXIMUM MARGINAL RELEVANCE AND PORTFOLIO DIVERSITY
		// for each topicID from BM25,
		//		retrieve BM25Results
		// 		create a Dictionary called MMRResults with key = rank and value = null
		//		create a Dictionary called PDResults with key = rank and value = null
		//		create a Dictionary called UnsortedMMR
		//		create a Dictionary called UnsortedPD
		//		add top-ranked result from BM25Results to MMRResults
		//		add top-ranked result from BM25Results to PDResults
		//		for each document in BM25Results,
		//			retrieve cosine similarity with documents in MMRResults
		//			retrieve Pearson's coefficient with documents in PDResults
		//			find max cosine similarity and compute mmrscore
		//			add key = mmrscore and value = Document object to UnsortedMMR
		//			compute pdscore
		//			add key = pdscore and value = Document object to UnsortedPD
		//		find max from UnsortedMMR and add to MMRResults
		//		find max from UnsortedPD and add to PDRresults
		

		
		
	}

}
