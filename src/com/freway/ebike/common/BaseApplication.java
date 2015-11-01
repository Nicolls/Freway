package com.freway.ebike.common;

import com.freway.ebike.map.TravelConstant;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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
	public static void sendStateChangeBroadCast(Context context,int state,boolean isSelfReceiver,BroadcastReceiver receiver){
		if(!isSelfReceiver){
			context.unregisterReceiver(receiver);
		}
		Intent intent = new Intent(TravelConstant.ACTION_UI_SERICE_TRAVEL_STATE_CHANGE);
		intent.putExtra(TravelConstant.EXTRA_STATE, state);
		context.sendBroadcast(intent);
		if(!isSelfReceiver){
			IntentFilter filter=new IntentFilter(TravelConstant.ACTION_UI_SERICE_TRAVEL_STATE_CHANGE);
			context.registerReceiver(receiver, filter);
		}
	}
}
