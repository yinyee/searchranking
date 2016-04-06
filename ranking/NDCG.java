package ranking;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;

public class NDCG {
	
	static String path = NDCG.class.getClassLoader().getResource("data/").getPath();
	
	private BM25 bm25;
	private Hashtable<Integer, Topic> topicCollection;
	
	public NDCG(BM25 bm25, Hashtable<Integer, Topic> topicCollection) {
		
		this.bm25 = bm25;
		this.topicCollection = topicCollection;
		
	}
	
	public double atK(int position) {
		
		Iterator<Integer> itrTopics = bm25.keySet().iterator();
		
		double ndcg = 0;
		
		// Calculate NDCG at k for every topic
		while (itrTopics.hasNext()) {
			
			int topicID = itrTopics.next();
			Hashtable<Integer, Result> results = bm25.get(topicID);
			
			double dcg = 0;

			Result result = results.get(0);
			Document document = result.getDocument();
			Hashtable<Integer, Integer> relevances = document.getRelevances();
			
			int relevanceAt0;
			if (relevances.containsKey(topicID)) {
				relevanceAt0 = relevances.get(topicID);
			} else {
				relevanceAt0 = 0;
			}
			
			for (int i = 1; i < position; i++) {
				
				result = results.get(i);
				document = result.getDocument();
				relevances = document.getRelevances();
				
				int relevance;
				
				if (relevances.containsKey(topicID)) {
					relevance = relevances.get(topicID);
				} else {
					relevance = 0;
				}
				
				double denominator = Math.log10(i + 1) / Math.log10(2); 
				
				dcg += relevance / denominator;
				
			}
			
			dcg += relevanceAt0;
			
			Topic topic = topicCollection.get(topicID);
			
			ndcg += dcg / idcgAtK(topic, position);
			
		} // finished processing all topics
		
		int numberOfTopics = bm25.size();
		
		double ndcgk = ndcg / numberOfTopics;		// Get average over all topics
		
		return ndcgk;
		
	}
	
	private double idcgAtK(Topic topic, int position) {
		
		int numberOfFours = topic.getFour();
		int numberOfThrees = topic.getThree();
		int numberOfTwos = topic.getTwo();
		int numberOfOnes = topic.getOne();
		
		double idcg = 0;
		
		int relevanceAt0;
		
		if (numberOfFours > 0) {
			relevanceAt0 = 4;
			numberOfFours--;
		} else if (numberOfThrees > 0) {
			relevanceAt0 = 3;
			numberOfThrees--;
		} else if (numberOfTwos > 0) {
			relevanceAt0 = 2;
			numberOfTwos--;
		} else if (numberOfOnes > 0) {
			relevanceAt0 = 1;
			numberOfOnes--;
		} else {
			relevanceAt0 = 0;
		}
		
		for (int i = 1; i < position; i++) {
			
			int relevance;
			
			if (numberOfFours > 0) {
				relevance = 4;
				numberOfFours--;
			} else if (numberOfThrees > 0) {
				relevance = 3;
				numberOfThrees--;
			} else if (numberOfTwos > 0) {
				relevance = 2;
				numberOfTwos--;
			} else if (numberOfOnes > 0) {
				relevance = 1;
				numberOfOnes--;
			} else {
				relevance = 0;
			}
			
			double denominator = Math.log10(i + 1) / Math.log10(2);
			
			idcg += relevance / denominator;
			
		}
		
		idcg += relevanceAt0;
		
		return idcg;
		
	}
	
	public void outputToFile(int[] ks, String file) {
		
		
		StringBuilder str = new StringBuilder();
		
		String separator = "\t|\t";
		
		str.append("bm25\n");
		str.append("K");
		str.append(separator);
		str.append("NDCG@K\n");
		
		for (int i = 0; i < ks.length; i++) {
			str.append(ks[i]);
			str.append(separator);
			str.append(String.format("%.3g%n", atK(ks[i])));
		}

		try {
			
			PrintWriter printer = new PrintWriter(path + file, "UTF-8");
			printer.print(str.toString());
			printer.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}

	}


}
