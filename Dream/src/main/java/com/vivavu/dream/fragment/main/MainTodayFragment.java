package com.vivavu.dream.fragment.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.vivavu.dream.R;
import com.vivavu.dream.adapter.bucket.TodayRowAdapter;
import com.vivavu.dream.fragment.CustomBaseFragment;
import com.vivavu.dream.model.bucket.TodayGroup;
import com.vivavu.dream.repository.DataRepository;
import com.vivavu.dream.repository.task.CustomAsyncTask;
import com.vivavu.dream.repository.task.TodayAsyncTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 23.
 */
public class MainTodayFragment extends CustomBaseFragment implements PullToRefreshListView.OnRefreshListener<ListView>{
    public static String TAG = "com.vivavu.dream.fragment.main.MainTodayFragment";
    static public final int SEND_REFRESH_START = 0;
    static public final int SEND_REFRESH_STOP = 1;
    static public final int SEND_DATA_UPDATE = 2;
    @InjectView(R.id.list)
    protected PullToRefreshListView mList;

    private List<TodayGroup> todayList;
    private TodayRowAdapter todayRowAdapter;

    protected final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEND_REFRESH_START:
                    mList.setRefreshing();
                    break;
                case SEND_REFRESH_STOP:
                case SEND_DATA_UPDATE:
                    updateContents();
                    mList.onRefreshComplete();
                    break;
            }
        }
    };

    public MainTodayFragment() {
        this(new ArrayList<TodayGroup>());
    }

    public MainTodayFragment(List<TodayGroup> todayList) {
        this.todayList = todayList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_shelf_list, container, false);
        ButterKnife.inject(this, rootView);
        mList.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        handler.sendEmptyMessage(SEND_REFRESH_START);
    }

    protected void updateContents() {
        if(todayRowAdapter == null){
            todayRowAdapter = new TodayRowAdapter(getActivity(), todayList);
            mList.setAdapter(todayRowAdapter);
        }
        todayRowAdapter.setTodayGroupList(todayList);
        todayRowAdapter.notifyDataSetChanged();
    }

    /*@Override
    public void onRefresh(PullToRefreshBase<GridView> gridViewPullToRefreshBase) {
        TodayAsyncTask todayAsyncTask = new TodayAsyncTask(getContext());
        todayAsyncTask.setOnPostExecuteCallback(new CustomAsyncTask.OnPostExecuteCallback() {
            @Override
            public void onPostExecuteCallback() {
                DataAsync dataAsync = new DataAsync();
                dataAsync.execute();
            }
        });
        todayAsyncTask.execute();
    }*/

    @Override
    public void onRefresh(PullToRefreshBase<ListView> listViewPullToRefreshBase) {
        TodayAsyncTask todayAsyncTask = new TodayAsyncTask();
        todayAsyncTask.setOnPostExecuteCallback(new CustomAsyncTask.OnPostExecuteCallback() {
            @Override
            public void onPostExecuteCallback() {
                DataAsync dataAsync = new DataAsync();
                dataAsync.execute();
            }
        });
        todayAsyncTask.execute();
    }

    public class DataAsync extends AsyncTask<Void, Void, List<TodayGroup>> {

        @Override
        protected List<TodayGroup> doInBackground(Void... params) {
            List<TodayGroup> todayGroup = DataRepository.listTodayGroupAndTodayData();
            return todayGroup;
        }

        @Override
        protected void onPostExecute(List<TodayGroup> todayGroupList) {
            super.onPostExecute(todayGroupList);
            todayList = todayGroupList;
            handler.sendEmptyMessage(SEND_REFRESH_STOP);
        }
    }
}
