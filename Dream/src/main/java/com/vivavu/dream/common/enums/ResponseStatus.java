package com.vivavu.dream.common.enums;

/**
 * Created by yuja on 2014-07-14.
 */
public enum ResponseStatus {
	UNKNOWN_ERROR("unknown_error"), SUCCESS("success"), SERVER_ERROR("error"), TIMEOUT("timeout");

	private String status;

	ResponseStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public static ResponseStatus fromString(String status){
		if(status == null){
			return ResponseStatus.UNKNOWN_ERROR;
		}

		for(ResponseStatus type : ResponseStatus.values()) {
			if(status.compareToIgnoreCase( type.getStatus() ) == 0) {
				return type;
			}
		}
		 return ResponseStatus.UNKNOWN_ERROR;
	}
}
