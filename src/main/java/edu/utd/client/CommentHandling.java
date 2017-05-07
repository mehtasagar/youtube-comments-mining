package edu.utd.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.common.collect.Lists;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.utd.authentication.Auth;
import edu.utd.model.Content;
import edu.utd.model.Movie;
import edu.utd.model.MovieDTO;

public class CommentHandling {
	/**
	 * Define a global instance of a YouTube object, which will be used to make
	 * YouTube Data API requests.
	 */
	private static YouTube youtube;
	private static final String FILENAME = "./src/main/resources/";

	/**
	 * List, reply to comment threads; list, update, moderate, mark and delete
	 * replies.
	 *
	 * @param args
	 *            command line args (not used).
	 */
	public static void main(String[] args) {
		int noOfPoints = 3;

		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> ids = new ArrayList<String>();
		ArrayList<Double> directorFollowers = new ArrayList<Double>();
		ArrayList<Double> actorFollowers = new ArrayList<Double>();
		names.add("Iron Man 3");
		names.add("The Iceman");
		names.add("The Great Gatsby");
		names.add("The Purge");
		names.add("Star Trek Into Darkness");
		names.add("Epic");
		names.add("Now You See Me");
		names.add("Fast & Furious 6");
		names.add("After Earth");
		names.add("The Internship");
		Boolean localFile = true;
		ArrayList<String> localFileNames = new ArrayList<String>();
		localFileNames.add("Iron Man 3.txt");
		localFileNames.add("The Iceman.txt");
		localFileNames.add("The Great Gatsby.txt");
		localFileNames.add("The_Purge.txt");
		localFileNames.add("StarTrekIntoDarkness.txt");
		localFileNames.add("Epic.txt");
		localFileNames.add("NowYouSeeMe.txt");
		localFileNames.add("Fast&Furious6.txt");
		localFileNames.add("AfterEarth.txt");
		localFileNames.add("TheInternship.txt");

		ids.add("Ke1Y3P9D0Bc");
		ids.add("CJIXOx2-GZ8");
		ids.add("rARN6agiW7o");
		ids.add("K0LLaybEuzA");
		ids.add("QAEkuVgt6Aw");
		ids.add("-xu3JLXfuwQ");
		ids.add("4OtM9j2lcUA");
		ids.add("dKi5XoeTN0k");
		ids.add("CZIt20emgLY");
		ids.add("cdnoqCViqUo");

		directorFollowers.add(7943d);
		directorFollowers.add(1836d);
		directorFollowers.add(8303d);
		directorFollowers.add(1374d);
		directorFollowers.add(657d);
		directorFollowers.add(760d);
		directorFollowers.add(37d);
		directorFollowers.add(6368d);
		directorFollowers.add(7632d);
		directorFollowers.add(6930d);

		actorFollowers.add(530173d);
		actorFollowers.add(4638d);
		actorFollowers.add(568136d);
		actorFollowers.add(27689d);
		actorFollowers.add(44600d);
		actorFollowers.add(930313d);
		actorFollowers.add(67661d);
		actorFollowers.add(616965d);
		actorFollowers.add(257326d);
		actorFollowers.add(6683d);

		List<Movie> movies = new ArrayList<Movie>();
		List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");
		try {
			Credential credential = Auth.authorize(scopes, "commentthreads");

			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
					.setApplicationName("youtube-cmdline-commentthreads-sample").build();

			Double minPositiveSent = Double.MAX_VALUE;
			Double minNegativeSent = Double.MAX_VALUE;
			Double minNeutralSent = Double.MAX_VALUE;
			Double minDirectorFollowers = Double.MAX_VALUE;
			Double minActorFollowers = Double.MAX_VALUE;

			Double maxPositiveSent = Double.MIN_VALUE;
			Double maxNegativeSent = Double.MIN_VALUE;
			Double maxNeutralSent = Double.MIN_VALUE;
			Double maxDirectorFollowers = Double.MIN_VALUE;
			Double maxActorFollowers = Double.MIN_VALUE;

			Double totalDirFol = 0d;
			Double totalActorFol = 0d;
			String videoId;
			// String videoId = getVideoId();
			for (int i = 0; i < noOfPoints; i++) {
				Movie movie = new Movie();
				movie.setName(names.get(i));
				movie.setActorFollowers(actorFollowers.get(i));
				movie.setDirectorFollowers(directorFollowers.get(i));
				videoId = ids.get(i);
				Double positiveSent = new Double(0);
				Double negativeSent = new Double(0);
				Double neutralSent = new Double(0);
				MovieDTO tempMovie = new MovieDTO();
				List<String> allComments = new ArrayList<String>();
				System.out.println("You chose " + videoId + " to subscribe.");
				CommentThreadListResponse videoCommentsListResponse = null;
				List<CommentThread> videoComments = new ArrayList<CommentThread>();

				if (localFile) {
					List<String> comments = new ArrayList<String>();
					try (BufferedReader br = new BufferedReader(new FileReader(FILENAME + localFileNames.get(i)))) {
						String line;
						while ((line = br.readLine()) != null) {
							if (!line.isEmpty() && line.length() > 0)
								comments.add(line);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					tempMovie = analyseComments(comments);
				} else {
					videoCommentsListResponse = youtube.commentThreads().list("snippet").setMaxResults(100l)
							.setVideoId(videoId).setTextFormat("plainText").execute();

					videoComments = videoCommentsListResponse.getItems();
					System.out.println("Total results" + videoCommentsListResponse.getPageInfo().getTotalResults());
					System.out
							.println("Results per page" + videoCommentsListResponse.getPageInfo().getResultsPerPage());
					tempMovie = printComments(videoComments);
					allComments.addAll(tempMovie.getComments());
					positiveSent = positiveSent + tempMovie.getMovie().getPositiveComments();
					negativeSent += tempMovie.getMovie().getNegativeComments();
					neutralSent += tempMovie.getMovie().getNeutralComments();
					String nextPageToken = videoCommentsListResponse.getNextPageToken();
					System.out.println("Next Page Token" + nextPageToken);
					while (nextPageToken != null && !nextPageToken.isEmpty()) {
						CommentThreadListResponse videoCommentsListResponse1 = youtube.commentThreads().list("snippet")
								.setMaxResults(100l).setVideoId(videoId).setPageToken(nextPageToken)
								.setTextFormat("plainText").execute();
						videoComments = videoCommentsListResponse1.getItems();
						System.out
								.println("Total results" + videoCommentsListResponse1.getPageInfo().getTotalResults());
						System.out.println(
								"Results per page" + videoCommentsListResponse1.getPageInfo().getResultsPerPage());
						tempMovie = printComments(videoComments);
						allComments.addAll(tempMovie.getComments());

						positiveSent = positiveSent + tempMovie.getMovie().getPositiveComments();
						negativeSent += tempMovie.getMovie().getNegativeComments();
						neutralSent += tempMovie.getMovie().getNeutralComments();
						nextPageToken = videoCommentsListResponse1.getNextPageToken();

						System.out.println("Next Page Token" + nextPageToken);
					}
				}

				if (negativeSent < minNegativeSent) {
					minNegativeSent = negativeSent;
				}
				if (positiveSent < minPositiveSent) {
					minPositiveSent = positiveSent;
				}
				if (neutralSent < minNeutralSent) {
					minNeutralSent = neutralSent;
				}
				if (actorFollowers.get(i) < minActorFollowers) {
					minActorFollowers = actorFollowers.get(i);
				}
				if (directorFollowers.get(i) < minDirectorFollowers) {
					minDirectorFollowers = directorFollowers.get(i);
				}

				if (negativeSent > maxNegativeSent) {
					maxNegativeSent = negativeSent;
				}
				if (positiveSent > maxPositiveSent) {
					maxPositiveSent = positiveSent;
				}
				if (neutralSent > maxNeutralSent) {
					maxNeutralSent = neutralSent;
				}
				if (actorFollowers.get(i) > maxActorFollowers) {
					maxActorFollowers = actorFollowers.get(i);
				}
				if (directorFollowers.get(i) > maxDirectorFollowers) {
					maxDirectorFollowers = directorFollowers.get(i);
				}

				totalDirFol += directorFollowers.get(i);
				totalActorFol += actorFollowers.get(i);
				double total = negativeSent + positiveSent + neutralSent;
				movie.setNegativeComments(negativeSent / total);
				movie.setPositiveComments(positiveSent / total);
				movie.setNeutralComments(neutralSent / total);
				// saveComments(allComments, movie.getName());

				movies.add(movie);
			}

			for (int i = 0; i < noOfPoints; i++) {
				System.out.println(
						"##############################################################################################################");
				System.out.println(movies.get(i).getName() + " " + movies.get(i).getPositiveComments() + " "
						+ movies.get(i).getNeutralComments() + " " + movies.get(i).getNegativeComments() + " "
						+ movies.get(i).getDirectorFollowers() + " " + movies.get(i).getActorFollowers());
			}

			System.out.println(minPositiveSent + " " + maxPositiveSent);
			System.out.println(minNeutralSent + " " + maxNeutralSent);
			System.out.println(minNegativeSent + " " + maxNegativeSent);
			System.out.println(minDirectorFollowers + " " + maxDirectorFollowers);
			System.out.println(minActorFollowers + " " + maxActorFollowers);

			for (int i = 0; i < noOfPoints; i++) {
				System.out.println(
						"##############################################################################################################");
				movies.get(i).setDirectorFollowers(
						(Math.abs(movies.get(i).getDirectorFollowers() - (totalDirFol / noOfPoints)))
								/ (maxDirectorFollowers - minDirectorFollowers));
				movies.get(i)
						.setActorFollowers((Math.abs(movies.get(i).getActorFollowers() - (totalActorFol / noOfPoints)))
								/ (maxActorFollowers - minActorFollowers));

				// Movie m
				// =normalize(minNegativeSent,maxNegativeSent,movies.get(i).getNegativeComments());
				System.out.println(movies.get(i).getName() + " " + movies.get(i).getPositiveComments() + " "
						+ movies.get(i).getNeutralComments() + " " + movies.get(i).getNegativeComments() + " "
						+ movies.get(i).getDirectorFollowers() + " " + movies.get(i).getActorFollowers());
				movies.get(i).setOverallWeight((movies.get(i).getPositiveComments() * 2)
						+ (movies.get(i).getNegativeComments() * 2) + (movies.get(i).getNeutralComments())
						+ (movies.get(i).getActorFollowers() * 2.5) + (movies.get(i).getDirectorFollowers() * 2.5));
			}
			for (int i = 0; i < noOfPoints; i++) {
				System.out.println("Movie Name: " + movies.get(i).getName() + " Total Weight: "
						+ movies.get(i).getOverallWeight());
			}

			TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

			Map<String, Object> json = new HashMap<String, Object>();
			for (int i = 0; i < noOfPoints; i++) {
				json.put("movie", movies.get(i).getName());
				json.put("weight", movies.get(i).getOverallWeight());

				IndexResponse response = client.prepareIndex("youtube", "comment").setSource(json).get();
			}

			KMeansWithEuclideanDistance kMeans = new KMeansWithEuclideanDistance(3, movies);
			List<Movie> clusterPoints = new ArrayList<Movie>();
			clusterPoints.add(new Movie("Centroid1", "centroid1", 4.0d));
			clusterPoints.add(new Movie("Centroid2", "centroid2", 3.25d));
			clusterPoints.add(new Movie("Centroid3", "centroid3", 2.75d));
			kMeans.findMeans(clusterPoints);

		} catch (GoogleJsonResponseException e) {
			System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "
					+ e.getDetails().getMessage());
			e.printStackTrace();

		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
			e.printStackTrace();
		} catch (Throwable t) {
			System.err.println("Throwable: " + t.getMessage());
			t.printStackTrace();
		}
	}

	private static MovieDTO analyseComments(List<String> comments) {
		Content con = new Content();
		MovieDTO mdto = new MovieDTO();
		Movie m = new Movie();
		Double positiveSentiments = new Double(0);
		Double negativeSentiments = new Double(0);
		Double neutralSentiments = new Double(0);

		if (comments.isEmpty()) {
			System.out.println("Can't get video comments.");
		} else {
			System.out.println("Total comments :: " + comments.size());
			for (String comment : comments) {

				con.setComment(comment);
				performSentimentAnalysis(con);

				String sentiment = con.getSentiment();
				if (sentiment.equalsIgnoreCase("POSITIVE")) {
					positiveSentiments += 1l;
				} else if (sentiment.equalsIgnoreCase("NEGATIVE")) {
					negativeSentiments += 1l;
				} else {
					neutralSentiments += 1l;

				}

			}
			m.setNegativeComments(negativeSentiments);
			m.setPositiveComments(positiveSentiments);
			m.setNeutralComments(neutralSentiments);
			mdto.setMovie(m);
			mdto.setComments(comments);

		}
		return mdto;
	}

	private static Movie normalize(Double minNegativeSent, Double maxNegativeSent, Double negativeComments) {
		// TODO Auto-generated method stub
		Movie m = new Movie();
		double d = (Math.abs(negativeComments - minNegativeSent)) / (maxNegativeSent - minNegativeSent);
		m.setNegativeComments(d);
		return m;
	}

	private static MovieDTO printComments(List<CommentThread> videoComments) {
		Content con = new Content();
		MovieDTO mdto = new MovieDTO();
		Movie m = new Movie();
		Double positiveSentiments = new Double(0);
		Double negativeSentiments = new Double(0);
		Double neutralSentiments = new Double(0);
		List<String> comments = new ArrayList<String>();
		if (videoComments.isEmpty()) {
			System.out.println("Can't get video comments.");
		} else {
			for (CommentThread videoComment : videoComments) {
				CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment().getSnippet();
				con.setComment(snippet.getTextDisplay());
				performSentimentAnalysis(con);
				/*
				 * System.out.println(con.getComment());
				 * System.out.println(con.getSentiment());
				 */

				String comment = con.getComment();
				String sentiment = con.getSentiment();
				comments.add(comment);
				if (sentiment.equalsIgnoreCase("POSITIVE")) {
					positiveSentiments += 1l;
				} else if (sentiment.equalsIgnoreCase("NEGATIVE")) {
					negativeSentiments += 1l;
				} else {
					neutralSentiments += 1l;

				}

			}
			m.setNegativeComments(negativeSentiments);
			m.setPositiveComments(positiveSentiments);
			m.setNeutralComments(neutralSentiments);
			mdto.setMovie(m);
			mdto.setComments(comments);

		}
		return mdto;
	}

	public static Content performSentimentAnalysis(Content t) {
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		int mainSentiment = 0;
		if (t.getComment() != null && t.getComment().length() > 0) {
			int longest = 0;
			Annotation annotation = pipeline.process(t.getComment());
			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String partText = sentence.toString();
				if (partText.length() > longest) {
					mainSentiment = sentiment;
					longest = partText.length();
				}

			}
		}
		switch (mainSentiment) {
		case 2:
			t.setSentiment("NEUTRAL");
			break;
		case 1:
		case 0:
			t.setSentiment("NEGATIVE");
			break;
		case 3:
		case 4:
			t.setSentiment("POSITIVE");
			break;
		default:
			break;
		}
		return t;
	}
}
