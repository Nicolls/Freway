package com.freway.ebike.activity;

import com.freway.ebike.R;
import com.freway.ebike.utils.FontUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class SignInActivity extends AppCompatActivity {

	private TextView mTvSignup;
	private TextView mTvSignin;
	private EditText mEtEmail;
	private EditText mEtPassword;
	private int signinType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		initView();
		initFontStyle();
	}
	
	private void initView(){
		mTvSignin = (TextView) findViewById(R.id.sign_tv_signin);
		mTvSignup = (TextView) findViewById(R.id.sign_tv_signup);
		mEtEmail=(EditText) findViewById(R.id.sign_et_email);
		mEtPassword=(EditText) findViewById(R.id.sign_et_password);
		mTvSignin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSignin(null);
			}
		});
		mTvSignup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSignup(null);
			}
		});
	}
	/**设置字体风格*/
	private void initFontStyle(){
		//已有的
		mTvSignin.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mTvSignup.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mEtEmail.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mEtPassword.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		
		//未有的
		((TextView)findViewById(R.id.sign_title)).setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		((TextView)findViewById(R.id.sign_tip)).setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
	}
	/**初始化数据*/
	private void initData(){
		
	}

	public void onSignin(View view) {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}

	public void onSignup(View view) {
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
	}

	public void onSigninFacebook(View view) {

	}

	public void onSignInTwitter(View view) {

	}

}
