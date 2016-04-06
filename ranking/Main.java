package ranking;

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
		
//		MMR mmr = new MMR("top100.txt", documentCollection);
//		mmr.getRankings(0.25);
//		mmr.getRankings(0.5);
		
//		PortfolioScoring portfolio = new PortfolioScoring("top100.txt", documentCollection);
//		portfolio.getRankings(4);
//		portfolio.getRankings(-4);
				
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

}