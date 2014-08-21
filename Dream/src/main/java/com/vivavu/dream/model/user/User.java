package com.vivavu.dream.model.user;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.vivavu.dream.R;
import com.vivavu.dream.common.DreamApp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuja on 14. 1. 7.
 */
@DatabaseTable(tableName = "user")
public class User{
	@DatabaseField(id = true)
    @SerializedName("id")
    private Integer id;

	@DatabaseField
    @SerializedName("email")
    private String email;

	@DatabaseField
    @SerializedName("about_me")
    private String aboutMe;

	@DatabaseField
    @SerializedName("birthday")
    private String birthday;

	@DatabaseField
    @SerializedName("is_following")
    private Boolean isFollowing;

	@DatabaseField
    @SerializedName("last_seen")
    private Date lastSeen;

	@DatabaseField
    @SerializedName("pic")
    private String pic;

	@DatabaseField
    @SerializedName("uri")
    private String uri;

	@DatabaseField
    @SerializedName("username")
    private String username;

	@DatabaseField
    @SerializedName("title_life")
    private String title_life;

	@DatabaseField
    @SerializedName("title_10")
    private String title_10;

	@DatabaseField
    @SerializedName("title_20")
    private String title_20;

	@DatabaseField
    @SerializedName("title_30")
    private String title_30;

	@DatabaseField
    @SerializedName("title_40")
    private String title_40;

	@DatabaseField
    @SerializedName("title_50")
    private String title_50;

	@DatabaseField
    @SerializedName("title_60")
    private String title_60;

	@DatabaseField
    @SerializedName("profile_img_url")
    private String profileImgUrl;

	@DatabaseField
	@SerializedName("fb_id")
	private String facebookId;

	@DatabaseField
	@SerializedName("fb_token")
	private String fbToken;

	@DatabaseField
	@SerializedName("latest_notice")
	protected String latestNoticeKey;

    private File photo;

    public String getTitle_60() {
        return getDefaultText(title_60, "60");
    }

    public void setTitle_60(String title_60) {
        this.title_60 = title_60;
    }

    public String getTitle_10() {
        return getDefaultText(title_10, "10");
    }

    public void setTitle_10(String title_10) {
        this.title_10 = title_10;
    }

    public String getTitle_20() {
        return getDefaultText(title_20, "20");
    }

    public void setTitle_20(String title_20) {
        this.title_20 = title_20;
    }

    public String getTitle_30() {
        return getDefaultText(title_30, "30");
    }

    public void setTitle_30(String title_30) {
        this.title_30 = title_30;
    }

    public String getTitle_40() {
        return getDefaultText(title_40, "40");
    }

    public void setTitle_40(String title_40) {
        this.title_40 = title_40;
    }

    public String getTitle_50() {
        return getDefaultText(title_50, "50");
    }

    public void setTitle_50(String title_50) {
        this.title_50 = title_50;
    }

    public String getTitle_life() {
        return getDefaultText(title_life, null);
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

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public boolean isFacebookLogin(){
		if(this.facebookId != null && this.facebookId.length() > 0){
			return true;
		}

		return false;
	}

	public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

	public String getFbToken() {
		return fbToken;
	}

	public void setFbToken(String fbToken) {
		this.fbToken = fbToken;
	}

	public String getLatestNoticeKey() {
		return latestNoticeKey;
	}

	public void setLatestNoticeKey(String latestNoticeKey) {
		this.latestNoticeKey = latestNoticeKey;
	}

	public int getUserAge() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        String strCurYear = CurYearFormat.format(date);

        if (birthday!=null && birthday.length()>0) {
            return Integer.parseInt(strCurYear) - Integer.parseInt(birthday.substring(0, 4)) + 1;
        } else {
            return 0;
        }
    }

	public String getDefaultText(String range, String defaultRange){
		if(range == null){
			if(defaultRange == null) {
				return DreamApp.getInstance().getString(R.string.in_my_life);
			}else {
				return String.format(DreamApp.getInstance().getString(defaultRange.equals("60") ? R.string.after_sixties : R.string.each_decades), defaultRange);
			}
		}

		 return range;
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
