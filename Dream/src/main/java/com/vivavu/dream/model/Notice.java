package com.vivavu.dream.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by yuja on 2014-08-11.
 */
@DatabaseTable(tableName = "notices")
public class Notice {
	@DatabaseField(id=true)
	@SerializedName("key")
	protected String id;

	@DatabaseField
	@SerializedName("subject")
	protected String subject;

	@DatabaseField
	@SerializedName("content")
	protected String content;

	@DatabaseField(dataType = DataType.SERIALIZABLE)
	@SerializedName("reg_dt")
	protected MongoDBDate regDt;

	@DatabaseField
	protected boolean read;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public MongoDBDate getRegDt() {
		return regDt;
	}

	public void setRegDt(MongoDBDate regDt) {
		this.regDt = regDt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}
}
