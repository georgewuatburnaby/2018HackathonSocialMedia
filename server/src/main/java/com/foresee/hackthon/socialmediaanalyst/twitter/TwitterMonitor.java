package com.foresee.hackthon.socialmediaanalyst.twitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.foresee.hackthon.socialmediaanalyst.entity.SocialMediaComment;
import com.foresee.hackthon.socialmediaanalyst.reddit.RedditMonitor;
import com.foresee.hackthon.socialmediaanalyst.reddit.entity.Comment;
import com.foresee.hackthon.socialmediaanalyst.reddit.entity.CommentData;
import com.foresee.hackthon.socialmediaanalyst.reddit.entity.ListResponse;
import com.foresee.hackthon.socialmediaanalyst.repository.SocialMediaCommentRepository;
import com.foresee.hackthon.socialmediaanalyst.service.EngineService;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope("prototype")
public class TwitterMonitor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterMonitor.class);

    @Autowired
    EngineService engineService;

    @Autowired
    SocialMediaCommentRepository repository;

    static Long lastCommentId = null;

    static String searchWord;

    static int limit;

    static int pollRate;

    static Twitter twitter;

    public static void setConfig(final String newSearchWord, final int newLimit, final int newPollRate) {
        searchWord = newSearchWord;
        limit = newLimit;
        pollRate = newPollRate;
        lastCommentId = null;
    }

    public static void init() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("lI3aMoOR9Mkw8PjrTq294t4QK")
                .setOAuthConsumerSecret("iQvrDhnvTnRaLQL1IT7YOFMdsmSurNiJtmvBOVCFn01U7StLxv")
                .setOAuthAccessToken("1663226474-mkLTcAIepNGW3T403v9dLMeS5KgE0uWsVyhgQ8u")
                .setOAuthAccessTokenSecret("Oo4dAiu8VEHyte6FrqscNpAosmcic7HInWqRIPeJ1Gvp1");
        twitter = new TwitterFactory(cb.build()).getInstance();
    }

    @Override
    public void run() {
        while (true) {
            try {
                final Optional<SocialMediaComment> twitterPostOptional = getPost();
                if (twitterPostOptional.isPresent()) {
                    final SocialMediaComment twitterPost = twitterPostOptional.get();
                    final String text = twitterPost.getText();
                    System.out.println(text);
                    final float rating = engineService.getRating(text);
                    System.out.println(rating);
                    twitterPost.setRating(rating);

                    repository.save(twitterPost);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(pollRate);
                } catch (InterruptedException e) {
                    // should never happen
                    e.printStackTrace();
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    protected SocialMediaComment asSocialMediaComment(final Status twitterComment) {
        final SocialMediaComment socialMediaComment = new SocialMediaComment();
        socialMediaComment.setSource("Twitter");

        if (twitterComment.getURLEntities() != null &&
                twitterComment.getURLEntities().length >= 1) {
            socialMediaComment.setUrl(twitterComment.getURLEntities()[0].getExpandedURL());
        }

        socialMediaComment.setText(twitterComment.getText());
        socialMediaComment.setTimeStamp(twitterComment.getCreatedAt().getTime() / 1000);
        return socialMediaComment;
    }

    public Optional<SocialMediaComment> getPost() throws TwitterException {
        System.out.println(searchWord);
        Query query = new Query(searchWord);
        query.setLang("en");
        query.setCount(limit);
        if (lastCommentId != null) {
            query.setSinceId(lastCommentId);
        }

        QueryResult result = twitter.search(query);
        List<Status> comments = result.getTweets();

        // check if there any new comment
        if (comments == null || comments.size() == 0) {
            return Optional.empty();
        }

        // get earliest comment for demo purposes
        final Status twitterComment = comments.get(comments.size() - 1);

        // check for same comment as last time
        if (lastCommentId != null && twitterComment.getId() == lastCommentId) {
            return Optional.empty();
        }

        // save last comment
        lastCommentId = twitterComment.getId();

        return Optional.of(asSocialMediaComment(twitterComment));
    }
}
