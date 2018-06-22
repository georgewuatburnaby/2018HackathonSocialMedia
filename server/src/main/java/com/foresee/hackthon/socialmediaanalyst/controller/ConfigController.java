package com.foresee.hackthon.socialmediaanalyst.controller;

import com.foresee.hackthon.socialmediaanalyst.reddit.RedditMonitor;
import com.foresee.hackthon.socialmediaanalyst.twitter.TwitterMonitor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.http.HttpStatus.*;

@RestController
public class ConfigController {

    @RequestMapping(value = "config", method = RequestMethod.GET)
    public String greeting(@RequestParam(name="search", required=true) String searchWord,
                           @RequestParam(name="limit", required=false, defaultValue = "20") int limit,
                           @RequestParam(name="rate", required=false, defaultValue = "5000") int rate) {
        RedditMonitor.setConfig(searchWord, limit, rate);
        TwitterMonitor.setConfig(searchWord, limit, rate);
        return "success";
    }
}
