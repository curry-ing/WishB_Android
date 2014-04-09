package com.vivavu.dream.repository.task;

import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.Bucket;
import com.vivavu.dream.repository.BucketConnector;
import com.vivavu.dream.repository.DataRepository;

import java.util.List;

/**
 * Created by yuja on 14. 3. 6.
 */
public class BucketAsyncTask extends CustomAsyncTask<Void, Void, ResponseBodyWrapped<List<Bucket>>>  {

    @Override
    protected ResponseBodyWrapped<List<Bucket>> doInBackground(Void... params) {
        BucketConnector bucketConnector = new BucketConnector();
        ResponseBodyWrapped<List<Bucket>> result = bucketConnector.getBucketList();
        return result;
    }

    @Override
    protected void onPostExecute(ResponseBodyWrapped<List<Bucket>> listResponseBodyWrapped) {
        if(listResponseBodyWrapped != null) {
            DataRepository.saveBuckets(listResponseBodyWrapped.getData());
        }
        if(onPostExecuteCallback != null) {
            onPostExecuteCallback.onPostExecuteCallback();//위에것을 하고 이 문장을 실행시켜야함
        }
    }
}
