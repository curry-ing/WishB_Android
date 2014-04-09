package com.vivavu.dream.model.user;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by yuja on 14. 1. 7.
 */
public class User{
    @SerializedName("about_me")
    private String aboutMe;
    @SerializedName("birthday")
    private String birthday;
    @SerializedName("email")
    private String email;
    @SerializedName("id")
    private Integer id;
    @SerializedName("is_following")
    private Boolean isFollowing;

    @SerializedName("last_seen")
    private Date lastSeen;
    @SerializedName("pic")
    private String pic;
    @SerializedName("uri")
    private String uri;
    @SerializedName("username")
    private String username;

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "aboutMe='" + aboutMe + '\'' +
                ", birthday='" + birthday + '\'' +
                ", email='" + email + '\'' +
                ", id=" + id +
                ", isFollowing=" + isFollowing +
                ", lastSeen=" + lastSeen +
                ", pic='" + pic + '\'' +
                ", uri='" + uri + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
