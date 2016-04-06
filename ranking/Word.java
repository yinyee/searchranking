package ranking;

public class Word {
	
	private int id, frequency;
	private double idf;
	
	public Word(int id, int frequency) {
		this.id = id;
		this.frequency = frequency;
	}
	
	public int getID() {
		return id;
	}
	
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public double getIDF(int totalNumberOfDocuments) {
		
		if (idf == 0) {
			calculateIDF(totalNumberOfDocuments);
		}
		
		return idf;
		
	}
	
	private void calculateIDF(int totalNumberOfDocuments) {

			double numerator = totalNumberOfDocuments - frequency + 0.5;
			double denominator = frequency + 0.5;
			double idf = Math.log(numerator/denominator);
			this.idf = idf;
			
	}

}
