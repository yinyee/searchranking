package ranking;

import java.util.ArrayList;

public class Topic {
	
	private int topicID;
	private ArrayList<Word> terms;
	private int zero, one, two;
	
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
	
	public void setZero(int zero) {
		this.zero = zero;
	}
	
	public void setOne(int one) {
		this.one = one;
	}
	
	public void setTwo(int two) {
		this.two = two;
	}
	
	public int getZero() {
		return zero;
	}
	
	public int getOne() {
		return one;
	}
	
	public int getTwo() {
		return two;
	}

}
