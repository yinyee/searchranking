package ranking;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class Diversity {
	
	static String path = Diversity.class.getClassLoader().getResource("data/").getPath();
	DocumentCollection documentCollection;
	Hashtable<Document, Integer> top100;
	BM25 bm25;
	
	public BM25 getBM25() {
		return bm25;
	}
	
	public Diversity(String file, DocumentCollection documentCollection) {
		this.documentCollection = documentCollection;
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(path + file));
			
			String line;
			
			top100 = new Hashtable<Document, Integer>();
			
			bm25 = new BM25(documentCollection);
			
			while ((line = reader.readLine()) != null) {
				
				StringTokenizer tokeniser = new StringTokenizer(line);
				
				int topicID = 0;
				Hashtable<Integer, Result> entry = new Hashtable<Integer, Result>();
				
				while (tokeniser.hasMoreTokens()) {
					
					String sTopicID = tokeniser.nextToken();
					sTopicID = sTopicID.trim();
					topicID = Integer.parseInt(sTopicID);
					
					String docNo = tokeniser.nextToken();
					docNo = docNo.trim();
					
					String sRank = tokeniser.nextToken();
					sRank = sRank.trim();
					int rank = Integer.parseInt(sRank);
					
					String sScore = tokeniser.nextToken();
					sScore = sScore.trim();
					double score = Double.parseDouble(sScore);
					
					Document document = documentCollection.get(docNo);
					top100.put(document, 1);
					
					Result result = new Result(document, score, rank);
					entry.put(rank, result);
					
				}
				
				bm25.put(topicID, entry);
				
			}
			
			System.out.println(top100.size());
			reader.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void calculateDiversity() throws FileNotFoundException, UnsupportedEncodingException {
		
		PrintWriter printer = new PrintWriter(path + "similarities.txt", "UTF-8");
		StringBuilder str = new StringBuilder();
		String space = " ";
		int outsideCount = 0, insideCount;
		
		Object[] top100array = top100.keySet().toArray();
		
		for (int i = 0; i < top100array.length; i++) {
			
			Document doc1 = (Document) top100array[i];
			String docNo1 = doc1.getDocNo();
			System.out.println(docNo1);
			str.append(docNo1);
			str.append("\n");
//			System.out.println(docNo1);				// clueweb12-0512wb-25-19179
//			Hashtable<Integer, Word> words1 = doc1.getWords();
//			
//			double avg1;
//			if (words1.size() == 0) {
//				avg1 = 3.069912065746759;								// average word frequency per document
//			} else {
//				avg1 = doc1.getDocLength() / words1.size();
//			}
//			
//			outsideCount++;
//			insideCount = 0;
						
//			for (int j = i + 1; j < top100array.length; j++) {
//				
//				Document doc2 = (Document) top100array[j];
//				String docNo2 = doc2.getDocNo();
////				System.out.println(docNo2);			// clueweb12-0800wb-34-17958
//				Hashtable<Integer, Word> words2 = doc2.getWords();
//				
//				double avg2;
//				if (words2.size() == 0)  {									
//					avg2 = 3.069912065746759;							// average word frequency per document 
//				} else {
//					avg2 = doc2.getDocLength() / words2.size();
//				}
//				
//				insideCount++;
//				Iterator<Entry<Integer, Word>> itrWords = words1.entrySet().iterator();
//				
//				// Cosine
//				long prod, sq1, sq2;
//				long product = 0;
//				long sqFreq1 = 0;
//				long sqFreq2 = 0;
//				double cosine = 0;
//				
//				// Pearson
//				double diff1, diff2, prodDiff, sqDiff1, sqDiff2;
//				long productDiff = 0;
//				long squareDiff1 = 0;
//				long squareDiff2 = 0;
//				double pearson = 0;
//				
//				while (itrWords.hasNext()) {
//					
//					Word word = itrWords.next().getValue();
//					int wordID = word.getID();
////					System.out.println(wordID);
//					
//					if (words2.containsKey(wordID)) {
//						
//						int freq1 = word.getFrequency();
//						int freq2 = words2.get(wordID).getFrequency();
//						
////						System.out.println("freq1: " + freq1);
////						System.out.println(" freq2: " + freq2);
////						System.out.println(words2.get(wordID).getFrequency());
//						
//						// Cosine
//						prod = freq1 * freq2;
//						sq1 = freq1 * freq1;
//						sq2 = freq2 * freq2;
//						
//						product += prod;
//						sqFreq1 += sq1;
//						sqFreq2 += sq2;
//						
//						// Pearson
//						diff1 = freq1 - avg1;
//						diff2 = freq2 - avg2;
//						
//						prodDiff = diff1 * diff2;
//						sqDiff1 = diff1 * diff1;
//						sqDiff2 = diff2 * diff2;
//						
//						productDiff += prodDiff;
//						squareDiff1 += sqDiff1;
//						squareDiff2 += sqDiff2;
//						
//					}
//					
//					cosine = product / (Math.sqrt(sqFreq1) * Math.sqrt(sqFreq2));
//					pearson = productDiff / (Math.sqrt(squareDiff1) * Math.sqrt(squareDiff2));
////					System.out.println("cosine: " + cosine);
////					System.out.print(" pearson: " + pearson);
//					
//				} // finished processing all words contained in doc1
//				
//				System.out.println(i + " " + j);
//				
//				str.append(docNo1);
//				str.append(space);
//				str.append(docNo2);
//				str.append(space);
//				str.append(cosine);
//				str.append(space);
//				str.append(pearson);
//				str.append("\n");
//				
////				System.out.println(str.toString());
//				printer.print(str.toString());
//				
//			} // finished processing all remaining doc2
			
			
			
		} // finished processing all documents in collection
		
		printer.println(str.toString());
		printer.close();		
		
	}

}
