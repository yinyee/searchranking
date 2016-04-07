package ranking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

@SuppressWarnings("serial")
public class SubtopicCollection extends Hashtable<Integer, ArrayList<Integer>> {
	
	static String path = SubtopicCollection.class.getClassLoader().getResource("data/").getPath();
	
	public SubtopicCollection (String file, DocumentCollection documentCollection) {
		
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
					
					// Subtopic ID
					String sSubtopicID = tokeniser.nextToken();
					sSubtopicID = sSubtopicID.trim();
					int subtopicID = Integer.parseInt(sSubtopicID);
					
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
					
					// Novelty judgment
					String sNovelty = tokeniser.nextToken();
					sNovelty = sNovelty.trim();
					int novelty = Integer.parseInt(sNovelty);
					
					// Update document with novelty judgment
					Hashtable<Integer, Hashtable<Integer, Integer>> novelties = document.getNovelties();
					Hashtable<Integer, Integer> subtopicNovelty;
					
					if (novelties.containsKey(topicID)) {
						
						subtopicNovelty = novelties.get(topicID);
						subtopicNovelty.put(subtopicID, novelty);
						
					} else {
						
						subtopicNovelty = new Hashtable<Integer, Integer>();
						subtopicNovelty.put(subtopicID, novelty);
						novelties.put(topicID, subtopicNovelty);
						
					}
					
					// Update subtopic collection
					ArrayList<Integer> subtopics;
					if (this.containsKey(topicID)) {
						subtopics = this.get(topicID);
						if (!(subtopics.contains(subtopicID))) {
							subtopics.add(subtopicID);
						}
					} else {
						subtopics = new ArrayList<Integer>();
						subtopics.add(subtopicID);
						this.put(topicID, subtopics);
					}
					
				}		
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
