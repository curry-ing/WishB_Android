package com.vivavu.dream.fragment.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.main.MainActivity;
import com.vivavu.dream.common.DreamApp;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 1. 23.
 */
public class LeftMenuDrawerFragment extends Fragment {
    private DreamApp context = null;
    @InjectView(R.id.main_left_menu_btn_profile)
    ImageButton mMainLeftMenuBtnProfile;
    @InjectView(R.id.main_left_menu_txt_name)
    EditText mMainLeftMenuTxtName;
    @InjectView(R.id.main_left_menu_btn_name)
    Button mMainLeftMenuBtnName;
    @InjectView(R.id.main_left_menu_btn_badge1)
    Button mMainLeftMenuBtnBadge1;
    @InjectView(R.id.main_left_menu_btn_badge2)
    Button mMainLeftMenuBtnBadge2;
    @InjectView(R.id.main_left_menu_btn_badge3)
    Button mMainLeftMenuBtnBadge3;
    @InjectView(R.id.main_left_menu_btn_cnt_bucket)
    Button mMainLeftMenuBtnCntBucket;
    @InjectView(R.id.main_left_menu_txt_bucket)
    TextView mMainLeftMenuTxtBucket;
    @InjectView(R.id.main_left_menu_btn_cnt_friends)
    Button mMainLeftMenuBtnCntFriends;
    @InjectView(R.id.main_left_menu_txt_friends)
    TextView mMainLeftMenuTxtFriends;
    @InjectView(R.id.main_left_menu_btn_cnt_badge)
    Button mMainLeftMenuBtnCntBadge;
    @InjectView(R.id.main_left_menu_txt_badge)
    TextView mMainLeftMenuTxtBadge;
    @InjectView(R.id.main_left_menu_btn_setting)
    Button mMainLeftMenuBtnSetting;
    @InjectView(R.id.main_left_menu_btn_notice)
    Button mMainLeftMenuBtnNotice;
    @InjectView(R.id.main_left_menu_btn_update)
    Button mMainLeftMenuBtnUpdate;
    @InjectView(R.id.main_left_menu_btn_logout)
    Button mMainLeftMenuBtnLogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = DreamApp.getInstance();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main_left_menu, container, false);
        ButterKnife.inject(this, rootView);

        mMainLeftMenuBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "로그아웃", Toast.LENGTH_SHORT ).show();
                if(getActivity() instanceof MainActivity){
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.logout();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        bindData();
    }

    private void bindData(){
        //todo: 로그인 체크하는 것은 한곳에서만 수행할것
        if(context.isLogin()){
            mMainLeftMenuTxtName.setText(context.getUser().getUsername());
        }
    }


}
