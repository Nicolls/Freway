package com.freway.ebike.activity;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.model.RspLogin;
import com.freway.ebike.model.RspRegister;
import com.freway.ebike.model.RspUpdateUserInfo;
import com.freway.ebike.model.RspUserInfo;
import com.freway.ebike.model.User;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;

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
		messageTv=(TextView) findViewById(R.id.tv_message);
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		switch(id){
		case EBikeRequestService.ID_REGISTER:
			RspRegister rsp=(RspRegister) obj;
			if(rsp!=null){
				User user=new User();
				user.setUsername(username);
				user.setPassword(password);
				SPUtils.setUser(getApplicationContext(), user);
			}
			messageTv.setText(rsp.getText());
			ToastUtils.toast(getApplicationContext(), "注册成功");
			break;
		case EBikeRequestService.ID_LOGIN:
			RspLogin login=(RspLogin) obj;
			if(login!=null){
				SPUtils.setToken(getApplicationContext(), login.getData().getToken());
				User user=new User();
				user.setUsername(username);
				user.setPassword(password);
				SPUtils.setUser(getApplicationContext(), user);
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
		mEBikeRequestService.login("nnnnnn", "111111");
	}
	
	public void onThirdLogin(View view) {
		mEBikeRequestService.loginFaceBook(username, "123", "male", "1898-10-19", "http://www.saner5.com/map.jpg", "");
	}

	public void onInfo(View view) {
		mEBikeRequestService.userInfo(SPUtils.getToken(this));
	}
	
	public void onUpdateInfo(View view){
		mEBikeRequestService.updateUserInfo(SPUtils.getToken(this),null);
	}
	
	public void onUpLoadTravel(View view){
		mEBikeRequestService.upLoadTravel(SPUtils.getToken(this), "1", "", "", "1000", "3000", "100", "1000", "[10,20,30]", "[[\"x1\",\"y1\"],[\"x2\",\"y2\"],[\"x3\",\"y3\"]]", "80","65");
	}
	
	public void onUpLoadPhoto(View view){
		String photoPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/qq.png";
		File file=new File(photoPath);
		//s1ucl1KehmpwhZJYqJydnlhtW5NiZZhdamNma5dTZJhybWVtYZWGtg==
		System.out.println("token="+SPUtils.getToken(this));
		if(file.exists()){
			System.out.println("找到 图片了");
			mEBikeRequestService.updatePhoto(SPUtils.getToken(this), photoPath);
		}else{
			System.out.println("文件不存在");
		}
	}

}
