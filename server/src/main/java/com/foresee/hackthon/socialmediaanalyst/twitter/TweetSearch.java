package com.foresee.hackthon.socialmediaanalyst.twitter;

import com.foresee.hackthon.socialmediaanalyst.entity.SocialMediaComment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;
/**
 * @Author Mahsa Forati
 */
@RestController
public class TweetSearch {
    @RequestMapping(value = "/tweeter")
    public List<SocialMediaComment> getComments(@RequestParam String term) throws InterruptedException {
        List<SocialMediaComment> response = new ArrayList<>();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("lI3aMoOR9Mkw8PjrTq294t4QK")
                .setOAuthConsumerSecret("iQvrDhnvTnRaLQL1IT7YOFMdsmSurNiJtmvBOVCFn01U7StLxv")
                .setOAuthAccessToken("1663226474-mkLTcAIepNGW3T403v9dLMeS5KgE0uWsVyhgQ8u")
                .setOAuthAccessTokenSecret("Oo4dAiu8VEHyte6FrqscNpAosmcic7HInWqRIPeJ1Gvp1");
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        try {
            Query query = new Query(term);
            query.setLang("en");
            QueryResult result = null;
            do {
                try{result = twitter.search(query);
                    List<Status> tweets = result.getTweets();
                    for (Status tweet : tweets) {
                        SocialMediaComment comment = new SocialMediaComment();
                        comment.setSource("twitter");
                        comment.setTimeStamp(tweet.getCreatedAt().getTime());
                        comment.setText(tweet.getText());
                        response.add(comment);
                    }
                }catch(NullPointerException e){

                }
            }
            while ((query = result.nextQuery()) != null);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
        }
        return response;
    }
}
