package com.vivavu.dream.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yuja on 2014-08-06.
 */
public class AppVersionInfo {

	@SerializedName("version")
	private Double version;

	@SerializedName("url")
	private String url;

	@SerializedName("version_n")
	private AppVersion versionNew;

	public Double getVersion() {
		return version;
	}

	public void setVersion(Double version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public AppVersion getVersionNew() {
		return versionNew;
	}

	public void setVersionNew(AppVersion versionNew) {
		this.versionNew = versionNew;
	}
}
