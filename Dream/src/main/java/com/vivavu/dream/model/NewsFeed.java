package com.vivavu.dream.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yuja on 2014-08-26.
 */
public class NewsFeed implements Serializable {
	public enum Action {
		@SerializedName("registered")
		REGISTERED,
		@SerializedName("modified")
		MODIFIED;
	}

	public enum Type{
		@SerializedName("bucket")
		BUCKET,
		@SerializedName("journal")
		JOURNAL;
	}

	public class Contents {
		@SerializedName("img")
		protected String img;
		@SerializedName("text")
		protected String text;

		public String getImg() {
			return img;
		}

		public void setImg(String img) {
			this.img = img;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}

	@SerializedName("id")
	protected int id;
	@SerializedName("action")
	protected Action action;
	@SerializedName("type")
	protected Type type;
	@SerializedName("title")
	protected String title;
	@SerializedName("contents")
	protected Contents contents;
	@SerializedName("deadline")
	protected Date deadline;
	@SerializedName("fb_feed_id")
	protected String fbFeedId;
	@SerializedName("lst_mod_dt")
	protected Date lstModDt;
	@SerializedName("username")
	protected String username;
	@SerializedName("user_id")
	protected Integer userId;
	@SerializedName("user_profile_img")
	protected String userProfileImg;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Contents getContents() {
		return contents;
	}

	public void setContents(Contents contents) {
		this.contents = contents;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public String getFbFeedId() {
		return fbFeedId;
	}

	public void setFbFeedId(String fbFeedId) {
		this.fbFeedId = fbFeedId;
	}

	public Date getLstModDt() {
		return lstModDt;
	}

	public void setLstModDt(Date lstModDt) {
		this.lstModDt = lstModDt;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserProfileImg() {
		return userProfileImg;
	}

	public void setUserProfileImg(String userProfileImg) {
		this.userProfileImg = userProfileImg;
	}
}
