package edu.utd.client;

import java.util.ArrayList;
import java.util.List;

import edu.utd.model.Movie;

public class KMeansWithEuclideanDistance {

	private int numberOfCluster = -1;

	private List<Movie> pointList = null;
	private List<Cluster> clusterList = new ArrayList<Cluster>();
	private List<Movie> inputPoints = null;
	private final int MAX_ITERATIONS = 25;

	public KMeansWithEuclideanDistance(int numberOfCluster, List<Movie> movies) {

		this.numberOfCluster = numberOfCluster;
		this.inputPoints = movies;
	}

	public void findMeans(List<Movie> initialClusters) {
		pointList = inputPoints;

		// get Initial Clusters
		int i = 0;
		for (Movie initialCluster : initialClusters) {
			clusterList.add(new Cluster(initialCluster, i));
			//clusterList.add(new Cluster(new Points(initialCluster.getX(), initialCluster.getY()), i));
			i++;
		}
		
		findMeanIterativly();
		// calucalteSSE();
	}

	private void findMeanIterativly() {
		int loopCount = 0;
		while (loopCount < MAX_ITERATIONS) {
			List<Movie> prevCentroids = copyCentroids();
			clearClusterPoints();
			assignPointsToCluster();
			System.out.println("iteration : " + (loopCount + 1));
			printClusters();
			System.out.println("-------------------------------------------------------------");
			calculateNewCentroids();
			List<Movie> newCentroids = copyCentroids();
			if (!isCentroidsChanged(prevCentroids, newCentroids)) {
				break;
			}
			loopCount++;
		}
//		System.out.println("iteration : " + (loopCount + 1));
//		printClusters();
//		System.out.println("-------------------------------------------------------------");
	}

	private void printClusters() {
		// System.setOut(new PrintStream(new File(outputFileName)));
		for (Cluster cluster : clusterList) {
			System.out.print(cluster.toString() + " - ");
			// System.out.println("Cluster " +
			// cluster.getCentroid().toString());
			for (Movie points : cluster.getClusterPoints()) {
				// System.out.print(points.getId() + ",");
				System.out.print(points.toString() + " ");
			}
			System.out.println("");
		}
	}

	private boolean isCentroidsChanged(List<Movie> prevCentroids, List<Movie> newCentroids) {
		double dist = 0;
		for (int i = 0; i < newCentroids.size(); i++) {
			dist += calculateDistance(prevCentroids.get(i), newCentroids.get(i));
			if (dist > 0)
				return true;
		}
		return false;
	}

	private void clearClusterPoints() {
		for (Cluster cluster : clusterList) {
			cluster.clearClusterPoints();
		}
	}

	private void calculateNewCentroids() {
		for (Cluster cluster : clusterList) {
			double sumOfX = 0;
			double sumOfY = 0;
			int pointsCnt = cluster.getClusterPoints().size();
			if (pointsCnt > 0) {
				for (Movie points : cluster.getClusterPoints()) {
					sumOfX += points.getOverallWeight();

				}
				Movie centroid = cluster.getCentroid();
				centroid.setOverallWeight(sumOfX / pointsCnt);
				//centroid.setY(sumOfY / pointsCnt);
			}
		}
	}

	private List<Movie> copyCentroids() {
		List<Movie> centroidList = new ArrayList<Movie>();
		for (Cluster cluster : clusterList) {

			Movie centroid = cluster.getCentroid();
			Movie copyCentroid = new Movie(centroid.getMovieId(),centroid.getName(),centroid.getOverallWeight());
			centroidList.add(copyCentroid);
		}
		return centroidList;
	}

	private void assignPointsToCluster() {

		for (Movie point : pointList) {
			double minDistence = Double.MAX_VALUE;
			int centroidTobeAssigned = -1;
			int count = 0;
			for (Cluster cluster : clusterList) {
				double distance = calculateDistance(cluster.getCentroid(), point);
				if (distance < minDistence) {
					minDistence = distance;
					centroidTobeAssigned = count;
				}
				count++;
			}
			point.setAssignedCluster(centroidTobeAssigned);
			Cluster cluster = clusterList.get(centroidTobeAssigned);
			cluster.addClusterPoints(point);
		}
	}

	// private List<Cluster> listClusters(){
	// int size = pointList.size();
	// List<Cluster> clusterListTemp = new ArrayList<Cluster>();
	// List<Integer> centroidIndexLst = new ArrayList<Integer>();
	// for(int i = 0; i < numberOfCluster ; i++){
	// boolean bNextCentroidSelected = false;
	// while(!bNextCentroidSelected){
	// Integer index = (int)(Math.random() * size) ;
	//// Integer index = (int)( i * size / 6 );
	// if(!centroidIndexLst.contains(index)){
	// centroidIndexLst.add(index);
	// bNextCentroidSelected = true;
	//
	// Points selectedPoint = pointList.get(index);
	// Points centroid = new Points(selectedPoint.getX(), selectedPoint.getY());
	// Cluster cluster = new Cluster(centroid, i + 1 );
	// clusterListTemp.add(cluster);
	// }
	// }
	// }
	// return clusterListTemp;
	// }
	//
	// TO-DO : change the input reading part here
/*	private List<Movie> readDataIntoList(List<Movie> movies) {
		List<Movie> pointList = new ArrayList<Movie>();

		for (Movie point : movies) {
			pointList.add(new Points(point.getX(), point.getY()));
		}

		return pointList;
	}*/

	private double calculateDistance(Movie centroid, Movie genPoint) {
		return Math.sqrt(Math.pow((centroid.getOverallWeight() - genPoint.getOverallWeight()), 2.0));
	}

	// public void calucalteSSE(){
	// double sum = 0;
	// for(Cluster cluster : clusterList){
	// for(Points points : cluster.getClusterPoints()){
	// double temp = calculateDistance(cluster.getCentroid(), points);
	// sum += temp*temp;
	// }
	// }
	// System.out.println(sum);
	// }
}

/*class Points {
	private double x;
	private double y;
	// private int id;
	private int assignedCluster;

	public Points(double x, double y) {
		this.x = x;
		this.y = y;
		// this.id = id;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	// public int getId() {
	// return id;
	// }
	// public void setId(int id) {
	// this.id = id;
	// }
	public int getAssignedCluster() {
		return assignedCluster;
	}

	public void setAssignedCluster(int assignedCluster) {
		this.assignedCluster = assignedCluster;
	}

	@Override
	public String toString() {
		return  "Id : " + this.id +  "(" + this.x + ", " + this.y + ")";
	}
}*/

class Cluster {
	private List<Movie> clusterPoints = new ArrayList<Movie>();
	private Movie centroid = null;
	private int index;

	public Cluster(Movie centroid, int index) {
		this.centroid = centroid;
		this.index = index;
	}

	public List<Movie> getClusterPoints() {
		return clusterPoints;
	}

	public void setClusterPoints(List<Movie> clusterPoints) {
		this.clusterPoints = clusterPoints;
	}

	public void addClusterPoints(Movie point) {
		clusterPoints.add(point);
	}

	public void clearClusterPoints() {
		clusterPoints.clear();
	}

	public Movie getCentroid() {
		return centroid;
	}

	public void setCentroid(Movie centroid) {
		this.centroid = centroid;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/*
	 * public void printCluster(){
	 * 
	 * System.out.println(this.centroid.toString() + " - "); for(Points points :
	 * this.clusterPoints){ System.out.print(points.toString() + ","); }
	 * System.out.println(""); }
	 */

	public String toString() {
		return /* "Id : " + this.id + */ "(" + this.centroid.getName() + ", " + this.centroid.getOverallWeight() + ")";
	}
}