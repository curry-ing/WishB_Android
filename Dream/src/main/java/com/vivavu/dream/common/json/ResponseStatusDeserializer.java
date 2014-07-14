package com.vivavu.dream.common.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.vivavu.dream.common.enums.ResponseStatus;

import java.lang.reflect.Type;

/**
 * Created by yuja on 2014-07-14.
 */
public class ResponseStatusDeserializer implements JsonDeserializer<ResponseStatus> {
	@Override
	public ResponseStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		String jsonAsString = json.getAsString();

		return ResponseStatus.fromString(jsonAsString);
	}
}
