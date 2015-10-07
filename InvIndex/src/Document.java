
public class Document implements Comparable<Document> {
	
	double rank;
	String scene;
	
	public Document (String scene, double rank) {
		this.rank = rank;
		this.scene = scene;
	}
	
	public String getScene () {
		return scene;
	}
	
	public double getRank () {
		return rank;
	}
	
	public int compareTo(Document other) {
		if (other.getRank() > rank) {
			return 1;
		} else if (other.getRank() == rank) {
			return 0;
		} else {
			return -1;
		}
	}

}
