package com.vivavu.dream.adapter.bucket.timeline;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vivavu.dream.R;
import com.vivavu.dream.common.BaseActionBarActivity;
import com.vivavu.dream.common.DreamApp;
import com.vivavu.dream.model.bucket.timeline.Post;
import com.vivavu.dream.model.bucket.timeline.TimelineMetaInfo;
import com.vivavu.dream.util.DateUtils;
import com.vivavu.dream.view.LinkEllipseTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-04-01.
 */
public class TimelineListAdapter extends BaseAdapter {
	public static final String EXTRA_KEY_FACEBOOK_FEED_ID = "facebook_feed_id";
	public static final String EXTRA_KEY_LIKES_COUNT = "likesCount";
	public static final String EXTRA_KEY_COMMENTS_COUNT = "commentsCount";
	protected Context context;
    protected LayoutInflater layoutInflater;
    protected List<Post> postList;
    protected TimelineMetaInfo timelineMetaInfo;

	public TimelineListAdapter(Activity context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        /*if (timelineMetaInfo == null) {
            return 0;
        }
        return timelineMetaInfo.getCount();*/
        if (postList == null) {
            return 0;
        }
        return postList.size();
    }

    @Override
    public Object getItem(int position) {
        return postList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return postList.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ButterknifeViewHolder viewHolder = null;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.timeline_item, parent, false);
            viewHolder = new ButterknifeViewHolder(convertView);
        } else {
            viewHolder = (ButterknifeViewHolder) convertView.getTag();
        }
        final Post post = (Post) getItem(position);
        viewHolder.mTxtPostText.setText(post.getText());
        if(post.getImgUrl() == null){
            viewHolder.mTxtPostText.setMaxLines(5);
        } else {
            viewHolder.mTxtPostText.setMaxLines(3);
        }
        viewHolder.mTxtPostDate.setText(DateUtils.getDateString(post.getContentDt(), "yyyy.MM.dd HH:mm"));
        viewHolder.mTxtPostText.setTypeface(BaseActionBarActivity.getNanumBarunGothicFont());

        ImageLoader.getInstance().displayImage(post.getImgUrl(), viewHolder.mIvTimelineImage, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                // 이미지가 없을 경우에는 imageview 자체를 안보여줌
                if(loadedImage != null) {
                    view.setVisibility(View.VISIBLE);
                }else {
                    view.setVisibility(View.GONE);
                }
            }
        });

	    if(post.getFbFeedId() != null && post.getFbFeedId().length() > 0) {
		    viewHolder.mFacebookLikesComments.setVisibility(View.VISIBLE);
		    final ButterknifeViewHolder finalViewHolder = viewHolder;
		    /*viewHolder.mFacebookLikesComments.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View v) {
				    Intent i = new Intent();
				    i.setClass(context, SocialReactViewActivity.class);
				    i.putExtra(EXTRA_KEY_FACEBOOK_FEED_ID, post.getFbFeedId());
				    i.putExtra(EXTRA_KEY_LIKES_COUNT, likesCount);
				    i.putExtra(EXTRA_KEY_COMMENTS_COUNT, commentsCount);

				    //i.setData(Uri.parse(String.format("https://www.facebook.com/%s", post.getFbFeedId())));
				    context.startActivity(i);
			    }
		    });*/

		    finalViewHolder.mFacebookLikesComments.setText(String.format("로딩중..."));
		    convertView.post(new Runnable() {
			    @Override
			    public void run() {
				    final String s = post.getFbFeedId().split("_")[1];
			        List<String> readPermissions = new ArrayList<String>();
				    readPermissions.add("publish_actions");
				    AccessToken fromExistingAccessToken = AccessToken.createFromExistingAccessToken(DreamApp.getInstance().getFbToken(), null, null, AccessTokenSource.FACEBOOK_APPLICATION_NATIVE, readPermissions);
				    Session.openActiveSessionWithAccessToken(context, fromExistingAccessToken, new Session.StatusCallback() {
					    @Override
					    public void call(Session session, SessionState state, Exception exception) {
						    if (state.isOpened()) {
							    Bundle bundle = new Bundle();
							    bundle.putString("fields", "comments{attachment,message,from,created_time},likes{name,pic}");
							    new Request(Session.getActiveSession(), String.format("/%s", s), bundle, HttpMethod.GET, new Request.Callback() {
								    @Override
								    public void onCompleted(Response response) {
									    int likesCount = 0;
									    int commentsCount = 0;
									    if(response != null && response.getGraphObject() != null) {
										    GraphObject graphObject = response.getGraphObject();
										    JSONObject jsonObject = graphObject.getInnerJSONObject();
										    try {
											    if(!jsonObject.isNull("likes")) {
												    JSONObject likes = jsonObject.getJSONObject("likes");
												    if (!likes.isNull("data")) {
													    JSONArray likesData = likes.getJSONArray("data");
													    likesCount = likesData.length();
												    }
											    }

											    if(!jsonObject.isNull("comments")) {
												    JSONObject comments = jsonObject.getJSONObject("comments");
												    if (!comments.isNull("data")) {
													    JSONArray commentsData = comments.getJSONArray("data");
													    commentsCount = commentsData.length();
												    }
											    }
												post.setLikesCount(likesCount);
											    post.setCommentsCount(commentsCount);
											    finalViewHolder.mFacebookLikesComments.setText(String.format("좋아요 %d개 답글 %d개", likesCount, commentsCount));

										    } catch (JSONException e) {
											    Log.e(this.getClass().getName(), e.toString());
											    finalViewHolder.mFacebookLikesComments.setVisibility(View.GONE);
											    //finalViewHolder.mFacebookLikesComments.setText(String.format("좋아요 0개 답글 0개"));
										    }
									    }
								    }
							    }).executeAsync();
						    }
					    }
				    });
			    }
		    });
	    } else {
		    viewHolder.mFacebookLikesComments.setVisibility(View.GONE);
	    }

        convertView.setTag(viewHolder);
        return convertView;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public TimelineMetaInfo getTimelineMetaInfo() {
        return timelineMetaInfo;
    }

    public void setTimelineMetaInfo(TimelineMetaInfo timelineMetaInfo) {
        this.timelineMetaInfo = timelineMetaInfo;
    }


/**
 * This class contains all butterknife-injected Views & Layouts from layout file 'null'
 * for easy to all layout elements.
 *
 * @author Android Butter Zelezny, plugin for IntelliJ IDEA/Android Studio by Inmite (www.inmite.eu)
 */
    static class ButterknifeViewHolder {
        @InjectView(R.id.txt_post_date)
        TextView mTxtPostDate;
        @InjectView(R.id.txt_post_text)
        LinkEllipseTextView mTxtPostText;
        @InjectView(R.id.iv_timeline_image)
        ImageView mIvTimelineImage;
		@InjectView(R.id.facebook_likes_comments)
		TextView mFacebookLikesComments;

        ButterknifeViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


}
