package ranking;

public class Word {
	
	private int id, frequency;
	
	public Word(int id, int frequency) {
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
