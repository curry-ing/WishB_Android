package com.vivavu.dream.repository.connector;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.Connector;
import com.vivavu.dream.util.JsonFactory;
import com.vivavu.dream.util.RestTemplateUtils;

import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuja on 2014-08-27.
 */
public class FriendsBucketConnector extends Connector<Bucket> {
	@Override
	public ResponseBodyWrapped<Bucket> post(Bucket data) {
		return null;
	}

	@Override
	public ResponseBodyWrapped<Bucket> put(Bucket data) {
		return null;
	}

	public ResponseBodyWrapped<List<Bucket>> getList(int userId) {
		RestTemplate restTemplate = RestTemplateFactory.getInstance();
		HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
		HttpEntity request = new HttpEntity<String>(requestHeaders);
		ResponseEntity<String> resultString = null;

		try {
			resultString = restTemplate.exchange(Constants.apiBuckets, HttpMethod.GET, request, String.class, userId);
		} catch (ResourceAccessException timeoutException){
			Log.e("dream", timeoutException.toString());
			if(timeoutException.getCause() instanceof ConnectTimeoutException){
				return new ResponseBodyWrapped<List<Bucket>>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
			}
		} catch (RestClientException e) {
			Log.e("dream", e.toString());
		}

		ResponseBodyWrapped<List<Bucket>> result = new ResponseBodyWrapped<List<Bucket>>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new ArrayList<Bucket>());

		if(RestTemplateUtils.isAvailableParseToJson(resultString)){
			Gson gson = JsonFactory.getInstance();
			Type type = new TypeToken<ResponseBodyWrapped<List<Bucket>>>(){}.getType();
			result = gson.fromJson((String) resultString.getBody(), type);
		}

		return result;
	}

	@Override
	public ResponseBodyWrapped<Bucket> get(Bucket data) {
		return null;
	}

	@Override
	public ResponseBodyWrapped<Bucket> delete(Bucket data) {
		return null;
	}
}
