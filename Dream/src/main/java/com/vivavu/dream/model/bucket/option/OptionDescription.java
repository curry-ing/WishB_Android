package com.vivavu.dream.model.bucket.option;

/**
 * Created by yuja on 14. 1. 24.
 */
public class OptionDescription extends Option {
    protected String description;

    public OptionDescription() {
    }

    public OptionDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OptionDescription)) return false;

        OptionDescription that = (OptionDescription) o;

        if (description != null ? !description.equals(that.description) : that.description != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return description != null ? description.hashCode() : 0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
