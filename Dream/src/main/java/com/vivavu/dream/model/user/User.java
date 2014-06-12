package com.vivavu.dream.model.user;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuja on 14. 1. 7.
 */
public class User{
    @SerializedName("id")
    private Integer id;

    @SerializedName("email")
    private String email;

    @SerializedName("about_me")
    private String aboutMe;

    @SerializedName("birthday")
    private String birthday;

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

    @SerializedName("title_life")
    private String title_life;

    @SerializedName("title_10")
    private String title_10;

    @SerializedName("title_20")
    private String title_20;

    @SerializedName("title_30")
    private String title_30;

    @SerializedName("title_40")
    private String title_40;

    @SerializedName("title_50")
    private String title_50;

    @SerializedName("title_60")
    private String title_60;

    private File photo;

    public String getTitle_60() {
        return title_60;
    }

    public void setTitle_60(String title_60) {
        this.title_60 = title_60;
    }

    public String getTitle_10() {
        return title_10;
    }

    public void setTitle_10(String title_10) {
        this.title_10 = title_10;
    }

    public String getTitle_20() {
        return title_20;
    }

    public void setTitle_20(String title_20) {
        this.title_20 = title_20;
    }

    public String getTitle_30() {
        return title_30;
    }

    public void setTitle_30(String title_30) {
        this.title_30 = title_30;
    }

    public String getTitle_40() {
        return title_40;
    }

    public void setTitle_40(String title_40) {
        this.title_40 = title_40;
    }

    public String getTitle_50() {
        return title_50;
    }

    public void setTitle_50(String title_50) {
        this.title_50 = title_50;
    }

    public String getTitle_life() {
        return title_life;
    }

    public void setTitle_life(String title_life) {
        this.title_life = title_life;
    }

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

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public int getUserAge() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        String strCurYear = CurYearFormat.format(date);

        return Integer.parseInt(strCurYear) - Integer.parseInt(birthday.substring(0,4)) + 1;
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
