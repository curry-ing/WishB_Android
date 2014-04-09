package com.vivavu.dream.model.bucket;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yuja on 14. 1. 9.
 */
@DatabaseTable(tableName = "todays")
public class Today implements Serializable{
    @DatabaseField(id = true)
    @SerializedName("id")
    private Integer id;

    @DatabaseField
    @SerializedName("title")
    private String title;

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
    private Integer isPrivate;

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
    @SerializedName("date")
    private Date date;

    @DatabaseField
    @SerializedName("cvr_img_id")
    private Integer cvrImgId;

    @DatabaseField
    @SerializedName("cvr_img_url")
    private String cvrImgUrl;

    @DatabaseField
    @SerializedName("bucket_id")
    private Integer bucketId;

    @DatabaseField
    @SerializedName("parent_id")
    private Integer parentId;

    public Today(){

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Integer getBucketId() {
        return bucketId;
    }

    public void setBucketId(Integer bucketId) {
        this.bucketId = bucketId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Today{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", userId=" + userId +
                ", deadline=" + deadline +
                ", status=" + status +
                ", isPrivate=" + isPrivate +
                ", scope='" + scope + '\'' +
                ", range='" + range + '\'' +
                ", rptType='" + rptType + '\'' +
                ", rptCndt='" + rptCndt + '\'' +
                ", date=" + date +
                ", cvrImgId=" + cvrImgId +
                ", cvrImgUrl='" + cvrImgUrl + '\'' +
                ", bucketId=" + bucketId +
                ", parentId=" + parentId +
                '}';
    }
}
