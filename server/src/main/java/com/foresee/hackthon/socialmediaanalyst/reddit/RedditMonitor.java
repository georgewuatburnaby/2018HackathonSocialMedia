package com.foresee.hackthon.socialmediaanalyst.reddit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foresee.hackthon.socialmediaanalyst.reddit.entity.Comment;
import com.foresee.hackthon.socialmediaanalyst.reddit.entity.ListResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Scope("prototype")
public class RedditMonitor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedditMonitor.class);

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println(RedditMonitor.getLastCommentText());
                Thread.sleep(3000);
            } catch (UnirestException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
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

    public static String getLastCommentText() throws UnirestException {
        GetRequest request = Unirest.get("https://www.reddit.com/search.json?q={searchTerm}")
                .header("User-agent", "ForeSee Hackathon Social Media Sentiments 0.1")
                .routeParam("searchTerm", searchTerm);

        if (lastCommentId == null) {
            request.queryString("before", lastCommentId);
        }
        request.queryString("limit", 1);

        ListResponse listResponse = request.asObject(ListResponse.class).getBody();
        List<Comment> comments = listResponse.getData().getChildren();

        // check if there any new comment
        if (comments == null || comments.size() == 0) {
            return "no comments on this subreddit at all";
        }

        // check for same comment as last time
        if (comments.get(0).getData().getName().equals(lastCommentId)) {
            return "no new comments";
        }

        // have new comment
        lastCommentId = comments.get(0).getData().getName();
        final String selfText = listResponse.getData().getChildren().get(0).getData().getSelftext();
        final String title = listResponse.getData().getChildren().get(0).getData().getTitle();


        return (selfText != null) ? selfText : title;
    }
}