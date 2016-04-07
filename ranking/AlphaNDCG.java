package ranking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

public class AlphaNDCG {
	
	static String path = AlphaNDCG.class.getClassLoader().getResource("data/").getPath();
	
	DocumentCollection documentCollection;
	SubtopicCollection subtopicCollection;
	
	Hashtable<Integer, ArrayList<String>> results;
	
	public AlphaNDCG(String file, DocumentCollection documentCollection, SubtopicCollection subtopicCollection) {
		
		this.documentCollection = documentCollection;
		this.subtopicCollection = subtopicCollection;
		
		try {
			
			results = new Hashtable<Integer, ArrayList<String>>();
			
			BufferedReader reader = new BufferedReader(new FileReader(path + file));
			
			String line;
			
			while ((line = reader.readLine()) != null) {
				
				StringTokenizer tokeniser = new StringTokenizer(line);
				
				while (tokeniser.hasMoreTokens()) {
					
					String sTopicID = tokeniser.nextToken();
					sTopicID = sTopicID.trim();
					int topicID = Integer.parseInt(sTopicID);
					
					String docNo = tokeniser.nextToken();
					docNo = docNo.trim();
					
					// Skip rank and name of ranking model
					tokeniser.nextToken();
					tokeniser.nextToken();
					
					ArrayList<String> ranking;
					if (results.containsKey(topicID)) {
						ranking = results.get(topicID);
						ranking.add(docNo);
					} else {
						ranking = new ArrayList<String>();
						ranking.add(docNo);
						results.put(topicID, ranking);
					}
										
				}
				
			}
			
			reader.close();
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
	
	}
	
	private Hashtable<Integer, ArrayList<Double>> calculateGains(double alpha) {
		
		Hashtable<Integer, ArrayList<Double>> gains = new Hashtable<Integer, ArrayList<Double>>();
		
		Iterator<Integer> itrTopics = results.keySet().iterator();
		
		while (itrTopics.hasNext()) {
			
			int topicID = itrTopics.next();
			
			ArrayList<String> ranking = results.get(topicID);
			ArrayList<Double> gain = new ArrayList<Double>();
			
			for (int i = 0; i < ranking.size(); i++) {
				
				String docNo = ranking.get(i);
				
				Document document = documentCollection.get(docNo);
				Hashtable<Integer, Hashtable<Integer, Integer>> novelties = document.getNovelties();
				
				if (novelties.containsKey(topicID)) {

					double gainAtPosition = 0;
					
					Hashtable<Integer, Integer> novelty = novelties.get(topicID);
					
					ArrayList<Integer> subtopics = subtopicCollection.get(topicID);
					
					int count = 0;
					
					for (int j = 0; j < subtopics.size(); j++) { // iterate through subtopics in this topic
						
						int subtopicID = subtopics.get(j);
						
						if (novelty.containsKey(subtopicID)) {
							
							int judgement = novelty.get(subtopicID);
							
							if (judgement != 0) {
													
								for (int k = 0; k < i; k++) {
									
									String previousDocNo = ranking.get(k);
									Document previousDocument = documentCollection.get(previousDocNo);
									Hashtable<Integer, Hashtable<Integer, Integer>> previousNovelties = previousDocument.getNovelties();
									
									if (previousNovelties.containsKey(topicID)) {
									
										Hashtable<Integer, Integer> previousNovelty = previousNovelties.get(topicID);
										
										if (previousNovelty.containsKey(subtopicID) && (previousNovelty.get(subtopicID) != 0)) {
											count++;
										} 
									
									}
									
								} // finished checking all the previous documents
								
							} // if judgement is zero, no need to check previous
							
							gainAtPosition += judgement * Math.pow(1 - alpha, count);
							
						} // if there is a judgement for this subtopic
						
					} // finished processing all the subtopics in this topic
					
					gain.add(gainAtPosition);
					
				} else {
					
					gain.add(0.00);
					
				}
				
			} // finished processing all the documents
			
			gains.put(topicID, gain);
			
		} // finished processing all the topics
		
		return gains;
		
	}
	
	private Hashtable<Integer, ArrayList<Double>> calculateIdealGains(double alpha) {
		
		Hashtable<Integer, ArrayList<Double>> idealGains = new Hashtable<Integer, ArrayList<Double>>(); 
		
		Iterator<Integer> itrTopics = results.keySet().iterator();
		
		while (itrTopics.hasNext()) {  // For each topic
			
			int topicID = itrTopics.next();
			
			ArrayList<String> documents = results.get(topicID);
			
			int maxJudgement = -1;
			String maxDocument = "";
			
			// Get the highest scoring document
			for (int i = 0; i < documents.size(); i++) {  // For each document in the ranking
				
				String docNo = documents.get(i);
				Document document = documentCollection.get(docNo);
				
				Hashtable<Integer, Hashtable<Integer, Integer>> novelties = document.getNovelties();
				
				int judgement = 0;
				
				if (novelties.containsKey(topicID)) {
					
					Hashtable<Integer, Integer> novelty = novelties.get(topicID);
					
					Iterator<Integer> itrSubtopics = novelty.keySet().iterator();
					
					while (itrSubtopics.hasNext()) {
						
						judgement += novelty.get(itrSubtopics.next());
						
					} // finished iterating through all the subtopics
					
				} // no need to check if document is not relevant at all to the topic
				
				if (judgement > maxJudgement) {
					maxJudgement = judgement;
					maxDocument = docNo;
				}
				
			} // finished checking all documents
			
			ArrayList<Double> idealGain = new ArrayList<Double>();
			idealGain.add( (double) maxJudgement);

			ArrayList<String> newRanking = new ArrayList<String>();
			newRanking.add(maxDocument);

			// At this point we have added the first document
			// We now need to add the rest in order
			
			while (newRanking.size() < documents.size()) {	// make sure to reorder all the documents in ranking
				
				double maxGain = 0;
				
				for (int i = 0; i < documents.size(); i++) {
					
					String docNo = documents.get(i);
					
					if (!(newRanking.contains(docNo))) { 	// if document is NOT YET added to the new ranking
						
						Document document = documentCollection.get(docNo);
						Hashtable<Integer, Hashtable<Integer, Integer>> novelties = document.getNovelties();
						
						if (novelties.containsKey(topicID)) {	// if this document has judgements for this topic
							
							Hashtable<Integer, Integer> novelty = novelties.get(topicID);							
							Iterator<Integer> itrSubtopics = novelty.keySet().iterator();
							
							double gain = 0;
							
							while (itrSubtopics.hasNext()) {	// for every subtopic in this topic
								
								int subtopicID = itrSubtopics.next();
								
								if (novelty.containsKey(subtopicID)) {	// if non-zero judgement for this subtopic for this document
									
									int count = 0;
									
									for (int j = 0; j < newRanking.size(); j++) {	// check the documents which have already been chosen
										
										String previousDocNo = newRanking.get(j);
										Document previousDocument = documentCollection.get(previousDocNo);
										
										Hashtable<Integer, Hashtable<Integer, Integer>> previousNovelties = previousDocument.getNovelties();
										Hashtable<Integer, Integer> previousNovelty = previousNovelties.get(topicID);
										
										if (previousNovelty.containsKey(subtopicID)) {
											count++;
										}	// count how many times this subtopic has been covered by documents which have already been chosen
										
									}	// finished checking the documents which have already been chosen
									
									gain += novelty.get(subtopicID) * Math.pow(1 - alpha, count);
									
								} // no need to calculate gain if this document does not have a novelty judgement for this subtopic
								
							} // finished adding up gains for all subtopics in topic
									
							if (gain > maxGain) { // update max values
								
								maxGain = gain;
								maxDocument = docNo;
								
							}  
							
						} // no need to check if document is not relevant at all to the topic 
						
					} // no need to check if document is already in the new ranking
					
				} // finished processing all documents
				
				idealGain.add(maxGain);
				newRanking.add(maxDocument);

			} // finished reordering all documents in ranking
			
			idealGains.put(topicID, idealGain);
			
		} // finished processing all topics
		
		return idealGains;
		
	}
	
	private double atK(Hashtable<Integer, ArrayList<Double>> gains, Hashtable<Integer, ArrayList<Double>> idealGains, int position) {
		
		Iterator<Integer> itrTopics = gains.keySet().iterator();
		
		double ndcg = 0;
		int count = 0;	// keep count of how many topics have at least one non-zero novelty judgement
		
		while (itrTopics.hasNext()) {
			
			int topicID = itrTopics.next();
			
			ArrayList<Double> gain = gains.get(topicID);
			ArrayList<Double> idealGain = idealGains.get(topicID);
			
			double dcg = 0;
			double idcg = 0;
			
			for (int i = 0; i < position; i++) {	// sum up gains to get cumulative gains
				
				double denominator = Math.log10(i + 2) / Math.log10(2);
				
				dcg += gain.get(i) / denominator;
				idcg += idealGain.get(i) / denominator;
				
			}
			
			if (idcg != 0) {
				ndcg += dcg / idcg;
				count++;
			}
			
		} // finished processing all topics
		
		ndcg = ndcg / count;
		
		return ndcg;
		
	}
	
	public void outputToFile(int[] ks, double[] alphas, String file) {
		
		for (int i = 0; i < alphas.length; i++) {
			
			Hashtable<Integer, ArrayList<Double>> gains = calculateGains(alphas[i]);
			Hashtable<Integer, ArrayList<Double>> idealGains = calculateIdealGains(alphas[i]);
			
			StringBuilder str = new StringBuilder();
			
			String separator = "\t|\t";
			
			str.append(file);
			str.append("\n");
			str.append("alpha = ");
			str.append(alphas[i]);
			str.append("\n");
			str.append("K");
			str.append("\t|\t");
			str.append("Alpha NDCG@K\n");
						
			for (int j = 0; j < ks.length; j++) {
				
				str.append(ks[j]);
				str.append(separator);
				str.append(String.format("%.3g%n", atK(gains, idealGains, ks[j])));
				
			}
			
			try {
				
				PrintWriter printer = new PrintWriter(path + file + "_alphaNDCG_" + alphas[i], "UTF-8");
				printer.print(str.toString());
				printer.close();
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
			}
			
		}
		
	}
	
}