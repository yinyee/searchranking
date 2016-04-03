package ranking;

public class Term {
	
	private Object id;
	private int frequency;
	
	public Term(Object id, int frequency) {
		this.id = id;
		this.frequency = frequency;
	}
	
	public Object getID() {
		return id;
	}
	
	public int getFrequency() {
		return frequency;
	}

}
