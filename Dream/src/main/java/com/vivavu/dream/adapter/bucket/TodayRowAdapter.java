package com.vivavu.dream.adapter.bucket;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.adapter.main.TodayItemAdapter;
import com.vivavu.dream.model.bucket.TodayGroup;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.view.ExpandableHeightGridView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 9.
 */
public class TodayRowAdapter extends BaseAdapter implements View.OnClickListener {

    private LayoutInflater mInflater;
    private List<TodayGroup> mTodayGroupList;
    private FragmentActivity mContext;
    private Fragment parentFragment;

    public TodayRowAdapter(FragmentActivity context, List<TodayGroup> todayGroupList) {
        super();
        this.mContext = context;
        this.mInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mTodayGroupList = todayGroupList;
    }

    @Override
    public int getCount() {
        return mTodayGroupList.size();
    }

    @Override
    public TodayGroup getItem(int i) {
        return mTodayGroupList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TodayGroup item = getItem(i);
        ButterknifeViewHolder holder = null;
        if(view == null) {
            view = mInflater.inflate(R.layout.today_row, viewGroup, false);
            holder = new ButterknifeViewHolder(view);
            view.setTag(holder);

            holder.mBtnDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "날짜 클릭", Toast.LENGTH_SHORT).show();
                }
            });

            holder.mTodayContents.setAdapter(new TodayItemAdapter(getContext()));
            holder.mTodayContents.setExpanded(true);
        } else {
            holder = (ButterknifeViewHolder) view.getTag();
        }

        holder.mBtnDate.setText(DateUtils.getDateString(item.getDate(), "yyyy-MM-dd"));
        TodayItemAdapter adapter = (TodayItemAdapter) holder.mTodayContents.getAdapter();
        adapter.setTodayList(item.getTodayList());
        adapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.bucket_item_title:
            case R.id.bucket_btn_done:
                //R.layout.dialog는 xml 파일명이고  R.id.popup은 보여줄 레이아웃 아이디
                /*View layout = mInflater.inflate(R.layout.popup_plan_done, null);
                final PlanPopupDoneViewHolder planPopupDoneViewHolder = new PlanPopupDoneViewHolder(layout);

                AlertDialog.Builder aDialog = new AlertDialog.Builder(_context);

                aDialog.setTitle("완료확인"); //타이틀바 제목
                aDialog.setView(layout); //dialog.xml 파일을 뷰로 셋팅

                aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Plan plan = new Plan();
                        plan.setId((Integer) view.getTag());
                        int checkedButtonId = planPopupDoneViewHolder.mRadioGroupPopupPlanDone.getCheckedRadioButtonId();
                        plan.setIsDone(checkedButtonId == planPopupDoneViewHolder.mRadioBtnDoneTrue.getId());

                        *//*DataRepository.updatePlanStatus(plan);*//*
                    }
                });
                //그냥 닫기버튼을 위한 부분
                aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //팝업창 생성
                AlertDialog ad = aDialog.create();
                ad.show();//보여줌!*/
        }
    }

    private Context getContext() {
        return this.mContext;
    }

    public List<TodayGroup> getTodayGroupList() {
        return mTodayGroupList;
    }

    public void setTodayGroupList(List<TodayGroup> mTodayGroupList) {
        this.mTodayGroupList = mTodayGroupList;
    }

    /**
 * This class contains all butterknife-injected Views & Layouts from layout file 'null'
 * for easy to all layout elements.
 *
 * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
 */
    static

    class ButterknifeViewHolder {
        @InjectView(R.id.btn_date)
        Button mBtnDate;
        @InjectView(R.id.today_contents)
        ExpandableHeightGridView mTodayContents;
        @InjectView(R.id.today_container)
        LinearLayout mTodayContainer;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
