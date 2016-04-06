package ranking;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class Main {
	
	static double averageDocLength;
	
	public static void main (String[] args) {
		
		long startTime = System.currentTimeMillis();

		DocumentCollection documentCollection = new DocumentCollection("document_term_vectors.dat");
//		int[] ks = {1, 5, 10, 20, 30, 40, 50};
//		
//		BM25 myBM25 = calculateBM25(documentCollection);
//		calculateNDCG(myBM25, ks, "myBM25_ndcg.txt");
		
//		BM25 givenBM25 = new BM25("BM25b0.75_0.res", documentCollection);
//		calculateNDCG(givenBM25, ks, "bm25_ndcg.txt");
		
		try {
			calculateSimilarities(documentCollection);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
//		Document document = documentCollection.get("clueweb12-0905wb-50-14578");
//		System.out.println(document.getSimilarities().get("clueweb12-1700tw-22-12689").getCosine());
//		System.out.println(document.getSimilarities().get("clueweb12-1700tw-22-12689").getPearson());
		
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
		
		System.out.println("Run time: " + ((System.currentTimeMillis() - startTime) / 1000));
		
	}
	
	static BM25 calculateBM25(DocumentCollection documentCollection) {
		
		TopicCollection topicCollection = new TopicCollection("query_term_vectors.dat");

		BM25 bm25 = new BM25(documentCollection);
		
		try {
			bm25.getResults(topicCollection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bm25;
		
	}
	
	static void calculateNDCG(BM25 bm25, int[] ks, String file) {
		
		TopicCollection topicCollection = new TopicCollection("query_term_vectors.dat");
		
		Relevance relevance = new Relevance();
		relevance.loadJudgements("adhoc.txt", bm25.documentCollection, topicCollection);
		
		NDCG ndcg = new NDCG(bm25, topicCollection);
		ndcg.outputToFile(ks, file);
		
	}
	
	static void calculateSimilarities(DocumentCollection documentCollection) throws FileNotFoundException, UnsupportedEncodingException {
		
		Diversity diversity = new Diversity("top100.txt", documentCollection);
		diversity.calculateDiversity();
		
	}

}