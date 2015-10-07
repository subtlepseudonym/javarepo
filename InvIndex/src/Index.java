import java.util.*;
import java.io.*;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Index { //Inverted index for storing positional information
	
	private String input; //input filename
	private Map<String, List<Posting>> index = new HashMap<String, List<Posting>>(); //actual inverted index
	private Map<String, Scene> scenes = new TreeMap<String, Scene>(); //contains scene info
	private Map<String, Integer> docLengthMap = new HashMap<String, Integer>(); //contains doclength info (for each scene)
	private int numDocs = 0;
	private double avgDocLength = 0.0;
	
	public Index (String inputJsonFile, boolean readFromDisk) { //expects to read JSON from input file
		input = inputJsonFile;
		docLengthMap.put("total", 0);
		
		try {
			readJsonFile(readFromDisk);
		} catch (ParseException pe) { //Lazy error handling (corpus is known)
			System.err.println("Error parsing JSON");
			pe.printStackTrace();
		} catch (FileNotFoundException err) {
			System.err.println("JSON file not found on disk");
			err.printStackTrace();
		} catch (IOException ioe) {
			System.err.println("Error reading from file");
			ioe.printStackTrace();
		}
	}
	
	public int getNumDocs () {
		return numDocs;
	}
	
	public double getAvgDocLength () {
		return avgDocLength;
	}
	
	public Map<String, Integer> getDocLengthMap () {
		return docLengthMap;
	}
	
	public Map<String, List<Posting>> getIndex () {
		return index;
	}
	
	private void readJsonFile (boolean read) throws IOException, ParseException, FileNotFoundException {
		if (read) {
			if (buildFromDisk()) {
				return;
			}
		}
		
		BufferedReader br = new BufferedReader(new FileReader(input));
		JSONParser parser = new JSONParser();
		br.readLine(); br.readLine(); //skip first two lines because of formatting
		do {
			String line = "{" + br.readLine() + br.readLine() + br.readLine() + br.readLine() + "}"; //build properly formatted JSON String
			// line should be formatted as follows:
			// { "playID" : "<PLAY>",
			//   "sceneID" : "<PLAY:ACT.SCENE>",
			//   "sceneNum" : <SCENE INDEX>,
			//   "text" : "<SCENE TEXT>" }
			buildIndex((JSONObject)parser.parse(line)); //builds index scene by scene
			numDocs++;
		} while (br.readLine().equals("    } , {")); //skips line between JSON objects and stops just before EOF
		avgDocLength /= numDocs; // total number of words divided by total number of documents
		
		br.close();
	}
	
	private void buildIndex (JSONObject jMap) {
		String sceneId = (String)jMap.get("sceneId");
		int sceneNum = (int)(long)jMap.get("sceneNum"); //unfortunate list of type casts
		String[] terms = ((String)jMap.get("text")).split("\\s+");
		Map<String, List<Integer>> termPos = new HashMap<String, List<Integer>>();
		avgDocLength += terms.length; // add up total word count
		docLengthMap.put(sceneId, terms.length); // map scene name to scene length
		docLengthMap.put("total", docLengthMap.get("total") + terms.length);
		
		scenes.put(sceneId, new Scene(sceneId, sceneNum, terms.length)); //preserve scene information
		
		for (int i=0; i<terms.length; i++) { //build list for posting
			List<Integer> pos = termPos.get(terms[i]);
			if (pos == null) {
				pos = new ArrayList<Integer>();
			}
			
			pos.add((Integer)i);
			termPos.put(terms[i], pos);
		}
		
		for (String term : termPos.keySet()) {
			List<Integer> positions = termPos.get(term);
			int[] pos = new int[positions.size()];
			for (int i=0; i<pos.length; i++) { //from Integer[] to int[] to save space
				pos[i] = positions.get(i);
			}
			
			List<Posting> postings = index.get(term); 
			if (postings == null) { //check if posting already exists, create new one if not
				postings = new ArrayList<Posting>();
			}
			postings.add(new Posting(sceneId, sceneNum, pos)); //new posting for index
			index.put(term, postings);
		}
	}
	
	public void saveIndex () throws FileNotFoundException { //takes roughly 55 seconds to save
		System.out.println("Saving..."); //TODO: improve saving and reading speed by altering format
		PrintWriter pw = new PrintWriter(new File("index.json"));
		for (Map.Entry<String, List<Posting>> entry : index.entrySet()) {
			StringBuilder line = new StringBuilder("{ \"term\" : ");
			line.append("\"" + entry.getKey() + "\", \"postings\" : [ ");
			for (int j=0; j<entry.getValue().size(); j++) {
				Posting post = entry.getValue().get(j);
				line.append("{ \"sceneId\" : \"" + post.getScene() + "\", ");
				line.append("\"sceneNum\" : " + post.getId() + ", ");
				line.append("\"positions\" : [ ");
				for (int i=0; i<post.getPos().length; i++) {
					line.append(post.getPos()[i]);
					if (i < post.getPos().length-1) {
						line.append(", ");
					}
				}
				line.append("] }");
				if (j < entry.getValue().size()-1) {
					line.append(", ");				
				}
			}
			line.append("] }\n");
			pw.write(line.toString());
		}
		pw.close();
		System.out.println("Successfully saved to disk");
	}
	
	private boolean buildFromDisk () throws IOException, ParseException { //slower than building from small raw json file
		try {
			BufferedReader br = new BufferedReader(new FileReader("index.json"));
			JSONParser parser = new JSONParser();
			String line;
			while ((line = br.readLine()) != null) {
				JSONObject indexElement = (JSONObject)parser.parse(line);
				JSONArray postings = (JSONArray)indexElement.get("postings");
				List<Posting> postList = new ArrayList<Posting>();
				for (int i=0; i<postings.size(); i++) {
					JSONObject posts = (JSONObject)postings.get(i);
					JSONArray jsonPositions = (JSONArray)posts.get("positions");
					int[] positions = new int[jsonPositions.size()];
					for (int j=0; j<positions.length; j++) {
						positions[j] = (int)(long)jsonPositions.get(j);
					}
					postList.add(new Posting((String)posts.get("sceneId"), (int)(long)posts.get("sceneNum"), positions));
				}
				
				index.put((String)indexElement.get("term"), postList);
			}
			br.close();
			return true;
		} catch (FileNotFoundException e) {
			System.err.println("Error: index does not exist on disk");
			return false;
		}
	}
	
	public String playQuery (String q) {
		return query(q, true);
	}
	
	public String sceneQuery (String q) {
		return query(q, false);
	}
	
	public Map<String, Integer> getResultScenes (String q) { //useful for getting term counts
		String[] query = q.toLowerCase().trim().split("\\s+");
		return getScenes(generateTerms(query));
	}
	
	private String query (String q, boolean playSearch) { //default to OR over all terms, use "" for exact phrases (ex. "exact phrase")
		String[] query = q.toLowerCase().trim().split("\\s+"); //transform query to provide better search
		//playSearch is whether to just return play names in result
		//termCount adds number of term occurrences per scene to result
		
		Map<String, Integer> terms = generateTerms(query); //generate terms to query index with
		Map<String, Integer> resultScenes = getScenes(terms); //query index and produce ordered list of scenes
		
		StringBuilder result = new StringBuilder(); //build result (meant to be written to file; likely a very large string)
		if (!playSearch) {
			for (String scene : resultScenes.keySet()) {
				result.append(scene + "\n");
			}
		} else {
			Map<String, Boolean> plays = new HashMap<String, Boolean>();
			for (String scene : resultScenes.keySet()) {
				String play = scene.substring(0, scene.indexOf(':'));
				plays.put(play, true);
			}
			
			for (String play : plays.keySet()) {
				result.append(play + "\n");
			}
		}
		return result.toString();
	}
	
	private Map<String, Integer> generateTerms (String[] query) {
		Map<String, Integer> terms = new HashMap<String, Integer>(); //avoids duplicated search terms
		
		String exactPhrase = "";
		boolean phrase = false;
		for (String word : query) {
			if (terms.get(word) != null) { //don't add duplicate terms to search list
				continue;
			}
			
			if (word.charAt(0) == '\"') { //indicates beginning of exact phrase
				exactPhrase += word.substring(1); //cuts preceding " character
				phrase = true;
			} else if (phrase) {
				if (word.endsWith("\"")) { //indicates end of exact phrase
					exactPhrase += " " + word.substring(0, word.length()-1); //cuts trailing " character
					phrase = false;
					terms.put(exactPhrase, 0);
					exactPhrase = "";
				} else {
					exactPhrase += " " + word;
				}
			} else {
				terms.put(word, 0);
			}
			
		}
		if (phrase) {
			System.err.println("Error parsing query: unclosed quotation");
			return new HashMap<String, Integer>(); //empty hashmap
		}
		
		return terms;
	}
	
	private Map<String, Integer> getScenes (Map<String, Integer> terms) {
		Map<String, Integer> resultScenes = new TreeMap<String, Integer>(); //for producing alphabetized list of scenes
		
		for (String term : terms.keySet()) { //obtain list of scenes containing one or more terms (to be stored in resultScenes)
			String[] words = term.split("\\s+"); //splits up phrases for querying index
			Map<String, Integer[]> phraseScenes = new HashMap<String, Integer[]>();
			
			boolean safeTerm = true;
			for (String word : words) {
				if (index.get(word) == null) {
					safeTerm = false;
				}
			}
			if (!safeTerm) {
				continue; //term is not contained in index, skip term
			}
			
			for (Posting post : index.get(words[0])) {
				Integer[] scenePos = new Integer[post.getPos().length+1];
				scenePos[0] = 0; //used as index for removing map entries
				for (int i=1; i<=post.getPos().length; i++) { //put posting positions into map
					scenePos[i] = post.getPos()[i-1];
				}
				phraseScenes.put(post.getScene(), scenePos);
			}
			for (int i=1; i<words.length; i++) { //TODO: iterate through words from least common to most (improves efficiency for longer phrases)
				for (Posting post : index.get(words[i])) {
					Integer[] phrasePos = phraseScenes.get(post.getScene());
					if (phrasePos != null) { //check if word shares scene with phrase
						List<Integer> newPos = new ArrayList<Integer>(); //fill with matching positions (phrase words next to one another)
						newPos.add(i); //shows that this positions array is up to date
						for (int j=1; j<phrasePos.length; j++) { //O(n^2) comparison :(
							int[] postPos = post.getPos();
							for (int k=0; k<postPos.length; k++) {
								if (phrasePos[j].equals(postPos[k] - i)) { //ensures word is at proper position in exact phrase
									newPos.add(phrasePos[j]);
								}
							}
						}
						if (newPos.size() > 1) {
							phraseScenes.put(post.getScene(), newPos.toArray(new Integer[newPos.size()])); //phrase segment exists in scene
						}
					}
				}
				
				List<String> keys = new ArrayList<String>(phraseScenes.keySet());
				for (String key : keys) {
					if (phraseScenes.get(key)[0] != i) {
						phraseScenes.remove(key); //remove scenes that didn't contain subsequent phrase terms (ones that weren't updated above)
					}
				}
			}
			
			for (String scene : phraseScenes.keySet()) {
				Integer currCount = resultScenes.get(scene);
				if (currCount == null) {
					currCount = 0;
				}
				resultScenes.put(scene, phraseScenes.get(scene).length-1 + currCount); //adds number of term occurrences for each scene
			}	
		}
		
		return resultScenes;
	}
	
	public boolean printToFile (String result, String fileName) {
		try { //writes query result to disk
			PrintWriter pw = new PrintWriter(new File(fileName));
			pw.write(result.toString());
			pw.close();
		} catch (IOException e) {
			System.err.println("Error writing to file");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String getPlayId (String sceneId) {
		return sceneId.substring(0, sceneId.indexOf(':'));
	}
	
	public Map<String, Scene> getScenes () {
		return scenes;
	}
	
	public static void main (String[] args) {
		String inputJSON = args[0];
		
		long start = System.nanoTime();
		Index idx = new Index(inputJSON, false); //builds index from raw json file
		System.out.println("Buildtime: " + (System.nanoTime() - start) / Math.pow(10.0, 9.0));
		
		//Example of counting/comparison query
		long time = System.nanoTime();
		Map<String, Integer> tResult = idx.getResultScenes("thou thee");
		Map<String, Integer> yResult = idx.getResultScenes("you");
		StringBuilder result = new StringBuilder();
		for (String scene : tResult.keySet()) {
			if (yResult.get(scene) == null || tResult.get(scene) > yResult.get(scene)) {
				result.append(scene + "\n");
			}
		}
		String example = idx.sceneQuery("\"lady macbeth\""); //Example of scene query with exact phrase
		String example2 = idx.playQuery("verona rome italy"); //Example of play query with three normal terms (treated as "verona OR rome OR italy")
		System.out.println("Querytime: " + (System.nanoTime() - time) / Math.pow(10.0, 9.0));
		
		//Printing scene and play stats
		time = System.nanoTime();
		List<Scene> sceneList = new ArrayList<Scene>(idx.getScenes().values());
		int totalLength = 0;
		String shortest = ""; 
		int shortScene = Integer.MAX_VALUE;
		String currPlay = sceneList.get(0).getPlayId();
		int currPlayLength = sceneList.get(0).size();
		String longer = "";
		int longPlay = Integer.MIN_VALUE;
		String shorter = "";
		int shortPlay = Integer.MAX_VALUE;
		for (int i=1; i<sceneList.size(); i++) {
			if (!sceneList.get(i).getPlayId().equals(currPlay)) {
				if (currPlayLength > longPlay) {
					longPlay = currPlayLength;
					longer = currPlay;
				}
				if (currPlayLength < shortPlay) {
					shortPlay = currPlayLength;
					shorter = currPlay;
				}
				currPlayLength = sceneList.get(i).size();
				currPlay = idx.getPlayId(sceneList.get(i).getSceneId());
			} else {
				currPlayLength += sceneList.get(i).size();
			}
			totalLength += sceneList.get(i).size();
			if (sceneList.get(i).size() < shortScene) {
				shortScene = sceneList.get(i).size();
				shortest = sceneList.get(i).getSceneId();
			}
		}
		if (currPlayLength > longPlay) { //check if last play is longest or shortest
			longPlay = currPlayLength;
			longer = currPlay;
		}
		if (currPlayLength < shortPlay) {
			shortPlay = currPlayLength;
			shorter = currPlay;
		}
		System.out.println("\nAverage scene length: " + (totalLength / sceneList.size()));
		System.out.println("Shortest scene: " + shortest);
		System.out.println("Longest play: " + longer);
		System.out.println("Shortest play: " + shorter);
		System.out.println("Stattime: " + (System.nanoTime() - time) / Math.pow(10.0, 9.0) + "\n");
		
		time = System.nanoTime();
		//idx.printToFile(result.toString(), "example.txt");
		System.out.println("Writetime: " + (System.nanoTime() - time) / Math.pow(10.0, 9.0));
		
		try { // creates graph info (as .csv)
			Map<String, Scene> scenes = idx.getScenes();
			
			Map<Integer, Integer> thou = new TreeMap<Integer, Integer>();
			Map<Integer, Integer> you = new TreeMap<Integer, Integer>();
			for (Scene scene : scenes.values()) {
				Integer tCount = tResult.get(scene.getSceneId()); //getResultScenes already done for "thou thee" above
				Integer yCount = yResult.get(scene.getSceneId()); //getResultScenes already done for "you" above
				if (tCount == null) {
					tCount = 0;
				}
				if (yCount == null) {
					yCount = 0;
				}
				thou.put(scene.getSceneNum(), tCount);
				you.put(scene.getSceneNum(), yCount);
			}
			PrintWriter pwT = new PrintWriter(new File("thou.csv"));
			PrintWriter pwY = new PrintWriter(new File("you.csv"));
			for (int i=0; i<scenes.size(); i++) {
				pwT.write(thou.get(new Integer(i)).toString() + "\n");
				pwY.write(you.get(new Integer(i)).toString() + "\n");
			}
			pwT.close();
			pwY.close();
		} catch (IOException ioe) {
			System.err.println("Error printing graph information");
		}
		
		time = System.nanoTime();
		boolean saveToDisk = false; //saving takes a long time
		if (saveToDisk) {
			try {
				idx.saveIndex(); //saves inverted index to disk
			} catch (FileNotFoundException e) {
				System.err.println("Error saving index to disk");
				e.printStackTrace();
			}
			System.out.println("Savetime: " + (System.nanoTime() - time) / Math.pow(10.0, 9.0));
		}
		
		System.out.println("Total Runtime: " + (System.nanoTime() - start) / Math.pow(10.0, 9.0)); //each time may not sum to total runtime
		
	}

}
