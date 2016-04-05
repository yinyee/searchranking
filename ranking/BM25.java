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
public class BM25 extends Hashtable<Integer, Hashtable<Integer, Result>> {
	
	private static final float b = 0.75f;
	private static final float k = 1.5f;
	
	private static final String q0 = "Q0";
	private static final String bm25 = "BM25b0.75";
	
	public void getResults(InvertedIndex index, Hashtable<Integer, Topic> topicCollection, Hashtable<String, Document> documentCollection) throws FileNotFoundException, UnsupportedEncodingException {
		
		float averageDocLength = this.getAverageDocLength(documentCollection);
		
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
			
			Hashtable<Integer, Result> results = new Hashtable<Integer, Result>();
			
			for (int i = 0; i < sorted.size(); i++) {
				
				Entry<Document, Double> entry = sorted.get(i);
				Document document = entry.getKey();
				double score = entry.getValue();
				
				Result result = new Result(document, score, i);
				
				results.put(i, result);
				
			}
			
			this.put(topicID, results);
			
		} // no more topics to process
		
		this.printResults();
		
	}
	
	private float getAverageDocLength(Hashtable<String, Document> documentCollection) {
		
		Iterator<Entry<String, Document>> itrDocuments = documentCollection.entrySet().iterator();
		
		int totalDocCount = documentCollection.size();
		long totalDocLength = 0;
		
		while (itrDocuments.hasNext()) {
			totalDocLength += itrDocuments.next().getValue().getDocLength();
		}
		
		float averageDocLength = totalDocLength / totalDocCount;
		
//		System.out.println("length of all documents combined: " + totalDocLength);	// 6,480,612
//		System.out.println("average length of document: " + averageDocLength);		// 1,365
		
		return averageDocLength;
		
	}
	
		
	private void printResults() throws FileNotFoundException, UnsupportedEncodingException {
		
		Iterator<Integer> itrTopics = this.keySet().iterator();

		String space = " ";
		String newline = "\n";
		
		StringBuilder str = new StringBuilder();
		
		// Iterate over topics
		while (itrTopics.hasNext()) {
			
			int topicID = itrTopics.next();
			
			Hashtable<Integer, Result> results = this.get(topicID);
			Iterator<Entry<Integer, Result>> itrResults = results.entrySet().iterator();
			
			while (itrResults.hasNext()) {
				
				Entry<Integer, Result> entry = itrResults.next();
				
				str.append(String.valueOf(topicID));
				str.append(space);
				str.append(BM25.q0);
				str.append(space);
				str.append(entry.getValue().getDocument().getDocNo());
				str.append(space);
				str.append(entry.getKey());
				str.append(space);
				str.append(entry.getValue().getScore());
				str.append(space);
				str.append(BM25.bm25);
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