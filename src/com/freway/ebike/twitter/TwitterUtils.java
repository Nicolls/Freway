package com.freway.ebike.twitter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.freway.ebike.common.EBConstant;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.ToastUtils;

public class TwitterUtils {

	private static final String TAG = TwitterUtils.class.getSimpleName();
	public static final String CALLBACK = "http://www.saner5.com"; // callback
																	// url
	public static final String OAUTH_VERIFIER = "oauth_verifier";
	public static final String AUTHENTICATION_URL = "authentication_url";

	private static final String CONSUMER_KEY = "6AV5A41Ck3oRnJpDo10pBERuc";
	private static final String CONSUMER_SECRET = "0lPHQbSA30iaz87RyXdljtOdXhoTX24sjJoCPxFTluQ92d7gdt";

	// https://dev.twitter.com/docs/error-codes-responses
	private static final int ERROR_CODE_STATUS_DUPLICATE = 187;

	public static final int REQ_TWITTER = 1008;

	private TwitterSp mPrefs;
	private Twitter mTwitter;
	private RequestToken mRequestToken;
	private Activity context;
	private Handler handler;

	public TwitterUtils(Activity context) {
		mPrefs = TwitterSp.get(context);
		this.context = context;
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(CONSUMER_KEY);
		builder.setOAuthConsumerSecret(CONSUMER_SECRET);
		Configuration conf = builder.build();
		mTwitter = new TwitterFactory(conf).getInstance();
	}

	public void bind(Handler handler) {
		this.handler = handler;
		String accessTokenKey = mPrefs.getTwitterAccessTokenKey();
		String accessTokenSecret = mPrefs.getTwitterAccessTokenSecret();
		if (!TextUtils.isEmpty(accessTokenKey) && TextUtils.isEmpty(accessTokenSecret)) {// 说明已经成功登录
			com.freway.ebike.model.User user = mPrefs.getUser();
			if (user != null) {
				Message msg = Message.obtain();
				msg.what = EBConstant.STATE_LOGIN_ED;
				msg.obj = user;
				handler.sendMessage(msg);
			}
		} else {
			oAuth();
		}
	}

	private void oAuth() {
		mTwitter.setOAuthAccessToken(null);
		new GetOAuthRequestTokenTask().execute();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && requestCode == REQ_TWITTER&&data!=null&&data.hasExtra(OAUTH_VERIFIER)) {
			String oauthVerifier = data.getStringExtra(OAUTH_VERIFIER);
			new GetOAuthAccessTokenTask().execute(oauthVerifier);
		}
	}

	public void postTwitter(String message) {
		String accessTokenKey = mPrefs.getTwitterAccessTokenKey();
		String accessTokenSecret = mPrefs.getTwitterAccessTokenSecret();
		if (accessTokenKey == null && accessTokenSecret == null) {
			oAuth();
		} else {
			mTwitter.setOAuthAccessToken(new AccessToken(accessTokenKey, accessTokenSecret));
			new UpdateTwitterStatus().execute(message);
		}
	}

	private class GetOAuthRequestTokenTask extends AsyncTask<Void, Void, RequestToken> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// ProgressHelper.getInstance().show(MainActivity.this,
			// "Geting oauth request token!");
		}

		@Override
		protected RequestToken doInBackground(Void... arg0) {
			try {
				return mTwitter.getOAuthRequestToken(CALLBACK);
			} catch (TwitterException e) {
				e.printStackTrace();
				Log.e("John", e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(RequestToken result) {
			super.onPostExecute(result);
			// ProgressHelper.getInstance().cancel(MainActivity.this);
			mRequestToken = result;
			if (mRequestToken != null) {
				Intent intent = new Intent(context, TwitterWebActivity.class);
				intent.putExtra(AUTHENTICATION_URL, mRequestToken.getAuthenticationURL());
				context.startActivityForResult(intent, REQ_TWITTER);
			}
		}
	}

	private class GetOAuthAccessTokenTask extends AsyncTask<String, Void, AccessToken> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// ProgressHelper.getInstance().show(MainActivity.this,
			// "Geting oauth access token!");
		}

		@Override
		protected AccessToken doInBackground(String... params) {
			try {
				return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
			} catch (TwitterException e) {
				e.printStackTrace();
				Log.e("John", e.getErrorMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(AccessToken accessToken) {
			super.onPostExecute(accessToken);
			if (accessToken != null) {
				mTwitter.setOAuthAccessToken(accessToken);
				mPrefs.setTwitterAccessToken(accessToken.getToken(), accessToken.getTokenSecret());

				try {
					new GetUserProfileTask().execute();
				} catch (IllegalStateException e) {
					LogUtils.e(TAG, e.getMessage());
					e.printStackTrace();
					if (handler != null)
						handler.sendEmptyMessage(EBConstant.STATE_LOGIN_FAIL);
				}
			}
		}
	}

	private class GetUserProfileTask extends AsyncTask<Long, Void, com.freway.ebike.model.User> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// ProgressHelper.getInstance().show(MainActivity.this,
			// "Post tweet!");
		}

		@Override
		protected com.freway.ebike.model.User doInBackground(Long... params) {
			try {
				User user = mTwitter.showUser(mTwitter.getId());
				com.freway.ebike.model.User eUser = new com.freway.ebike.model.User();
				eUser.setUserid(user.getId() + "");
				eUser.setUsername(user.getName());
				eUser.setPhoto(user.getProfileImageURL());
				return eUser;
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(com.freway.ebike.model.User result) {
			super.onPostExecute(result);
			if (handler != null) {
				Message msg = Message.obtain();
				msg.what = EBConstant.STATE_LOGIN_SUCCESS;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}
	}

	private class UpdateTwitterStatus extends AsyncTask<String, Void, Status> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected twitter4j.Status doInBackground(String... params) {
			try {
				return mTwitter.updateStatus(params[0]);
			} catch (TwitterException e) {
				e.printStackTrace();
				if (e.getErrorCode() == ERROR_CODE_STATUS_DUPLICATE) {
					ToastUtils.toast(context, "Twitter do not let us post same tweet during a short time");
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(twitter4j.Status result) {
			super.onPostExecute(result);
			if (result != null) {
				LogUtils.i(TAG, "Result: " + result.toString());
				ToastUtils.toast(context, "Post Success!");
			}
		}
	}

}
