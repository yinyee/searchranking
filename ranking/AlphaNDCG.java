package ranking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
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
					
					for (int j = 0; j < subtopics.size(); j++) { // iterate through subtopics in this topic
						
						int subtopicID = subtopics.get(j);
						
						if (novelty.containsKey(subtopicID)) {
							
							int judgement = novelty.get(subtopicID);
							int count = 0;
							
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
					System.out.println(topicID + " " + i + " " + gainAtPosition);
					
				} else {
					
					gain.add(0.00);
					System.out.println(topicID + " " + i + " 0.0");
					
				}
				
			} // finished processing all the documents
			
			gains.put(topicID, gain);
			
		} // finished processing all the topics
		
		return gains;
		
	}
	
	private double atK(Hashtable<Integer, ArrayList<Double>> gains, int position) {
		
		Iterator<Entry<Integer, ArrayList<Double>>> itrTopics = gains.entrySet().iterator();
		
		double ndcg = 0;
		
		while (itrTopics.hasNext()) {
			
			Entry<Integer, ArrayList<Double>> entry = itrTopics.next();
			
			ArrayList<Double> gain = entry.getValue();
			
//			System.out.println(gain.size());
			
			double dcg = 0;
			
			for (int i = 0; i < position; i++) {
				
				dcg += gain.get(i) / (Math.log10(i + 2) / Math.log10(2));
				
			}
			
			Collections.sort(gain);
			
			double idcg = 0;
			
			for (int i = 0; i < position; i++) {
				
				idcg += gain.get(i) / (Math.log10(i + 2) / Math.log10(2));
				
			}
			
			ndcg += dcg / idcg;
			
		}
		
		double ndcgK = ndcg / gains.size();
		
		return ndcgK;
		
	}
	
	public void outputToFile(int[] ks, double[] alphas, String file) {
		
		for (int i = 0; i < alphas.length; i++) {
			
			Hashtable<Integer, ArrayList<Double>> gains = calculateGains(alphas[i]);
			
			StringBuilder str = new StringBuilder();
			
			String separator = "\t\t|\t";
			
			str.append(file);
			str.append("K");
			str.append("\t|\t");
			str.append("Alpha NDCG@K\n");
						
			for (int j = 0; j < ks.length; j++) {
				
				str.append(ks[j]);
				str.append(separator);
				str.append(String.format("%.3g%n", atK(gains, ks[j])));
				
			}
			
			try {
				
				PrintWriter printer = new PrintWriter(path + file + "_alphaNDCG" + alphas[i], "UTF-8");
				printer.print(str.toString());
				printer.close();
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
			}
			
		}
		
	}
	
}