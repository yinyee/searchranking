package ranking;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

@SuppressWarnings("serial")
public class DocumentCollection extends Hashtable<String, Document> {
	
	static String path = DocumentCollection.class.getClassLoader().getResource("data/").getPath();
	Hashtable<Integer, Word> lexicon;
	
	public DocumentCollection(String file) {
		
		try {
			
			lexicon = new Hashtable<Integer, Word>();
			
			BufferedReader reader = new BufferedReader(new FileReader(path + file));
			String line;
			
			while ((line = reader.readLine()) != null) {
				
				StringTokenizer tokeniser = new StringTokenizer(line);
				
				String docNo = tokeniser.nextToken(" :");
				docNo = docNo.trim();
//				System.out.println(docNo);
				Hashtable<Integer, Word> words = new Hashtable<Integer, Word>();
				long docLength = 0;
				
				while (tokeniser.hasMoreTokens()) {
					
					String sWordID = tokeniser.nextToken();
					sWordID = sWordID.trim();
					int wordID = Integer.parseInt(sWordID);
					
					if (!(lexicon.containsKey(wordID))) {
						lexicon.put(wordID, new Word(wordID, 1));
					} else {
						Word word = lexicon.get(wordID);
						int frequency = word.getFrequency();
						word.setFrequency(frequency + 1);
					}
					
					String sWordFrequency = tokeniser.nextToken();
					sWordFrequency = sWordFrequency.trim();
					int wordFrequency = Integer.parseInt(sWordFrequency);
					
					docLength += wordFrequency;
					
//					System.out.println("wordID: " + wordID);
//					System.out.println("wordFrequency: " + wordFrequency);
//					System.out.println("docLength = " + docLength);
					
					Word word = new Word(wordID, wordFrequency);
					words.put(wordID, word);
					
				}
				
				Document document = new Document(docNo, docLength, words);
				this.put(docNo, document);
				
			}
			
			reader.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
//		System.out.println(documentCollection.size());
		
	}
	
}
