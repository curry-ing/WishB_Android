package com.vivavu.dream.model.bucket;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yuja on 14. 3. 6.
 */
public class BucketGroup implements Comparable<BucketGroup> {

    @SerializedName("range")
    private String range;
    @SerializedName("buckets")
    private List<Bucket> bukets;

    public BucketGroup() {
    }

    public BucketGroup(String range) {
        this.range = range;
    }

    public BucketGroup(String range, List<Bucket> bukets) {
        this.range = range;
        this.bukets = bukets;
    }

    public String getRange() {
        return range;
    }

    public String getRangeText() {
        if(range == null){
            return "In My Life";
        }
        return range + "대";
    }

    public void setRange(String range) {
        this.range = range;
    }

    public List<Bucket> getBukets() {
        return bukets;
    }

    public void setBukets(List<Bucket> bukets) {
        this.bukets = bukets;
    }

    @Override
    public int compareTo(BucketGroup another) {
        if(range == null){
            return -1;
        }else if(another.getRange() == null){
            return 1;
        }

        return range.compareTo(another.getRange());
    }
}
