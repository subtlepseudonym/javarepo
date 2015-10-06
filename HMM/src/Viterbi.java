import java.io.*;

public class Viterbi {
	
	private double unfairProb, fToUProb, uToFProb;
	private String[] flips;
	private String[][] paths;
	int lastFlip;
	
	public static void main (String[] args) throws IOException {
		double ufProb, f2uProb, u2fProb;
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		ufProb = Double.parseDouble(br.readLine());
		f2uProb = Double.parseDouble(br.readLine());
		u2fProb = Double.parseDouble(br.readLine());
		String[] seq = br.readLine().split(" ");
		br.close();
		
		Viterbi vit = new Viterbi(ufProb, f2uProb, u2fProb, seq);
		vit.printPath();
	}
	
	public Viterbi (double ufProb, double f2uProb, double u2fProb, String[] seq) {
		unfairProb = ufProb;
		fToUProb = f2uProb;
		uToFProb = u2fProb;
		flips = seq;
		paths = new String[2][flips.length]; //gives backward path (best predecessor)
		
		lastFlip = calcPath(1, 1.0, 0.0);
	}
	
	private int calcPath (int flipIdx, double prevFairProb, double prevUnfairProb) {
		if (flipIdx >= flips.length) {
			if (prevFairProb >= prevUnfairProb) {
				return 0; //last flip was fair
			} else {
				return 1; //last flip was unfair
			}
		}
		double fProb, uProb;
		boolean obsHeads = flips[flipIdx].equals("h"); //is observation at t=flipIdx heads
		double obsFairProb = Math.log(0.5); //P(et|Xt=f) = P(et=h|Xt=f) = P(et=t|Xt=f)
		double obsUnfairProb; //P(et|Xt=u)
		if (obsHeads) {obsUnfairProb = Math.log(unfairProb);} //P(et=h|Xt=u)
		else {obsUnfairProb = Math.log(1-unfairProb);} //P(et=t|Xt=u)
		fProb = Math.max((prevFairProb + Math.log(1-fToUProb)), (prevUnfairProb + Math.log(uToFProb))) + obsFairProb;
		uProb = Math.max((prevFairProb + Math.log(fToUProb)), (prevUnfairProb + Math.log(1-uToFProb))) + obsUnfairProb;
		if (fProb == prevFairProb + Math.log(1-fToUProb) + obsFairProb) {paths[0][flipIdx] = "f";}
		else {paths[0][flipIdx] = "u";}
		if (uProb == prevFairProb + Math.log(fToUProb) + obsUnfairProb) {paths[1][flipIdx] = "f";}
		else {paths[1][flipIdx] = "u";}
		return calcPath(++flipIdx, fProb, uProb);
	}
	
	public void printPath () {
		String[] path = new String[flips.length];
		int stateIdx = lastFlip;
		for (int i=path.length-1; i>0; i--) {
			path[i] = paths[stateIdx][i];
			if (path[i] == "f") {stateIdx = 0;}
			else {stateIdx = 1;}
		}
		path[0] = "f";
		for (int i=0; i<path.length; i++) {
			System.out.print(path[i] + " ");
		}
		System.out.println();
	}

}
