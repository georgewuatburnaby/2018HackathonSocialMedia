package com.foresee.hackthon.socialmediaanalyst;

import com.foresee.hackthon.socialmediaanalyst.reddit.RedditMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SocialMediaAnalystApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocialMediaAnalystApplication.class, args);
		RedditMonitor.init();
	}

	@Bean
	public RedditMonitor redditMonitor() {
		return new RedditMonitor();
	}
}
