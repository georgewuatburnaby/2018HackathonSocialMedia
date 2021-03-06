package com.foresee.hackthon.socialmediaanalyst.reddit.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "modhash", "dist", "children", "after", "before" })
public class ListResponseData {

	@JsonProperty("modhash")
	private String modhash;
	@JsonProperty("dist")
	private Integer dist;
	@JsonProperty("children")
	private List<Comment> children = null;
	@JsonProperty("after")
	private String after;
	@JsonProperty("before")
	private Object before;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("modhash")
	public String getModhash() {
		return modhash;
	}

	@JsonProperty("modhash")
	public void setModhash(String modhash) {
		this.modhash = modhash;
	}

	@JsonProperty("dist")
	public Integer getDist() {
		return dist;
	}

	@JsonProperty("dist")
	public void setDist(Integer dist) {
		this.dist = dist;
	}

	@JsonProperty("children")
	public List<Comment> getChildren() {
		return children;
	}

	@JsonProperty("children")
	public void setChildren(List<Comment> children) {
		this.children = children;
	}

	@JsonProperty("after")
	public String getAfter() {
		return after;
	}

	@JsonProperty("after")
	public void setAfter(String after) {
		this.after = after;
	}

	@JsonProperty("before")
	public Object getBefore() {
		return before;
	}

	@JsonProperty("before")
	public void setBefore(Object before) {
		this.before = before;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}