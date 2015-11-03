package com.freway.ebike.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import com.freway.ebike.R;
import com.freway.ebike.db.DBHelper;
import com.freway.ebike.db.Travel;
import com.freway.ebike.utils.ToastUtils;

public class BlueToothUtil {
	private Context context;
	private Handler scanHandler;
	private Handler sendDataHandler;
	private Handler syncHandler;
	private Handler bleStateHandler;
	public BlueToothUtil(Context context,Handler bleStateHandler) {
		this.bleStateHandler=bleStateHandler;
		this.context = context;
		startService();
	}

	/** 判断是否支持蓝牙 */
	public boolean isLebAvailable() {
		boolean isOk = true;
		if (BluetoothAdapter.getDefaultAdapter() == null) {
			isOk = false;
			ToastUtils.toast(context, context.getString(R.string.not_support_bluetooth));
		}
		if (!context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {// 不支持低功耗蓝牙
			isOk = false;
			ToastUtils.toast(context, context.getString(R.string.not_supported_ble));
		}
		return isOk;
	}

	private void startService() {
		Intent service = new Intent(context, BlueToothService.class);
		context.startService(service);
		IntentFilter filter = new IntentFilter(
				BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER_RESULT);
		context.registerReceiver(mHandleReceiver, filter);
		filter = new IntentFilter(BlueToothConstants.BLE_SERVER_STATE_CHANAGE);
		context.registerReceiver(mBleStateReceiver, filter);
	}

	/** 退出服务 */
	public void exit() {
		context.unregisterReceiver(mHandleReceiver);
		context.unregisterReceiver(mBleStateReceiver);
	}

	/** 停止服务，一般不需要调用 */
	public void stop() {
		exit();
		Intent service = new Intent(context, BlueToothService.class);
		context.stopService(service);
	}

	/**
	 * @param context
	 *            void
	 * @Description 控制服务
	 */
	private void handleService(int flag, String data) {
		if(isLebAvailable()){
			Intent intent = new Intent(
					BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER);
			intent.putExtra(BlueToothConstants.EXTRA_HANDLE_TYPE, flag);
			intent.putExtra(BlueToothConstants.EXTRA_DATA, data);
			context.sendBroadcast(intent);
		}
	}
	/**设置travel状态*/
	public void setBikeState(int control, int flag) {
		EBikeStatus.setBikeStatus(EBikeStatus.BIKING_HELP_POWER_1, flag);
	}
	
	/**接收发送数据返回*/
	public void handleSendData(Handler sendDataHandler){
		this.sendDataHandler=sendDataHandler;
	}
	
	/**接收扫描设备返回*/
	public void handleScanDevice(Handler scanHandler){
		this.scanHandler = scanHandler;
		handleService(BlueToothConstants.HANDLE_SERVER_SCAN, null);
	}
	
	/**接收同步数据*/
	public void handleSyncData(Handler syncHandler){
		this.syncHandler = syncHandler;
		handleService(BlueToothConstants.HANDLE_SERVER_SYNC, null);
	}
	/**链接蓝牙服务*/
	public void connectBLE(String address){
		handleService(BlueToothConstants.HANDLE_SERVER_CONNECT,address);
	}

	/**
	 * The BroadcastReceiver that listens for discovered devices and changes the
	 * title when discovery is finished
	 */
	private final BroadcastReceiver mBleStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BlueToothConstants.BLE_SERVER_STATE_CHANAGE.equals(action)) {
				int state = intent.getIntExtra(BlueToothConstants.EXTRA_STATE,
						0);
				if (sendDataHandler != null) {
					bleStateHandler.sendEmptyMessage(state);
				}
			}
		}
	};
	/**
	 * The BroadcastReceiver that listens for discovered devices and changes the
	 * title when discovery is finished
	 */
	private final BroadcastReceiver mHandleReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER_RESULT
					.equals(action)) {
				int handle = intent.getIntExtra(
						BlueToothConstants.EXTRA_HANDLE_TYPE, 0);
				switch (handle) {
				case BlueToothConstants.HANDLE_SERVER_CONNECT:// 链接
					
					break;
				case BlueToothConstants.HANDLE_SERVER_SCAN:// 扫描
					BluetoothDevice device = (BluetoothDevice) intent
					.getParcelableExtra(BlueToothConstants.EXTRA_DATA);
					if(scanHandler!=null){
						Message msg=Message.obtain();
						msg.obj=device;
						scanHandler.sendMessage(msg);
					}
					break;
				case BlueToothConstants.HANDLE_SERVER_SEND_DATA:// 发数据
					if(sendDataHandler!=null){
						sendDataHandler.sendEmptyMessage(0);//更新
					}
					break;
				case BlueToothConstants.HANDLE_SERVER_SYNC:// 同步
					int result=intent.getIntExtra(BlueToothConstants.EXTRA_DATA, 0);
					//同步完成
					Travel travel=new Travel();
					travel.setAltitude(EBikeHistoryData.travel_altitude);
					travel.setAvgSpeed(EBikeHistoryData.travel_avgSpeed);
					travel.setCadence(EBikeHistoryData.travel_cadence);
					travel.setCalorie(EBikeHistoryData.travel_calorie);
					travel.setDistance(EBikeHistoryData.travel_distance);
					travel.setEndTime(EBikeHistoryData.travel_endTime);
					travel.setMaxSpeed(EBikeHistoryData.travel_maxSpeed);
					travel.setSpendTime(EBikeHistoryData.travel_spendTime);
					travel.setStartTime(EBikeHistoryData.travel_startTime);
					DBHelper.getInstance(context).insertTravel(travel);
					if(syncHandler!=null){
						syncHandler.sendEmptyMessage(result);//1完成。0同步失败
					}
					break;
					default:
						break;
				}
			}
		}
	};

}
