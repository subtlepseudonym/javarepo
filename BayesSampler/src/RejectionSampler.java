import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by liberato on 10/25/14.
 */
public class RejectionSampler {

    public static String readEntireFile(File f) {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // tomfoolery relying on '\A' meaning "separate tokens using only the
        // beginning of the input as a boundary"
        java.util.Scanner scanner = new java.util.Scanner(fin,"UTF-8").useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public static void main(String[] args) {
    	long runtime = System.nanoTime();
    	
        String fileContents = readEntireFile(new File(args[0]));
        Map<String, Node> nodeMap = Node.nodesFromString(fileContents);
        String[] nodes = Node.getNodeOrder(fileContents);
        
        String queryString = readEntireFile(new File(args[1]));
        Object[] query = Node.queryFromString(queryString);
        
        String[] qNodes = (String[])query[0];
        Map<String, Boolean> evidence = (Map<String, Boolean>)query[1];
        
        int[] probDist = new int[(int)Math.pow(2, qNodes.length)];
        int validSamples = 0;
        int iter = (int)Integer.parseInt(args[2]); //number of iterations
        Random rand = new Random();//0xCAFEDEAD);
        for (int i=0; i<iter; i++) {
        	Map<Node, Boolean> state = new HashMap<Node, Boolean>();
        	boolean reject = false;
        	for (String node : nodes) {
        		Node n = nodeMap.get(node);
        		int idx = 0;
        		int addIdx = n.getProbs().length / 2;
        		for (Node rent : n.getParents()) {
        			if (!state.get(rent)) {
        				idx += addIdx;
        			}
        			addIdx /= 2;
        		}
        		if (rand.nextDouble() > n.getProbs()[idx]) {
        			if (evidence.get(node) != null && evidence.get(node) == true) {
        				reject = true;
        				break;
        			}
        			state.put(n, false);
        		} else {
        			if (evidence.get(node) != null && evidence.get(node) == false) {
        				reject = true;
        				break;
        			}
        			state.put(n, true);
        		}
        	}
        	if (!reject) {
        		int probIdx = 0;
        		int addIdx = probDist.length / 2;
        		for (String node : qNodes) {
        			if (!state.get(nodeMap.get(node))) {
        				probIdx += addIdx;
        			}
        			addIdx /= 2;
        		}
        		probDist[probIdx]++;
        		validSamples++;
        	}
        }
        runtime = System.nanoTime() - runtime;
        
        System.out.print("[");
        if (validSamples > 0) {
        	for (int i=0; i<probDist.length; i++) {
        		System.out.print((double)probDist[i] / (double)validSamples);
        		if (i != probDist.length-1) {
        			System.out.print(", ");
        		}
        	}
        }
        System.out.println("]");
        System.err.println((double)iter / (double)(runtime / Math.pow(10,9)));
    }
}
