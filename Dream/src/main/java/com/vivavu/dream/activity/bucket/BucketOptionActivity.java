package com.vivavu.dream.activity.bucket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    @InjectView(R.id.btn_option_remove)
    Button mBtnOptionRemove;
    @InjectView(R.id.layout_bucket_option_note)
    LinearLayout mLayoutBucketOptionNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bucket_option_template);
        ButterKnife.inject(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, bucketOption)
                .commit();
        mBtnOptionRemove.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bucket_add_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.bucket_add_menu_save:
                saveOption();

                return true;
            /*case R.id.bucket_add_menu_cancel:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if(view == mBtnOptionRemove){
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
        }
    }

    public void saveOption() {

        Option option = bucketOption.getContents();
        Intent intent = new Intent();
        intent.putExtra("option.result", option);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
