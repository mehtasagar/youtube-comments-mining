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
	
	
}
