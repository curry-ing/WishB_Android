package com.vivavu.dream.activity.bucket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
    Button mMenuSave;

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
        actionBar.setCustomView(R.layout.actionbar_bucket_edit_option);
        actionBar.setDisplayShowCustomEnabled(true);

        ButterKnife.inject(this);

        Intent data = getIntent();

        Option initData = (Option) data.getSerializableExtra("option");
        option = (Option) initData.clone();
        if(initData instanceof OptionDDay){
            bucketOption = new DDayFragment();
        }else if(initData instanceof OptionDescription){
            bucketOption = new DescriptionFragment();
        }else if(initData instanceof OptionRepeat){
            bucketOption = new RepeatFragment();
        }else{
            finish();
        }
        bucketOption.setContents(initData);

        mMenuSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOption();
            }
        });
        mMenuPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, bucketOption)
                .commit();
    }

    public void confirm(){
        if(bucketOption.isModFlag()){
            AlertDialog.Builder alertConfirm = new AlertDialog.Builder(this);
            alertConfirm.setTitle(getString(R.string.txt_bucket_option_confirm_edit_title));
            alertConfirm.setMessage(getString(R.string.txt_bucket_option_confirm_edit_body)).setCancelable(false).setPositiveButton(getString(R.string.confirm_yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveOption();
                        }
                    }
            ).setNegativeButton(getString(R.string.confirm_no),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(Activity.RESULT_CANCELED);
                            finish();
                            return;
                        }
                    }
            );
            AlertDialog alert = alertConfirm.create();
            alert.show();
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    public void saveOption() {

        Option option = bucketOption.getContents();
        Intent intent = new Intent();
        intent.putExtra("option.result", option);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        confirm();
    }
}
