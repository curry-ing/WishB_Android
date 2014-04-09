package com.vivavu.dream.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Type;
import java.util.Collections;

/**
 * Created by yuja on 14. 2. 21.
 */
public class RestTemplateUtils {


    public static HttpHeaders getBasicAuthHeader(String username, String password){

        HttpHeaders httpHeaders = new HttpHeaders();
        if(username != null && password != null) {
            HttpAuthentication httpAuthentication = new HttpBasicAuthentication(username, password);
            httpHeaders.setAuthorization(httpAuthentication);
        }
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));

        return httpHeaders;
    }

    public static <T> T responseToJson(ResponseEntity<String> responseEntity, Type typeOfT ){
        T result = null;
        HttpHeaders headers = responseEntity.getHeaders();
        if(headers != null && headers.getContentType() == MediaType.APPLICATION_JSON){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            result = gson.fromJson((String) responseEntity.getBody(), typeOfT);
        }else if( headers != null && headers.getContentType() != MediaType.APPLICATION_JSON){

        }

        return result;
    }

    public static boolean isAvailableParseToJson(ResponseEntity<?> responseEntity){
        HttpHeaders headers = responseEntity.getHeaders();
        if(headers != null && MediaType.APPLICATION_JSON.equals(headers.getContentType() )){
            return true;
        }
        return false;
    }

}
