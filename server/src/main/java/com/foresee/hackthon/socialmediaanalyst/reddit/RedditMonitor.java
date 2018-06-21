package com.foresee.hackthon.socialmediaanalyst.reddit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foresee.hackthon.socialmediaanalyst.entity.SocialMediaComment;
import com.foresee.hackthon.socialmediaanalyst.reddit.entity.Comment;
import com.foresee.hackthon.socialmediaanalyst.reddit.entity.CommentData;
import com.foresee.hackthon.socialmediaanalyst.reddit.entity.ListResponse;
import com.foresee.hackthon.socialmediaanalyst.repository.SocialMediaCommentRepository;
import com.foresee.hackthon.socialmediaanalyst.service.EngineService;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
public class RedditMonitor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedditMonitor.class);

    EngineService engineService;

    SocialMediaCommentRepository repository;

    public RedditMonitor(final EngineService engineService, SocialMediaCommentRepository repository) {
        this.engineService = engineService;
        this.repository = repository;
    }

    @Override
    public void run() {
        while (true) {
            try {
                final Optional<SocialMediaComment> redditPostOptional = getPost();
                if (redditPostOptional.isPresent()) {
                    final SocialMediaComment redditPost = redditPostOptional.get();
                    final String text = redditPost.getText();
                    System.out.println(text);
                    final float rating = engineService.getRating(text);
                    System.out.println(rating);
                    redditPost.setRating(rating);

                    repository.save(redditPost);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    Thread.sleep(3000);
                }
                catch (InterruptedException e) {
                    // should never happen
                    e.printStackTrace();
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    static String searchTerm;
    static String lastCommentId = null;

    public static void init(final String newSearchTerm) {
        searchTerm = newSearchTerm;
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    protected SocialMediaComment asSocialMediaComment(final CommentData redditComment) {
        final SocialMediaComment socialMediaComment = new SocialMediaComment();
        socialMediaComment.setSource("Reddit");
        socialMediaComment.setUrl("https://www.reddit.com/" + redditComment.getPermalink());
        socialMediaComment.setText(redditComment.getTitle());
        socialMediaComment.setTimeStamp(String.valueOf(redditComment.getCreated()));
        return socialMediaComment;
    }

    public Optional<SocialMediaComment> getPost() throws UnirestException {
        GetRequest request = Unirest.get("https://www.reddit.com/search.json?q={searchTerm}")
                .header("User-agent", "ForeSee Hackathon Social Media Sentiments 0.1")
                .routeParam("searchTerm", searchTerm);
        request.queryString("sort", "created");
        request.queryString("limit", 5);
        if (lastCommentId != null) {
            request.queryString("before", lastCommentId);
        }

        ListResponse listResponse = request.asObject(ListResponse.class).getBody();
        List<Comment> comments = listResponse.getData().getChildren();

        // check if there any new comment
        if (comments == null || comments.size() == 0) {
            return Optional.empty();
        }

        // get earliest comment for demo purposes
        final List<Comment> commentList = listResponse.getData().getChildren();
        final CommentData redditComment = commentList.get(commentList.size()-1).getData();

        // check for same comment as last time
        if (redditComment.getName().equals(lastCommentId)) {
            return Optional.empty();
        }

        // save last comment
        lastCommentId = redditComment.getName();

        // body text, not used right now
        final String selfText = listResponse.getData().getChildren().get(0).getData().getSelftext();

        return Optional.of(asSocialMediaComment(redditComment));
    }
}