package ranking;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class BM25Model extends Hashtable<Integer, ArrayList<Entry<Document, Double>>> {
	
	private static BM25Model instance = null;
	
	private static final float b = 0.75f;
	private static final float k = 1.5f;
	
	private static final String q0 = "Q0";
	private static final String bm25 = "BM25b0.75";
	
	private int totalDocCount;
	private float averageDocLength;
	
	public BM25Model(InvertedIndex index, Hashtable<Integer, Topic> topicCollection, Hashtable<String, Document> documentCollection) {
		
		this.getDocumentCollectionMetrics(documentCollection);
		this.getResults(index, topicCollection, documentCollection);
		
	}

	public static BM25Model getInstance(InvertedIndex index, Hashtable<Integer, Topic> topicCollection, Hashtable<String, Document> documentCollection) {
		
		if (instance == null)
			instance = new BM25Model(index, topicCollection, documentCollection);
		
		return instance;
		
	}
	
	private void getResults(InvertedIndex index, Hashtable<Integer, Topic> topicCollection, Hashtable<String, Document> documentCollection) {
		
		// For every topic, there is a ranking of all documents
		// A ranking is a sorted ArrayList<Document, Float>
		
		Iterator<Entry<Integer, Topic>> itrTopics = topicCollection.entrySet().iterator();
		
		while (itrTopics.hasNext()) {
			
			Hashtable<Document, Double> unsorted = new Hashtable<Document, Double>();
			
			Topic topic = itrTopics.next().getValue();
			int topicID = topic.getTopicID();
			
			Iterator<Entry<String, Document>> itrDocuments = documentCollection.entrySet().iterator();
			
			while (itrDocuments.hasNext()) {
				
				Document document = itrDocuments.next().getValue();
				Hashtable<Integer, Word> words = document.getWords();
				
				double score = 0;
				
				Iterator<Word> itrWords = topic.getTerms().iterator();
				
				while (itrWords.hasNext()) {
					
					Word word = itrWords.next();
					int wordID = word.getID();
					
					if (words.containsKey(wordID)) {			// If document contains the word
						
						int wordFrequency = words.get(wordID).getFrequency();
						long docLength = document.getDocLength();
						
						IndexItem item = index.get(wordID);
						double idf = item.getIDF();
						
						double numerator = idf * wordFrequency * (k + 1);
						double denominator = wordFrequency + (k * (1 - b + (b * docLength / averageDocLength)));
						score += numerator / denominator;
						
					}
					
				} // no more words in this topic
			
				unsorted.put(document, score);
				
			} // finished all documents in collection
			
			ArrayList<Entry<Document, Double>> sorted = new ArrayList<Entry<Document, Double>>(unsorted.entrySet());
			
			// Sort the scores in descending order
			Collections.sort(sorted, new Comparator<Entry<Document, Double>>() {

				@Override
				// Returns negative if o2 < o1 and vice versa
				public int compare(Entry<Document, Double> o1, Entry<Document, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
				
			});
			
			this.put(topicID, sorted);
			
		} // no more topics to process
			
	}
	
	private void getDocumentCollectionMetrics(Hashtable<String, Document> documentCollection) {
		
		Iterator<Entry<String, Document>> itrDocuments = documentCollection.entrySet().iterator();
		
		totalDocCount = documentCollection.size();
		long totalDocLength = 0;
		
		while (itrDocuments.hasNext()) {
			totalDocLength += itrDocuments.next().getValue().getDocLength();
		}
		
		averageDocLength = totalDocLength / totalDocCount;
		
		System.out.println("length of all documents combined: " + totalDocLength);	// 6,480,612
		System.out.println("average length of document: " + averageDocLength);		// 1,365

	}
	
		
	public void printResults() throws FileNotFoundException, UnsupportedEncodingException {
		
		Iterator<Integer> itrTopics = this.keySet().iterator();

		String space = " ";
		String newline = "\n";
		
		StringBuilder str = new StringBuilder();
		
		// Iterate over topics
		while (itrTopics.hasNext()) {
			
			int topicID = itrTopics.next();
			ArrayList<Entry<Document, Double>> result = this.get(topicID);
			
			for (int i = 0; i < result.size(); i++) {
				
				Entry<Document, Double> entry = result.get(i);
				
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
				str.append(newline);
				
			}
			
			String path = Main.class.getClassLoader().getResource("data/").getPath();
			PrintWriter printer = new PrintWriter(path + "BM25Model.txt", "UTF-8");
			printer.print(str.toString());
			printer.close();
//			System.out.println(str.toString());
			
		}
		
	}

}