package com.foresee.hackthon.socialmediaanalyst;

import com.foresee.hackthon.socialmediaanalyst.reddit.RedditMonitor;
import com.foresee.hackthon.socialmediaanalyst.repository.SocialMediaCommentRepository;
import com.foresee.hackthon.socialmediaanalyst.service.EngineService;
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

    @Value("${search.word}")
    String searchWord;

    @Value("${reddit.limit}")
    int redditLimit;

    @Value("${reddit.monitor.rate}")
    int redditPollRate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RedditMonitor.setConfig(searchWord, redditLimit, redditPollRate);
        taskExecutor.execute(redditMonitor);
    }
}
