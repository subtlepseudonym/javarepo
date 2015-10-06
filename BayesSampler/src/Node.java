import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.*;

/**
 * Created by liberato on 10/25/14.
 */
public class Node {
    public final String name;
    public final Node[] parents;
    public final double[] probs;

    public Node(String name, Node[] parents, double[] probs) {
        this.name = name;
        this.parents = parents;
        this.probs = probs;
    }
    
    public Node[] getParents() {
    	return parents;
    }
    
    public double[] getProbs() {
    	return probs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{parents=[");
        for (Node parent : parents) {
            sb.append(parent.name);
            sb.append(',');
        }
        sb.append("], probs="+ Arrays.toString(probs) + '}');
        return sb.toString();
    }

    /**
     *
     * @param s a JSON encoded Bayes Net
     * @return a map, from node names to nodes
     */
    public static Map<String, Node> nodesFromString(String s) {
        Map<String, Node> nodeMap = new HashMap<String, Node>();

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray jsonNodes = parser.parse(s).getAsJsonArray();

        for (JsonElement element : jsonNodes) {
            JsonArray jsonNode = element.getAsJsonArray();
            String name = jsonNode.get(0).getAsString();
            String[] parentNames = gson.fromJson(jsonNode.get(1), String[].class);
            Node[] parents = new Node[parentNames.length];
            for (int i=0; i < parentNames.length; i++) {
                parents[i] = nodeMap.get(parentNames[i]);
            }
            double[] probs = gson.fromJson(jsonNode.get(2), double[].class);

            Node node = new Node(name, parents, probs);
            nodeMap.put(name, node);
        }
        return nodeMap;
    }
    
    public static Object[] queryFromString(String s) {
    	Object[] result = new Object[2];
    	
    	Gson gson = new Gson();
    	JsonParser parser = new JsonParser();
    	JsonArray query = parser.parse(s).getAsJsonArray();
    	
    	String[] queryNodes = gson.fromJson(query.get(0), String[].class);
    	
    	JsonArray eviArray = query.get(1).getAsJsonArray();
    	JsonArray valArray = query.get(2).getAsJsonArray();
    	Map<String, Boolean> evidence = new HashMap<String, Boolean>();
    	for (int i=0; i<eviArray.size(); i++) {
    		evidence.put(eviArray.get(i).getAsString(), valArray.get(i).getAsBoolean());
    	}
    	
    	result[0] = (Object[])queryNodes;
    	result[1] = (Object)evidence;
    	return result;
    }
    
    public static String[] getNodeOrder(String s) {    	
    	JsonParser parser = new JsonParser();
    	JsonArray jsonNodes = parser.parse(s).getAsJsonArray();
    	String[] result = new String[jsonNodes.size()];
    	
    	int idx = 0;
    	for (JsonElement element : jsonNodes) {
    		result[idx++] = element.getAsJsonArray().get(0).getAsString();
    	}
    	
    	return result;
    }
}
