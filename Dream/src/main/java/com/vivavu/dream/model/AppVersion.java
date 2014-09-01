package com.vivavu.dream.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yuja on 2014-09-01.
 */
public class AppVersion implements Comparable<AppVersion>{
	@SerializedName("major")
	int major;
	@SerializedName("minor")
	int minor;
	@SerializedName("tiny")
	int tiny;

	public AppVersion(int major, int minor, int tiny) {
		this.major = major;
		this.minor = minor;
		this.tiny = tiny;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getTiny() {
		return tiny;
	}

	public void setTiny(int tiny) {
		this.tiny = tiny;
	}

	@Override
	public int compareTo(AppVersion another) {
		if(another == null){
			return 1;
		}

		if (getMajor() > another.getMajor()){
			return 1;
		} else if(getMajor() < another.getMajor()){
			return -1;
		} else {
			if(getMajor() > another.getMinor()){
				return 1;
			}else if(getMinor() < another.getMinor()){
				return -1;
			} else {
				if(getTiny() > another.getTiny()){
					return 1;
				} else if(getTiny() < another.getTiny()){
					return -1;
				} else {
					return 0;
				}
			}
		}
	}
}