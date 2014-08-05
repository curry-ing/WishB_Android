package com.vivavu.dream.model;

import com.google.gson.annotations.SerializedName;
import com.vivavu.dream.common.enums.ReportingType;

/**
 * Created by yuja on 2014-08-01.
 */
public class Inquiry {


	@SerializedName("email")
	private String email;

	@SerializedName("type")
	private ReportingType reportingType;

	@SerializedName("subject")
	private String subject;

	@SerializedName("body")
	private String body;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public ReportingType getReportingType() {
		return reportingType;
	}

	public void setReportingType(ReportingType reportingType) {
		this.reportingType = reportingType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
