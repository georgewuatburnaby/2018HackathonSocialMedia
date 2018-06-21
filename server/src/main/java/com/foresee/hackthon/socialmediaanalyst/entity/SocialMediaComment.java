package com.foresee.hackthon.socialmediaanalyst.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
public class SocialMediaComment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long commentId;

    private String url;

    private String source;

    private String text;

    private float rating;

    private ArrayList<String> tags;

    private String timeStamp;
}
