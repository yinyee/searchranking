package ranking;

public class DocTermFrequency {

	private String docNo;
	private int frequency;
	
	public DocTermFrequency(String docNo, int frequency) {
		this.docNo = docNo;
		this.frequency = frequency;
	}
	
	public int getFrequency() {
		return frequency;
	}
}
