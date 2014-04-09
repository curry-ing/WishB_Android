package com.vivavu.dream.model.bucket.timeline;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuja on 2014-04-01.
 */
public class Timeline implements Serializable {
    @SerializedName("count")
    protected Integer count;
    @SerializedName("timelineData")
    protected List<Post> timelineData;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Post> getTimelineData() {
        return timelineData;
    }

    public void setTimelineData(List<Post> timelineData) {
        this.timelineData = timelineData;
    }
}
