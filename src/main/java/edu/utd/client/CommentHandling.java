package edu.utd.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import edu.utd.authentication.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Comment;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentListResponse;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.common.collect.Lists;

public class CommentHandling {
	/**
	 * Define a global instance of a YouTube object, which will be used to make
	 * YouTube Data API requests.
	 */
	private static YouTube youtube;

	/**
	 * List, reply to comment threads; list, update, moderate, mark and delete
	 * replies.
	 *
	 * @param args
	 *            command line args (not used).
	 */
	public static void main(String[] args) {

		List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");
		try {
			Credential credential = Auth.authorize(scopes, "commentthreads");

			
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
					.setApplicationName("youtube-cmdline-commentthreads-sample").build();

			
			String videoId = getVideoId();
			System.out.println("You chose " + videoId + " to subscribe.");

		
			CommentThreadListResponse videoCommentsListResponse = youtube.commentThreads().list("snippet")
					.setMaxResults(100l).setVideoId(videoId).setTextFormat("plainText").execute();
			List<CommentThread> videoComments = videoCommentsListResponse.getItems();
			System.out.println("Total results" + videoCommentsListResponse.getPageInfo().getTotalResults());
			System.out.println("Results per page" + videoCommentsListResponse.getPageInfo().getResultsPerPage());
			printComments(videoComments);
			String nextPageToken = videoCommentsListResponse.getNextPageToken();
			System.out.println("Next Page Token" + nextPageToken);
			while (nextPageToken != null && !nextPageToken.isEmpty() ) {
				CommentThreadListResponse videoCommentsListResponse1 = youtube.commentThreads().list("snippet")
						.setMaxResults(100l).setVideoId(videoId).setPageToken(nextPageToken).setTextFormat("plainText")
						.execute();
				videoComments = videoCommentsListResponse1.getItems();
				System.out.println("Total results" + videoCommentsListResponse1.getPageInfo().getTotalResults());
				System.out.println("Results per page" + videoCommentsListResponse1.getPageInfo().getResultsPerPage());
				printComments(videoComments);
				nextPageToken = videoCommentsListResponse1.getNextPageToken();
				System.out.println("Next Page Token" + nextPageToken);
			}

			CommentThread firstComment = videoComments.get(0);

			// Will use this thread as parent to new reply.
			String parentId = firstComment.getId();



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

	
	private static String getVideoId() throws IOException {

		String videoId = "";

		System.out.print("Please enter a video id: ");
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
		videoId = bReader.readLine();

		return videoId;
	}

	
	private static String getText() throws IOException {

		String text = "";

		System.out.print("Please enter a comment text: ");
		BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
		text = bReader.readLine();

		if (text.length() < 1) {
			// If nothing is entered, defaults to "YouTube For Developers."
			text = "YouTube For Developers.";
		}
		return text;
	}

	private static void printComments(List<CommentThread> videoComments) {
		Content con = new Content();
		if (videoComments.isEmpty()) {
			System.out.println("Can't get video comments.");
		} else {
			// Print information from the API response.
			try{
			    PrintWriter writer = new PrintWriter("YoutubeComments.txt", "UTF-8");
			    for (CommentThread videoComment : videoComments) {
					CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment().getSnippet();
					con.setComment(snippet.getTextDisplay());
					performSentimentAnalysis(con);
					System.out.println(con.getComment());
					System.out.println(con.getSentiment());
					String comment = con.getComment();
					String sentiment = con.getSentiment();
					writer.println("Comment :"+comment+":.:Sentiment :"+sentiment);
					
					
				}
			   
			} catch (IOException e) {
			   // do something
			}
			
			
		}
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
