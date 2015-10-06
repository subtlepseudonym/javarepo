import java.io.File;
import java.util.*;


public class GibbsSampler {

	public static void main(String[] args) {
		long runtime = System.nanoTime();
    	
		//INIT ALL RESOURCES
		
        String fileContents = RejectionSampler.readEntireFile(new File(args[0]));
        Map<String, Node> nodeMap = Node.nodesFromString(fileContents);
        String[] nodes = Node.getNodeOrder(fileContents);
        
        String queryString = RejectionSampler.readEntireFile(new File(args[1]));
        Object[] query = Node.queryFromString(queryString);
        
        String[] qNodes = (String[])query[0];
        Map<String, Boolean> evidence = (Map<String, Boolean>)query[1];
        
        int[] probDist = new int[(int)Math.pow(2, qNodes.length)];
        int validSamples = 0;
        int iter = 10 * (int)Integer.parseInt(args[2]) + 1000; //number of iterations + burn in
        Random rand = new Random();//0xCAFEDEAD);
        
        Map<Node, Boolean> state = new HashMap<Node, Boolean>(); //init state
        for (String name : nodes) {
        	Node node = nodeMap.get(name);
        	if (evidence.get(node) != null) {
        		state.put(node, evidence.get(node));
        	} else {
        		boolean val = rand.nextBoolean();
        		state.put(node, val);
        	}
        }
        
        String[] enumNodes = new String[nodes.length - evidence.size()];
        int eIdx = 0;
        for (String name : nodes) {
        	if (evidence.get(name) == null) {
        		enumNodes[eIdx++] = name;
        	}
        }
        
        String[] enumStates = new String[(int)Math.pow(2, enumNodes.length)]; //for iterating through nodes to be enumerated over
        for (int i=0; i<(int)Math.pow(2, enumNodes.length); i++) {
        	enumStates[i] = Integer.toBinaryString(i);
        	for (int j=0; j<(enumNodes.length - enumStates[i].length()); j++) {
        		enumStates[i] = '0' + enumStates[i];
        	}
        }
        
        // DONE INIT RESOURCES
        
        int accept = 0;
        for (int i=0; i<iter; i++) {
			
			double[] workDist = {0.0, 0.0}; //working distribution for current sample [T, F]
			String name = enumNodes[rand.nextInt(enumNodes.length)]; //choose sample randomly from nodes to enumerate over
			Node node = nodeMap.get(name);
			for (String eState : enumStates) { //iterate through enumeration states ex. for (A,B) enumStates = {FF,FT,TF,TT}
				Map<String, Boolean> eStateMap = new HashMap<String, Boolean>(); //creating enumState map from binary string
				for (int j=0; j<enumNodes.length; j++) {
					eStateMap.put(enumNodes[j], (eState.charAt(j) == '1'));
				}
				
				double stateProb = 1.0; //will be multiplied by each conditional probability => (1.0)P(W|R,S)P(R|C)P(S|C)P(C)
				for (String s : enumNodes) { //only calculate conditional probability for enumeration nodes
					Node n = nodeMap.get(s); // (if C,S are evidence, P(S|C) and P(C) aren't needed)
					Boolean val = eStateMap.get(s);
				
					int idx = 0;
					int addIdx = n.getProbs().length / 2;
					for (Node rent : n.getParents()) { //calculate conditional probability for n
						Boolean rVal = (eStateMap.get(rent.name) != null) ? eStateMap.get(rent.name): evidence.get(rent.name); 
						if (!rVal) {
							idx += addIdx;
						}	
						addIdx /= 2;
					}
				
					if (val) {
						stateProb *= n.getProbs()[idx]; //multiply to build probability of current enumState
					} else {
						stateProb *= (1 - n.getProbs()[idx]);
					}
				}
				if (eStateMap.get(name)) { //add to build non-normalized distribution for node
					workDist[0] += stateProb; 
				} else {
					workDist[1] += stateProb;
				}
			}
			double divisor = workDist[0] + workDist[1]; //size of probability space
			double normProb;
			if (divisor == 0) { //avoid dividing by zero (happened during testing, better to have and not need)
				normProb = 0.0;
			} else {        		
				normProb = workDist[0]/divisor; //normalized!
			}
			if (rand.nextDouble() > normProb) { //set value of sample node
				state.put(node, false);
			} else {
				state.put(node, true);
			}
			
			if (iter > 1000 && accept % 10 == 0) { //wait for burn in and sample every tenth state
				int idx = 0;
				int addIdx = probDist.length / 2;
				for (String s : qNodes) {
					Node n = nodeMap.get(s);
					if (!state.get(n)) {
						idx += addIdx;
					}
					addIdx /= 2;
				}
				probDist[idx]++;
				validSamples++;
			}
			accept++;
		}
		
        
        runtime = System.nanoTime() - runtime; //time to print the goods
        
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
        System.err.println((double)(iter) / (double)(runtime / Math.pow(10,9)));
	}
	
}
