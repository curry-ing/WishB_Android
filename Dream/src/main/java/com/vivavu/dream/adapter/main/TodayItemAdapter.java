package com.vivavu.dream.adapter.main;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.bucket.BucketAddActivity;
import com.vivavu.dream.model.bucket.Today;
import com.vivavu.dream.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 2. 27.
 */
public class TodayItemAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<Today> todayList;

    public TodayItemAdapter(Context context) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.todayList = new ArrayList<Today>();
    }

    public TodayItemAdapter(Context context, List<Today> todayList) {
        this.context = context;
        this.mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.todayList = todayList;
    }

    @Override
    public int getCount() {
        return todayList.size();
    }

    @Override
    public Object getItem(int position) {
        return todayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ButterknifeViewHolder holder = null;
        Today today = (Today) getItem(position);
        if(convertView == null){
            convertView = (ViewGroup) mInflater.inflate(R.layout.today_item, parent, false);
            holder = new ButterknifeViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ButterknifeViewHolder) convertView.getTag();
        }

        bind(holder, today);
        return convertView;
    }

    private void bind(ButterknifeViewHolder holder, Today today) {
        holder.mBookTitle.setText(today.getTitle());
        holder.mBookDudate.setText(DateUtils.getDateString(today.getDeadline(), "yyyy.MM.dd"));
        holder.mBtnBookWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent();
                intent.setClass(getContext(), BucketAddActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                getContext().startActivity(intent);
            }
        });
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Today> getTodayList() {
        return todayList;
    }

    public void setTodayList(List<Today> todayList) {
        this.todayList = todayList;
    }

    /**
 * This class contains all butterknife-injected Views & Layouts from layout file 'null'
 * for easy to all layout elements.
 *
 * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
 */
    static

    class ButterknifeViewHolder {
        @InjectView(R.id.btn_today_complete)
        Button mBtnTodayComplete;
        @InjectView(R.id.book_cover_image)
        ImageView mBookCoverImage;
        @InjectView(R.id.btn_book_write)
        Button mBtnBookWrite;
        @InjectView(R.id.btn_book_setting)
        Button mBtnBookSetting;
        @InjectView(R.id.book_title)
        TextView mBookTitle;
        @InjectView(R.id.book_dudate)
        TextView mBookDudate;
        @InjectView(R.id.book_status)
        TextView mBookStatus;
        @InjectView(R.id.book_cover)
        LinearLayout mBookCover;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
