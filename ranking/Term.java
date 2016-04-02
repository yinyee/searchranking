package ranking;

public class Term {
	
	private int id, frequency;
	
	public Term(int id, int frequency) {
		this.id = id;
		this.frequency = frequency;
	}
	
	public int getID() {
		return id;
	}
	
	public int getFrequency() {
		return frequency;
	}

}
