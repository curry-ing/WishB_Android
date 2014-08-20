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
	protected Integer id;

	@DatabaseField
	@SerializedName("subject")
	protected String subject;

	@DatabaseField
	@SerializedName("content")
	protected String content;

	@DatabaseField(id = true, dataType = DataType.DATE)
	@SerializedName("reg_dt")
	protected MongoDBDate regDt;

	@DatabaseField
	protected boolean read = false;

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

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
