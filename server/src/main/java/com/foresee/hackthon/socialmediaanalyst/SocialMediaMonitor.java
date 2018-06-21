package com.foresee.hackthon.socialmediaanalyst;

import com.foresee.hackthon.socialmediaanalyst.reddit.RedditMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class SocialMediaMonitor implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TaskExecutor taskExecutor;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        RedditMonitor.init("wework");
        taskExecutor.execute(new RedditMonitor());
    }
}
