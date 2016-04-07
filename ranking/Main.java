package ranking;
/**
 * Please un-comment each section to run. 
 * Each section takes under a minute, if run separately.
 * Results are stored in bin/data/ folder.
 * 
 * @author yinyee
 *
 */
public class Main {
	
	static double averageDocLength;
	
	public static void main (String[] args) {
		
		long startTime = System.currentTimeMillis();

		DocumentCollection documentCollection = new DocumentCollection("document_term_vectors.dat");
		
		/**
		 *  Question 1: Implement Okapi BM25 [7 marks]
		 *  <ul> Output is stored as "BM25Model" </ul>
		 */
//		BM25 myBM25 = calculateBM25(documentCollection);
		
		/**
		 * Question 2: Implement Normalised Discounted Cumulative Gain @ k [5 marks]
		 * <ul> Output using my own BM25 results is stored as "myBM25_ndcg" </ul>
		 * <ul> Output using the given BM25 results is sored as "bm25_ndcg" </ul>
		 */
//		int[] ks = {1, 5, 10, 20, 30, 40, 50};		// Need to un-comment this line to run alpha NDCG as well
		
//		calculateNDCG(myBM25, ks, "myBM25_ndcg");
		
//		BM25 givenBM25 = new BM25("BM25b0.75_0.res", documentCollection);
//		calculateNDCG(givenBM25, ks, "bm25_ndcg");
		
		/**
		 * Question 3: Implement Maximum Marginal Relevance [10 marks]
		 * <ul> Output using lambda = 0.25 is stored as "MMRScoring_0.25" </ul>
		 * <ul> Output using lambda = 0.5 is stored as "MMRScoring_0.5" </ul>
		 */
//		MMR mmr = new MMR("top100.txt", documentCollection);
//		mmr.getRankings(0.25);
//		mmr.getRankings(0.5);
		
		/**
		 * Question 4: Implement Portfolio Theory Based Diversification [10 marks]
		 * <ul> Output using b = 4.0 is stored as "PortfolioScoring_4.0" </ul>
		 * <ul> Output using b = -4.0 is stored as "PortfolioScoring_-4.0" </ul>
		 */
//		PortfolioScoring portfolio = new PortfolioScoring("top100.txt", documentCollection);
//		portfolio.getRankings(4);
//		portfolio.getRankings(-4);
		
		/**
		 * Question 5: Implement alpha NDCG @ k [5 marks]
		 * <ul> Output using alpha = 0.1 is stored as "model_alphaNDCG_0.1" </ul>
		 * <ul> Output using alpha = 0.5 is stored as "model_alphaNDCG_0.5" </ul>
		 * <ul> Output using alpha = 0.9 is stored as "model_alphaNDCG_0.9" </ul>
		 */
//		double[] alphas = {0.1, 0.5, 0.9}; 
//		calculateAlphaNDCG(documentCollection, ks, alphas, "MMRScoring_0.25");
//		calculateAlphaNDCG(documentCollection, ks, alphas, "MMRScoring_0.5");
//		calculateAlphaNDCG(documentCollection, ks, alphas, "PortfolioScoring_4.0");
//		calculateAlphaNDCG(documentCollection, ks, alphas, "PortfolioScoring_-4.0");
		
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
	
	static void calculateAlphaNDCG(DocumentCollection documentCollection, int[] ks, double[] alphas, String file) {
		
		SubtopicCollection subtopicCollection = new SubtopicCollection("ndeval.txt", documentCollection);
		
		AlphaNDCG alphaNDCG = new AlphaNDCG(file, documentCollection, subtopicCollection);
		alphaNDCG.outputToFile(ks, alphas, file);
		
	}

}