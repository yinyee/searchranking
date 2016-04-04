package ranking;

import java.util.ArrayList;

public class Topic {
	
	private int topicID;
	private ArrayList<Word> terms;
	
	public Topic(int topicID, ArrayList<Word> terms) {
		this.topicID = topicID;
		this.terms = terms;
	}
	
	public int getTopicID() {
		return topicID;
	}
	
	public ArrayList<Word> getTerms() {
		return terms;
	}

}
