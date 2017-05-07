package edu.utd.client;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.CommentSnippet;
import com.google.api.services.youtube.model.CommentThread;
import com.google.api.services.youtube.model.CommentThreadListResponse;
import com.google.common.collect.Lists;

import edu.utd.authentication.Auth;

public class Test {

	private static final String FILENAME = "./src/main/resources/StarTrekIntoDarkness.txt";
	private static YouTube youtube;

	public static void main(String[] args) {

		List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.force-ssl");

		Credential credential;
		try {
			credential = Auth.authorize(scopes, "commentthreads");

			String videoId = "QAEkuVgt6Aw";
			youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
					.setApplicationName("youtube-cmdline-commentthreads-sample").build();

			CommentThreadListResponse videoCommentsListResponse = youtube.commentThreads().list("snippet")
					.setMaxResults(100l).setVideoId(videoId).setTextFormat("plainText").execute();

			List<CommentThread> videoComments = videoCommentsListResponse.getItems();
			System.out.println("Total results" + videoCommentsListResponse.getPageInfo().getTotalResults());
			System.out.println("Results per page" + videoCommentsListResponse.getPageInfo().getResultsPerPage());

			String nextPageToken = videoCommentsListResponse.getNextPageToken();
			System.out.println("Next Page Token" + nextPageToken);
		


				CommentThreadListResponse videoCommentsListResponse1 = youtube.commentThreads().list("snippet")
						.setMaxResults(100l).setVideoId(videoId).setPageToken(nextPageToken).setTextFormat("plainText")
						.execute();
				videoComments = videoCommentsListResponse1.getItems();
				System.out.println("Total results" + videoCommentsListResponse1.getPageInfo().getTotalResults());
				System.out.println("Results per page" + videoCommentsListResponse1.getPageInfo().getResultsPerPage());
				String s="";
				
				//System.out.println(s);
				
				try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))){
			
				while (nextPageToken != null && !nextPageToken.isEmpty()) {
					for (CommentThread c: videoComments ){
						CommentSnippet snippet = c.getSnippet().getTopLevelComment().getSnippet();
						 s=  snippet.getTextDisplay();
							bw.write(s);
							bw.newLine();
							bw.newLine();
					}
				
					CommentThreadListResponse videoCommentsListResponse11 = youtube.commentThreads().list("snippet")
							.setMaxResults(100l).setVideoId(videoId).setPageToken(nextPageToken)
							.setTextFormat("plainText").execute();
					videoComments.addAll(videoCommentsListResponse11.getItems());
					/*System.out.println("Total results" + videoCommentsListResponse11.getPageInfo().getTotalResults());
					System.out
							.println("Results per page" + videoCommentsListResponse11.getPageInfo().getResultsPerPage());
					*/
					
					nextPageToken = videoCommentsListResponse1.getNextPageToken();

					System.out.println("Next Page Token" + nextPageToken);
				}
				
				
				
				

				// no need to close it.
				// bw.close();

				System.out.println("Done");
				}
			
		} catch (IOException e) {

			e.printStackTrace();

		}

	}
}
