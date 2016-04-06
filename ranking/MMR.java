package ranking;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

public class MMR {
	
	private BM25 bm25;
	
	public MMR(BM25 bm25) {
		this.bm25 = bm25;
	}
	
	public void calculateMMR(float[] lambda) {
		
		for (int i = 0; i < lambda.length; i++) {
			
			Iterator<Entry<Integer, Hashtable<Integer, Result>>> itrTopics = bm25.entrySet().iterator();
			
			while (itrTopics.hasNext()) {
				
				Entry<Integer, Hashtable<Integer, Result>> entry = itrTopics.next();
				int topicID = entry.getKey();
				
				Document anchor = entry.getValue().get(0).getDocument();	// Get the first document
				
				
				
				
				
				
			} // no more topics to process
			
		} // finished for all lambda
		
	}

}
