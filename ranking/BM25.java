package ranking;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class BM25 extends Hashtable<Integer, Hashtable<Integer, Result>> {

	static String path = BM25.class.getClassLoader().getResource("data/").getPath();
	
	private static final float b = 0.75f;
	private static final float k = 1.5f;
	
	private static final String q0 = "Q0";
	private static final String bm25 = "BM25b0.75";
	
	protected DocumentCollection documentCollection;
	
	public BM25 (DocumentCollection documentCollection) {
		this.documentCollection = documentCollection;
	}
	
	public BM25 (String file, DocumentCollection documentCollection) {
		
		this.documentCollection = documentCollection;
		
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
					
					if (!(this.containsKey(topicID))) {
						Hashtable<Integer, Result> results = new Hashtable<Integer, Result>();
						this.put(topicID, results);
					}

					// Skip Q0
					tokeniser.nextToken();
					
					// Document number
					String docNo = tokeniser.nextToken();
					docNo = docNo.trim();
					
					// Rank
					String sRank = tokeniser.nextToken();
					sRank = sRank.trim();
					int rank = Integer.parseInt(sRank);
					
					// Score
					String sScore = tokeniser.nextToken();
					sScore = sScore.trim();
					double score = Double.parseDouble(sScore);
					
					// Skip BM
					tokeniser.nextToken();
				
					Hashtable<Integer, Result> results = this.get(topicID);
					
					Document document;
					if (this.documentCollection.containsKey(docNo)) {
						document = this.documentCollection.get(docNo);
					} else {
						document = new Document(docNo);
						documentCollection.put(docNo, document);
					}
					
					Result result = new Result(document, score, rank);
					results.put(rank, result);
					
				}
			}
			
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void getResults(TopicCollection topicCollection) throws FileNotFoundException, UnsupportedEncodingException {
		
		PrintWriter printer = new PrintWriter(path + "top100.txt", "UTF-8");
		StringBuilder str = new StringBuilder();
		String space = " ";
		
		float averageDocLength = this.getAverageDocLength(documentCollection);
		int totalNumberOfDocs = documentCollection.size();
		
		Iterator<Entry<Integer, Topic>> itrTopics = topicCollection.entrySet().iterator();
		
		while (itrTopics.hasNext()) {
			
			Hashtable<Document, Double> unsorted = new Hashtable<Document, Double>();
			
			Topic topic = itrTopics.next().getValue();
			int topicID = topic.getTopicID();
			
			Iterator<Entry<String, Document>> itrDocuments = documentCollection.entrySet().iterator();
			
			while (itrDocuments.hasNext()) {
				
				Document document = itrDocuments.next().getValue();
				Hashtable<Integer, Word> words = document.getWords();
				
				double score = 0;
				
				Iterator<Word> itrWords = topic.getTerms().iterator();
				
				while (itrWords.hasNext()) {
					
					int wordID = itrWords.next().getID();
					Word word = documentCollection.lexicon.get(wordID);
					
					if (words.containsKey(wordID)) {			// If document contains the word
						
						int wordFrequency = words.get(wordID).getFrequency();
						long docLength = document.getDocLength();
						
						double idf = word.getIDF(totalNumberOfDocs);
						
						double numerator = idf * wordFrequency * (k + 1);
						double denominator = wordFrequency + (k * (1 - b + (b * docLength / averageDocLength)));
						score += numerator / denominator;
						
					}
					
				} // no more words in this topic
			
				unsorted.put(document, score);
				
			} // finished all documents in collection
			
			ArrayList<Entry<Document, Double>> sorted = new ArrayList<Entry<Document, Double>>(unsorted.entrySet());
			
			// Sort the scores in descending order
			Collections.sort(sorted, new Comparator<Entry<Document, Double>>() {

				@Override
				// Returns negative if o2 < o1 and vice versa
				public int compare(Entry<Document, Double> o1, Entry<Document, Double> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
				
			});
			
			Hashtable<Integer, Result> results = new Hashtable<Integer, Result>();
			
			for (int i = 0; i < sorted.size(); i++) {
				
				Entry<Document, Double> entry = sorted.get(i);
				Document document = entry.getKey();
				double score = entry.getValue();
				
				Result result = new Result(document, score, i);
				
				results.put(i, result);
				
				// output top 100 documents to another file
				int j = i;
				
				if (j < 100) {
					
					str.append(topicID);
					str.append(space);
					str.append(document.getDocNo());
					str.append(space);
					str.append(j);
					str.append(space);
					str.append(score);
					str.append("\n");
					
					
				}
				
			}
			
			this.put(topicID, results);
			
		} // no more topics to process
		
		this.printResults();
		printer.print(str.toString());			
		printer.close();
		
	}
	
	private float getAverageDocLength(Hashtable<String, Document> documentCollection) {
		
		Iterator<Entry<String, Document>> itrDocuments = documentCollection.entrySet().iterator();
		
		int totalDocCount = documentCollection.size();
		long totalDocLength = 0;
//		long wordSize = 0;
		
		while (itrDocuments.hasNext()) {
			Document document = itrDocuments.next().getValue();
			totalDocLength += document.getDocLength();
//			wordSize += document.getWords().size();
		}
		
		float averageDocLength = totalDocLength / totalDocCount;
		
//		System.out.println("length of all documents combined: " + totalDocLength);						// 6,480,612
//		System.out.println("average length of document: " + averageDocLength);							// 1,365
//		System.out.println("average word frequency: " + ((double) totalDocLength / (double) wordSize));	// 3.069912065746759
		
		return averageDocLength;
		
	}
	
		
	private void printResults() throws FileNotFoundException, UnsupportedEncodingException {
		
		ArrayList<Integer> topics = new ArrayList<Integer>(this.keySet());
		
		Collections.sort(topics, new Comparator<Integer>() {

			@Override
			// Returns negative if o2 < o1 and vice versa
			public int compare(Integer o1, Integer o2) {
				return o1 - o2;
			}
			
		});

		String space = " ";
		String newline = "\n";
		
		StringBuilder str = new StringBuilder();
		
		for (int i = 0; i < topics.size(); i++) {
			
			int topicID = topics.get(i);
			Hashtable<Integer, Result> results = this.get(topicID);
			
			for (int j = 0; j < results.size(); j++) {
				
				Result result = results.get(j);
				
				str.append(String.valueOf(topicID));
				str.append(space);
				str.append(BM25.q0);
				str.append(space);
				str.append(result.getDocument().getDocNo());
				str.append(space);
				str.append(j);
				str.append(space);
				str.append(result.getScore());
				str.append(space);
				str.append(BM25.bm25);
				str.append(newline);

			}
			
		}
		
		PrintWriter printer = new PrintWriter(path + "BM25Model.txt", "UTF-8");
		printer.print(str.toString());
		printer.close();
//		System.out.println(str.toString());
		
	}

}