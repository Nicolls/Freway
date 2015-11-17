package com.freway.ebike.facebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.internal.ShareFeedContent;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.listener.OpenActivityResultListener;
import com.freway.ebike.model.User;
import com.freway.ebike.utils.LogUtils;
/**必须先调用setActivity再调用别的方法*/
public class FacebookUtil  {
	private static final String TAG = FacebookUtil.class.getSimpleName();
	public static final int STATE_UN_LOGIN = 0;
	public static final int STATE_LOGIN_SUCCESS = 1;
	public static final int STATE_LOGIN_FAIL = 2;
	public static final int STATE_LOGIN_ED = 3;
	public static final int STATE_SHARE_SUCCESS = 4;
	public static final int STATE_SHARE_FAIL = 5;
	private static final String PUBLISH_PERMISSION = "publish_actions";
	private BaseActivity activity;
	private CallbackManager callbackManager = CallbackManager.Factory.create();
	private static FacebookUtil mFacebookUtil;
	private ShareDialog shareDialog;
	private Handler shareHandler;

	private FacebookUtil() {
	}

	public static FacebookUtil getInstance() {
		if (mFacebookUtil == null) {
			mFacebookUtil = new FacebookUtil();
		}
		return mFacebookUtil;
	}

	/** 重置activity */
	public FacebookUtil setActivity(BaseActivity activity) {
		this.activity = activity;
		shareDialog = new ShareDialog(activity);
		shareDialog.registerCallback(callbackManager, shareCallback);
		return this;
	}

	/** 第三方绑定 */
	public void bind(final Handler handler) {
		boolean enable = AccessToken.getCurrentAccessToken() != null;
		if (enable) {// 已绑定过
			Profile profile = Profile.getCurrentProfile();
			Message msg = Message.obtain();
			if(profile!=null){
				User user = new User();
				user.setUserid(profile.getId());
				user.setUsername(profile.getName());
				msg.what = STATE_LOGIN_ED;
				msg.obj = user;
				handler.sendMessage(msg);
			}
			return;
		}
		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				Bundle parameters = new Bundle();
				AccessToken token = loginResult.getAccessToken();
				parameters.putString("fields", "id,name,email,gender,birthday,cover,picture");
				new GraphRequest(token, "/" + token.getUserId(), parameters, HttpMethod.GET,
						new GraphRequest.Callback() {
							public void onCompleted(GraphResponse response) {
								System.out.println("----" + response.getRawResponse());
								try {
									User user = new User();
									if (response.getJSONObject().has("id")) {
										user.setUserid(response.getJSONObject().getString("id"));
									}
									if (response.getJSONObject().has("gender")) {
										user.setGender(response.getJSONObject().getString("gender"));
									}
									if (response.getJSONObject().has("email")) {
										user.setEmail(response.getJSONObject().getString("email"));
									}
									if (response.getJSONObject().has("name")) {
										user.setUsername(response.getJSONObject().getString("name"));
									}
									if (response.getJSONObject().has("birthday")) {
										user.setBirthday(response.getJSONObject().getString("birthday"));
									}
									if (response.getJSONObject().has("picture")
											&& response.getJSONObject().getJSONObject("picture").has("data")) {
										user.setPhoto(response.getJSONObject().getJSONObject("picture")
												.getJSONObject("data").getString("url"));
									}
									Message msg = Message.obtain();
									msg.what = STATE_LOGIN_SUCCESS;
									msg.obj = user;
									handler.sendMessage(msg);
								} catch (Exception e) {
									LogUtils.e(TAG, e.getMessage());
								}
							}
						}).executeAsync();
			}

			@Override
			public void onCancel() {
//				ToastUtils.toast(activity, "登录取消");
				Message msg = Message.obtain();
				msg.what = STATE_LOGIN_FAIL;
				msg.obj = "取消登录";
				handler.sendMessage(msg);
			}

			@Override
			public void onError(FacebookException exception) {
				Message msg = Message.obtain();
				msg.what = STATE_LOGIN_FAIL;
				msg.obj = "验证失败";
				handler.sendMessage(msg);
//				if (exception instanceof FacebookAuthorizationException) {
//					ToastUtils.toast(activity, "验证失败");
//				}
			}
		});
		LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("user_status", "email"));
	}
	/** 分享带链接的文字状态 */
	public void shareLinkStatus(String title, String content, String link, Handler handler) {
		this.shareHandler = handler;
		Profile profile = Profile.getCurrentProfile();
		ShareLinkContent.Builder builder=new ShareLinkContent.Builder();
		if(!TextUtils.isEmpty(title)){
			builder.setContentTitle(title);
		}
		if(!TextUtils.isEmpty(content)){
			builder.setContentDescription(content);
		}
		if(!TextUtils.isEmpty(link)){
			builder.setContentUrl(Uri.parse(link));
		}
        ShareLinkContent linkContent = builder.build();
        boolean canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);
        if (canPresentShareDialog) {
            shareDialog.show(linkContent);
        } else if (profile != null && hasPublishPermission()) {
            ShareApi.share(linkContent, shareCallback);
        } 
	}

	/** 分享图片 */
	public void sharePhoto(List<String> captions, List<Bitmap> bitmaps, Handler handler) {
		this.shareHandler = handler;
		if (bitmaps != null && bitmaps.size() > 0) {// 图片分享
			ArrayList<SharePhoto> photos = new ArrayList<SharePhoto>();
			int m = 0;
			for (Bitmap bitmap : bitmaps) {
				String caption = "";
				if (m < captions.size()) {
					caption = captions.get(m);
				}
				SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(bitmap).setCaption(caption).build();
				photos.add(sharePhoto);
				m++;
			}
			SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder().setPhotos(photos).build();

			boolean isEnable = ShareDialog.canShow(SharePhotoContent.class);
			if (isEnable) {
				shareDialog.show(sharePhotoContent);
			} else if (hasPublishPermission()) {
				ShareApi.share(sharePhotoContent, shareCallback);
			} else {
				LoginManager.getInstance().logInWithPublishPermissions(activity, Arrays.asList(PUBLISH_PERMISSION));
			}
		} else {

		}
	}

	/** 退出 */
	public void logout() {
		LoginManager.getInstance().logOut();
	}
	private boolean hasPublishPermission() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		return accessToken != null && accessToken.getPermissions().contains(PUBLISH_PERMISSION);
	}

	private FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
		@Override
		public void onCancel() {
			LogUtils.d(TAG, "Canceled");
		}

		@Override
		public void onError(FacebookException error) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
			if (shareHandler != null) {
				shareHandler.sendEmptyMessage(STATE_SHARE_FAIL);
			}
		}

		@Override
		public void onSuccess(Sharer.Result result) {
			Log.d("HelloFacebook", "Success!");
			if (shareHandler != null) {
				shareHandler.sendEmptyMessage(STATE_LOGIN_SUCCESS);
			}
		}
	};

	public void onOpenActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils.i(TAG, "activity result");
		if(callbackManager!=null){
			callbackManager.onActivityResult(requestCode, resultCode, data);
		}
	}
}
