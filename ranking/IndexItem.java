package ranking;

import java.util.HashMap;

public class IndexItem {
	
	private int docFrequency;
	private HashMap<String, DocTermFrequency> docList;
	
	public IndexItem(int docFrequency, HashMap<String, DocTermFrequency> docList) {
		this.docFrequency = docFrequency;
		this.docList = docList;
	}
	
	public void setDocFrequency(int docFrequency) {
		this.docFrequency = docFrequency;
	}
	
	public int getDocFrequency() {
		return docFrequency;
	}
	
	public HashMap<String, DocTermFrequency> getDocList() {
		return docList;
	}

}
