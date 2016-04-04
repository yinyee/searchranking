package ranking;

public class Find {
	
	private String docNo;
	private int frequency;
	
	public Find(String docNo, int frequency) {
		this.docNo = docNo;
		this.frequency = frequency;
	}
	
	public String getDocNo() {
		return docNo;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
}
