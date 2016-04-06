package ranking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

@SuppressWarnings("serial")
public class TopicCollection extends Hashtable<Integer, Topic> {

	static String path = TopicCollection.class.getClassLoader().getResource("data/").getPath();
	
	public TopicCollection(String file) {
		
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(path + file));
			
			String line;
			
			while ((line = reader.readLine()) != null) {
				
				StringTokenizer tokeniser = new StringTokenizer(line);
				
				String sTopicID = tokeniser.nextToken(" :");
				sTopicID = sTopicID.trim();
				int topicID = Integer.parseInt(sTopicID);
				
				ArrayList<Word> words = new ArrayList<Word>();
				
//				System.out.println(topicID);
				
				while (tokeniser.hasMoreTokens()) {
					
					String sTermID = tokeniser.nextToken();
					sTermID = sTermID.trim();
					int termID = Integer.parseInt(sTermID);
					
//					System.out.println(termID);
					
					tokeniser.nextToken();			// Discard the term frequency for queries
					
					Word word = new Word(termID, 1);
					words.add(word);
					
				}
				
				Topic query = new Topic(topicID, words);
				this.put(topicID, query);
				
			}
			
			reader.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
//		System.out.println(topicCollection.size());
		
	}
		
}
