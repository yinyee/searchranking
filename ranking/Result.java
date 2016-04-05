package ranking;

public class Result {

	private Document document;
	private double score;
	private int rank;
	
	public Result(Document document, double score, int rank) {
		this.document = document;
		this.score = score;
		this.rank = rank;
	}
	
	public Document getDocument() {
		return document;
	}
	
	public double getScore() {
		return score;
	}
	
	public int getRank() {
		return rank;
	}
	
}
