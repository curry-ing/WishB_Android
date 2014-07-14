package com.vivavu.dream.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.common.json.DateDeserializer;
import com.vivavu.dream.common.json.ResponseStatusDeserializer;

import java.util.Date;

/**
 * Created by yuja on 14. 3. 6.
 */
public class JsonFactory {
    private static Gson gson;
    public static Gson getInstance() {
        if(gson == null){
            gson = new GsonBuilder()
		            .registerTypeAdapter(Date.class, new DateDeserializer())
		            .registerTypeAdapter(ResponseStatus.class, new ResponseStatusDeserializer())
		            .create();
        }
        return gson;
    }
    public static Gson newInstance(){
        return gson = new GsonBuilder()
		        .registerTypeAdapter(Date.class, new DateDeserializer())
		        .registerTypeAdapter(ResponseStatus.class, new ResponseStatusDeserializer())
		        .create();
    }
}
