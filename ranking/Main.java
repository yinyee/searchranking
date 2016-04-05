package ranking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class Main {
	
	static double averageDocLength;
	
	public static void main (String[] args) {
		
		String path = Main.class.getClassLoader().getResource("data/").getPath();
		
		// PRE-PROCESS DOCUMENTS
		Hashtable<String, Document> documentCollection = new Hashtable<String, Document>();
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(path + "document_term_vectors.dat"));
			
			String line;
			
			Document doc;
			String docNo;
			long docLength;
			
			Hashtable<Integer, Word> words;
			Word word;
			String sTermID, sTermFrequency;
			int termID, termFrequency;
			
			while ((line = reader.readLine()) != null) {
				
				StringTokenizer tokeniser = new StringTokenizer(line);
				
				docNo = tokeniser.nextToken(" :");
				docNo = docNo.trim();
//				System.out.println(docNo);
				words = new Hashtable<Integer, Word>();
				docLength = 0;
				
				while (tokeniser.hasMoreTokens()) {
					
					sTermID = tokeniser.nextToken();
					sTermID = sTermID.trim();
					termID = Integer.parseInt(sTermID);
					
					sTermFrequency = tokeniser.nextToken();
					sTermFrequency = sTermFrequency.trim();
					termFrequency = Integer.parseInt(sTermFrequency);
					
					docLength += Integer.parseInt(sTermFrequency);
					
//					System.out.println("termID: " + termID);
//					System.out.println("termFrequency: " + termFrequency);
//					System.out.println("docLength = " + docLength);
					
					word = new Word(termID, termFrequency);
					words.put(termID, word);
					
				}
				
				doc = new Document(docNo, docLength, words);
				documentCollection.put(docNo, doc);	
				
			}
			
			reader.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
//		System.out.println(documentCollection.size());
		
		// PRE-PROCESS QUERIES
		Hashtable<Integer, Topic> topicCollection = new Hashtable<Integer, Topic>();
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(path + "query_term_vectors.dat"));
			
			String line;
			
			while ((line = reader.readLine()) != null) {
				
				StringTokenizer tokeniser = new StringTokenizer(line);
				
				String sTopicID = tokeniser.nextToken(" :");
				sTopicID = sTopicID.trim();
				int topicID = Integer.parseInt(sTopicID);
				
				ArrayList<Word> words = new ArrayList<Word>();
				
//				System.out.println(topicID);
				
				while (tokeniser.hasMoreTokens()) {
					
					String sTermID = tokeniser.nextToken();
					sTermID = sTermID.trim();
					int termID = Integer.parseInt(sTermID);
					
//					System.out.println(termID);
					
					tokeniser.nextToken();			// Discard the term frequency for queries
					
					Word word = new Word(termID, 1);
					words.add(word);
					
				}
				
				Topic query = new Topic(topicID, words);
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
		BM25 myBM25 = new BM25();
		try {
			myBM25.getResults(index, topicCollection, documentCollection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// PRE-PROCESS GIVEN BM25 SCORES
		BM25 givenBM25 = new BM25();
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(path + "BM25b0.75_0.res"));
			
			String line;
			
			while ((line = reader.readLine()) != null) {
				
				StringTokenizer tokeniser = new StringTokenizer(line);
				
				while (tokeniser.hasMoreTokens()) {
					
					// Topic ID
					String sTopicID = tokeniser.nextToken();
					sTopicID = sTopicID.trim();
					int topicID = Integer.parseInt(sTopicID);
					
					if (!(givenBM25.containsKey(topicID))) {
						Hashtable<Integer, Result> results = new Hashtable<Integer, Result>();
						givenBM25.put(topicID, results);
					}

					// Skip Q0
					tokeniser.nextToken();
					
					// Document number
					String docNo = tokeniser.nextToken();
					docNo = docNo.trim();
					
					// Rank
					String sRank = tokeniser.nextToken();
					sRank = sRank.trim();
					int rank = Integer.parseInt(sRank);
					
					// Score
					String sScore = tokeniser.nextToken();
					sScore = sScore.trim();
					double score = Double.parseDouble(sScore);
					
					// Skip BM
					tokeniser.nextToken();
				
					Hashtable<Integer, Result> results = givenBM25.get(topicID);
					
					Document document;
					if (documentCollection.containsKey(docNo)) {
						document = documentCollection.get(docNo);
					} else {
						document = new Document(docNo);
						documentCollection.put(docNo, document);
					}
					
					Result result = new Result(document, score, rank);
					results.put(rank, result);
					
				}
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// PRE-PROCESS RELEVANCE JUDGEMENTS
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(path + "adhoc.txt"));
			
			String line;
			
			while ((line = reader.readLine()) != null) {
				
				StringTokenizer tokeniser = new StringTokenizer(line);
				
				while (tokeniser.hasMoreTokens()) {
					
					// Topic ID
					String sTopicID = tokeniser.nextToken();
					sTopicID = sTopicID.trim();
					int topicID = Integer.parseInt(sTopicID);
					
					Topic topic = topicCollection.get(topicID);
					
					// Skip topic intent
					tokeniser.nextToken();
					
					// Document number
					String docNo = tokeniser.nextToken();
					docNo = docNo.trim();

					Document document;
					if (documentCollection.containsKey(docNo)) {
						document = documentCollection.get(docNo);
					} else {
						document = new Document(docNo);
						documentCollection.put(docNo, document);
					}
					
					// Relevance judgment
					String sRelevance = tokeniser.nextToken();
					sRelevance = sRelevance.trim();
					int relevance = Integer.parseInt(sRelevance);
//					System.out.println("relevance: " + relevance);
					
					// Update document with relevance judgment
					Hashtable<Integer, Integer> relevances = document.getRelevances();
					relevances.put(topicID, relevance);
					
					// Update topic relevance counts
					switch (relevance) {
					case 2: {
						int two = topic.getTwo();
						topic.setTwo(two + 1);
						break;
					}
					case 1: {
						int one = topic.getOne();
						topic.setOne(one + 1);
						break;
					}
					}
					
				}		
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// OUTPUT NDCG at K
		NDCG ndcg = new NDCG(givenBM25, topicCollection);
		
		StringBuilder str = new StringBuilder();
		
		String separator = "\t|\t";
		
		str.append("bm25\n");
		str.append("K");
		str.append(separator);
		str.append("NDCG@K\n");
		str.append(1);
		str.append(separator);
		str.append(String.format("%.3g%n", ndcg.atK(1)));
		str.append(5);
		str.append(separator);
		str.append(String.format("%.3g%n", ndcg.atK(5)));
		str.append(10);
		str.append(separator);
		str.append(String.format("%.3g%n", ndcg.atK(10)));
		str.append(20);
		str.append(separator);
		str.append(String.format("%.3g%n", ndcg.atK(20)));
		str.append(30);
		str.append(separator);
		str.append(String.format("%.3g%n", ndcg.atK(30)));
		str.append(40);
		str.append(separator);
		str.append(String.format("%.3g%n", ndcg.atK(40)));
		str.append(50);
		str.append(separator);
		str.append(String.format("%.3g%n", ndcg.atK(50)));

		try {
			
			PrintWriter printer = new PrintWriter(path + "bm25_ndcg.txt", "UTF-8");
			printer.print(str.toString());
			printer.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		
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