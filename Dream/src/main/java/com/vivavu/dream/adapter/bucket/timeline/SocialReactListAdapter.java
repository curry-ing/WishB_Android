package com.vivavu.dream.adapter.bucket.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.vivavu.dream.R;
import com.vivavu.dream.model.SocialReact;
import com.vivavu.dream.util.DateUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yuja on 2014-08-19.
 */
public class SocialReactListAdapter extends BaseAdapter {
	protected Context context;
	protected LayoutInflater layoutInflater;

	protected List<SocialReact> socialReactList;

	public SocialReactListAdapter(Context context) {
		this.context = context;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (socialReactList == null) {
			return 0;
		}

		return socialReactList.size();
	}

	@Override
	public SocialReact getItem(int position) {
		if (getCount() <= position) {
			return null;
		}
		return socialReactList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.activity_social_react_view_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		SocialReact socialReact = getItem(position);
		if( socialReact != null) {
			if(socialReact.getSocialType() == SocialReact.SocialType.FACEBOOK) {
				ImageLoader.getInstance().displayImage("drawable://" + R.drawable.ic_facebook_active, viewHolder.mImgSocialIcon);
			}
			viewHolder.mTxtSocialName.setText(socialReact.getName());
			viewHolder.mTxtSocialReply.setText(socialReact.getMessage());
			if(socialReact.getAttachmentUrl() != null && socialReact.getAttachmentUrl().length() > 0){
				ImageLoader.getInstance().displayImage(socialReact.getAttachmentUrl(), viewHolder.mImgSocialReactAttachment, new SimpleImageLoadingListener(){
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						if(view instanceof ImageView){
							((ImageView) view).setImageBitmap(loadedImage);
						} else {

						}
						view.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						view.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						view.setVisibility(View.GONE);
					}
				});
			}
			viewHolder.mTxtSocialReplyCreatedTime.setText(DateUtils.getDateString(socialReact.getCreatedTime(), "yyyy-MM-dd HH:mm:ss"));
		}

		return convertView;
	}

	public List<SocialReact> getSocialReactList() {
		return socialReactList;
	}

	public void setSocialReactList(List<SocialReact> socialReactList) {
		this.socialReactList = socialReactList;
	}

	/**
	 * This class contains all butterknife-injected Views & Layouts from layout file 'activity_social_react_view_item.xml'
	 * for easy to all layout elements.
	 *
	 * @author ButterKnifeZelezny, plugin for Android Studio by Inmite Developers (http://inmite.github.io)
	 */
	static class ViewHolder {
		@InjectView(R.id.img_social_icon)
		ImageView mImgSocialIcon;
		@InjectView(R.id.img_social_profile)
		ImageView mImgSocialProfile;
		@InjectView(R.id.txt_social_name)
		TextView mTxtSocialName;
		@InjectView(R.id.txt_social_reply)
		TextView mTxtSocialReply;
		@InjectView(R.id.txt_social_reply_created_time)
		TextView mTxtSocialReplyCreatedTime;
		@InjectView(R.id.img_social_react_attachment)
		ImageView mImgSocialReactAttachment;

		ViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}
}
