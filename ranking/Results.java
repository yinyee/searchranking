package ranking;

import java.util.Hashtable;

public class Results {
	
	private Hashtable<Object, Object> results;
	
	public Results(Hashtable<Object, Object> results) {
		this.results = results;
	}
	
	public Hashtable<Object, Object> getResults() {
		return results;
	}

}
