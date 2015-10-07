import java.util.*;
import java.io.*;

public class SumSet {

	ArrayList<Integer> set = new ArrayList<Integer>();
	
	public SumSet(ArrayList<Integer> arr) {
		set = arr;
	}
	
	public ArrayList<Integer> getSet() {
		return set;
	}
	
	public boolean contains(Integer i) {
		return set.contains(i);
	}
	
	@Override
	public boolean equals(Object o) {
		SumSet other = (SumSet)o;
		return getSet().equals(other.getSet());
	}
	
	@Override
	public String toString() {
		return set.toString();
	}
	
	public static ArrayList<SumSet> readSets(int sum, int len) {
		ArrayList<SumSet> result = new ArrayList<SumSet>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("src/sets.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("Sum " + Integer.toString(sum))) {
					while ((line = br.readLine()) != null && !line.contains("Sum")) {
						if (line.length() == (2*len) + 2) {
							String[] nums = line.split("[\\t(,)\\n]+");
							ArrayList<Integer> setNums = new ArrayList<Integer>();
							for (String s : nums) {
								if (!s.equals("")) {
									setNums.add(Integer.parseInt(s));
								}
							}
							result.add(new SumSet(setNums));
						}
					}
					break;
				}
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("WARNING: set lookup table sets.txt not found in local directory\n");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("I/O error in set lookup table\n");
			e.printStackTrace();
		}
		
		return result;
	}
}
