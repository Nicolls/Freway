package com.freway.ebike.activity;

import com.freway.ebike.R;
import com.freway.ebike.common.EBConstant;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.utils.ToastUtils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

public class WebViewActivity extends AppCompatActivity implements OnClickListener {

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
		webView.loadUrl(url);
		if(TextUtils.equals(EBConstant.HTML5_URL_RECORDS, url)){//分享
			iconButton.setVisibility(View.VISIBLE);
		}else{
			iconButton.setVisibility(View.GONE);
		}
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
			ToastUtils.toast(getApplicationContext(), getString(R.string.in_development));
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
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				titleTv.setText(view.getTitle());
			}
			
			
		});
	}


	@Override
	public void onBackPressed() {
		backChange();
	}

}
