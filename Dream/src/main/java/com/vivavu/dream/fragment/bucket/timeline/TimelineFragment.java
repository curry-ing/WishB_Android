package com.vivavu.dream.fragment.bucket.timeline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.TimelineActivity;
import com.vivavu.dream.activity.bucket.timeline.TimelineCalendarActivity;
import com.vivavu.dream.adapter.bucket.timeline.TimelineListAdapter;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.ResponseBodyWrapped;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.model.bucket.timeline.Timeline;
import com.vivavu.dream.model.bucket.timeline.TimelineMetaInfo;
import com.vivavu.dream.repository.connector.TimelineConnector;
import com.vivavu.dream.util.DateUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-03-27.
 */
public class TimelineFragment extends CustomBaseFragment {
    public static final String TAG = "com.vivavu.dream.fragment.bucket.timeline.TimelineFragment";

    protected TimelineMetaInfo timelineMetaInfo;
    protected Date selectedDate;
    protected List<Post> postList;
    protected TimelineListAdapter timelineListAdapter;

    @InjectView(R.id.btn_date)
    Button mBtnDate;
    @InjectView(R.id.btn_show)
    Button mBtnShow;
    @InjectView(R.id.list_timeline)
    ListView mListTimeline;

    private ProgressDialog progressDialog;
    private static final int FETCH_DATA_START = 6;
    private static final int FETCH_DATA_SUCCESS = 7;
    private static final int FETCH_DATA_FAIL = 8;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FETCH_DATA_START:
                    progressDialog.show();
                    break;
                case FETCH_DATA_SUCCESS:
                    postList = (List<Post>) msg.obj;
                    Collections.sort(postList);
                    updateTimeline();
                    progressDialog.dismiss();
                    break;
                case FETCH_DATA_FAIL:
                    progressDialog.dismiss();
                    break;
            }
        }
    };


    public TimelineFragment(TimelineMetaInfo timelineMetaInfo, int position) {
        this.timelineMetaInfo = timelineMetaInfo;
        this.selectedDate = timelineMetaInfo.getDateList().get(position);
    }

    public static TimelineFragment newInstance(TimelineMetaInfo timelineMetaInfo, int position){
        return new TimelineFragment(timelineMetaInfo, position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("진행중");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_timeline, container, false);
        ButterKnife.inject(this, rootView);

        initView();
        initEvent();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNetworkData();
    }

    private void initEvent() {
        mBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimelineCalendarActivity.class);
                intent.putExtra(TimelineCalendarActivity.dateExtraName, (Date) v.getTag());
                intent.putExtra(TimelineCalendarActivity.timelineMetaInfoExtraName, timelineMetaInfo);
                getActivity().startActivityForResult(intent, TimelineActivity.REQUEST_CALENDAR);
            }
        });
        mListTimeline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(getActivity() instanceof TimelineActivity){
                    TimelineActivity activity = (TimelineActivity) getActivity();
                    activity.viewPost(postList.get(position));
                }
            }
        });
    }

    private void updateTimeline(){
        if(timelineListAdapter == null){
            timelineListAdapter = new TimelineListAdapter(getActivity(), postList);
            mListTimeline.setAdapter(timelineListAdapter);
        }
        timelineListAdapter.setPostList(postList);
        timelineListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void getNetworkData() {
        Thread thread = new Thread(new NetworkThread());
        thread.start();
    }

    private void initView(){
        mBtnDate.setText(DateUtils.getDateString(selectedDate, "yyyy-MM-dd"));
        mBtnDate.setTag(selectedDate);
    }

    private class NetworkThread implements Runnable{
        @Override
        public void run() {
            handler.sendEmptyMessage(FETCH_DATA_START);

            TimelineConnector timelineConnector = new TimelineConnector();
            ResponseBodyWrapped<Timeline> result = timelineConnector.getTimelineForDate(timelineMetaInfo.getBucketId(), DateUtils.getDateString(selectedDate, "yyyyMMdd"));

            if(result.isSuccess()) {
                Message message = handler.obtainMessage(FETCH_DATA_SUCCESS, result.getData().getTimelineData());
                handler.sendMessage(message);
            }else {
                handler.sendEmptyMessage(FETCH_DATA_FAIL);
            }
        }
    }
}
