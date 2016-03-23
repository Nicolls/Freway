package com.freway.ebike.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.text.TextUtils;
import android.view.View;

import com.facebook.FacebookSdk;
import com.freway.ebike.R;
import com.freway.ebike.bluetooth.BLEScanConnectActivity;
import com.freway.ebike.bluetooth.BlueToothConstants;
import com.freway.ebike.bluetooth.BlueToothService;
import com.freway.ebike.bluetooth.BlueToothUtil;
import com.freway.ebike.bluetooth.EBikeTravelData;
import com.freway.ebike.db.DBHelper;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.model.Travel;
import com.freway.ebike.utils.AlertUtil;
import com.freway.ebike.utils.AlertUtil.AlertClick;
import com.freway.ebike.utils.SPUtils;

/**
 * Application基类
 * */
public class BaseApplication extends Application{

	public static long travelId=-1;
	public static int travelState=TravelConstant.TRAVEL_STATE_NONE;
	public static int workModel=EBConstant.WORK_BLUETOOTH;//工作模式默认为蓝牙
	
	@Override
	public void onCreate() {
		super.onCreate();
		workModel=SPUtils.getWorkModel(this);//工作模式
		initSdk();
	}
	/**初始化第三方sdk*/
	private void initSdk(){
		FacebookSdk.sdkInitialize(this.getApplicationContext());
	}
	
	/**
	 * @param context 上下文，只能是发送者如activity或者service
	 * @param state 改变的状态
	 * @param isSelfReceiver 自己是否要接收自己发送的此次状态改变的广播
	 * @param receiver 状态改变的广播 如果isSelfReceiver为false就是不需要接收，那么receiver一定不能为空
	 * @return void
	 * @Description 发送状态改变广播
	 */
	public static void sendStateChangeBroadCast(final Context context,int state){
		if(state==TravelConstant.TRAVEL_STATE_START||state==TravelConstant.TRAVEL_STATE_RESUME){//要去检查蓝牙有没有断线
			if(workModel==EBConstant.WORK_BLUETOOTH&&BlueToothService.ble_state!=BlueToothConstants.BLE_STATE_CONNECTED){//未链接，则要提示用户去链接
				if(TextUtils.isEmpty(SPUtils.getEBkieAddress(context))){
					BlueToothUtil.toBindBleActivity(context,BLEScanConnectActivity.HANDLE_SCAN);
				}else{
					BlueToothUtil.toBindBleActivity(context,BLEScanConnectActivity.HANDLE_CONNECT);
				}
				return;
			}
		}
		travelState=state;
		Intent intent = new Intent(TravelConstant.ACTION_UI_SERICE_TRAVEL_STATE_CHANGE);
		intent.putExtra(TravelConstant.EXTRA_STATE, travelState);
		if (state == TravelConstant.TRAVEL_STATE_START && checkGpsEnable(context)) {// 在这里判断定位有没有打开
			Travel travel=new Travel();
			DBHelper.getInstance(context).insertTravel(travel);
			travelId=travel.getId();
			EBikeTravelData.getInstance(context).start(travel.getId(),TravelConstant.TRAVEL_TYPE_IM);
			intent.putExtra(TravelConstant.EXTRA_TRAVEL_ID, travelId);
		}else if(state == TravelConstant.TRAVEL_STATE_PAUSE){
			EBikeTravelData.getInstance(context).pause();
		}else if(state == TravelConstant.TRAVEL_STATE_RESUME){
			EBikeTravelData.getInstance(context).resume();
		}else if(state==TravelConstant.TRAVEL_STATE_COMPLETED){//存储
			EBikeTravelData.getInstance(context).completed();
		}else if(state==TravelConstant.TRAVEL_STATE_STOP){
			EBikeTravelData.getInstance(context).stop();
		}else if(state==TravelConstant.TRAVEL_STATE_FAKE_PAUSE){//伪暂停
			EBikeTravelData.getInstance(context).fakePause();
		}else{
			travelState=TravelConstant.TRAVEL_STATE_NONE;
		}
		context.sendBroadcast(intent);
	}
	/**发送退出App的广播*/
	public static void sendQuitAppBroadCast(Context context){
		Intent intent = new Intent(TravelConstant.ACTION_UI_SERICE_QUIT_APP);
		context.sendBroadcast(intent);
	}
	
	/** 判断定位是否打开 */
	public static boolean checkGpsEnable(final Context context) {
		if(context instanceof Activity){
			final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				AlertUtil.getInstance().alertChoice((Activity)context,context.getString(R.string.gps_not_open), context.getString(R.string.yes), context.getString(R.string.no), 
						new AlertClick() {
							
							@Override
							public void onClick(AlertDialog dialog,View v) {
								dialog.dismiss();
								context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						}, 
						new AlertClick() {
							
							@Override
							public void onClick(AlertDialog dialog,View v) {
								dialog.dismiss();
							}
						},
						true);
				return false;
			} else {
				return true;
			}
		}else{
			return true;
		}
	}
	/*
	 * project.properties
	 * #This project.properties file is being managed by Fabric.
#Manifest merger has been enabled to pull in kit resources.
#Wed Jan 06 15:21:29 CST 2016
manifestmerger.enabled=true
proguard.config=proguard.cfg
android.library.reference.4=..\\facebook
android.library.reference.3=..\\appcompat_v7
target=android-22
android.library.reference.2=..\\google-play-services_lib
android.library.reference.1=kit-libs/com-crashlytics-sdk-android_crashlytics_2
	 * 
	 * 
	 * */
}
