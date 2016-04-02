package ranking;

/**
 * Input format for document term vectors:
 * 		docID	termID:termFrequency [termID:termFrequemcy]
 * 
 * Input format for query term vectors:
 * 		topicID	termID:termFrequency [termID:termFrequency]
 * 
 * Calculate:
 * 		docScore(D,Q) = sum_{i=1}^{n} IDF(qi) x frac {{ TF_qi=(termFrequency of termID=qi in docID=D) x (1 + k) } / { TF_qi + k x (1 - b + b x numberOfWordsInDoc(D) / averageDocLength)
 * where
 * 		b = 0.75 and k = 1.5
 * and where
 * 		IDF(qi) = log ( frac { N - n(qi) + 0.5 } / { n(qi) + 0.5 } )
 * and where
 * 		N = total number of documents in the collection
 * 		n(qi) = number of documents containing qi
 * and where
 * 		docRank = from 0 to N
 * 
 * Output format:
 * 		topicID	q0	docID	docRank	docScore	bm25
 * where q0 and bm25 are constants
 * 
 * @author yinyee
 *
 */

public class BM25Model {
	
	private static final float b = 0.75f;
	private static final float k = 1.5f;
	private static final String q0 = "Q0";
	private static final String bm25 = "BM25";
	
	
	
	public String calculate(String docID, Query query) {
		
		StringBuilder str = new StringBuilder();
		
		float IDF;
		for (int i = 1; i < query.get().size(); i++) {
			
		}
		
		
		
		
		return ""; 
	}

}
