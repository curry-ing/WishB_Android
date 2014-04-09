package com.vivavu.dream.model.bucket.timeline;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yuja on 2014-04-01.
 */
public class TimelineMetaInfo implements Serializable {
    protected Integer bucketId;
    protected Integer count;
    protected Date minDate;
    protected Date maxDate;
    protected List<Date> dateList;

    public TimelineMetaInfo() {
        count = 0;
        minDate = maxDate = new Date();
        dateList = new ArrayList<Date>();
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public List<Date> getDateList() {
        return dateList;
    }

    public void setDateList(List<Date> dateList) {
        this.dateList = dateList;
    }

    public Integer getBucketId() {
        return bucketId;
    }

    public void setBucketId(Integer bucketId) {
        this.bucketId = bucketId;
    }
}
