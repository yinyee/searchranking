package ranking;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * Discounted Cumulative Gain, DCG(k) = rel(1) + sum_{i=2}^{k} frac { rel(i) / log_2 (i) }
 * where	i		= position
 * 			rel(i)	= relevance of result at position i
 * 
 * Normalised DCG, NDCG(k) = DCG(k) / IDCG(k)
 * where	IDCG(k)	= the DCG(k) for the ideal ranking
 * 			ideal ranking	=  ranking sorted by relevance
 * 
 * Output format:
 * 		model
 * e.g.	k	|	NDCG(k)
 * 		1	|	0.45
 * 		5	| 	0.56
 * 
 * @author yinyee
 *
 */
public class NDCG {
	
	private BM25 bm25;
	private Hashtable<Integer, Topic> topicCollection;
	
	public NDCG(BM25 bm25, Hashtable<Integer, Topic> topicCollection) {
		this.bm25 = bm25;
		this.topicCollection = topicCollection;
	}
	
	public double atK(int position) {
		
		Iterator<Integer> itrTopics = bm25.keySet().iterator();
		
		double ndcg = 0;
		
		// Calculate NDCG at k for every topic
		while (itrTopics.hasNext()) {
			
			int topicID = itrTopics.next();
			Hashtable<Integer, Result> results = bm25.get(topicID);
			
			double dcg = 0;
			
			for (int i = 0; i < position; i++) {
				
				Result result = results.get(i);
				Document document = result.getDocument();
				Hashtable<Integer, Integer> relevances = document.getRelevances();
				
				int relevance;
				
				if (relevances.containsKey(topicID)) {
					relevance = relevances.get(topicID);
				} else {
					relevance = 0;
				}
				
				double numerator = Math.pow(2, relevance) - 1;
				double denominator = Math.log10(i + 2) / Math.log10(2);
				
				dcg += numerator / denominator;
				
			}
			
			Topic topic = topicCollection.get(topicID);
			
			ndcg += dcg / idcgAtK(topic, position);
			
		} // finished processing all topics
		
		int numberOfTopics = bm25.size();
		
		double ndcgk = ndcg / numberOfTopics;		// Get average over all topics
		
		return ndcgk;
		
	}
	
	private double idcgAtK(Topic topic, int position) {
		
		int numberOfTwos = topic.getTwo();
		int numberOfOnes = topic.getOne();
		
		double idcg = 0;
		
		for (int i = 0; i < position; i++) {
			
			int relevance;
			
			if (numberOfTwos > 0) {
				relevance = 2;
				numberOfTwos--;
			} else if (numberOfOnes > 0) {
				relevance = 1;
				numberOfOnes--;
			} else {
				relevance = 0;
			}
			
			double numerator = Math.pow(2, relevance);
			double denominator = Math.log10(i + 2) / Math.log10(2);
			
			idcg += numerator / denominator;
			
		}
		
		return idcg;
		
	}


}
