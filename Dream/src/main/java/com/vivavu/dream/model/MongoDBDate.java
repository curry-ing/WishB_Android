package com.vivavu.dream.model;

import java.util.Date;

/**
 * Created by yuja on 2014-08-11.
 */
public class MongoDBDate extends Date{
	public MongoDBDate() {
	}

	public MongoDBDate(Long milliseconds){
		super(milliseconds);
	}
}
