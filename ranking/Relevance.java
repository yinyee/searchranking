package ranking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Relevance {
	
	static String path = Relevance.class.getClassLoader().getResource("data/").getPath();
	
	public void loadJudgements(String file, DocumentCollection documentCollection, TopicCollection topicCollection) {
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(path + file));
			
			String line;
			
			while ((line = reader.readLine()) != null) {
				
				StringTokenizer tokeniser = new StringTokenizer(line);
				
				while (tokeniser.hasMoreTokens()) {
					
					// Topic ID
					String sTopicID = tokeniser.nextToken();
					sTopicID = sTopicID.trim();
					int topicID = Integer.parseInt(sTopicID);
					
					Topic topic = topicCollection.get(topicID);
					
					// Skip topic intent
					tokeniser.nextToken();
					
					// Document number
					String docNo = tokeniser.nextToken();
					docNo = docNo.trim();

					Document document;
					if (documentCollection.containsKey(docNo)) {
						document = documentCollection.get(docNo);
					} else {
						document = new Document(docNo);
						documentCollection.put(docNo, document);
					}
					
					// Relevance judgment
					String sRelevance = tokeniser.nextToken();
					sRelevance = sRelevance.trim();
					int relevance = Integer.parseInt(sRelevance);
//					System.out.println("relevance: " + relevance);
					
					// Update document with relevance judgment
					Hashtable<Integer, Integer> relevances = document.getRelevances();
					relevances.put(topicID, relevance);
					
					// Update topic relevance counts
					switch (relevance) {
					case 4: {
						int four = topic.getFour();
						topic.setFour(four + 1);
						break;
					}
					case 3: {
						int three = topic.getThree();
						topic.setThree(three + 1);
						break;
					}
					case 2: {
						int two = topic.getTwo();
						topic.setTwo(two + 1);
						break;
					}
					case 1: {
						int one = topic.getOne();
						topic.setOne(one + 1);
						break;
					}
					default: {
						int zero = topic.getZero();
						topic.setZero(zero + 1);
						break;
					}
					}
					
				}		
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
