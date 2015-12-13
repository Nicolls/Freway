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
import com.freway.ebike.facebook.FacebookUtil;
import com.freway.ebike.model.RspLogin;
import com.freway.ebike.model.RspVersion;
import com.freway.ebike.model.User;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.service.UpdateAPPService;
import com.freway.ebike.twitter.TwitterUtils;
import com.freway.ebike.utils.AlertUtil;
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
	private TwitterUtils mTwitterUtils;
	private User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		initView();
		initFontStyle();
		showLoading(false);
		mEBikeRequestService.version("Android", CommonUtil.getAppVersion(this));//新版本检查
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
		mEBikeRequestService.login(email, password);
	}

	public void onSignup() {
		openActivity(SignUpActivity.class, null, false);
	}

	public void onSigninFacebook(View view) {
		showLoading(true);
		FacebookUtil.getInstance().setActivity(this).bind(new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
//				hideLoading();
				LogUtils.i(tag, "Facebook返回");
				if(msg.what!=EBConstant.STATE_LOGIN_FAIL){//没有错
					LogUtils.i(tag, "Facebook登录成功");
					User user=(User) msg.obj;
					LogUtils.i(tag, "Facebook用户名是："+user.getUsername()+"--photo="+user.getPhoto());
					showLoading(true);
					mEtEmail.setText(user.getEmail());
					SPUtils.setUser(getApplicationContext(), user);
					mEBikeRequestService.loginFaceBook(user.getUserid(), user.getUsername(), user.getGender(),  user.getPhoto(), user.getEmail());
				}else{
					ToastUtils.toast(getApplicationContext(), getString(R.string.login_failt));
				}
				
			}
			
		});
	}

	public void onSignInTwitter(View view) {
		showLoading(true);
		if(mTwitterUtils==null){
			mTwitterUtils=new TwitterUtils(this);
			mTwitterUtils.bind(new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
//					hideLoading();
					LogUtils.i(tag, "Twtitter返回");
					if(msg.what!=EBConstant.STATE_LOGIN_FAIL){//没有错
						LogUtils.i(tag, "Twtitter登录成功");
						User user=(User) msg.obj;
						LogUtils.i(tag, "twitter用户名是："+user.getUsername()+"--photo="+user.getPhoto());
						showLoading(true);
						mEtEmail.setText(user.getEmail());
						SPUtils.setUser(getApplicationContext(), user);
						mEBikeRequestService.loginFaceBook(user.getUserid(), user.getUsername(), user.getGender(),user.getPhoto(), user.getEmail());
					}else{
						ToastUtils.toast(getApplicationContext(), getString(R.string.login_failt));
					}
					
				}
			});
		}
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
		}else if(id==EBikeRequestService.ID_VERSION){
			RspVersion version = (RspVersion) obj;
			chargeUpdate(version);
		}
	}

	/** 判断更新 */
	private void chargeUpdate(RspVersion version) {
		if (version != null) {
			String newest = version.getData().getNewest();
			final String url = version.getData().getUrl();
			// final String url =
			// "http://www.saner5.com/index.aspx?appId=1&appDownLoadCount=55&appDownloadUrl=upload/app/2014_07_17_17_44_48ear.apk";
			int m = Integer.parseInt(version.getData().getForce_update());
			boolean isForceUpdate = (m == 0 ? false : true);
			if(isForceUpdate){//强制
				AlertUtil.getInstance(this).alertChoice(getString(R.string.app_update_force_tip), getString(R.string.yes), getString(R.string.no),
						new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								AlertUtil.getInstance(SignInActivity.this).dismiss();
								updateApk(url,true);
							}
						},
						new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								AlertUtil.getInstance(SignInActivity.this).dismiss();
								finish();
							}
						},false);
			}else if (!TextUtils.isEmpty(newest)) {
					AlertUtil.getInstance(this).alertConfirm(getString(R.string.app_update_tip),
							getString(R.string.confirm), new OnClickListener() {

								@Override
								public void onClick(View v) {
									AlertUtil.getInstance(SignInActivity.this).dismiss();
									updateApk(url,false);
									initData();
								}
							});
			} 
		} else{
			initData();
		}
	}
	/** 版本更新 */
	private void updateApk(String downloadUrl,boolean isfinish) {
		// final String downloadUrl =
		// "http://www.saner5.com/index.aspx?appId=1&appDownLoadCount=55&appDownloadUrl=upload/app/2014_07_17_17_44_48ear.apk";
		ToastUtils.toast(SignInActivity.this, getString(R.string.start_download));
		Intent intent = new Intent(UpdateAPPService.class.getName());
		intent.putExtra(UpdateAPPService.INTENT_DOWNLOAD_URL, downloadUrl);
		SignInActivity.this.startService(intent);
		if(isfinish){
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i(tag, "Activity打开回来了");
		if(requestCode==TwitterUtils.REQ_TWITTER){
			mTwitterUtils.onActivityResult(requestCode, resultCode, data);
		}else{
			FacebookUtil.getInstance().onOpenActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		hideLoading();
	}
	/**请求出错*/
	protected void requestError(int id){
		hideLoading();
		initData();
	}
	
}
