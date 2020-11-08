package com.freway.ebike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.common.EBConstant;
import com.freway.ebike.model.RspLogin;
import com.freway.ebike.model.User;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;

public class SignInActivity extends BaseActivity {

	private TextView mTvSignup;
	private TextView mTvSignin;
	private EditText mEtEmail;
	private EditText mEtPassword;
	private int signinType;
	private User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		initView();
		initFontStyle();
		showLoading(false);
		initData();
	}
	
	private void initView(){
		mTvSignin = (TextView) findViewById(R.id.sign_tv_signin);
		mTvSignup = (TextView) findViewById(R.id.sign_tv_signup);
		mEtEmail=(EditText) findViewById(R.id.sign_et_email);
		mEtPassword=(EditText) findViewById(R.id.sign_et_password);
		mTvSignin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSignin();
			}
		});
		mTvSignup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSignup();
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
		if(!TextUtils.isEmpty(SPUtils.getToken(this))){//token不为空、直接进入应用
			openActivity(HomeActivity.class, null, true);
		}else{
			user=SPUtils.getUser(this);
			user.setEmail("851778509@qq.com");
			user.setPassword("111111");
			SPUtils.setUser(this, user);
			mEtEmail.setText(user.getEmail());
			mEtPassword.setText(user.getPassword());
		}
	}

	public void onSignin() {
		String email=mEtEmail.getText().toString();
		String password=mEtPassword.getText().toString();
		if(TextUtils.isEmpty(email)){
			ToastUtils.toast(this, getString(R.string.email_can_not_be_null));
			return;
		}
		if(TextUtils.isEmpty(password)){
			ToastUtils.toast(this, getString(R.string.password_can_not_be_null));
			return;
		}
		if(!CommonUtil.isEmail(email)){
			ToastUtils.toast(this, getString(R.string.email_incorrect));
			return;
		}
		showLoading(true);
/*
		mEBikeRequestService.login(email, password);
*/
		test();
	}

	private void test(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				final RspLogin login = new RspLogin();
				RspLogin.Result result = login.new Result();
				result.setToken("my_test_token");
				login.setData(result);
				dateUpdate(101,login);
			}
		}).start();
	}

	public void onSignup() {
		openActivity(SignUpActivity.class, null, false);
	}


	@Override
	public void dateUpdate(int id, Object obj) {//不管是哪种登录回来的都是一样的
		hideLoading();
		if(obj instanceof RspLogin){
			RspLogin rspLogin=(RspLogin) obj;
			SPUtils.setToken(getApplicationContext(), rspLogin.getData().getToken());
			User user=SPUtils.getUser(this);
			String email=mEtEmail.getText().toString();
			String password=mEtPassword.getText().toString();
			user.setEmail(email);
			user.setPassword(password);
			SPUtils.setUser(this, user);
			openActivity(HomeActivity.class, null, true);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i(tag, "Activity打开回来了");
	}

	@Override
	protected void onResume() {
		super.onResume();
		hideLoading();
	}
	/**请求出错*/
	protected void requestError(int id){
		if (id == EBikeRequestService.ID_REQUEST_ERROR) {
			ToastUtils.toast(this, getString(R.string.request_server_error));
		}else{
			ToastUtils.toast(this, getString(R.string.login_fail));
		}
		hideLoading();
		initData();
	}
	
}
