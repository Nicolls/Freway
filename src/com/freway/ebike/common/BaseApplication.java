package com.freway.ebike.common;

import com.freway.ebike.bluetooth.EBikeTravelData;
import com.freway.ebike.db.DBHelper;
import com.freway.ebike.db.Travel;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.utils.ToastUtils;

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

	private static final float MUST_MIN_TRAVEL = 10;// 最短行程10米
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
		EBikeTravelData.travel_state=state;
		if (state == TravelConstant.TRAVEL_STATE_START && checkGpsEnable(context)) {// 在这里判断定位有没有打开
			Travel travel=new Travel();
			DBHelper.getInstance(context).insertTravel(travel);
			EBikeTravelData.start(travel.getId());
		}else if(state == TravelConstant.TRAVEL_STATE_PAUSE){
			EBikeTravelData.pause();
		}else if(state == TravelConstant.TRAVEL_STATE_RESUME){
			EBikeTravelData.resume();
		}else if(state==TravelConstant.TRAVEL_STATE_COMPLETED){//存储
			if (EBikeTravelData.travel_distance < MUST_MIN_TRAVEL) {
				//数据少，不存储
				ToastUtils.toast(context, "travel is too short not save");
			}else{
				Travel travel=new Travel();
				travel.setId(EBikeTravelData.travel_id);
				travel.setAltitude(EBikeTravelData.travel_altitude);
				travel.setAvgSpeed(EBikeTravelData.travel_avgSpeed);
				travel.setCadence(EBikeTravelData.travel_cadence);
				travel.setCalorie(EBikeTravelData.travel_calorie);
				travel.setDistance(EBikeTravelData.travel_distance);
				travel.setEndTime(EBikeTravelData.travel_endTime);
				travel.setMaxSpeed(EBikeTravelData.travel_maxSpeed);
				travel.setSpendTime(EBikeTravelData.travel_spendTime);
				travel.setStartTime(EBikeTravelData.travel_startTime);
				DBHelper.getInstance(context).updateTravel(travel);// 更新
			}
		}else if(state==TravelConstant.TRAVEL_STATE_STOP){
			EBikeTravelData.stop();
			DBHelper.getInstance(context).deleteTravel(EBikeTravelData.travel_id);// 停止则删除这条数据
		}else{
			EBikeTravelData.travel_state=TravelConstant.TRAVEL_STATE_NONE;
		}
		Intent intent = new Intent(TravelConstant.ACTION_UI_SERICE_TRAVEL_STATE_CHANGE);
		intent.putExtra(TravelConstant.EXTRA_STATE, EBikeTravelData.travel_state);
		context.sendBroadcast(intent);
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
