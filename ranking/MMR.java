package ranking;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class MMR {
	
	static String path = MMR.class.getClassLoader().getResource("data/").getPath();

	private Hashtable<Integer, String[][]> input = new Hashtable<Integer, String[][]>();
	private Hashtable<Integer, ArrayList<String>> results;
	private DocumentCollection documentCollection;
	
	public MMR(String file, DocumentCollection documentCollection) {
		
		this.documentCollection = documentCollection;
		
		try {
			
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
					
					String sRank = tokeniser.nextToken();
					sRank = sRank.trim();
					int rank = Integer.parseInt(sRank);
					
					String sScore = tokeniser.nextToken();
					sScore = sScore.trim();
					
					String[][] ranking;
					
					if (input.containsKey(topicID)) {
						ranking = input.get(topicID);
						ranking[rank][0] = docNo;
						ranking[rank][1] = sScore;
					} else {
						ranking = new String[100][2];
						ranking[rank][0] = docNo;
						ranking[rank][1] = sScore;
						input.put(topicID, ranking);
					}
					
				}
				
			}
			
			reader.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void getRankings(double lambda) {
		
		results = new Hashtable<Integer, ArrayList<String>>();
		
		Iterator<Integer> itrTopics = input.keySet().iterator();
		
		while (itrTopics.hasNext()) {
			
			int topicID = itrTopics.next();
			
			String[][] ranking = input.get(topicID);
			
			String sHighestScore = ranking[0][1];
			double highestScore = Double.parseDouble(sHighestScore);
			
			double[][] cosines = getCosineSimilarity(ranking);
			
			ArrayList<String> result = new ArrayList<String>();
			ArrayList<Integer> indices = new ArrayList<Integer>();
			
			result.add(ranking[0][0]);			// add first document
			indices.add(0);
			
			while (result.size() < 100) {
			
				double maxMMRScore = 0;
				int maxIndex = 0;
				
				for (int k = 1; k < 100; k++) {
					
					if (!(indices.contains(k))) {	// if this document is not in the result set
					
						double maxCosine = 0;
						
						for (int i = 0; i < indices.size(); i++) {		// check against every document in the result set
							
							int index = indices.get(i);
			
							// look in row
							if (cosines[index][k] > maxCosine) {
								maxCosine = cosines[index][k];
							}
							
							// look in column
							if (cosines[k][index] > maxCosine) {
								maxCosine = cosines[k][index];
							}
							
						} // finished checking against all documents in the result set
						
						String sScore = ranking[k][1];
						double bm25Score = Double.parseDouble(sScore);
						bm25Score = bm25Score / highestScore;		// need to normalise the score
						
						double mmrScore = (lambda * bm25Score) - ((1 - lambda) * maxCosine);
						
						if (mmrScore > maxMMRScore) {
							maxMMRScore = mmrScore;
							maxIndex = k;
						}
						
					} // end if
					
				} // finishing checking all documents which are not in the result set

				String docNo = ranking[maxIndex][0];
				result.add(docNo);
				indices.add(maxIndex);
				
			} // finished re-ranking all the documents
			
			results.put(topicID, result);
			
		} // finished processing all topics
		
		
		try {
			outputToFile(results, lambda);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	private double[][] getCosineSimilarity(String[][] ranking) {
		
		double[][] cosines = new double[100][100];
		
		for (int i = 0; i < 100; i++) {
			
			String docNo1 = ranking[i][0];
			Document doc1 = documentCollection.get(docNo1);
			
			Hashtable<Integer, Word> words1 = doc1.getWords();
			Iterator<Entry<Integer, Word>> itrWords = words1.entrySet().iterator();
			
			for (int j = i + 1; j < 100; j++) {
				
				String docNo2 = ranking[j][0];
				Document doc2 = documentCollection.get(docNo2);
				
				Hashtable<Integer, Word> words2 = doc2.getWords();
				
				long product = 0;
				long sumSq1 = 0;
				long sumSq2 = 0;
				
				while (itrWords.hasNext()) { // iterating over all the words in doc1
					
					int wordID = itrWords.next().getKey();
					
					if (words2.containsKey(wordID)) {	// if both documents have the same word
						
						int freq1 = words1.get(wordID).getFrequency();
						int freq2 = words2.get(wordID).getFrequency();
						
						long prod = freq1 * freq2;
						long sq1 = freq1 * freq1;
						long sq2 = freq2 * freq2;
						
						product += prod;
						sumSq1 += sq1;
						sumSq2 += sq2;
						
					}
					
				}	// no more words in document 1
				
				
				double denominator = sumSq1 * sumSq2;
				
				double cosine = 0;
				
				if (denominator != 0) {
					cosine = product / denominator;
				}
				
				cosines[i][j] = cosine;
				cosines[j][i] = cosine;
				
			} // no more document 2
			
		} // no more document 1

		return cosines;
		
	}
	
	private void outputToFile(Hashtable<Integer, ArrayList<String>> results, double lambda) throws FileNotFoundException, UnsupportedEncodingException {
		
		StringBuilder str = new StringBuilder();
		
		String space = " ";
		String newline = "\n";
		
		ArrayList<Integer> topics = new ArrayList<Integer>(results.keySet());
		
		Collections.sort(topics, new Comparator<Integer>() {

			@Override
			// Returns negative if o2 < o1 and vice versa
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
			
		});

		for (int i = 0; i < topics.size(); i++) {
			
			ArrayList<String> result = results.get(topics.get(i));
			
			for (int j = 0; j < 100; j++) {
				
				str.append(topics.get(i));
				str.append(space);
				str.append(result.get(j));
				str.append(space);
				str.append(j);
				str.append(space);
				str.append("MMR");
				str.append(lambda);
				str.append(newline);
				
			}
			
		}
		
		PrintWriter printer = new PrintWriter(path + "MMRScoring_" + lambda, "UTF-8");
		printer.print(str.toString());
		printer.close();
		
	}

}