package com.freway.ebike.activity;

import io.fabric.sdk.android.Fabric;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.model.RspVersion;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.service.UpdateAPPService;
import com.freway.ebike.utils.AlertUtil;
import com.freway.ebike.utils.AlertUtil.AlertClick;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;

public class WelcomeActivity extends BaseActivity {

//	private ImageView ib;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_welcome);
//		ib=(ImageView) findViewById(R.id.iv_welcome);
//		Animation anim=AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in_scale);
//		ib.startAnimation(anim);
		mEBikeRequestService.version("Android", CommonUtil.getAppVersion(this));//新版本检查
	}


	private void initData(){
		if(!TextUtils.isEmpty(SPUtils.getToken(this))){//token不为空、直接进入应用
			openActivity(HomeActivity.class, null, true);
		}else{
			openActivity(SignInActivity.class, null, true);
		}
	}
	
	@Override
	public void dateUpdate(int id, Object obj) {
		hideLoading();
		if(id==EBikeRequestService.ID_VERSION){
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
				AlertUtil.getInstance().alertChoice(this,getString(R.string.app_update_force_tip), getString(R.string.yes), getString(R.string.no),
						new AlertClick() {
							
							@Override
							public void onClick(AlertDialog dialog,View v) {
								dialog.dismiss();
								updateApk(url,true);
							}
						},
						new AlertClick() {
							
							@Override
							public void onClick(AlertDialog dialog,View v) {
								dialog.dismiss();
								finish();
							}
						},false);
			}else if (!TextUtils.isEmpty(newest)) {
					AlertUtil.getInstance().alertConfirm(this,getString(R.string.app_update_tip),
							getString(R.string.confirm), new AlertClick() {

								@Override
								public void onClick(AlertDialog dialog,View v) {
									dialog.dismiss();
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
		ToastUtils.toast(WelcomeActivity.this, getString(R.string.start_download));
		Intent intent = new Intent(UpdateAPPService.class.getName());
		intent.putExtra(UpdateAPPService.INTENT_DOWNLOAD_URL, downloadUrl);
		WelcomeActivity.this.startService(intent);
		if(isfinish){
			finish();
		}
	}
	

	/**请求出错*/
	protected void requestError(int id){
		hideLoading();
		initData();
	}
	
}
