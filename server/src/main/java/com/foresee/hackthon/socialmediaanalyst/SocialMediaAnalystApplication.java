package com.foresee.hackthon.socialmediaanalyst;

import com.foresee.hackthon.socialmediaanalyst.reddit.RedditMonitor;
import com.foresee.hackthon.socialmediaanalyst.twitter.TwitterMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@SpringBootApplication
public class SocialMediaAnalystApplication {

	public static void main(String[] args) {
        RedditMonitor.init();
        TwitterMonitor.init();
		SpringApplication.run(SocialMediaAnalystApplication.class, args);
	}

	@Bean
	public RedditMonitor redditMonitor() {
		return new RedditMonitor();
	}

    @Bean
    public TwitterMonitor twitterMonitor() {
        return new TwitterMonitor();
    }

}
