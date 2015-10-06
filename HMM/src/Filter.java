import java.io.*;

public class Filter {
	
	private double unfairProb, fToUProb, uToFProb;
	private String[] flips;
	private double lastCoin;
	
	public static void main (String[] args) throws IOException {
		double ufProb, f2uProb, u2fProb;
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		ufProb = Double.parseDouble(br.readLine());
		f2uProb = Double.parseDouble(br.readLine());
		u2fProb = Double.parseDouble(br.readLine());
		String[] seq = br.readLine().split(" ");
		br.close();
		
		Filter fil = new Filter(ufProb, f2uProb, u2fProb, seq);
		fil.printLastProb();
	}
	
	public Filter (double ufProb, double f2uProb, double u2fProb, String[] seq) {
		unfairProb = ufProb;
		fToUProb = f2uProb;
		uToFProb = u2fProb;
		flips = seq;
		
		lastCoin = findLastProb(1, 1.0);
	}
	
	private double findLastProb (int flipIdx, double prevFairProb) { //recursive forward algorithm
		if (flipIdx >= flips.length) {
			return prevFairProb;
		}
		double fProb, uProb;
		boolean obsHeads = flips[flipIdx].equals("h"); //is observation at t=flipIdx heads
		double obsFairProb = 0.5; //P(et|Xt=f) = P(et=h|Xt=f) = P(et=t|Xt=f)
		double obsUnfairProb; //P(et|Xt=u)
		if (obsHeads) {obsUnfairProb = unfairProb;} //P(et=h|Xt=u)
		else {obsUnfairProb = (1-unfairProb);} //P(et=t|Xt=u)
		fProb = ((prevFairProb*(1-fToUProb)) + ((1-prevFairProb)*uToFProb)) * obsFairProb;
		//Above: ((P(Xt-1=f)P(Xt=f|Xt-1=f)) + (P(Xt-1=u)P(Xt=f|Xt-1=u))) * P(et|Xt=f)
		uProb = ((prevFairProb*fToUProb) + ((1-prevFairProb)*(1-uToFProb))) * obsUnfairProb;
		//Above: (P(Xt-1=f)P(Xt=u|Xt-1=f)) + (P(Xt-1=u)P(Xt=u|Xt-1=u))) * P(et|Xt=u)
		double fairProb = fProb / (fProb + uProb); //normalize
		return findLastProb(++flipIdx, fairProb); //recurse
	}
	
	public void printLastProb () {
		System.out.println(lastCoin);
	}

}
