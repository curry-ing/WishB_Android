package com.vivavu.dream.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.Today;
import com.vivavu.dream.model.bucket.TodayPager;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.util.ImageUtil;
import com.vivavu.dream.util.JsonFactory;
import com.vivavu.dream.util.RestTemplateUtils;

import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuja on 14. 3. 6.
 */
public class BucketConnector {
    private static DreamApp context;

    public HttpHeaders getBasicAuthHeader(DreamApp context){
        if(context.getTokenType() !=null &&  !"facebook".equals(context.getTokenType())){
            return RestTemplateUtils.getBasicAuthHeader(context.getToken(), "unused");
        }
        else{
            return RestTemplateUtils.getBasicAuthHeader(context.getToken(), "facebook");
        }
    }

	public ResponseBodyWrapped<Bucket> getBucket(Integer bucketId){

		RestTemplate restTemplate = RestTemplateFactory.getInstance();
		HttpHeaders requestHeaders = getBasicAuthHeader(getContext());
		HttpEntity request = new HttpEntity<String>(requestHeaders);
		ResponseEntity<String> resultString = null;

		try {
			resultString = restTemplate.exchange(Constants.apiBucketInfo, HttpMethod.GET, request, String.class, bucketId);
		} catch (ResourceAccessException timeoutException){
			Log.e("dream", timeoutException.toString());
			if(timeoutException.getCause() instanceof ConnectTimeoutException){
				return new ResponseBodyWrapped<Bucket>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
			}
		} catch (RestClientException e) {
			Log.e("dream", e.toString());
		}

		ResponseBodyWrapped<Bucket> result = new ResponseBodyWrapped<Bucket>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new Bucket());

		if(RestTemplateUtils.isAvailableParseToJson(resultString)){
			Gson gson = JsonFactory.getInstance();
			Type type = new TypeToken<ResponseBodyWrapped<Bucket    >>(){}.getType();
			result = gson.fromJson((String) resultString.getBody(), type);
		}

		return result;
	}

    public ResponseBodyWrapped<List<Bucket>> getBucketList(){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(getContext());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;

        try {
            resultString = restTemplate.exchange(Constants.apiBuckets, HttpMethod.GET, request, String.class, getContext().getUser().getId());
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

    public MultiValueMap convertBucketToMap(final Bucket bucket){
        MultiValueMap<String, Object> requestBucket = new LinkedMultiValueMap<String, Object>();

        requestBucket.add("title", bucket.getTitle());
        if (bucket.getDescription() != null) {
            requestBucket.add("description", bucket.getDescription());
        }
        if (bucket.getDeadline() != null) {
            requestBucket.add("deadline", DateUtils.getDateString(bucket.getDeadline(), "yyyy-MM-dd"));
        } else {
            requestBucket.add("deadline", "2999-12-31");
        }

        if(bucket.getIsPrivate() != null){
            requestBucket.add("private", String.valueOf(bucket.getIsPrivate()));
        }
        if(bucket.getRange() != null){
            requestBucket.add("range", bucket.getRange());
        }

        if(bucket.getScope() != null){
            requestBucket.add("scope", bucket.getScope());
        }
        if(bucket.getStatus() != null){
            requestBucket.add("status", String.valueOf(bucket.getStatus()));
        }
        if (bucket.getRptType() != null) {
            requestBucket.add("rpt_type", bucket.getRptType());
            requestBucket.add("rpt_cndt", bucket.getRptCndt());
        }
        if(bucket.getCvrImgUrl() == null){
            requestBucket.add("cvr_img_id", "");
        }
        if(bucket.getFile() != null && bucket.getFile().exists()) {
            ByteArrayResource byteArrayResource = ImageUtil.convertImageFileToByteArrayResource(bucket.getFile(), 1024, 1024, 70);
            requestBucket.add("photo", byteArrayResource);
        }
	    if(bucket.getFbShare() == null){
		    requestBucket.set("fb_share", "false");
	    } else {
		    requestBucket.set("fb_share", bucket.getFbShare());
	    }

        return requestBucket;
    }

    public ResponseBodyWrapped<Bucket> postBucketDefault(final Bucket bucket, Object... variable){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(getContext());

        final MultiValueMap<String, Object> requestBucket = convertBucketToMap(bucket);

        if(bucket.getFile() != null && bucket.getFile().exists()) {
            requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        }else {
            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }

        HttpEntity request = new HttpEntity<MultiValueMap<String, Object>>(requestBucket, requestHeaders);
        //HttpEntity request = new HttpEntity<Bucket>(bucket, requestHeaders);

        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiBuckets, HttpMethod.POST, request, String.class, getContext().getUser().getId());
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<Bucket>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Bucket> result = new ResponseBodyWrapped<Bucket>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new Bucket());

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<Bucket>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }

        return result;
    }

    public ResponseBodyWrapped<Bucket> updateBucketInfo(final Bucket bucket, Object... variable){

        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(getContext());

        final MultiValueMap<String, Object> requestBucket = convertBucketToMap(bucket);

        if(bucket.getFile() != null && bucket.getFile().exists()) {
            requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        }else {
            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }

        HttpEntity request = new HttpEntity<MultiValueMap<String, Object>>(requestBucket, requestHeaders);
        //HttpEntity request = new HttpEntity<Bucket>(bucket, requestHeaders);

        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiBucketInfo, HttpMethod.PUT, request, String.class, bucket.getId());
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<Bucket>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Bucket> result = new ResponseBodyWrapped<Bucket>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new Bucket());

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<Bucket>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }

        return result;
    }

    public ResponseBodyWrapped<Bucket> deleteBucket(Bucket bucket){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(getContext());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;

        try {
            resultString = restTemplate.exchange(Constants.apiBucketInfo, HttpMethod.DELETE, request, String.class, bucket.getId());
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<Bucket>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Bucket> result = new ResponseBodyWrapped<Bucket>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new Bucket());

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<List<Bucket>>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }

        return result;
    }

    public ResponseBodyWrapped<TodayPager> getTodayList(Integer page){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(getContext());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;
        if(page == null || page < 1){
            page = 1;
        }
        try {
            resultString = restTemplate.exchange(Constants.apiTodayListWithPage, HttpMethod.GET, request, String.class, getContext().getUser().getId(), page);
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<TodayPager>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<TodayPager> result = new ResponseBodyWrapped<TodayPager>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new TodayPager());

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            if(resultString.getStatusCode() == HttpStatus.NO_CONTENT){
                result.setResponseStatus(ResponseStatus.SUCCESS);
                TodayPager data = result.getData();
                data.setPageData(new ArrayList<Today>());
                return result;
            }
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<TodayPager>>(){}.getType();
            result  = gson.fromJson((String) resultString.getBody(), type);
        }

        return result;
    }

    public static DreamApp getContext() {
        if (context == null){
            context = DreamApp.getInstance();
        }
        return context;
    }

    public static void setContext(DreamApp context) {
        BucketConnector.context = context;
    }
}
