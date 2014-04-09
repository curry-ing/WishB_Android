package com.vivavu.dream.handler;

import com.vivavu.dream.common.DreamApp;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by yuja on 14. 2. 17.
 */
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    private static DreamApp context;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        MediaType contentType = response.getHeaders().getContentType();
        Charset charset = contentType != null ? contentType.getCharSet() : null;
        if(contentType != null && contentType == MediaType.APPLICATION_JSON){

        }else{

        }

        byte[] body = getResponseBody(response);
        /*switch (statusCode.series()) {
            case CLIENT_ERROR:
                throw new HttpClientErrorException(statusCode, response.getStatusText(), body, charset);
            case SERVER_ERROR:
                throw new HttpServerErrorException(statusCode, response.getStatusText(), body, charset);
            default:
                throw new RestClientException("Unknown status code [" + statusCode + "]");
        }*/
    }

    private byte[] getResponseBody(ClientHttpResponse response) {
        try {
            InputStream responseBody = response.getBody();
            if (responseBody != null) {
                return FileCopyUtils.copyToByteArray(responseBody);
            }
        } catch (IOException ex) {
            // ignore
        }
        return new byte[0];
    }

    public static DreamApp getContext() {
        if (context == null) {
            context = DreamApp.getInstance();
        }
        return context;
    }
}
