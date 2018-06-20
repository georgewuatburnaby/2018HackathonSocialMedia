package com.foresee.hackthon.socialmediaanalyst.repository;

import com.foresee.hackthon.socialmediaanalyst.entity.SocialMediaComment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "comments", path = "comments")
public interface SocialMediaCommentRepository extends PagingAndSortingRepository<SocialMediaComment, Long> {
}
