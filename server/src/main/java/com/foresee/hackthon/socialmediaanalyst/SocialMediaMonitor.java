package com.foresee.hackthon.socialmediaanalyst;

import com.foresee.hackthon.socialmediaanalyst.reddit.RedditMonitor;
import com.foresee.hackthon.socialmediaanalyst.repository.SocialMediaCommentRepository;
import com.foresee.hackthon.socialmediaanalyst.service.EngineService;
import com.foresee.hackthon.socialmediaanalyst.twitter.TwitterMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class SocialMediaMonitor implements ApplicationRunner {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private RedditMonitor redditMonitor;

    @Autowired
    private TwitterMonitor twitterMonitor;

    @Value("${search.word}")
    String searchWord;

    @Value("${post.limit}")
    int postLimit;

    @Value("${poll.rate}")
    int pollRate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RedditMonitor.setConfig(searchWord, postLimit, pollRate);
        TwitterMonitor.setConfig(searchWord, postLimit, pollRate);
        taskExecutor.execute(redditMonitor);
        taskExecutor.execute(twitterMonitor);
    }
}
