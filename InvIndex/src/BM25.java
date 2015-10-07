import java.util.*;
import java.io.*;

public class BM25 {
	
	Index invIndex;
	private double k1;
	private int k2;
	
	public BM25 (String inputJsonFile, double k1, int k2) {
		invIndex = new Index(inputJsonFile, false); //default to build from json rather than disk
		this.k1 = k1;
		this.k2 = k2;
	}
	
	public String query (String query, String topicNum) {
		// initialize term and query frequency arrays
		String[] terms = query.toLowerCase().split("\\s+");
		Map<String, Integer> termMap = new HashMap<String, Integer>(); // maps terms to their query frequency, removes duplicated terms
		for (int i=0; i<terms.length; i++) {
			Integer count = termMap.get(terms[i]);
			if (count == null) {
				count = 0;
			}
			termMap.put(terms[i], count + 1);
		}
		
		Map<String, Double> rankMap = new HashMap<String, Double>();
		
		Map<String, List<Posting>> index = invIndex.getIndex();
		// calculates document weight in parts by adding partial weight contributed by each term
		// assumes partial document weight of 1 if term is not in document
		// this is equivalent to assuming half of all documents are non-relevant to term
		// and the term occurs once in each document
		// and the term occurs once in the query
		for (String term : termMap.keySet()) {
			if (index.get(term) != null) {
				for (Posting post : index.get(term)) {
					// haven't got any relevance information
					int r = 0; // number of relevant document containing terms[i]
					int R = 0; // number of total relevant documents
					int n = index.get(term).size(); // number of documents containing terms[i]
					int N = invIndex.getNumDocs();
					double PDR  = (r + 0.5) / (R - r + 0.5); // equal to 1.0 with no relevance info, demonstrates P(D|R) calculation
					double PDNR = (n - r + 0.5) / (N - n - R + r + 0.5); // equal to (n + 0.5) / (N - n + 0.5) with no relevance
					double b    = 0.75; // suggested for its success in TREC experiments
					double K    = k1 * ((1 - b) + (b * invIndex.getDocLengthMap().get(post.getScene()) / invIndex.getAvgDocLength()));
					double tf   = ((k1 + 1) * post.getPos().length) / (K + post.getPos().length);
					double qtf  = ((k2 + 1) * termMap.get(term)) / (k2 + termMap.get(term));
					
					Double partialRank = rankMap.get(post.getScene());
					double rank = Math.log((PDR / PDNR) * tf * qtf);
					if (partialRank == null) {
						partialRank = rank;
					} else {
						partialRank += rank;
					}
					rankMap.put(post.getScene(), partialRank);
				}
			} else {
				System.out.println("Error: " + term + " not in corpus"); // probably a misspelling
			}
		}
		
		List<Document> rankedDocs = new ArrayList<Document>();
		
		for (String scene : rankMap.keySet()) {
			rankedDocs.add(new Document(scene, rankMap.get(scene)));
		}
		Collections.sort(rankedDocs);
		
		int count = 1;
		StringBuilder result = new StringBuilder();
		for (Document scene : rankedDocs) {
			Double rank = Math.pow(Math.E, scene.getRank());
			result.append(topicNum + " skip " + scene.getScene() + "\t" + count++ + " " + rank + " cdemille-bm25\n");
		}
		
		return result.toString();
	}
	
	public static void main(String[] args) {
		long start = System.nanoTime();
		
		BM25 bm25 = new BM25(args[0], 1.2, 100);
		// System.out.println(bm25.query("caesar", "Q0")); // test query (comparing bm25 to ql)
		/*
		try {
			PrintWriter pw = new PrintWriter(new File("bm25.trecrun"));
			
			pw.write(bm25.query("the king queen royalty", "Q1"));
			pw.write(bm25.query("servant guard soldier", "Q2"));
			pw.write(bm25.query("hope dream sleep", "Q3"));
			pw.write(bm25.query("ghost spirit", "Q4"));
			pw.write(bm25.query("fool jester player", "Q5"));
			pw.write(bm25.query("to be or not to be", "Q6"));
			
			pw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		*/
		System.out.println("setting the scene:\n" + bm25.query("setting the scene", "Q7"));
		System.out.println("Runtime: " + (start - System.nanoTime()) * Math.pow(10.0, 9.0));
	}

}
