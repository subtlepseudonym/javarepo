import java.io.*;
import java.util.*;

public class KMeans {
	
	private ArrayList<Point> points = new ArrayList<Point>();
	private ArrayList<Point> centers = new ArrayList<Point>();
	private int numClusters;
	private HashMap<Point, Integer> clusterMap = new HashMap<Point, Integer>();

	public static void main (String[] args) {
		KMeans clusterCalc = new KMeans(args[0], args[1]);		
		while (!clusterCalc.formClusters()) {
			clusterCalc.calcCenters();
		}
		clusterCalc.printTypes();
	}
	
	public KMeans (String dataLoc, String k) { //init data in constructor
		numClusters = Integer.parseInt(k);
		
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i=0; i<numClusters; i++) {
			centers.add(points.get(i));
		}
	}
	
	public boolean formClusters () {
		boolean steady = true;
		for (Point p : points) {
			double[] sqDist = new double[numClusters];
			for (int i=0; i<sqDist.length; i++) {
				double tmpDist = 0;
				double[] point = p.getCoords();
				double[] center = centers.get(i).getCoords();
				for (int j=0; j<p.getDim(); j++) {
					tmpDist += Math.pow((point[j] - center[j]), 2);
				}
				sqDist[i] = tmpDist;
			}
			
			double minDist = Double.MAX_VALUE;
			int clusterIdx = -1;
			for (int i=0; i<sqDist.length; i++) {
				if (sqDist[i] < minDist) {
					minDist = sqDist[i];
					clusterIdx = i;
				}
			}
			
			Integer mapVal = clusterMap.get(p);
			if (mapVal == null || !mapVal.equals((Integer)clusterIdx)) {
				clusterMap.put(p, clusterIdx);
				steady = false;
			}
		}
		
		return steady;
	}
	
	private void calcCenters() {
		double[][] newCenterCoords = new double[numClusters][points.get(0).getDim()];
		int[] clusterSizes = new int[numClusters];
		for (Map.Entry<Point, Integer> entry : clusterMap.entrySet()) {
			double[] entryCoords = entry.getKey().getCoords();
			for (int i=0; i<entryCoords.length; i++) {
				newCenterCoords[entry.getValue()][i] += entryCoords[i];
			}
			clusterSizes[entry.getValue()]++;
		}
		
		for (int i=0; i<numClusters; i++) {
			for (int j=0; j<newCenterCoords[i].length; j++) {
				if (clusterSizes[i] != 0.0) {
					newCenterCoords[i][j] /= (double)clusterSizes[i];
				}
			}
		}
		
		ArrayList<Point> newCenters = new ArrayList<Point>();
		for (int i=0; i<numClusters; i++) {
			if (clusterSizes[i] == 0.0) {
				numClusters--;
			} else {
				newCenters.add(new Point(newCenterCoords[i], centers.get(i).getType()));
			}
		}
		
		centers = newCenters;
	}
	
	private void printTypes() {
		for (Point p : points) {
			System.out.println(clusterMap.get(p));
		}
	}
	
}
