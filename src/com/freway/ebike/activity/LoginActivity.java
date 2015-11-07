package com.freway.ebike.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.facebook.CallbackManager;
import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.facebook.FacebookUtil;
import com.freway.ebike.model.User;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;

public class LoginActivity extends BaseActivity {

	private int loginType;
	private CallbackManager callbackManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initData();
	}

	private void initData() {
		loginType = SPUtils.getLoginType(this);
		callbackManager = CallbackManager.Factory.create();
	}

	@Override
	public void dateUpdate(int id, Object obj) {

	}

	/** 登录 */
	public void onLogin(View view) {
		
	}

	/** 注册 */
	public void onRegister(View view) {
		openActivity(RegisterActivity.class, null, true);
	}

	/**fb登录*/
	public void onLoginFacebooke(View view){
		FacebookUtil.getInstance().setActivity(this).bind(new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				User user=(User) msg.obj;
				
			}
			
		});
	}
	
	
}
