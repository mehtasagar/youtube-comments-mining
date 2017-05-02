package edu.utd.model;

import java.util.List;

public class MovieDTO {
 
	private List<String> comments;
	private Movie movie;
	public List<String> getComments() {
		return comments;
	}
	public void setComments(List<String> comments) {
		this.comments = comments;
	}
	public Movie getMovie() {
		return movie;
	}
	public void setMovie(Movie movie) {
		this.movie = movie;
	}
	
	
	
}
