package com.freway.ebike.twitter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.utils.LogUtils;

public class TwitterWebActivity extends Activity {

	private WebView mWebview;
	private TextView cancelTv;
	private ProgressBar mProgressbar;
	private static final String TAG=TwitterWebActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_web);
		initView();
	}

	private void initView() {
		mProgressbar=(ProgressBar) findViewById(R.id.web_progress);
		mWebview = (WebView) findViewById(R.id.webview);
		cancelTv = (TextView) findViewById(R.id.web_cancel_btn);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.setWebViewClient(new OAuthWebViewClient());
		mWebview.loadUrl(getIntent().getExtras().getString(TwitterUtils.AUTHENTICATION_URL));
		cancelTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	private class OAuthWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mProgressbar.setVisibility(View.GONE);
			LogUtils.i(TAG, "url="+url);
			if (url != null && url.startsWith(TwitterUtils.CALLBACK)) {
				String oauthVerifer = Uri.parse(url).getQueryParameter(TwitterUtils.OAUTH_VERIFIER);
				LogUtils.i(TAG, "OAauthVerifer= " + oauthVerifer);
				Intent intent = new Intent();
				intent.putExtra(TwitterUtils.OAUTH_VERIFIER, oauthVerifer);
				setResult(RESULT_OK, intent);
				LogUtils.i(TAG,  "Before finish");
				finish();
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mProgressbar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		if(mWebview.canGoBack()){
			mWebview.goBack();
		}else{
			super.onBackPressed();
		}
	}

}
