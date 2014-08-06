package com.vivavu.dream.model;

import com.google.gson.annotations.SerializedName;
import com.vivavu.dream.model.user.User;

/**
 * Created by yuja on 14. 1. 17.
 */
public class BaseInfo extends User{
	@SerializedName("latest_app")
	protected AppVersionInfo appVersionInfo;

	public AppVersionInfo getAppVersionInfo() {
		return appVersionInfo;
	}

	public void setAppVersionInfo(AppVersionInfo appVersionInfo) {
		this.appVersionInfo = appVersionInfo;
	}
}
