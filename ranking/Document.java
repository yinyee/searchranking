package ranking;

import java.util.Hashtable;

public class Document {
	
	private String docNo;
	private long docLength;
	private Hashtable<Integer, Word> words;
	private Hashtable<Integer, Integer> relevances;
	private Hashtable<String, Similarity> similarities;
	
	public Document(String docNo) {
		this.docNo = docNo;
		relevances = new Hashtable<Integer, Integer>();
		similarities = new Hashtable<String, Similarity>();
	}
	
	public Document(String docNo, long docLength, Hashtable<Integer, Word> words) {
		this.docNo = docNo;
		this.docLength = docLength;
		this.words = words;
		relevances = new Hashtable<Integer, Integer>();
		similarities = new Hashtable<String, Similarity>();
	}
	
	public String getDocNo() {
		return docNo;
	}
	
	public long getDocLength() {
		return docLength;
	}
	
	public Hashtable<Integer, Word> getWords() {
		return words;
	}
	
	public Hashtable<Integer, Integer> getRelevances() {
		return relevances;
	}
	
	public Hashtable<String, Similarity> getSimilarities() {
		return similarities;
	}

}
