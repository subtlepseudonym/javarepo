
public class TreeNode {
	
	int attributeNum = -1; //only used in decision nodes
	TreeNode pos, neg; //only used in decision nodes
	double posProb = -1.0; //fraction of republicans in leaf node
	
	public TreeNode (int attNum) { //DECISION NODE CONSTRUCTOR
		attributeNum = attNum;
	}
	
	public TreeNode (double prob) { //LEAF NODE CONSTRUCTOR
		posProb = prob;
	}
	
	public int getAttNum() { //returns -1 if leaf node
		return attributeNum;
	}
	
	public TreeNode getPosBranch() {
		return pos;
	}
	
	public void setPosBranch(TreeNode p) {
		pos = p;
	}
	
	public TreeNode getNegBranch() {
		return neg;
	}
	
	public void setNegBranch(TreeNode n) {
		neg = n;
	}
	
	public double getProb() { //returns -1.0 if decision node
		return posProb;
	}
	
	@Override
	public String toString() {
		String result = "attNum: " + attributeNum + "\nrepubProb: " + posProb + "\n";
		if (pos == null || neg == null) {
			return result;
		} else {
			result += "POSBRANCH:\n" + pos.toString() + "NEGBRANCH:\n" + neg.toString();
			return result;
		}
	}

}
