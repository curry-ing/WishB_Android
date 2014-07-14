package com.vivavu.dream.model;

import com.google.gson.annotations.SerializedName;
import com.vivavu.dream.common.enums.ResponseStatus;

/**
 * Created by yuja on 14. 2. 14.
 */
public class ResponseBodyWrapped<T> {
	@SerializedName("status")
	private ResponseStatus responseStatus;

	@SerializedName("description")
    private String description;

    @SerializedName("data")
    private T data;

    public ResponseBodyWrapped(){
        this.description="unknown";
	    this.responseStatus = ResponseStatus.UNKNOWN_ERROR;
        this.data = null;
    }

    public ResponseBodyWrapped(ResponseStatus responseStatus, String description, T data) {
        this.responseStatus = responseStatus;
        this.description = description;
        this.data = data;
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
        return responseStatus == ResponseStatus.SUCCESS;
    }

	public ResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(ResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}
}
