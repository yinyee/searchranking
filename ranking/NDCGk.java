package ranking;

/**
 * Normalised Discounted Cumulative Gain
 * 
 * Input format for BM25 results:
 * 		topicID	|	q0	|	docID	|	docRank	|	docScore	| bm25
 * where	docRank		= from 0 to 999
 * 
 * Input format for relevance judgements:
 * 		topicID	| intent	|	docID	|	relevanceScore
 * where	relevanceScore	= -2 to 2
 * 
 * Possible format for intermediate output:
 * 		topicID	| docID	|	docRank	|	re-scaled relevanceScore
 * where 	re-scaled relevanceScore	= 0 to 2
 * 
 * Discounted Cumulative Gain, DCG(k) = rel(1) + sum_{i=2}^{k} frac { rel(i) / log_2 (i) }
 * where	i		= position
 * 			rel(i)	= relevance of result at position i
 * 
 * Normalised DCG, NDCG(k) = DCG(k) / IDCG(k)
 * where	IDCG(k)	= the DCG(k) for the ideal ranking
 * 			ideal ranking	=  ranking sorted by relevance
 * 
 * Calculation steps:
 * 		1. Merge inputs on topicID and docID; re-scale relevance judgements and attach to each line
 * 		2. For each topicID, 
 * 		For each docID, 
 * 		3. For each k, compute the average of NDCG at k across all topics
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
public class NDCGk {
	
	// Merge two inputs together
	
	// docRank is from 0 to 999
	// for each topicID, compute

}
