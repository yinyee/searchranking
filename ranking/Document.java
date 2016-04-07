package ranking;

import java.util.Hashtable;

public class Document {
	
	private String docNo;
	private long docLength;
	private Hashtable<Integer, Word> words;
	private Hashtable<Integer, Integer> relevances;
	private Hashtable<Integer, Hashtable<Integer, Integer>> novelties;
	
	public Document(String docNo) {
		
		this.docNo = docNo;
		docLength = 0;
		words = new Hashtable<Integer, Word>();
		relevances = new Hashtable<Integer, Integer>();
		novelties = new Hashtable<Integer, Hashtable<Integer, Integer>>();
		
	}
	
	public Document(String docNo, long docLength, Hashtable<Integer, Word> words) {
		
		this.docNo = docNo;
		this.docLength = docLength;
		this.words = words;
		relevances = new Hashtable<Integer, Integer>();
		novelties = new Hashtable<Integer, Hashtable<Integer, Integer>>();
		
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
	
	public Hashtable<Integer, Hashtable<Integer, Integer>> getNovelties() {
		return novelties;
	}
	
}
