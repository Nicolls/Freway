package com.freway.ebike.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.utils.LogUtils;

public class WebViewActivity extends BaseActivity implements OnClickListener {

	private WebView webView;
	private ImageView iconButton;
	private ImageView leftButton;
	private TextView rightButton;
	private TextView titleTv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		iconButton=(ImageView) findViewById(R.id.top_bar_right_icon);
		leftButton=(ImageView) findViewById(R.id.top_bar_left);
		rightButton=(TextView) findViewById(R.id.top_bar_right);
		titleTv=(TextView) findViewById(R.id.top_bar_title);
		titleTv.setText(getString(R.string.loading));
		titleTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		rightButton.setVisibility(View.GONE);
		iconButton.setImageResource(R.drawable.records_icon_share);
		iconButton.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		webView=(WebView) findViewById(R.id.webview);
		initView(webView);
		String url=getIntent().getStringExtra("url");
		LogUtils.i("WebViewActivity", "加载的地址是:"+url);
		webView.loadUrl(url);
		iconButton.setVisibility(View.GONE);
	}
	
	private void backChange(){
		if(webView.canGoBack()){
			webView.goBack();
		}else{
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.top_bar_right_icon:
			v.setSelected(!v.isSelected());
			if(v.isSelected()){
				webView.loadUrl("javascript:showShare()");
			}else{
				webView.loadUrl("javascript:hideShare()");
			}
//			ToastUtils.toast(getApplicationContext(), getString(R.string.in_development));
			break;
		case R.id.top_bar_left:
			backChange();
			break;
			default:
				break;
		}
	}
	
	
	
	@SuppressLint({ "NewApi", "SetJavaScriptEnabled", "JavascriptInterface" })
	private void initView(WebView webView) {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.addJavascriptInterface(new JsInterface(), "freway");
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				titleTv.setText(view.getTitle());
				hideLoading();
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				showLoading(true);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				hideLoading();
			}
			
			
		});
	}
	/**
	 * js接口回调类
	 * */
	class JsInterface {

		@JavascriptInterface
		public void share() {
			LogUtils.i(tag, "js触发的方法");
			//当是分享页面时，就会调用到这个方法，UI要显示分享的按钮
			iconButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		backChange();
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		
		
	}

}
