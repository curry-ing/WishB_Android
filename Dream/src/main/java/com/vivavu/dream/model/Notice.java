package com.vivavu.dream.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yuja on 2014-08-11.
 */
public class Notice {
	@SerializedName("subject")
	protected String subject;

	@SerializedName("content")
	protected String content;

	@SerializedName("reg_dt")
	protected MongoDBDate regDt;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public MongoDBDate getRegDt() {
		return regDt;
	}

	public void setRegDt(MongoDBDate regDt) {
		this.regDt = regDt;
	}
}
