package com.freway.ebike.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.freway.ebike.common.BaseService;
import com.freway.ebike.utils.EBikeDownLoadAsyncTask;
import com.freway.ebike.utils.LogUtils;

/** 应用更新Service */
public class UpdateAPPService extends BaseService {

	public static final String INTENT_DOWNLOAD_URL = "_INTENT_DOWNLOAD_URL";
	public static final String INTENT_DOWNLOAD_APP_MD5_CODE = "_INTENT_DOWNLOAD_APP_MD5_CODE";
	private static final String TAG = "UpdateAPPService";
	private static UpdateAppListener mUpdateAppListener;
	private Notification mNotification;
	private String appDownLoadUrl;
	private String appMd5Code = "";

	public static void setUpdateAppListener(UpdateAppListener updateAppListener) {
		mUpdateAppListener = updateAppListener;
	}

	/** 应用更新完成监听器 */
	public interface UpdateAppListener {
		void updateAppCompleted(String message);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null != intent && intent.hasExtra(INTENT_DOWNLOAD_URL)) {
			appDownLoadUrl = intent.getStringExtra(INTENT_DOWNLOAD_URL);
			appMd5Code = intent.getStringExtra(INTENT_DOWNLOAD_APP_MD5_CODE);
			LogUtils.i(TAG, "客户端更新包下载地址" + appDownLoadUrl + "－－客户端更新包md5验证码＝" + appMd5Code);
			if (mNotification == null) {
				startDownLoad();
			} else {
				LogUtils.i(TAG, "应用仍在更新中");
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/** 开始下载 */
	private void startDownLoad() {

		new EBikeDownLoadAsyncTask(this, mUpdateAppListener).execute(appDownLoadUrl);
	}
}
