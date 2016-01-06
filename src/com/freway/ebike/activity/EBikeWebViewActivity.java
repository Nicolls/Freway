package com.freway.ebike.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.freway.ebike.common.EBConstant;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.utils.LogUtils;

public class EBikeWebViewActivity extends BaseActivity implements OnClickListener {

	private WebView webView;
	private ImageView iconButton;
	private ImageView leftButton;
	private TextView rightButton;
	private TextView titleTv;
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		String url=getIntent().getStringExtra("url");
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
		iconButton.setVisibility(View.GONE);
		webView=(WebView) findViewById(R.id.webview);
		initView(webView,url);
		LogUtils.i("WebViewActivity", "加载的地址是:"+url);
		webView.loadUrl(url);
	}
	
	private void backChange(){
		finish();
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
	private void initView(WebView webView,String url) {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.getSettings().setLoadsImagesAutomatically(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setBuiltInZoomControls(false);
		if(!TextUtils.isEmpty(url)&&(url.indexOf(EBConstant.HTML5_URL_RECORDS)!=-1)){
			webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		}else{
			webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
		webView.addJavascriptInterface(new JsInterface(), "freway");
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Intent intent=null;
				if(url.startsWith("http://"+EBConstant.DEFAULT_HOST)){
					intent = new Intent(EBikeWebViewActivity.this, EBikeWebViewActivity.class);
				}else{
					intent = new Intent(EBikeWebViewActivity.this, OtherWebViewActivity.class);
				}
				intent.putExtra("url", url);
				startActivity(intent);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				String title=view.getTitle();
				if(!TextUtils.isEmpty(title)){
					title=title.trim();
				}else{
					title="";
				}
				titleTv.setText(title);
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
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					iconButton.setVisibility(View.VISIBLE);
				}
			});
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
