package ranking;

import java.util.Hashtable;

public class Document {
	
	private String docNo;
	private long docLength;
	private Hashtable<Integer, Word> words;
	private Hashtable<Integer, Score> scores;
	
	public String getDocNo() {
		return docNo;
	}
	
	public long getDocLength() {
		return docLength;
	}
	
	public Document(String docNo, long docLength, Hashtable<Integer, Word> words) {
		this.docNo = docNo;
		this.docLength = docLength;
		this.words = words;
	}
	
	public Hashtable<Integer, Word> getWords() {
		return words;
	}
	
	public void setScores(Hashtable<Integer, Score> scores) {
		this.scores = scores;
	}
	
	public Hashtable<Integer, Score> getScores() {
		return scores;
	}

}
