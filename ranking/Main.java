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
		
		// PRE-PROCESS DOCUMENTS
		Hashtable<String, Document> documentCollection = new Hashtable<String, Document>();
		
		try {
			
			String path = Main.class.getClassLoader().getResource("data/").getPath();
			BufferedReader reader = new BufferedReader(new FileReader(path + "document_term_vectors.dat"));
			
			String line;
			
			Document doc;
			String docNo;
			long docLength;
			
			Hashtable<Integer, Term> terms = new Hashtable<Integer, Term>();
			Term term;
			String sTermID, sTermFrequency;
			int termID, termFrequency;
			
			while ((line = reader.readLine()) != null) {
				
				StringTokenizer tokeniser = new StringTokenizer(line);
				
				docNo = tokeniser.nextToken(" :");
//				System.out.println(docNo);
				docLength = 0;
				
				while (tokeniser.hasMoreTokens()) {
					
					sTermID = tokeniser.nextToken();
					sTermID = sTermID.trim();
					termID = Integer.parseInt(sTermID);
					
					sTermFrequency = tokeniser.nextToken();
					sTermFrequency = sTermFrequency.trim();
					termFrequency = Integer.parseInt(sTermFrequency);
					
					docLength += Integer.parseInt(sTermFrequency);
					
//					System.out.println(termID);
//					System.out.println(termFrequency);
//					System.out.println("docLength = " + docLength);
					
					term = new Term(termID, termFrequency);
					terms.put(termID, term);
					
				}
				
				doc = new Document(docNo, docLength, terms);
				documentCollection.put(docNo, doc);	
				
			}
			
			
//			System.out.println(totalDocLength);
			
			reader.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
//		System.out.println(documentCollection.size());
		
		// PRE-PROCESS QUERIES
		Hashtable<Integer, Topic> topicCollection = new Hashtable<Integer, Topic>();
		
		try {
			
			String path = Main.class.getClassLoader().getResource("data/").getPath();
			BufferedReader reader = new BufferedReader(new FileReader(path + "query_term_vectors.dat"));
			
			String line;
			
			ArrayList<Term> terms = new ArrayList<Term>();
			Topic query;
			Term term;
			String sTopicID, sTermID;
			int topicID, termID;
			
			while ((line = reader.readLine()) != null) {
				
				StringTokenizer tokeniser = new StringTokenizer(line);
				
				sTopicID = tokeniser.nextToken(" :");
				sTopicID = sTopicID.trim();
				topicID = Integer.parseInt(sTopicID);
				
//				System.out.println(topicID);
				
				while (tokeniser.hasMoreTokens()) {
					
					sTermID = tokeniser.nextToken();
					sTermID = sTermID.trim();
					termID = Integer.parseInt(sTermID);
					
//					System.out.println(termID);
					
					tokeniser.nextToken();			// Discard the term frequency for queries
					
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
		
//		System.out.println(topicCollection.size());
		
		// CREATE INVERTED INDEX
		InvertedIndex index = InvertedIndex.getInstance(documentCollection, topicCollection);
//		System.out.println(index.size());
		
		// CALCULATE BM25 SCORES
		BM25Model bm25model = BM25Model.getInstance(index, topicCollection, documentCollection);
		bm25model.printResults();
		
		
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
