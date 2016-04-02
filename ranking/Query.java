package ranking;

import java.util.ArrayList;

public class Query {
	
	private int topicID;
	private ArrayList<Term> terms;
	
	public Query(int topicID, ArrayList<Term> terms) {
		this.topicID = topicID;
		this.terms = terms;
	}
	
	public ArrayList<Term> getTerms() {
		return terms;
	}

}
