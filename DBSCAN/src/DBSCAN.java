import java.util.*;
import java.io.*;

public class DBSCAN {
	
	private double eps;
	private int minPts;
	private ArrayList<Point> points = new ArrayList<Point>();
	private HashMap<Point, Integer> clusterMap = new HashMap<Point, Integer>();
	private Integer clusterIdx = new Integer(-1);
	private HashMap<Point, Integer> nMap = new HashMap<Point, Integer>();
	
	public static void main (String[] args) {
		DBSCAN db = new DBSCAN(args[0], args[1]);
		db.printClusters();
		//db.printNeighbors();
	}
	
	public DBSCAN (String dataSet, String e) {
		readData(dataSet, e);
		
		for (Point p : points) { //for points not already in cluster, evaluate for addition to cluster and if yes, evaluate neighbors
			if (clusterMap.get(p) == null) {
				ArrayList<Point> neighbors = regionQuery(p);
				nMap.put(p, neighbors.size());
				if (neighbors.size() < (double)minPts) {
					clusterMap.put(p, -1);
				} else {
					clusterMap.put(p, ++clusterIdx);
					expandCluster(neighbors);
				}
			}
		}
	}
	
	private void readData (String dataLoc, String e) { //read from input data, populate list of points
		eps = Double.parseDouble(e);
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataLoc));
			String line;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(" ");
				double[] coords = new double[split.length-1];
				for (int i=0; i<split.length-1; i++) {
					coords[i] = Double.parseDouble(split[i+1]);
				}
				points.add(new Point(coords, split[0]));
			}
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		minPts = points.get(0).getDim() + 1;
	}
	
	private ArrayList<Point> regionQuery (Point qPoint) { //returns qPoint's e-neighborhood
		ArrayList<Point> neighbors = new ArrayList<Point>();
		for (Point p : points) {
			double sqDist = 0.0;
			double[] pCoords = p.getCoords();
			double[] qCoords = qPoint.getCoords();
			for (int i=0; i<p.getDim(); i++) {
				sqDist += (double)Math.pow((pCoords[i] - qCoords[i]), 2);
			}
			
			if (sqDist <= eps) {
				neighbors.add(p);
			}
		}
		return neighbors;
	}
	
	private void expandCluster (ArrayList<Point> neighbors) { //considers all neighbors for inclusion in current cluster (clusterIdx)
		for (int i=0; i<neighbors.size(); i++) {
			Point p = neighbors.get(i);
			if (clusterMap.get(p) == null) {
				ArrayList<Point> newNeighbors = regionQuery(p);
				nMap.put(p, newNeighbors.size());
				if (newNeighbors.size() >= minPts) {
					neighbors = union(neighbors, newNeighbors);
				}
			}
			if (clusterMap.get(p) == null || clusterMap.get(p).equals((Integer)(-1))) {
				clusterMap.put(p, clusterIdx);
			}
		}
	}
	
	private ArrayList<Point> union (List<Point> list1, List<Point> list2) { //simple set union for use in expandCluster
		ArrayList<Point> result = new ArrayList<Point>(list1);
		for (Point p : list2) {
			if (!result.contains(p)) {
				result.add(p);
			}
		}
		return result;
	}
	
	private void printClusters () {
		for (Point p : points) {
			System.out.println(clusterMap.get(p));
		}
	}
	
	private void printNeighbors () { //good for debugging
		for (Point p : points) {
			ArrayList<Point> n = regionQuery(p);
			String printer = (p + ": ");
			for (Point np : n) {
				printer += (np.toString() + " ");
			}
			System.out.println(printer);
		}
	}

}
