package com.vivavu.dream.repository.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

/**
 * Created by yuja on 14. 3. 16.
 */
public class ImageDownloadTask extends CustomAsyncTask<String, Void, Bitmap>{

    @Override
    protected Bitmap doInBackground(String... params) {
        // 직접 다운로드 하는 방법 개선 필요.
        DefaultHttpClient client = new DefaultHttpClient();
        String url = params[0];
        final HttpGet getRequest = new HttpGet(url);
        try
        {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK)
            {
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                InputStream inputStream = null;
                try
                {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally
                {
                    if (inputStream != null)
                    {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e)
        {
            getRequest.abort();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
            //Drawable drawable = new BitmapDrawable( getResources(), bitmap );
            //mIvCardImage.setImageBitmap(bitmap);
        }
    }
}