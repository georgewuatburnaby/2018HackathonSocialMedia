package com.foresee.hackthon.socialmediaanalyst;

import com.foresee.hackthon.socialmediaanalyst.entity.SocialMediaComment;
import com.foresee.hackthon.socialmediaanalyst.repository.SocialMediaCommentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SocialMediaAnalystApplicationTests {

	@Autowired
	SocialMediaCommentRepository repository;

	@Test
	public void initialData() {
		SocialMediaComment comment = new SocialMediaComment();
		comment.setText("Test Comment, awesome");
		comment.setRating(0.7f);

		ArrayList<String> labels = new ArrayList<>();
		labels.add("test");
		labels.add("positive");
		comment.setTags(labels);

		repository.save(comment);

		System.out.println("done");
	}

}
