package com.vivavu.dream.activity.intro;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vivavu.dream.R;
import com.vivavu.dream.activity.login.LoginActivity;
import com.vivavu.dream.activity.login.PrivacyActivity;
import com.vivavu.dream.activity.login.UserAgreementActivity;
import com.vivavu.dream.activity.login.UserRegisterActivity;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.Code;
import com.vivavu.dream.util.AndroidUtils;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IntroActivity extends BaseActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */

    @InjectView(R.id.sign_in_button)
    Button mSignInButton;
    @InjectView(R.id.register_button)
    Button mRegisterButton;
    @InjectView(R.id.intro_agreement_txt)
    TextView mIntroAgreementTxt;
//    @InjectView(R.id.pager)
//    ViewPager mPager;
//    @InjectView(R.id.intro_viewpager_indicator)
//    CirclePageIndicator mIntroViewpagerIndicator;
//    @InjectView(R.id.facebook_container)
//    LinearLayout mFacebookContainer;
//    @InjectView(R.id.btn_user_agreement)
//    Button mBtnUserAgreement;
//    @InjectView(R.id.btn_private)
//    Button mBtnPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.inject(this);
        setResult(RESULT_CANCELED);//intro에서 종료는 무조건 프로그램 종료
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
//        mPager.setAdapter(mSectionsPagerAdapter);
//        mIntroViewpagerIndicator.setViewPager(mPager);
        mRegisterButton.setTypeface(getNanumBarunGothicFont());
        mSignInButton.setTypeface(getNanumBarunGothicFont());
        mIntroAgreementTxt.setTypeface(getNanumBarunGothicFont());
        mIntroAgreementTxt.setTextColor(Color.WHITE);
        //mIntroAgreementTxt.setTextSize(12);

        SpannableString agreementText = new SpannableString(getResources().getString(R.string.regist_agreement));
        ClickableSpan agreement = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UserAgreementActivity.class);
                startActivity(intent);
            }
        };
        ClickableSpan privacy = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PrivacyActivity.class);
                startActivity(intent);
            }
        };
        agreementText.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 26, 0);
        agreementText.setSpan(agreement, 27, 31, 0);
        agreementText.setSpan(new ForegroundColorSpan(Color.GRAY), 27, 31, 0);
        agreementText.setSpan(new MyclickableSpan("test"), 27, 31, 0);
        agreementText.setSpan(new StyleSpan(Typeface.BOLD), 27, 31, 0);

        mIntroAgreementTxt.setMovementMethod(LinkMovementMethod.getInstance());
        mIntroAgreementTxt.setText(agreementText, TextView.BufferType.SPANNABLE);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goRegister();
            }
        });
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLogin();
            }
        });

        AndroidUtils.getKeyHashes(this);

    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ( keyCode == KeyEvent.KEYCODE_MENU ) {
			// do nothing
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void goPrivacy() {
        Intent intent = new Intent();
        intent.setClass(this, PrivacyActivity.class);
        startActivity(intent);
    }

    private void goUserAgreement() {
        Intent intent = new Intent();
        intent.setClass(this, UserAgreementActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Code.ACT_LOGIN:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                return;
            case Code.ACT_USER_REGISTER:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                return;
        }
    }

    private void goRegister() {
        Intent intent = new Intent();
        intent.setClass(this, UserRegisterActivity.class);
        startActivityForResult(intent, Code.ACT_USER_REGISTER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void goLogin() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivityForResult(intent, Code.ACT_LOGIN);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d("test", "introActivity start!!!");
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_intro, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    class MyclickableSpan extends ClickableSpan {
        String clicked;
        public MyclickableSpan(String string){
            super();
            clicked = string;
        }

        public void onClick(View tv){
            Toast.makeText(context, "test", Toast.LENGTH_LONG).show();
        }

        public void updateDrawState(TextPaint ds){
            ds.setUnderlineText(false);
        }
    }

}
