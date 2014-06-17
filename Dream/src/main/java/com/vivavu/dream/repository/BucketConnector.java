package com.vivavu.dream.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vivavu.dream.common.Constants;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.common.RestTemplateFactory;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.model.bucket.Today;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.util.ImageUtil;
import com.vivavu.dream.util.JsonFactory;
import com.vivavu.dream.util.RestTemplateUtils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    public ResponseBodyWrapped<List<Bucket>> getBucketList(){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(getContext());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;

        try {
            resultString = restTemplate.exchange(Constants.apiBuckets, HttpMethod.GET, request, String.class, getContext().getUser().getId());
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<List<Bucket>> result = new ResponseBodyWrapped<List<Bucket>>("error", String.valueOf(resultString.getStatusCode()), new ArrayList<Bucket>());

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

        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Bucket> result = new ResponseBodyWrapped<Bucket>("error", String.valueOf(resultString.getStatusCode()), new Bucket());

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

        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Bucket> result = new ResponseBodyWrapped<Bucket>("error", String.valueOf(resultString.getStatusCode()), new Bucket());

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
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<Bucket> result = new ResponseBodyWrapped<Bucket>("error", String.valueOf(resultString.getStatusCode()), new Bucket());

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            Type type = new TypeToken<ResponseBodyWrapped<List<Bucket>>>(){}.getType();
            result = gson.fromJson((String) resultString.getBody(), type);
        }

        return result;
    }

    public ResponseBodyWrapped<List<Today>> getTodayList(String lastTodayDate){
        RestTemplate restTemplate = RestTemplateFactory.getInstance();
        HttpHeaders requestHeaders = getBasicAuthHeader(getContext());
        HttpEntity request = new HttpEntity<String>(requestHeaders);
        ResponseEntity<String> resultString = null;
        StringBuffer url = new StringBuffer(Constants.apiPlanList);
        if(lastTodayDate != null){
            url.append("?fdate=");
            url.append(lastTodayDate);
        }
        try {
            resultString = restTemplate.exchange(url.toString(), HttpMethod.GET, request, String.class, getContext().getUser().getId());
        } catch (RestClientException e) {
            Log.e("dream", e.toString());
        }

        ResponseBodyWrapped<List<Today>> result = new ResponseBodyWrapped<List<Today>>("error", String.valueOf(resultString.getStatusCode()), new ArrayList<Today>());

        if(RestTemplateUtils.isAvailableParseToJson(resultString)){
            Gson gson = JsonFactory.getInstance();
            //Type type = new TypeToken<ResponseBodyWrapped<List<Today>>>(){}.getType();
            //result = gson.fromJson((String) resultString.getBody(), type);
            Type type = new TypeToken<ResponseBodyWrapped<List<Today>>>(){}.getType();
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
