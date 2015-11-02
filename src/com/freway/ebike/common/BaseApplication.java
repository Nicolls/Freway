package com.freway.ebike.common;

import com.freway.ebike.map.TravelConstant;

import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;

/**
 * Application基类
 * */
public class BaseApplication extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	/**
	 * @param context 上下文，只能是发送者如activity或者service
	 * @param state 改变的状态
	 * @param isSelfReceiver 自己是否要接收自己发送的此次状态改变的广播
	 * @param receiver 状态改变的广播 如果isSelfReceiver为false就是不需要接收，那么receiver一定不能为空
	 * @return void
	 * @Description 发送状态改变广播
	 */
	public static void sendStateChangeBroadCast(Context context,int state){
		
		if (state == TravelConstant.TRAVEL_STATE_START && checkGpsEnable(context)) {// 在这里判断定位有没有打开
			Intent intent = new Intent(TravelConstant.ACTION_UI_SERICE_TRAVEL_STATE_CHANGE);
			intent.putExtra(TravelConstant.EXTRA_STATE, state);
			context.sendBroadcast(intent);
		}
	}
	
	/** 判断定位是否打开 */
	public static boolean checkGpsEnable(Context context) {
		final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps(context);
			return false;
		} else {
			return true;
		}
	}
	/** 弹出确认打开Gps对话框 */
	private static void buildAlertMessageNoGps(final Context context) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}
}
