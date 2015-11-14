package com.freway.ebike.activity;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.facebook.FacebookUtil;
import com.freway.ebike.model.RspLogin;
import com.freway.ebike.model.User;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class SignInActivity extends BaseActivity {

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
				openActivity(HomeActivity.class, null, false);
//				onSignin();
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
		mEBikeRequestService.login(email, password);
	}

	public void onSignup() {
		openActivity(SignUpActivity.class, null, true);
	}

	public void onSigninFacebook(View view) {
		showLoading(true);
		FacebookUtil.getInstance().setActivity(this).bind(new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				hideLoading();
				if(msg.what!=FacebookUtil.STATE_LOGIN_FAIL){//没有错
					User user=(User) msg.obj;
					mEBikeRequestService.loginFaceBook(user.getUserid(), user.getUsername(), user.getGender(), user.getBirthday(), user.getPhoto(), user.getEmail());
				}else{
					ToastUtils.toast(getApplicationContext(), getString(R.string.login_failt));
				}
				
			}
			
		});
	}

	public void onSignInTwitter(View view) {

	}

	@Override
	public void dateUpdate(int id, Object obj) {
		/*switch(id){
		case EBikeRequestService.ID_LOGIN:
			openActivity(HomeActivity.class, null, true);
			break;
		case EBikeRequestService.ID_LOGINFACEBOOK:
			openActivity(HomeActivity.class, null, true);
			break;
			default:
				break;
		}*/
		if(obj instanceof RspLogin){
			RspLogin rspLogin=(RspLogin) obj;
			SPUtils.setToken(getApplicationContext(), rspLogin.getData().getToken());
		}
	}

}