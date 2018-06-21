package com.foresee.hackthon.socialmediaanalyst.service;

import com.foresee.hackthon.socialmediaanalyst.reddit.entity.Comment;
import com.foresee.hackthon.socialmediaanalyst.reddit.entity.ListResponse;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EngineService {

    @Value( "${engine.sentiment.url}" )
    private String sentimentEngineUrl;

    public float getRating(final String text) throws UnirestException {
        EngineResponse engineResponse = Unirest.post(sentimentEngineUrl)
                .body(text)
                .asObject(EngineResponse.class)
                .getBody();
        return engineResponse.getRating();
    }
}
