import java.io.*;
import java.util.*;

public class DecisionTree {
	
	public static ArrayList<Boolean[]> training = new ArrayList<Boolean[]>();
	static int totalTrainingPols = 0;
	static int repubTrainingPols = 0;
	public static ArrayList<Boolean[]> test = new ArrayList<Boolean[]>();
	
	public static void main (String[] args) {
		readData(training, args[0]); //fills training
		readData(test, args[1]); //fills test
		
		ArrayList<Integer> startAttributes = new ArrayList<Integer>();
		for (int i=1; i<training.get(0).length; i++) {
			startAttributes.add((Integer)i);
		}
		DecisionTree func = new DecisionTree(); //assumed constructor
		TreeNode root = func.buildTree(training, startAttributes);
		func.test(test, root);
	}
	
	private static void readData (ArrayList<Boolean[]> container, String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");
				Boolean[] pol = new Boolean[split.length];
				for (int i=0; i<split.length; i++) {
					if (split[i].equals("y") || split[i].equals("republican")) {
						pol[i] = true;
					} else {
						pol[i] = false;
					}
				}
				container.add(pol);
				totalTrainingPols++;
				if (split[0].equals("republican")) {
					repubTrainingPols++;
				}
			}
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void test (ArrayList<Boolean[]> tests, TreeNode root) {
		for (Boolean[] b : tests) {
			System.out.println(readTree(b, root));
		}
	}
	
	private String readTree (Boolean[] test, TreeNode node) {
		if (node.getProb() != -1.0) {
			if (node.getProb() >= 0.5) {
				return "republican," + node.getProb();
			} else {
				return "democrat," + (1 - node.getProb());
			}
		}
		int attNum = node.getAttNum();
		if (test[attNum-1]) {
			return readTree(test, node.getPosBranch());
		} else {
			return readTree(test, node.getNegBranch());
		}
	}
	
	private TreeNode buildTree (ArrayList<Boolean[]> availPols, ArrayList<Integer> attributes) {
		double repubProb = 0.0;
		for (Boolean[] b : availPols) {
			if (b[0]) {
				repubProb += 1.0;
			}
		}
		repubProb /= (double)availPols.size();
		if (repubProb == 1.0 || repubProb == 0.0 || attributes.size() == 0) {
			return new TreeNode(repubProb);
		}
		
		double maxGain = 0.0;
		int maxAtt = 0;
		for (Integer i : attributes) {
			double tmp;
			if ((tmp = infoGain(i, availPols)) > maxGain) {
				maxGain = tmp;
				maxAtt = i;
			}
		}
		ArrayList<Integer> newAttributes = new ArrayList<Integer>(attributes);
		newAttributes.remove((Integer)maxAtt);
		Object[] split = getSplit(maxAtt, availPols);
		ArrayList<Boolean[]> splitPos = (ArrayList<Boolean[]>)split[0];
		ArrayList<Boolean[]> splitNeg = (ArrayList<Boolean[]>)split[1];
		
		TreeNode result = new TreeNode(maxAtt);
		result.setPosBranch(buildTree(splitPos, newAttributes));
		result.setNegBranch(buildTree(splitNeg, newAttributes));
		return result;
	}
	
	private double infoGain (int attNum, ArrayList<Boolean[]> parentPols) {
		double totalPos = 0;
		double repubPos = 0;
		double totalNeg = 0;
		double repubNeg = 0;
		double totalRepub = 0;
		
		for (Boolean[] b : parentPols) {
			if (b[attNum]) {
				totalPos++;
				if (b[0]) {
					repubPos++;
					totalRepub++;
				}
			} else {
				totalNeg++;
				if (b[0]) {
					repubNeg++;
					totalRepub++;
				}
			}
		}
		
		double gain = B((double)totalRepub / (double)parentPols.size());
		double remainder = (totalPos / totalTrainingPols) * B(repubPos / totalPos) + (totalNeg / totalTrainingPols) * B(repubNeg / totalNeg);
		if (remainder == 0.0) {
			return Double.MAX_VALUE;
		} else {
			return gain - remainder;
		}
	}
	
	private double B (double prob) { //binary entropy
		if (prob == 0.0 || prob == 1.0) {
			return 0.0;
		}
		double result = - (prob * (Math.log(prob) / Math.log(2.0)) + (1-prob) * (Math.log(1-prob) / Math.log(2.0)));
		return result;
	}
	
	private Object[] getSplit (int attNum, ArrayList<Boolean[]> toSplit) {
		Object[] result = new Object[2];
		result[0] = new ArrayList<Boolean[]>();
		result[1] = new ArrayList<Boolean[]>();
		for (Boolean[] b : toSplit) {
			if (b[attNum]) {
				ArrayList<Boolean[]> res0 = (ArrayList<Boolean[]>)result[0];
				res0.add(b);
			} else {
				ArrayList<Boolean[]> res1 = (ArrayList<Boolean[]>)result[1];
				res1.add(b);
			}
		}
		return result;
	}

}
