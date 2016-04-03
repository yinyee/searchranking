package ranking;

import java.util.ArrayList;

public class Topic {
	
	private int topicID;
	private ArrayList<Term> terms;
	
	public Topic(int topicID, ArrayList<Term> terms) {
		this.topicID = topicID;
		this.terms = terms;
	}
	
	public int getTopicID() {
		return topicID;
	}
	
	public ArrayList<Term> getTerms() {
		return terms;
	}

}
