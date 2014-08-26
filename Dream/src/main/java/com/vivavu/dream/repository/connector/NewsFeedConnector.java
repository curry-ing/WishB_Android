package com.vivavu.dream.repository.connector;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.NewsFeed;
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
 * Created by yuja on 2014-08-26.
 */
public class NewsFeedConnector extends Connector<NewsFeed> {
	@Override
	public ResponseBodyWrapped<NewsFeed> post(NewsFeed data) {
		return null;
	}

	@Override
	public ResponseBodyWrapped<NewsFeed> put(NewsFeed data) {
		return null;
	}

	public ResponseBodyWrapped<List<NewsFeed>> getList(int page){
		RestTemplate restTemplate = RestTemplateFactory.getInstance();
		HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
		HttpEntity request = new HttpEntity<String>(requestHeaders);
		ResponseEntity<String> result = null;
		try{
			result = restTemplate.exchange(Constants.apiNewsFeed, HttpMethod.GET, request, String.class, page);
		} catch (ResourceAccessException timeoutException){
			Log.e("dream", timeoutException.toString());
			if(timeoutException.getCause() instanceof ConnectTimeoutException){
				return new ResponseBodyWrapped<List<NewsFeed>>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
			}
		} catch (RestClientException e) {
			Log.e("dream", e.toString());
			return new ResponseBodyWrapped<List<NewsFeed>>(ResponseStatus.SERVER_ERROR, "오류가 발생하였습니다. 다시 시도해주시기 바랍니다.", null);
		}

		if(result != null && result.getStatusCode() == HttpStatus.OK){
			Gson gson = JsonFactory.getInstance();
			Type type = new TypeToken<ResponseBodyWrapped<List<NewsFeed>>>(){}.getType();
			ResponseBodyWrapped<List<NewsFeed>> responseBodyWrapped = gson.fromJson(String.valueOf(result.getBody()), type);
			return responseBodyWrapped;
		}else if (result != null && result.getStatusCode() == HttpStatus.UNAUTHORIZED){
			return new ResponseBodyWrapped<List<NewsFeed>>(ResponseStatus.SERVER_ERROR, "사용자 정보 확인 필요", null);
		}
		return new ResponseBodyWrapped<List<NewsFeed>>(ResponseStatus.SERVER_ERROR, "오류가 발생하였습니다. 다시 시도해주시기 바랍니다.", null);
	}

	@Override
	public ResponseBodyWrapped<NewsFeed> get(NewsFeed data) {

		return null;
	}

	@Override
	public ResponseBodyWrapped<NewsFeed> delete(NewsFeed data) {
		return null;
	}
}
