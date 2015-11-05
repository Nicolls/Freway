package com.freway.ebike.activity;

import android.os.Bundle;
import android.view.View;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.utils.SPUtils;

public class RegisterActivity extends BaseActivity {

	private int loginType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginType=SPUtils.getLoginType(this);
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		
		
	}
	/**登录*/
	public void onLogin(View view){
		
	}
	
	/**注册*/
	public void onRegister(View view){
		
	}

}
