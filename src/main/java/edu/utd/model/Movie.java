package edu.utd.model;

public class Movie {

	private String movieId;
	private Double positiveComments;
	private Double negativeComments;
	private Double neutralComments;
	private Double actorFollowers;
	private Double directorFollowers;
	private Double overallWeight;
	private String name;
	private int assignedCluster;
	
	
	public Movie(String movieId2, String name2, Double overallWeight2) {
		this.movieId=movieId2;
		this.name=name2;
		this.overallWeight=overallWeight2;
}
	
	public Movie() {
		// TODO Auto-generated constructor stub
	}

	public int getAssignedCluster() {
		return assignedCluster;
	}

	public void setAssignedCluster(int assignedCluster) {
		this.assignedCluster = assignedCluster;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getOverallWeight() {
		return overallWeight;
	}
	public void setOverallWeight(Double overallWeight) {
		this.overallWeight = overallWeight;
	}
	public String getMovieId() {
		return movieId;
	}
	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}
	public Double getPositiveComments() {
		return positiveComments;
	}
	public void setPositiveComments(Double positiveComments) {
		this.positiveComments = positiveComments;
	}
	public Double getNegativeComments() {
		return negativeComments;
	}
	public void setNegativeComments(Double negativeComments) {
		this.negativeComments = negativeComments;
	}
	public Double getNeutralComments() {
		return neutralComments;
	}
	public void setNeutralComments(Double neutralComments) {
		this.neutralComments = neutralComments;
	}
	public Double getActorFollowers() {
		return actorFollowers;
	}
	public void setActorFollowers(Double actorFollowers) {
		this.actorFollowers = actorFollowers;
	}
	public Double getDirectorFollowers() {
		return directorFollowers;
	}
	public void setDirectorFollowers(Double directorFollowers) {
		this.directorFollowers = directorFollowers;
	}
	
	public String toString() {
		return  "(" + this.getName() + ", " + this.getOverallWeight() +", " + this.directorFollowers +", " + this.getActorFollowers() +", " + this.getPositiveComments() +  ", " + this.getNegativeComments() + ", " + this.getNeutralComments()+ ")";
	}
}
