package ranking;

public class Similarity {
	
	private String doc1, doc2;
	private double pearson, cosine;
	
	public Similarity(String doc1, String doc2, double cosine, double pearson) {
		
		this.doc1 = doc1;
		this.doc2 = doc2;
		this.cosine = cosine;
		this.pearson = pearson;
		
	}
		
	public double getCosine() {
		return cosine;
	}
	
	public double getPearson() {
		return pearson;
	}
	

}
