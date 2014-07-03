package com.vivavu.dream.model.bucket;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuja on 14. 1. 9.
 */
@DatabaseTable(tableName = "todays")
public class TodayPager implements Serializable{
    @SerializedName("page")
    private Integer page = 0;

    @SerializedName("total_cnt")
    private Integer totalCnt = 0;

    @SerializedName("page_cnt")
    private Integer pageCnt = 0;

    @SerializedName("page_data")
    private List<Today> pageData;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalCnt() {
        return totalCnt;
    }

    public void setTotalCnt(Integer totalCnt) {
        this.totalCnt = totalCnt;
    }

    public Integer getPageCnt() {
        return pageCnt;
    }

    public void setPageCnt(Integer pageCnt) {
        this.pageCnt = pageCnt;
    }

    public List<Today> getPageData() {
        return pageData;
    }

    public void setPageData(List<Today> pageData) {
        this.pageData = pageData;
    }

    @Override
    public String toString() {
        return "TodayPager{" +
                "page=" + page +
                ", totalCnt=" + totalCnt +
                ", pageCnt=" + pageCnt +
                ", pageData=" + pageData +
                '}';
    }
}
