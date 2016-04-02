package ranking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

public class Main {
	
	public static void main (String[] args) {
		
		// DOCUMENT OBJECT
		// String docNo
		// long sumOfTermFrequency (aka length of document)
		// ArrayList<term, term frequency>
		// ArrayList<topicID, bm25score, bm25rank, ndcgscore, mmrscore>
		
		// PRE-PROCESS DOCUMENTS
		// create a Dictionary called DocumentCollection
		// read document term vectors file
		// for each line,
		//		create an ArrayList<term, term frequency>
		//		create a Document object
		//		add key = docNo and value = Document object
		
		Hashtable<String, Document> documentCollection = new Hashtable<String, Document>();
		
		try {			
			BufferedReader reader = new BufferedReader(new FileReader("../data/document_term_vectors.dat"));
			
			String line;
			Document doc;
			String docNo;
			long docLength;
			Term term;
			int termID, termFrequency;
			Hashtable<Integer,Term> terms = new Hashtable<Integer, Term>();
			
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokeniser = new StringTokenizer(line);
				docNo = tokeniser.nextToken();
				docLength = 0;
				while (tokeniser.hasMoreTokens()) {
					termID = Integer.parseInt(tokeniser.nextToken("\\s"));
					termFrequency = Integer.parseInt(tokeniser.nextToken(":"));
					docLength += termFrequency;
					term = new Term(termID, termFrequency);
					terms.put(termID, term);	
				}
				doc = new Document(docNo, docLength, terms);
				documentCollection.put(docNo, doc);	
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		// PRE-PROCESS QUERIES
		// create a Dictionary called QueryCollection
		// read query term vectors file
		// for each line, add key = topicID and value = ArrayList<term, term frequency>
		
		Hashtable<Integer, Query> queryCollection = new Hashtable<Integer, Query>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("../data/query_term_vectors.dat"));
			
			String line;
			Query query;
			int topicID;
			ArrayList<Term> terms = new ArrayList<Term>();
			Term term;
			int termID;
			
			while ((line = reader.readLine()) != null) {
				StringTokenizer tokeniser = new StringTokenizer(line);
				topicID = Integer.parseInt(tokeniser.nextToken());
				while (tokeniser.hasMoreTokens()) {
					termID = Integer.parseInt(tokeniser.nextToken("\\s"));
					term = new Term(termID, 1);
					terms.add(term);
				}
				query = new Query(topicID, terms);
				queryCollection.put(topicID, query);
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// CREATE INVERTED INDEX
		// create a Dictionary called InvertedIndex
		// iterate over QueryCollection
		// for each unique term, add key = term, value = null
		// iterate over DocumentCollection
		// for each document, if (term exists in document && term exists in QueryCollection) add value = <docNo, Document object> to key = term
		
		Hashtable<Integer, IndexItem> invertedIndex = new Hashtable<Integer, IndexItem>();
		
		Set<Entry<Integer, Query>> queries = queryCollection.entrySet();
		Iterator<Entry<Integer, Query>> itrQueries = queries.iterator();
		int termID;
		ArrayList<Term> queryTerms;
		IndexItem item;
		int docFrequency = 0;
		HashMap<String, DocTermFrequency> docTermFrequencies = new HashMap<String, DocTermFrequency>();
		
		while (itrQueries.hasNext()) {
			queryTerms = itrQueries.next().getValue().getTerms();
			Iterator<Term> itrTerms = queryTerms.iterator();
			while (itrTerms.hasNext()) {
				termID = itrTerms.next().getID();
				if (!(invertedIndex.containsKey(termID))) {
					item = new IndexItem(docFrequency, docTermFrequencies);
					invertedIndex.put(termID, item);
				}
			}
		}
		
		Set<Entry<String, Document>> documents = documentCollection.entrySet();
		Iterator<Entry<String, Document>> itrDocuments = documents.iterator();
		Document document;
		Set<Entry<Integer, Term>> docTerms;
		HashMap<String, DocTermFrequency> docs;
		String docNo;
		Term term;
		Entry<Integer, Term> entry;
		int termFrequency;
		DocTermFrequency docTerm;
		
		while (itrDocuments.hasNext()) {
			document = itrDocuments.next().getValue();			// Get next document from documentCollection
			docTerms = document.getTerms().entrySet();			// Get list of words found in document
			Iterator<Entry<Integer, Term>> itrTerms = docTerms.iterator();
			while (itrTerms.hasNext()) {
				entry = itrTerms.next();						// Get next word from list of words
				termID = entry.getKey();
				if (invertedIndex.containsValue(termID)) {		// If word is found in queryCollection
					item = invertedIndex.get(termID);			// Get associated index item
					docFrequency = item.getDocFrequency() + 1;	// Increment document frequency
					item.setDocFrequency(docFrequency);
					docs = item.getDocList();
					docNo = document.getDocNo();
					termFrequency = entry.getValue().getFrequency();
					docTerm = new DocTermFrequency(docNo, termFrequency);
					docs.put(docNo, docTerm);					// Add new entry consisting of (docNo, term frequency in document)
				}
			}
		}
		
		// CALCULATE BM25 SCORES
		// create a Dictionary called BM25 with key = topicID and value = null
		// for each topicID from QueryCollection, create a Dictionary called Unsorted
		// set constants k and b
		// calculate the average length of documents in DocumentCollection
		// for each topicID,
		// 		using InvertedIndex, calculate the inverse document frequency of terms in the query
		//		from the InvertedIndex, retrieve list of documents which contain the query terms
		//		for each retrieved document,
		//				retrieve the length of the document
		//				retrieve the frequency of the term
		//				compute bm25score and add to Document object
		//				add key = bm25score and value = Document object to Unsorted
		// create a Dictionary called BM25Results for each topicID
		// get keys for Unsorted
		// 		find max
		//		update Document.rank
		//		add to BM25Results with key = rank and value = Document object
		// add value = BM25Results to BM25
		
		// PRE-PROCESS RELEVANCE JUDGEMENTS
		// read qrels adhoc file
		// create a Dictionary called IDCG with key = topicID
		// for each topicID from QueryCollection, create an ArrayList called Unsorted
		// for each line,
		//		use docNo to retrieve the document from DocumentCollection
		//		if negative, adjust relevance
		//		retrieve rank from document
		//		compute partial dcg
		// find max for Unsorted and compute IDCG
		// add value = IDCG to IDCG
		
		// CALCULATE NORMALISED DISCOUNTED CUMULATIVE GAIN
		// create a Dictionary called NDCG with key = topicID and value = null
		// for each k, create an ArrayList called K
		// create a Dictionary called NDCGK with key = k and value = K
		// for each topicID from QueryCollection,
		//		float idcg
		// 		float cumulative
		// 		for each rank from Results,
		// 			retrieve document
		//			retrieve partial dcg
		//			compute ndcgscore
		//			if rank = {k}, add ndcgscore to K
		
		// CALCULATE COSINE SIMILARITY AND PEARSON'S COEFFICIENT
		// create a Dictionary called Similarities with key = doc_1 || doc_2
		// for each pair of documents in DocumentCollection,
		//		for each term in common,
		//			compute product of frequencies and squares of individual frequencies (cosine similarity)
		//			compute product of (frequency - mean term frequency) and squares (Pearson's coefficient)
		//		compute cosine similarity
		//		compute Pearson's coefficient
		//		create a Similarity object to store cosine similarity and Pearson's coefficient
		// add value = Similarity object to Similarities
		
		// CALCULATE MAXIMUM MARGINAL RELEVANCE AND PORTFOLIO DIVERSITY
		// for each topicID from BM25,
		//		retrieve BM25Results
		// 		create a Dictionary called MMRResults with key = rank and value = null
		//		create a Dictionary called PDResults with key = rank and value = null
		//		create a Dictionary called UnsortedMMR
		//		create a Dictionary called UnsortedPD
		//		add top-ranked result from BM25Results to MMRResults
		//		add top-ranked result from BM25Results to PDResults
		//		for each document in BM25Results,
		//			retrieve cosine similarity with documents in MMRResults
		//			retrieve Pearson's coefficient with documents in PDResults
		//			find max cosine similarity and compute mmrscore
		//			add key = mmrscore and value = Document object to UnsortedMMR
		//			compute pdscore
		//			add key = pdscore and value = Document object to UnsortedPD
		//		find max from UnsortedMMR and add to MMRResults
		//		find max from UnsortedPD and add to PDRresults
		

		
		
	}

}
