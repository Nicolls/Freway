package com.freway.ebike.common;

import com.freway.ebike.utils.LogUtils;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/**
 * service基类
 * */
public class BaseIntentService extends IntentService{

	public BaseIntentService(String name) {
		super(name);
	}
	protected String tag = "BaseService";
	protected boolean isLog = true;
	@Override
	public void onCreate() {
		tag = this.getClass().getSimpleName();
		super.onCreate();
		LogUtils.i(tag, isLog, "onCreate");
	}
	@Override
	public IBinder onBind(Intent intent) {
		LogUtils.i(tag, isLog, "onBind");
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		LogUtils.i(tag, isLog, "onStart");
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.i(tag, isLog, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		LogUtils.i(tag, isLog, "onDestroy");
		super.onDestroy();
	}
	@Override
	public boolean onUnbind(Intent intent) {
		LogUtils.i(tag, isLog, "onUnbind");
		return super.onUnbind(intent);
	}
	@Override
	public void onRebind(Intent intent) {
		LogUtils.i(tag, isLog, "onRebind");
		super.onRebind(intent);
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		LogUtils.i(tag, isLog, "onHandleIntent");
	}

	
}
