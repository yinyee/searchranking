package ranking;

import java.util.Hashtable;

public class IndexItem {
	
	private int wordID, docFrequency;
	private double idf;
	private Hashtable<String, Find> docList;
	
	public IndexItem(int wordID, int docFrequency, Hashtable<String, Find> docList) {
		this.wordID = wordID;
		this.docFrequency = docFrequency;
		this.docList = docList;
	}
	
	public int getWordID() {
		return wordID;
	}
	
	public void setDocFrequency(int docFrequency) {
		this.docFrequency = docFrequency;
	}
	
	public int getDocFrequency() {
		return docFrequency;
	}
	
	public Hashtable<String, Find> getDocList() {
		return docList;
	}
	
	public void setIDF(double idf) {
		this.idf = idf;
	}

	public double getIDF() {
		return idf;
	}
}
