package com.freway.ebike.activity;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.model.RspLogin;
import com.freway.ebike.model.RspRegister;
import com.freway.ebike.model.RspUpdateUserInfo;
import com.freway.ebike.model.RspUserInfo;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TestActivity extends BaseActivity {

	private TextView messageTv;
	private String email="cccc@gmail.com";
	private String username="cccccc";
	private String password="111111";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		super.initCommonView();
		messageTv=(TextView) findViewById(R.id.tv_message);
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		switch(id){
		case EBikeRequestService.ID_REGISTER:
			RspRegister rsp=(RspRegister) obj;
			if(rsp!=null){
				SPUtils.setUsername(getApplicationContext(),username);
				SPUtils.setPassword(getApplicationContext(),password);
			}
			messageTv.setText(rsp.getText());
			ToastUtils.toast(getApplicationContext(), "注册成功");
			break;
		case EBikeRequestService.ID_LOGIN:
			RspLogin login=(RspLogin) obj;
			if(login!=null){
				SPUtils.setToken(getApplicationContext(), login.getData().getToken());
				SPUtils.setUsername(getApplicationContext(),"123");
				SPUtils.setPassword(getApplicationContext(),"123");
			}
			messageTv.setText(login.getText());
			ToastUtils.toast(getApplicationContext(), "登录成功");
			break;
		case EBikeRequestService.ID_USERINFO:
			RspUserInfo info=(RspUserInfo) obj;
			if(info!=null){
				LogUtils.i(tag, "用户名："+info.getData().getUsername());
			}
			messageTv.setText(info.getText());
			break;
		case EBikeRequestService.ID_UPDATEUSERINFO:
			RspUpdateUserInfo updateInfo=(RspUpdateUserInfo) obj;
			ToastUtils.toast(getApplicationContext(), "更新成功");
			messageTv.setText(updateInfo.getText());
			break;
		case EBikeRequestService.ID_LOGINFACEBOOK:
			RspLogin facebookLogin=(RspLogin) obj;
			if(facebookLogin!=null){
				SPUtils.setToken(getApplicationContext(), facebookLogin.getData().getToken());
			}
			ToastUtils.toast(getApplicationContext(), "facebook登录成功");
			messageTv.setText(facebookLogin.getText());
			break;
			default:
				break;
		}
	}

	public void onRegister(View view) {
		mEBikeRequestService.register(email, username, password);
	}

	public void onLogin(View view) {
		mEBikeRequestService.login(username, password);
	}
	
	public void onThirdLogin(View view) {
		mEBikeRequestService.loginFaceBook(username, "123", "male", "1898-10-19", "http://www.saner5.com/map.jpg", "");
	}

	public void onInfo(View view) {
		mEBikeRequestService.userInfo(SPUtils.getToken(this));
	}
	
	public void onUpdateInfo(View view){
		mEBikeRequestService.updateUserInfo(SPUtils.getToken(this), SPUtils.getUsername(this),SPUtils.getPassword(this), "", "1898-12-09", "123@gmail.com");
	}

}
