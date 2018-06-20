package com.foresee.hackthon.socialmediaanalyst;

import com.foresee.hackthon.socialmediaanalyst.entity.SocialMediaComment;
import com.foresee.hackthon.socialmediaanalyst.repository.SocialMediaCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.util.ArrayList;

@SpringBootApplication
public class SocialMediaAnalystApplication {



	public static void main(String[] args) {
		SpringApplication.run(SocialMediaAnalystApplication.class, args);


	}
}
