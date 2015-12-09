package com.freway.ebike.activity;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.model.RspRegister;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends BaseActivity {

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
				signUp();
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
	
	private void signUp(){
		String email=mEtEmail.getText().toString();
		String password=mEtPassword.getText().toString();
		String userName=mEtUsername.getText().toString();
		String confirmPassword=mEtConfirmPassword.getText().toString();
		if(TextUtils.isEmpty(email)){
			ToastUtils.toast(this, getString(R.string.email_can_not_be_null));
			return;
		}
		if(TextUtils.isEmpty(userName)){
			ToastUtils.toast(this, getString(R.string.username_can_not_be_null));
			return;
		}
		if(userName.length()<4){
			ToastUtils.toast(this, getString(R.string.username_at_least_4_characters));
			return;
		}
		if(TextUtils.isEmpty(password)){
			ToastUtils.toast(this, getString(R.string.password_can_not_be_null));
			return;
		}
		if(password.length()<6){
			ToastUtils.toast(this, getString(R.string.password_at_least_6_characters));
			return;
		}
		if(TextUtils.isEmpty(confirmPassword)){
			ToastUtils.toast(this, getString(R.string.confirm_password_can_not_be_null));
			return;
		}
		if(!CommonUtil.isEmail(email)){
			ToastUtils.toast(this, getString(R.string.email_incorrect));
			return;
		}
		if(!TextUtils.equals(password, confirmPassword)){
			ToastUtils.toast(this, getString(R.string.confirm_password_not_match));
			return;
		}
		mEBikeRequestService.register(email, userName, password);
		
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		switch(id){
		case EBikeRequestService.ID_REGISTER:
//			RspRegister rsp=(RspRegister) obj;
//			SPUtils.setToken(this,rsp.getData().getToken());
//			openActivity(HomeActivity.class, null, true);
			finish();
			break;
			default:
				break;
		}
		
	}

}
