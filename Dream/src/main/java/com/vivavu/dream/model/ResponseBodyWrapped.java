package com.vivavu.dream.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yuja on 14. 2. 14.
 */
public class ResponseBodyWrapped<T> {
    @SerializedName("status")
    private String status;
    @SerializedName("description")
    private String description;

    @SerializedName("data")
    private T data;

    public ResponseBodyWrapped(){
        this.status="error";
        this.description="unknown";
        this.data = null;
    }
    public ResponseBodyWrapped(String status, String description, T data) {
        this.status = status;
        this.description = description;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String reason) {
        this.description = reason;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess(){
        return "success".equals(status);
    }
}
