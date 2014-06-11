package com.vivavu.dream.repository.connector;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.model.bucket.timeline.Timeline;
import com.vivavu.dream.model.bucket.timeline.TimelineMetaInfo;
import com.vivavu.dream.repository.Connector;
import com.vivavu.dream.util.ImageUtil;
import com.vivavu.dream.util.JsonFactory;
import com.vivavu.dream.util.RestTemplateUtils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
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

        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<TimelineMetaInfo> result = null;

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<TimelineMetaInfo>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
            if(resultString.getStatusCode() == HttpStatus.NO_CONTENT){
                result = new ResponseBodyWrapped<TimelineMetaInfo>("succes", String.valueOf(resultString.getStatusCode()), new TimelineMetaInfo());
            }
        } else {
            result = new ResponseBodyWrapped<TimelineMetaInfo>("error", String.valueOf(resultString.getStatusCode()), new TimelineMetaInfo());
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

        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Timeline> result = new ResponseBodyWrapped<Timeline>("error", String.valueOf(resultString.getStatusCode()), new Timeline());

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

        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Timeline> result = new ResponseBodyWrapped<Timeline>("error", String.valueOf(resultString.getStatusCode()), new Timeline());

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

        final MultiValueMap<String, Object> requestBucket = new LinkedMultiValueMap<String, Object>();

        if(data != null && data.getPhoto() != null) {
            requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        }else {
            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }

        if(data.getText() != null){
            requestBucket.set("text", data.getText());
        }
        if(data.getPhoto() != null && data.getPhoto().isFile()){
            Bitmap bm = ImageUtil.getBitmap(data.getPhoto().getAbsolutePath(), 1024, 1024);
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 70, byteArray );
            ByteArrayResource bar = new ByteArrayResource(byteArray.toByteArray()){
                @Override
                public String getFilename() throws IllegalStateException {
                    return data.getPhoto().getName();
                }
            };
            requestBucket.add("photo", bar);
        }

        HttpEntity request = new HttpEntity<MultiValueMap<String, Object>>(requestBucket, requestHeaders);

        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiTimeline, HttpMethod.POST, request, String.class, data.getBucketId());

        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Post> result = new ResponseBodyWrapped<Post>("error", String.valueOf(resultString.getStatusCode()), new Post(new Date()));

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

        final MultiValueMap<String, Object> requestBucket = new LinkedMultiValueMap<String, Object>();

        if(data != null && data.getPhoto() != null) {
            requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        }else {
            requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }

        if(data.getText() != null){
            requestBucket.set("text", data.getText());
        }
        if(data.getPhoto() != null && data.getPhoto().isFile()){
            Bitmap bm = ImageUtil.getBitmap(data.getPhoto().getAbsolutePath(), 1024, 1024);
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 70, byteArray );
            ByteArrayResource bar = new ByteArrayResource(byteArray.toByteArray()){
                @Override
                public String getFilename() throws IllegalStateException {
                    return data.getPhoto().getName();
                }
            };
            requestBucket.add("photo", bar);
        }

        HttpEntity request = new HttpEntity<MultiValueMap<String, Object>>(requestBucket, requestHeaders);

        ResponseEntity<String> resultString = null;
        try {
            resultString = restTemplate.exchange(Constants.apiTimelineInfo, HttpMethod.PUT, request, String.class, data.getId());

        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Post> result = new ResponseBodyWrapped<Post>("error", String.valueOf(resultString.getStatusCode()), new Post(new Date()));

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

        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Post> result = new ResponseBodyWrapped<Post>("error", String.valueOf(resultString.getStatusCode()), new Post(new Date()));

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

        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Post> result = new ResponseBodyWrapped<Post>("error", String.valueOf(resultString.getStatusCode()), new Post(new Date()));

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<Post>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }
        return result;

    }
}
