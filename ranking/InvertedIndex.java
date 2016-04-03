package ranking;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class InvertedIndex extends Hashtable<Integer, IndexItem> {
	
	private static InvertedIndex instance = null;
	
	public InvertedIndex(Hashtable<String, Document> documentCollection, Hashtable<Integer, Topic> topicCollection) {
		
		Iterator<Entry<Integer, Topic>> itrTopics = topicCollection.entrySet().iterator();
		Hashtable<String, Term> docTermFrequencies = new Hashtable<String, Term>();
		IndexItem item;
		int termID;
		int docFrequency = 0;
		
		// Iterate through queryCollection; only unique terms will remain after this loop has finished running
		while (itrTopics.hasNext()) {
			
			Iterator<Term> itrTerms = itrTopics.next().getValue().getTerms().iterator();
			
			while (itrTerms.hasNext()) {
				
				termID = (int) itrTerms.next().getID();
				item = new IndexItem(termID, docFrequency, docTermFrequencies);
				this.put(termID, item);
				
			}
			
		}
		
		Hashtable<String, Term> docs;
		Document document;
		String docNo;
		Term docTerm;
		
		Entry<Integer, Term> entry;
		int termFrequency;
		
		// Iterate through documentCollection and match docNo and termFrequency to index key
		Iterator<Entry<String, Document>> itrDocuments = documentCollection.entrySet().iterator();
		int i = 0;
		while (itrDocuments.hasNext()) {
			
			document = itrDocuments.next().getValue();
//			System.out.println(i++ + ": " + document.getDocNo() + " " + document.getDocLength());
			
			Iterator<Entry<Integer, Term>> itrTerms = document.getTerms().entrySet().iterator();
			
			while (itrTerms.hasNext()) {
				
				entry = itrTerms.next();						// Get next word from list of words
				termID = entry.getKey();
				
				if (this.containsValue(termID)) {				// If word is found in queryCollection
					
					item = this.get(termID);					// Get associated index item
					
					docFrequency = item.getDocFrequency() + 1;	// Increment document frequency
					item.setDocFrequency(docFrequency);
					
					docNo = document.getDocNo();
					termFrequency = entry.getValue().getFrequency();
					
					docTerm = new Term(docNo, termFrequency);
					
					docs = item.getDocList();
					docs.put(docNo, docTerm);					// Add new entry consisting of (docNo, term frequency in document)
				
				}
			}
		}

	}

	public static InvertedIndex getInstance(Hashtable<String, Document> documentCollection, Hashtable<Integer, Topic> topicCollection) {
		
		if (instance == null)
			instance = new InvertedIndex(documentCollection, topicCollection);
		
		return instance;
	}		
	
}