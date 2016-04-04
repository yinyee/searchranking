package ranking;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class InvertedIndex extends Hashtable<Integer, IndexItem> {
	
	private static InvertedIndex instance = null;
	
	public InvertedIndex(Hashtable<String, Document> documentCollection, Hashtable<Integer, Topic> topicCollection) {
		
		this.getLexicon(topicCollection);
		this.buildIndex(documentCollection);
		this.calculateIDF(documentCollection);
		
	}

	public static InvertedIndex getInstance(Hashtable<String, Document> documentCollection, Hashtable<Integer, Topic> topicCollection) {
		
		if (instance == null)
			instance = new InvertedIndex(documentCollection, topicCollection);
		
		return instance;
	}
	
	private void getLexicon(Hashtable<Integer, Topic> topicCollection) {
		
		Iterator<Entry<Integer, Topic>> itrTopics = topicCollection.entrySet().iterator();
		
		while (itrTopics.hasNext()) {					// Only unique terms will remain after this loop has finished running
			
			Iterator<Word> itrWords = itrTopics.next().getValue().getTerms().iterator();
			
			while (itrWords.hasNext()) {				// While there are more words in this topic
				
				int wordID = itrWords.next().getID();
				Hashtable<String, Find> docs = new Hashtable<String, Find>();
				
				IndexItem item = new IndexItem(wordID, 0, docs);
				this.put(wordID, item);
				
			}
		}
	}
	
	private void buildIndex(Hashtable<String, Document> documentCollection) {
		
		Iterator<Entry<String, Document>> itrDocuments = documentCollection.entrySet().iterator();

		while (itrDocuments.hasNext()) {						
			
			Document document = itrDocuments.next().getValue();
			String docNo = document.getDocNo();
			
			Iterator<Entry<Integer, Word>> itrWords = document.getWords().entrySet().iterator();
			
			while (itrWords.hasNext()) {
				
				Entry<Integer, Word> entry = itrWords.next();						// Get next word from list of words
				int wordID = entry.getKey();
				
				if (this.containsKey(wordID)) {										// If word is found in topicCollection
					
					IndexItem item = this.get(wordID);								// Get associated index item
					
					int docFrequency = item.getDocFrequency() + 1;					// Increment document frequency
					item.setDocFrequency(docFrequency);
					
					int wordFrequency = entry.getValue().getFrequency();
					
					Find find = new Find (docNo, wordFrequency);
					
					Hashtable<String, Find> docs = item.getDocList();
					
					docs.put(docNo, find);									
				
				}
			}
		}
	}
	
	private void calculateIDF(Hashtable<String, Document> documentCollection) {
		
		int numberOfDocuments = documentCollection.size();
		
		Iterator<Entry<Integer, IndexItem>> itrItems = this.entrySet().iterator();
		
		while (itrItems.hasNext()) {
			
			IndexItem item = itrItems.next().getValue();
			int docFrequency = item.getDocFrequency();
			double numerator = numberOfDocuments - docFrequency + 0.5;
			double denominator = docFrequency + 0.5;
			double idf = Math.log(numerator/denominator);
			item.setIDF(idf);
			
		}
		
	}
	
}