package com.vivavu.dream.model.bucket;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;
import java.util.List;

/**
 * Created by yuja on 14. 3. 6.
 */
@DatabaseTable(tableName = "TodayGroups")
public class TodayGroup implements Comparable<TodayGroup> {

    @DatabaseField(id = true)
    @SerializedName("date")
    private Date date;

    @DatabaseField
    private Integer todayCount;

    @DatabaseField
    @SerializedName("reg_dt")
    private Date regDate;

    @DatabaseField
    @SerializedName("lst_mod_dt")
    private Date lastModDate;


    @SerializedName("todays")
    private List<Today> todayList;

    public TodayGroup() {
    }

    public TodayGroup(Date date) {
        this.date = date;
    }

    public TodayGroup(Date date, List<Today> todayList) {
        this.date = date;
        this.todayList = todayList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Today> getTodayList() {
        return todayList;
    }

    public void setTodayList(List<Today> todayList) {
        this.todayList = todayList;
    }

    public Integer getTodayCount() {
        return todayCount;
    }

    public void setTodayCount(Integer todayCount) {
        this.todayCount = todayCount;
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

    @Override
    public int compareTo(TodayGroup another) {
        if(date == null){
            return -1;
        }else if(another.getDate() == null){
            return 1;
        }

        return date.compareTo(another.getDate());
    }
}
