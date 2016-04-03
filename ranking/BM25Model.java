package ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class BM25Model extends Hashtable<Integer, Results> {
	
	private static Hashtable<Integer, ArrayList<Entry<Document, Float>>> results = new Hashtable<Integer, ArrayList<Entry<Document, Float>>>();
	private static BM25Model instance = null;
	
	private static final float b = 0.75f;
	private static final float k = 1.5f;
	
	private static final String q0 = "Q0";
	private static final String bm25 = "BM25b0.75";
	
	public BM25Model(InvertedIndex index, Hashtable<Integer, Topic> topicCollection, Hashtable<String, Document> documentCollection) {
		
		Hashtable<Document, Float> unsorted = new Hashtable<Document, Float>();
		
		Iterator<Entry<Integer, Topic>> itrTopics = topicCollection.entrySet().iterator();
		Iterator<Entry<String, Document>> itrDocuments = documentCollection.entrySet().iterator();
		
		Hashtable<String, Term> docList;
		Iterator<Term> itrQueryTerms;
		
		Document document;
		IndexItem item;
		Score bm25score;
		Topic topic;
		Term term;
		
		double idf;
		float score, numerator, denominator;
		long docLength;
		int docFrequency, docTermFrequency;
		
		// Get document collection metrics
		int totalDocCount = documentCollection.size();
		long totalDocLength = 0;
		
		while (itrDocuments.hasNext()) {
			totalDocLength += itrDocuments.next().getValue().getDocLength();
		}
		
		float averageDocLength = totalDocLength / totalDocCount;
		
//		System.out.println("length of all documents combined: " + totalDocLength);	// 6,480,612
//		System.out.println("average length of document: " + averageDocLength);		// 1,365
		
		// While there are more topics in the topic collection
		while (itrTopics.hasNext()) {								
			
			topic = itrTopics.next().getValue();
			topic.getTerms();
			
			itrDocuments = documentCollection.entrySet().iterator();
			itrQueryTerms = topic.getTerms().iterator();
			
			// For every document in the collection
			while (itrDocuments.hasNext()) {						
				
				System.out.println("Inside document loop");
				
				document = itrDocuments.next().getValue();
				score = 0;											// Reset score for new document to zero
				
				// Where there are more query terms in this topic
				while (itrQueryTerms.hasNext()) {		
					
					System.out.println("Inside query terms loop");
					
					item = index.get(itrQueryTerms.next().getID());	// Retrieve the index item associated with this query term
					
					docFrequency = item.getDocFrequency();			// Retrieve the number of documents which contain this query term 
					idf = (double) Math.log((totalDocCount + 0.5 - docFrequency) / (0.5 + docFrequency));
					
					docList = item.getDocList();					// Retrieve the list of documents which contain this query term
					term = docList.get(document.getDocNo());
					
					if (term != null) {								// Check if this document contains this query term
						
						docTermFrequency = docList.get(document.getDocNo()).getFrequency(); // Retrieve the number of times this query term appears in this document
						docLength = document.getDocLength();
						
						numerator = docTermFrequency * (k + 1);
						denominator = docTermFrequency + k * (1 - b + b * docLength / averageDocLength);
						
						score += idf * (numerator / denominator);
						
					}
					
				} // No more query terms in this topic
				
				bm25score = new Score();
				bm25score.setScoreBM25(score);
				
				document.getScores().put(topic.getTopicID(), bm25score);
				unsorted.put(document, score);
				
				System.out.println(topic.getTopicID() + " " + document.getDocNo() + ": " + score);
				
			} // Finished processing all documents for this topic
			
			ArrayList<Entry<Document, Float>> result = new ArrayList<Entry<Document, Float>>(unsorted.entrySet());
			
			System.out.println("empty?" + result.isEmpty());
			
			// Sort the scores in descending order
			Collections.sort(result, new Comparator<Entry<Document, Float>>() {

				@Override
				// Returns negative if o2 < o1 and vice versa
				public int compare(Entry<Document, Float> o1, Entry<Document, Float> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
				
			});
			
			results.put(topic.getTopicID(), result);
			
		} // No more topics to process
		
		System.out.println(results.size());
		
	}

	public static BM25Model getInstance(InvertedIndex index, Hashtable<Integer, Topic> topicCollection, Hashtable<String, Document> documentCollection) {
		
		if (instance == null)
			instance = new BM25Model(index, topicCollection, documentCollection);
		
		return instance;
		
	}
		
	public void printResults() {
		
		Iterator<Integer> itrTopics = results.keySet().iterator();
		ArrayList<Entry<Document, Float>> result;
		
		Iterator<Entry<Document, Float>> itrEntries;
		Entry<Document, Float> entry;
		
		String space = " ";
		int topicID;
		
		StringBuilder str = new StringBuilder();
		
		// Iterate over topics
		while (itrTopics.hasNext()) {
			
			topicID = itrTopics.next();
			result = results.get(topicID);
			
			for (int i = 0; i < result.size(); i++) {
				entry = result.get(i);
				str.append(String.valueOf(topicID));
				str.append(space);
				str.append(BM25Model.q0);
				str.append(space);
				str.append(entry.getKey().getDocNo());
				str.append(space);
				str.append(String.valueOf(i));
				str.append(space);
				str.append(String.valueOf(entry.getValue()));
				str.append(space);
				str.append(BM25Model.bm25);
			}
			
			System.out.println(str.toString());
			
		}
		
	}

}