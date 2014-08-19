package com.vivavu.dream.model.bucket.timeline;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by yuja on 2014-03-28.
 */
public class Post implements Serializable, Comparable<Post> {
    @SerializedName("id")
    protected Integer id;
    @SerializedName("content_dt")
    protected Date contentDt;
    @SerializedName("user_id")
    protected Integer userId;
    @SerializedName("bucket_id")
    protected Integer bucketId;
    @SerializedName("text")
    protected String text;
    @SerializedName("img_id")
    protected Integer imgId;
    @SerializedName("img_url")
    protected String imgUrl;
    @SerializedName("url1")
    protected String url1;
    @SerializedName("url2")
    protected String url2;
    @SerializedName("url3")
    protected String url3;
    @SerializedName("reg_dt")
    protected Date regDt;
    @SerializedName("lst_mod_dt")
    protected Date lstModDt;

    @SerializedName("photo")
    protected File photo;

    @SerializedName("fb_share")
    protected String fbShare;

    @SerializedName("fb_feed_id")
    protected String fbFeedId;

	protected int likesCount = 0;
	protected int commentsCount = 0;

    public Post(Date date) {
        this.contentDt = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getContentDt() {
        return contentDt;
    }

    public void setContentDt(Date contentDt) {
        this.contentDt = contentDt;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBucketId() {
        return bucketId;
    }

    public void setBucketId(Integer bucketId) {
        this.bucketId = bucketId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getImgId() {
        return imgId;
    }

    public void setImgId(Integer imgId) {
        this.imgId = imgId;
    }

    public String getUrl1() {
        return url1;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    public String getUrl2() {
        return url2;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }

    public String getUrl3() {
        return url3;
    }

    public void setUrl3(String url3) {
        this.url3 = url3;
    }

    public Date getRegDt() {
        return regDt;
    }

    public void setRegDt(Date regDt) {
        this.regDt = regDt;
    }

    public Date getLstModDt() {
        return lstModDt;
    }

    public void setLstModDt(Date lstModDt) {
        this.lstModDt = lstModDt;
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getFbShare() {
        return fbShare;
    }

    public void setFbShare(String fbShare) {
        this.fbShare = fbShare;
    }

    public String getFbFeedId() {
        return fbFeedId;
    }

    public void setFbFeedId(String fbFeedId) {
        this.fbFeedId = fbFeedId;
    }

	public int getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;

        Post post = (Post) o;

        if (bucketId != null ? !bucketId.equals(post.bucketId) : post.bucketId != null)
            return false;
        if (id != null ? !id.equals(post.id) : post.id != null) return false;
        if (imgId != null ? !imgId.equals(post.imgId) : post.imgId != null) return false;
        if (lstModDt != null ? !lstModDt.equals(post.lstModDt) : post.lstModDt != null)
            return false;
        if (regDt != null ? !regDt.equals(post.regDt) : post.regDt != null) return false;
        if (text != null ? !text.equals(post.text) : post.text != null) return false;
        if (contentDt != null ? !contentDt.equals(post.contentDt) : post.contentDt != null)
            return false;
        if (url1 != null ? !url1.equals(post.url1) : post.url1 != null) return false;
        if (url2 != null ? !url2.equals(post.url2) : post.url2 != null) return false;
        if (url3 != null ? !url3.equals(post.url3) : post.url3 != null) return false;
        if (userId != null ? !userId.equals(post.userId) : post.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (contentDt != null ? contentDt.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (bucketId != null ? bucketId.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (imgId != null ? imgId.hashCode() : 0);
        result = 31 * result + (url1 != null ? url1.hashCode() : 0);
        result = 31 * result + (url2 != null ? url2.hashCode() : 0);
        result = 31 * result + (url3 != null ? url3.hashCode() : 0);
        result = 31 * result + (regDt != null ? regDt.hashCode() : 0);
        result = 31 * result + (lstModDt != null ? lstModDt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", contentDt=" + contentDt +
                ", userId=" + userId +
                ", bucketId=" + bucketId +
                ", text='" + text + '\'' +
                ", imgId=" + imgId +
                ", url1='" + url1 + '\'' +
                ", url2='" + url2 + '\'' +
                ", url3='" + url3 + '\'' +
                ", regDt=" + regDt +
                ", lstModDt=" + lstModDt +
                '}';
    }

    @Override
    public int compareTo(Post another) {
        if(this.getRegDt() != null && another.getRegDt() != null){
            return this.getRegDt().compareTo(another.getRegDt());
        }
        return 0;
    }
}
