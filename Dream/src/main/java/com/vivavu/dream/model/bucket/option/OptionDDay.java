package com.vivavu.dream.model.bucket.option;

import com.vivavu.dream.util.DateUtils;

import java.util.Date;

/**
 * Created by yuja on 14. 1. 24.
 */
public class OptionDDay extends Option {
    private String range;
    private Date deadline;
    private String postfix;

    public OptionDDay(String range, Date deadline) {
        this.range = range;
        this.deadline = deadline;
        this.postfix = "";
    }

    public OptionDDay(String range, Date deadline, String postfix) {
        this.range = range;
        this.deadline = deadline;
        this.postfix = postfix;
    }

    public String getDdayString(){
        return DateUtils.getDateString(this.deadline, "yyyy-MM-dd");
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }

    public String getRangeString(){
        return range + String.valueOf(postfix);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OptionDDay)) return false;

        OptionDDay that = (OptionDDay) o;

        if (deadline != null ? !deadline.equals(that.deadline) : that.deadline != null)
            return false;
        if (range != null ? !range.equals(that.range) : that.range != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = range != null ? range.hashCode() : 0;
        result = 31 * result + (deadline != null ? deadline.hashCode() : 0);
        return result;
    }
}
