package ranking;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
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
		
		// Iterate through queryCollection and add unique terms to index
		while (itrTopics.hasNext()) {
			
			Iterator<Term> itrTerms = itrTopics.next().getValue().getTerms().iterator();
			
			while (itrTerms.hasNext()) {
				
				termID = (int) itrTerms.next().getID();
				
				if (!(this.containsKey(termID))) {
					item = new IndexItem(termID, docFrequency, docTermFrequencies);
					this.put(termID, item);
				}
				
			}
			
		}
		
		Set<Entry<Integer, Term>> docTerms;
		Entry<Integer, Term> entry;
		Hashtable<String, Term> docs;
		Term docTerm;
		Document document;
		String docNo;
		int termFrequency;
		
		// Iterate through documentCollection and match docNo and termFrequency to index key
		Iterator<Entry<String, Document>> itrDocuments = documentCollection.entrySet().iterator();
		
		while (itrDocuments.hasNext()) {
			
			document = itrDocuments.next().getValue();			// Get next document from documentCollection
			docTerms = document.getTerms().entrySet();			// Get list of words found in document
			
			// Iterate through the terms in the document
			Iterator<Entry<Integer, Term>> itrTerms = docTerms.iterator();
			
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