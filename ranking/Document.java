package ranking;

import java.util.Hashtable;

public class Document {
	
	private String docNo;
	private long docLength;
	private Hashtable<Integer, Term> terms;
	private Hashtable<Integer, Score> scores;
	
	public String getDocNo() {
		return docNo;
	}
	
	public long getDocLength() {
		return docLength;
	}
	
	public Document(String docNo, long docLength, Hashtable<Integer, Term> terms) {
		this.docNo = docNo;
		this.docLength = docLength;
		this.terms = terms;
	}
	
	public Hashtable<Integer, Term> getTerms() {
		return terms;
	}
	
	public Hashtable<Integer, Score> getScores() {
		return scores;
	}

}
