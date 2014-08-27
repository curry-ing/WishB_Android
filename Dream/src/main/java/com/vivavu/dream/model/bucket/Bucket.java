package com.vivavu.dream.model.bucket;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.vivavu.dream.util.DateUtils;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by yuja on 14. 1. 9.
 */
@DatabaseTable(tableName = "buckets")
public class Bucket implements Serializable{
    @DatabaseField(id = true)
    @SerializedName("id")
    private Integer id;

    @DatabaseField
    @SerializedName("title")
    private String title;

    @DatabaseField
    @SerializedName("description")
    private String description;

    @DatabaseField
    @SerializedName("user_id")
    private Integer userId;

    @DatabaseField
    @SerializedName("deadline")
    private Date deadline;

    @DatabaseField
    @SerializedName("status")
    private Integer status;

    @DatabaseField
    @SerializedName("private")
    private Integer isPrivate = 1;

    @DatabaseField
    @SerializedName("scope")
    private String scope;

    @DatabaseField
    @SerializedName("range")
    private String range;

    @DatabaseField
    @SerializedName("rpt_type")
    private String rptType;

    @DatabaseField
    @SerializedName("rpt_cndt")
    private String rptCndt;

    @DatabaseField
    @SerializedName("reg_dt")
    private Date regDate;

    @DatabaseField
    @SerializedName("lst_mod_dt")
    private Date lastModDate;

    @DatabaseField(defaultValue = "0")
    private int modFlag;

    @DatabaseField
    @SerializedName("cvr_img_id")
    private Integer cvrImgId;

    @DatabaseField
    @SerializedName("cvr_img_url")
    private String cvrImgUrl;

    @DatabaseField
    @SerializedName("fb_share")
    private String fbShare;

	@DatabaseField
	@SerializedName("fb_feed_id")
	protected String fbFeedId;

    private File file;

	protected int likesCount = 0;
	protected int commentsCount = 0;

    public Bucket(){

    }

    public Bucket(String title) {
        this.title = title;
    }

    public Bucket(String title, Date deadline) {
        this.deadline = deadline;
        this.title = title;
    }


    public String getRemainDays(){
        return DateUtils.getRemainDayInString(getDeadline());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getRptType() {
        return rptType;
    }

    public void setRptType(String rptType) {
        this.rptType = rptType;
    }

    public String getRptCndt() {
        return rptCndt;
    }

    public void setRptCndt(String rptCndt) {
        this.rptCndt = rptCndt;
    }

    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    public Date getLastModDate() {
        return lastModDate;
    }

    public void setLastModDate(Date lastModDate) {
        this.lastModDate = lastModDate;
    }

    public int getModFlag() {
        return modFlag;
    }

    public void setModFlag(int modFlag) {
        this.modFlag = modFlag;
    }

    public Integer getCvrImgId() {
        return cvrImgId;
    }

    public void setCvrImgId(Integer cvrImgId) {
        this.cvrImgId = cvrImgId;
    }

    public String getCvrImgUrl() {
        return cvrImgUrl;
    }

    public void setCvrImgUrl(String cvrImgUrl) {
        this.cvrImgUrl = cvrImgUrl;
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
    public String toString() {
        return "Bucket{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", userId=" + userId +
                ", deadline=" + deadline +
                ", status=" + status +
                ", isPrivate=" + isPrivate +
                ", scope='" + scope + '\'' +
                ", range='" + range + '\'' +
                ", rptType='" + rptType + '\'' +
                ", rptCndt='" + rptCndt + '\'' +
                ", regDate=" + regDate +
                ", lastModDate=" + lastModDate +
                ", modFlag=" + modFlag +
                '}';
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bucket)) return false;

        Bucket bucket = (Bucket) o;

        if (deadline != null ? !deadline.equals(bucket.deadline) : bucket.deadline != null)
            return false;
        if (description != null ? !description.equals(bucket.description) : bucket.description != null)
            return false;
        if (file != null ? !file.equals(bucket.file) : bucket.file != null) return false;
        if (id != null ? !id.equals(bucket.id) : bucket.id != null) return false;
        if (isPrivate != null ? !isPrivate.equals(bucket.isPrivate) : bucket.isPrivate != null)
            return false;
        if (range != null ? !range.equals(bucket.range) : bucket.range != null) return false;
        if (rptCndt != null ? !rptCndt.equals(bucket.rptCndt) : bucket.rptCndt != null)
            return false;
        if (rptType != null ? !rptType.equals(bucket.rptType) : bucket.rptType != null)
            return false;
        if (scope != null ? !scope.equals(bucket.scope) : bucket.scope != null) return false;
        if (status != null ? !status.equals(bucket.status) : bucket.status != null) return false;
        if (title != null ? !title.equals(bucket.title) : bucket.title != null) return false;
        if (userId != null ? !userId.equals(bucket.userId) : bucket.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (deadline != null ? deadline.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (isPrivate != null ? isPrivate.hashCode() : 0);
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        result = 31 * result + (range != null ? range.hashCode() : 0);
        result = 31 * result + (rptType != null ? rptType.hashCode() : 0);
        result = 31 * result + (rptCndt != null ? rptCndt.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }
}
