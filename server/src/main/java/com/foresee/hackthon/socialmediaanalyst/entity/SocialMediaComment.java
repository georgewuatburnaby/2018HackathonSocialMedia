package com.foresee.hackthon.socialmediaanalyst.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class SocialMediaComment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long commentId;

    private String comment;

    private float sentiment;

    private ArrayList<String> labels;

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getSentiment() {
        return sentiment;
    }

    public void setSentiment(float sentiment) {
        this.sentiment = sentiment;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }
}
