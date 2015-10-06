
public class Point {
	
	private double[] loc;
	private String type;
	
	public Point (double[] location, String name) {
		loc = location;
		type = name;
	}
	
	public double[] getCoords() {
		return loc;
	}
	
	public String getType() {
		return type;
	}
	
	public int getDim() {
		return loc.length;
	}
	
	public String toString() {
		String result = "[";
		for (int i=0; i<loc.length; i++) {
			if (i == loc.length-1) {
				result += loc[i];
			} else {
				result += (loc[i] + ",");
			}
		}
		result += "]";
		return result;
	}

}
