package com.vivavu.dream.repository.connector;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.common.enums.ResponseStatus;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.model.bucket.timeline.Timeline;
import com.vivavu.dream.model.bucket.timeline.TimelineMetaInfo;
import com.vivavu.dream.repository.Connector;
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
import java.util.Date;

/**
 * Created by yuja on 2014-03-31.
 */
public class TimelineConnector extends Connector<Post> {

    public ResponseBodyWrapped<TimelineMetaInfo> getTimelineMetaInfo(Integer bucketId){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiTimelineMetaInfo, HttpMethod.GET, request, String.class, bucketId);
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<TimelineMetaInfo>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<TimelineMetaInfo> result = null;

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<TimelineMetaInfo>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
            if(resultString.getStatusCode() == HttpStatus.NO_CONTENT){
                result = new ResponseBodyWrapped<TimelineMetaInfo>(ResponseStatus.SUCCESS, RestTemplateUtils.getStatusCodeString(resultString), new TimelineMetaInfo());
            }
        } else {
            result = new ResponseBodyWrapped<TimelineMetaInfo>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new TimelineMetaInfo());
        }
        return result;
    }

    public ResponseBodyWrapped<Timeline> getTimelineForDate(Integer bucketId, String date){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiTimelineForDate, HttpMethod.GET, request, String.class, bucketId, date);
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<Timeline>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Timeline> result = new ResponseBodyWrapped<Timeline>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new Timeline());

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<Timeline>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }
        return result;
    }

    public ResponseBodyWrapped<Timeline> getTimelineAll(Integer bucketId){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiTimeline, HttpMethod.GET, request, String.class, bucketId);
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<Timeline>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Timeline> result = new ResponseBodyWrapped<Timeline>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new Timeline());

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<Timeline>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }
        return result;
    }

    public ResponseBodyWrapped<Timeline> getTimelineWithPage(Integer bucketId, Integer page){
        if (page == null || page < 1){
            page = 1;
        }
        
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiTimelineWithPage, HttpMethod.GET, request, String.class, bucketId, page);
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<Timeline>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Timeline> result = new ResponseBodyWrapped<Timeline>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new Timeline());

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<Timeline>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }
        return result;
    }

    @Override
    public ResponseBodyWrapped<Post> post(final Post data) {
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());

        final MultiValueMap<String, Object> requestBucket = convertPostToMap(data);

        if(data != null && data.getPhoto() != null) {
            requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        }else {
            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }

        HttpEntity request = new HttpEntity<MultiValueMap<String, Object>>(requestBucket, requestHeaders);

        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiTimeline, HttpMethod.POST, request, String.class, data.getBucketId());
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<Post>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Post> result = new ResponseBodyWrapped<Post>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new Post(new Date()));

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<Post>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }
        return result;
    }

    @Override
    public ResponseBodyWrapped<Post> put(final Post data) {
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());

        final MultiValueMap<String, Object> requestBucket = convertPostToMap(data);

        if(data != null && data.getPhoto() != null) {
            requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        }else {
            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }

        HttpEntity request = new HttpEntity<MultiValueMap<String, Object>>(requestBucket, requestHeaders);

        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiTimelineInfo, HttpMethod.PUT, request, String.class, data.getId());
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<Post>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Post> result = new ResponseBodyWrapped<Post>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new Post(new Date()));

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<Post>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }
        return result;
    }

    @Override
    public ResponseBodyWrapped<Post> get(Post data) {
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiTimelineInfo, HttpMethod.POST, request, String.class, data.getId());
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<Post>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Post> result = new ResponseBodyWrapped<Post>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new Post(new Date()));

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<Post>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }
        return result;
    }

    @Override
    public ResponseBodyWrapped<Post> delete(Post data) {

        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(DreamApp.getInstance());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiTimelineInfo, HttpMethod.DELETE, request, String.class, data.getId());
        } catch (ResourceAccessException timeoutException){
	        Log.e("dream", timeoutException.toString());
	        if(timeoutException.getCause() instanceof ConnectTimeoutException){
		        return new ResponseBodyWrapped<Post>(ResponseStatus.TIMEOUT, "서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.", null);
	        }
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Post> result = new ResponseBodyWrapped<Post>(ResponseStatus.SERVER_ERROR, RestTemplateUtils.getStatusCodeString(resultString), new Post(new Date()));

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<Post>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }
        return result;

    }

    public MultiValueMap convertPostToMap(final Post post){
        MultiValueMap<String, Object> requestPost = new LinkedMultiValueMap<String, Object>();

        if(post.getText() != null){
            requestPost.set("text", post.getText());
        }
        if(post.getPhoto() != null && post.getPhoto().isFile()){
            ByteArrayResource byteArrayResource = ImageUtil.convertImageFileToByteArrayResource(post.getPhoto(), 1024, 1024, 70);
            requestPost.add("photo", byteArrayResource);
        }
        if(post.getContentDt() != null){
            requestPost.set("content_dt", DateUtils.getDateString(post.getContentDt(), "yyyy-MM-dd HH:mm:ss") );
        }
        if(post.getImgUrl() == null){
            requestPost.set("img_id", "");
        }
        if(post.getFbShare() == null){
            requestPost.set("fb_share", "false");
        } else {
            requestPost.set("fb_share", post.getFbShare());
        }

        return requestPost;
    }
}
