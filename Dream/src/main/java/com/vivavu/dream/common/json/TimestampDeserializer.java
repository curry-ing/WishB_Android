package com.vivavu.dream.common.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.vivavu.dream.model.MongoDBDate;

import java.lang.reflect.Type;

/**
 * Created by yuja on 14. 3. 6.
 */
public class TimestampDeserializer implements JsonDeserializer<MongoDBDate> {

	@Override
	public MongoDBDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if(json.isJsonObject()){
			JsonPrimitive date = (JsonPrimitive) json.getAsJsonObject().get("$date");
			return new MongoDBDate(date.getAsLong());
		}
		throw new JsonParseException("Unparseable date: \"" + json.getAsString());
	}
}
