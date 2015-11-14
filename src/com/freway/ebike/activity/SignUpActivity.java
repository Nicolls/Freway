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

public class SignUpActivity extends AppCompatActivity {

	private TextView mTvSignup;
	private EditText mEtEmail;
	private EditText mEtPassword;
	private EditText mEtUsername;
	private EditText mEtConfirmPassword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		initView();
		initFontStyle();
	}
	
	private void initView(){
		mEtEmail=(EditText) findViewById(R.id.sign_et_email);
		mEtPassword=(EditText) findViewById(R.id.sign_et_password);
		mEtUsername=(EditText) findViewById(R.id.sign_et_username);
		mEtConfirmPassword=(EditText) findViewById(R.id.sign_et_confirm_password);
		mTvSignup=(TextView) findViewById(R.id.sign_tv_signup);
		mTvSignup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SignUpActivity.this,HomeActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	/**设置字体风格*/
	private void initFontStyle(){
		//已有的
		mEtConfirmPassword.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mEtUsername.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mTvSignup.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mEtEmail.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mEtPassword.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		
		//未有的
		((TextView)findViewById(R.id.sign_title)).setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
	}

}
