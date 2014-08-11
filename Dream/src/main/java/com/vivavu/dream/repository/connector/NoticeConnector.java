package com.vivavu.dream.repository.connector;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.Notice;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.repository.Connector;
import com.vivavu.dream.util.JsonFactory;

import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by yuja on 2014-08-11.
 */
public class NoticeConnector extends Connector<Notice> {
	@Override
	public ResponseBodyWrapped<Notice> post(Notice data) {
		return null;
	}

	@Override
	public ResponseBodyWrapped<Notice> put(Notice data) {
		return null;
	}

	public ResponseBodyWrapped<List<Notice>> getList() {
		RestTemplate restTemplate = RestTemplateFactory.getInstance();
		HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
		HttpEntity request = new HttpEntity<String>(requestHeaders);
		ResponseEntity<String> result = null;
		try{
			result = restTemplate.exchange(Constants.apiNotice, HttpMethod.GET, request, String.class);
		} catch (ResourceAccessException timeoutException){
			Log.e("dream", timeoutException.toString());
			if(timeoutException.getCause() instanceof ConnectTimeoutException){
				return new ResponseBodyWrapped<List<Notice>>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
			}
		} catch (RestClientException e) {
			Log.e("dream", e.toString());
			return new ResponseBodyWrapped<List<Notice>>(ResponseStatus.SERVER_ERROR, "오류가 발생하였습니다. 다시 시도해주시기 바랍니다.", null);
		}

		if(result != null && result.getStatusCode() == HttpStatus.OK){
			Gson gson = JsonFactory.getInstance();
			Type type = new TypeToken<ResponseBodyWrapped<List<Notice>>>(){}.getType();
			ResponseBodyWrapped<List<Notice>> responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);
			return responseBodyWrapped;
		}else if (result != null && result.getStatusCode() == HttpStatus.UNAUTHORIZED){
			return new ResponseBodyWrapped<List<Notice>>(ResponseStatus.SERVER_ERROR, "사용자 정보 확인 필요", null);
		}
		return new ResponseBodyWrapped<List<Notice>>(ResponseStatus.SERVER_ERROR, "오류가 발생하였습니다. 다시 시도해주시기 바랍니다.", null);
	}

	@Override
	public ResponseBodyWrapped<Notice> get(Notice data) {
		RestTemplate restTemplate = RestTemplateFactory.getInstance();
		HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
		HttpEntity request = new HttpEntity<String>(requestHeaders);
		ResponseEntity<String> result = null;
		try{
			result = restTemplate.exchange(Constants.apiNotice, HttpMethod.GET, request, String.class);
		} catch (ResourceAccessException timeoutException){
			Log.e("dream", timeoutException.toString());
			if(timeoutException.getCause() instanceof ConnectTimeoutException){
				return new ResponseBodyWrapped<Notice>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
			}
		} catch (RestClientException e) {
			Log.e("dream", e.toString());
			return new ResponseBodyWrapped<Notice>(ResponseStatus.SERVER_ERROR, "오류가 발생하였습니다. 다시 시도해주시기 바랍니다.", null);
		}

		if(result != null && result.getStatusCode() == HttpStatus.OK){
			Gson gson = JsonFactory.getInstance();
			Type type = new TypeToken<ResponseBodyWrapped<Notice>>(){}.getType();
			ResponseBodyWrapped<Notice> responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);
			return responseBodyWrapped;
		}else if (result != null && result.getStatusCode() == HttpStatus.UNAUTHORIZED){
			return new ResponseBodyWrapped<Notice>(ResponseStatus.SERVER_ERROR, "사용자 정보 확인 필요", null);
		}
		return new ResponseBodyWrapped<Notice>(ResponseStatus.SERVER_ERROR, "오류가 발생하였습니다. 다시 시도해주시기 바랍니다.", null);
	}

	@Override
	public ResponseBodyWrapped<Notice> delete(Notice data) {
		return null;
	}
}
