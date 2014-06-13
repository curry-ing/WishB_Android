package com.vivavu.dream.activity.bucket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.fragment.bucket.option.OptionBaseFragment;
import com.vivavu.dream.fragment.bucket.option.dday.DDayFragment;
import com.vivavu.dream.fragment.bucket.option.description.DescriptionFragment;
import com.vivavu.dream.fragment.bucket.option.repeat.RepeatFragment;
import com.vivavu.dream.model.bucket.option.Option;
import com.vivavu.dream.model.bucket.option.OptionDDay;
import com.vivavu.dream.model.bucket.option.OptionDescription;
import com.vivavu.dream.model.bucket.option.OptionRepeat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 14. 3. 14.
 */
public class BucketOptionActivity extends BaseActionBarActivity {
    OptionBaseFragment bucketOption;
    Option option;
    @InjectView(R.id.content_frame)
    LinearLayout mContentFrame;
    @InjectView(R.id.layout_bucket_option_note)
    LinearLayout mLayoutBucketOptionNote;
    @InjectView(R.id.menu_previous)
    ImageButton mMenuPrevious;
    @InjectView(R.id.menu_save)
    ImageButton mMenuSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);//api level 11 이상 부터 사용가능

        setContentView(R.layout.bucket_option_template);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);//로고 버튼 보이는 것 설정
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.actionbar_bucket_edit);
        actionBar.setDisplayShowCustomEnabled(true);

        ButterKnife.inject(this);

        Intent data = getIntent();

        Option initData = (Option) data.getSerializableExtra("option");
        option = (Option) initData.clone();
        if(initData instanceof OptionDDay){
            bucketOption = new DDayFragment((OptionDDay) initData);
        }else if(initData instanceof OptionDescription){
            bucketOption = new DescriptionFragment((OptionDescription) initData);
        }else if(initData instanceof OptionRepeat){
            bucketOption = new RepeatFragment((OptionRepeat) initData);
        }else{
            finish();
        }

        mMenuSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOption();
            }
        });
        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, bucketOption)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.bucket_add_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        /*if(view == mBtnOptionRemove){
            AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
            alertConfirm.setTitle("초기화 확인");
            alertConfirm.setMessage("초기화 하시겠습니까?").setCancelable(false).setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //bucketOption.reset();
                            bucketOption.setContents(option);
                        }
                    }
            ).setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }
            );
            AlertDialog alert = alertConfirm.create();
            alert.show();
        }*/
    }

    public void saveOption() {

        Option option = bucketOption.getContents();
        Intent intent = new Intent();
        intent.putExtra("option.result", option);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
