package com.vivavu.dream.model;

import java.util.Date;

/**
 * Created by yuja on 2014-08-19.
 */
public class SocialReact {
	public enum SocialType{
		NONE
		, FACEBOOK
	}

	protected SocialType socialType;
	protected String img;
	protected String name;
	protected String message;
	protected Date createdTime;
	protected String attachmentUrl;

	public SocialType getSocialType() {
		return socialType;
	}

	public void setSocialType(SocialType socialType) {
		this.socialType = socialType;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getAttachmentUrl() {
		return attachmentUrl;
	}

	public void setAttachmentUrl(String attachmentUrl) {
		this.attachmentUrl = attachmentUrl;
	}

	@Override
	public String toString() {
		return "SocialReact{" +
				"socialType=" + socialType +
				", img='" + img + '\'' +
				", name='" + name + '\'' +
				", message='" + message + '\'' +
				", createdTime=" + createdTime +
				'}';
	}
}
