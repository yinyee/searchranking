package ranking;

import java.util.Hashtable;

public class IndexItem {
	
	private int termID, docFrequency;
	private Hashtable<String, Term> docList;
	
	public IndexItem(int termID, int docFrequency, Hashtable<String, Term> docList) {
		this.termID = termID;
		this.docFrequency = docFrequency;
		this.docList = docList;
	}
	
	public int getTermID() {
		return termID;
	}
	
	public void setDocFrequency(int docFrequency) {
		this.docFrequency = docFrequency;
	}
	
	public int getDocFrequency() {
		return docFrequency;
	}
	
	public Hashtable<String, Term> getDocList() {
		return docList;
	}

}
