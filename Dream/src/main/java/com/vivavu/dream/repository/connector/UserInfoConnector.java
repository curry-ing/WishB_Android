package com.vivavu.dream.repository.connector;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.model.BaseInfo;
import com.vivavu.dream.model.LoginInfo;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.SecureToken;
import com.vivavu.dream.model.user.User;
import com.vivavu.dream.repository.Connector;
import com.vivavu.dream.util.RestTemplateUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;

/**
 * Created by yuja on 2014-03-31.
 */
public class UserInfoConnector extends Connector<User> {


    @Override
    public ResponseBodyWrapped<User> post(User data) {
        return null;
    }

    @Override
    public ResponseBodyWrapped<User> put(User data) {
        return null;
    }

    @Override
    public ResponseBodyWrapped<User> get(User data) {
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
        HttpEntity request = new HttpEntity<String>(requestHeaders);

        ResponseEntity<String> result = null;
        try{
            result = restTemplate.exchange(Constants.apiBaseInfo, HttpMethod.GET, request, String.class);
        }catch (RestClientException e) {
            Log.e("dream", e.toString());
        }
        ResponseBodyWrapped<User> responseBodyWrapped = null;
        if(result != null && result.getStatusCode() == HttpStatus.OK || result.getStatusCode()== HttpStatus.NO_CONTENT){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            Type type = new TypeToken<ResponseBodyWrapped<User>>(){}.getType();
            responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);
            //GsonBuilder
            return responseBodyWrapped;
        }

        return new ResponseBodyWrapped<User>();
    }

    @Override
    public ResponseBodyWrapped<User> delete(User data) {
        return null;
    }

    public static ResponseBodyWrapped<SecureToken> getToken(String email, String password){

        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = RestTemplateUtils.getBasicAuthHeader(email, password);
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> result = null;
        try{
            result = restTemplate.exchange(Constants.apiToken, HttpMethod.GET, request, String.class);
        }catch (RestClientException e) {
            Log.e("dream", e.toString());
            return new ResponseBodyWrapped<SecureToken>("error", "오류가 발생하였습니다. 다시 시도해주시기 바랍니다.", null);
        }

        if(result.getStatusCode() == HttpStatus.OK){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            Type type = new TypeToken<ResponseBodyWrapped<SecureToken>>(){}.getType();
            ResponseBodyWrapped<SecureToken> responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);
            return responseBodyWrapped;
        }else if (result.getStatusCode() == HttpStatus.UNAUTHORIZED){
            return new ResponseBodyWrapped<SecureToken>("error", "사용자 정보 확인 필요", null);
        }
        return new ResponseBodyWrapped<SecureToken>("error", "오류가 발생하였습니다. 다시 시도해주시기 바랍니다.", null);
    }

    public ResponseBodyWrapped<BaseInfo> getBaseInfo(){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
        HttpEntity request = new HttpEntity<String>(requestHeaders);

        ResponseEntity<String> result = null;
        try{
            result = restTemplate.exchange(Constants.apiBaseInfo, HttpMethod.GET, request, String.class);
        }catch (RestClientException e) {
            Log.e("dream", e.toString());
        }
        ResponseBodyWrapped<BaseInfo> responseBodyWrapped = null;
        if(result != null && result.getStatusCode() == HttpStatus.OK || result.getStatusCode()== HttpStatus.NO_CONTENT){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            Type type = new TypeToken<ResponseBodyWrapped<BaseInfo>>(){}.getType();
            responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);
            //GsonBuilder
            return responseBodyWrapped;
        }

        return new ResponseBodyWrapped<BaseInfo>();
    }

    public static ResponseBodyWrapped<LoginInfo> resetPassword(String email){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = RestTemplateUtils.getBasicAuthHeader(null, null);
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> result = null;

        try {
            result = restTemplate.exchange(Constants.apiResetPassword, HttpMethod.POST, request, String.class, email);
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }


        if(result.getStatusCode() == HttpStatus.OK){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            Type type = new TypeToken<ResponseBodyWrapped<LoginInfo>>(){}.getType();

            ResponseBodyWrapped<LoginInfo> user = gson.fromJson((String) result.getBody(), type);
            /*TypeToken.get(user.getClass());
            ResponseBodyWrapped<LoginInfo> usr = RestTemplateUtils.responseToJson(result,type );*/
            return user;
        }

        return new ResponseBodyWrapped<LoginInfo>();
    }
}
