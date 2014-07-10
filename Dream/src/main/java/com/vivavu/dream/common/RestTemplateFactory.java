package com.vivavu.dream.common;

import com.vivavu.dream.handler.RestTemplateResponseErrorHandler;
import com.vivavu.dream.model.user.User;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuja on 14. 1. 8.
 */
public class RestTemplateFactory {
    private static RestTemplate restTemplate;
    private User user;

    public static RestTemplate getInstance(){
        if(restTemplate == null){
            HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpComponentsClientHttpRequestFactory.setReadTimeout(2000);
            httpComponentsClientHttpRequestFactory.setConnectTimeout(2000);
            restTemplate = new RestTemplate( httpComponentsClientHttpRequestFactory );
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            /**
             * jackson 라이브러리가 아닌 gson으로 변경
            MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
            restTemplate.getMessageConverters().add(converter);
            */
            //ObjectMapper om = converter.getObjectMapper();
            //om.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
            //om.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);//이부분은 json에 있는데 객체에 해당 대상이 없으면 그냥 넘기는 옵션
            List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();

            partConverters.add(new ByteArrayHttpMessageConverter());
            StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
            stringHttpMessageConverter.setWriteAcceptCharset(false);
            partConverters.add(stringHttpMessageConverter);
            partConverters.add(new ResourceHttpMessageConverter());

            FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            formHttpMessageConverter.setPartConverters(partConverters);
            restTemplate.getMessageConverters().add(formHttpMessageConverter);
            GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
            restTemplate.getMessageConverters().add(gsonHttpMessageConverter);
            restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());

        }
        return restTemplate;
    }

    public static RestTemplate getNewInstance(){
        RestTemplate newRestTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        newRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
        newRestTemplate.getMessageConverters().add(converter);
        return newRestTemplate;
    }

}
