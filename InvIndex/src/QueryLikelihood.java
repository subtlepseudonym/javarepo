import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class QueryLikelihood {
	
	Index invIndex;
	private double lambda;
	
	public QueryLikelihood (String inputJsonFile, double lambda) {
		invIndex = new Index(inputJsonFile, false);
		this.lambda = lambda;
	}
	
	public String query (String query, String topicNum) {
		String[] terms = query.toLowerCase().split("\\s+");
		
		Map<String, Double> rankMap = new HashMap<String, Double>();
		Map<String, List<Posting>> index = invIndex.getIndex();
		
		for (String term : terms) {
			if (index.get(term) != null) {
				int termCount = 0; // number of occurences of term in the corpus
				for (Posting post : index.get(term)) { // initialize termCount
					termCount += post.getPos().length;
				}
				
				for (Posting post : index.get(term)) {
					// relative word frequency (in document)
					double df = Math.pow(Math.E, Math.log(post.getPos().length) - Math.log(invIndex.getDocLengthMap().get(post.getScene())));
					// relative word frequency (in language)
					double lf = Math.pow(Math.E, Math.log(termCount) - Math.log(invIndex.getDocLengthMap().get("total"))); 
					
					Double partialRank = rankMap.get(post.getScene());
					if (partialRank == null) {
						partialRank = Math.log(((1 - lambda) * df) / (lambda * lf) + 1);
					} else {
						partialRank += Math.log(((1 - lambda) * df) / (lambda * lf) + 1);
					}
					rankMap.put(post.getScene(), partialRank);
				}
			} else {
				System.out.println("Error: " + term + " not in corpus"); // probably misspelled something
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
			result.append(topicNum + " skip " + scene.getScene() + "\t" + count++ + " " + rank + " cdemille-ql\n");
		}
		
		return result.toString();
	}
	
	public static void main(String[] args) {
		long start = System.nanoTime();
		
		QueryLikelihood ql = new QueryLikelihood(args[0], 0.8);
		//ql.query("caesar", "Q0"); // test query (comparing bm25 to ql)
		/*
		try {
			PrintWriter pw = new PrintWriter(new File("ql.trecrun"));
			
			pw.write(ql.query("the king queen royalty", "Q1"));
			pw.write(ql.query("servant guard soldier", "Q2"));
			pw.write(ql.query("hope dream sleep", "Q3"));
			pw.write(ql.query("ghost spirit", "Q4"));
			pw.write(ql.query("fool jester player", "Q5"));
			pw.write(ql.query("to be or not to be", "Q6"));
			
			pw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		*/
		System.out.println("setting the scene:\n" + ql.query("setting the scene", "Q7"));
		System.out.println("Runtime: " + (start - System.nanoTime()) * Math.pow(10.0, 9.0));
	}

}
