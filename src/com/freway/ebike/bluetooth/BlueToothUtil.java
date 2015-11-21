package com.freway.ebike.bluetooth;

import java.util.HashMap;

import com.freway.ebike.R;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.utils.AlertUtil;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;
import com.google.gson.Gson;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class BlueToothUtil {
	private static final String TAG = BlueToothUtil.class.getSimpleName();
	private Context context;
	private Handler scanHandler;
	private Handler sendDataHandler;
	// private Handler syncHandler;
	private Handler bleStateHandler;

	private Handler travelStateHandler;
	public BlueToothUtil(Context context, Handler bleStateHandler,Handler travelStateHandler) {
		this.bleStateHandler = bleStateHandler;
		this.travelStateHandler=travelStateHandler;
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
		if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {// 不支持低功耗蓝牙
			isOk = false;
			ToastUtils.toast(context, context.getString(R.string.not_supported_ble));
		}
		return isOk;
	}

	private void startService() {
		IntentFilter filter = new IntentFilter(BlueToothConstants.BLE_SERVER_STATE_CHANAGE);
		context.registerReceiver(mStateReceiver, filter);
		filter = new IntentFilter(TravelConstant.ACTION_UI_SERICE_TRAVEL_STATE_CHANGE);
		context.registerReceiver(mStateReceiver, filter);
		Intent service = new Intent(context, BlueToothService.class);
		context.startService(service);
	}

	/** 初始化 */
	public void initBle(final Handler updateUiHandler) {
		receiveSendData(updateUiHandler);
		if (TextUtils.isEmpty(SPUtils.getEBkieAddress(context))) {// 未绑定
			bleConnect(context.getString(R.string.ble_not_bind),context.getString(R.string.yes),context.getString(R.string.no));
		}
//		else if (EBikeTravelData.travel_state == TravelConstant.TRAVEL_STATE_STOP
//				|| EBikeTravelData.travel_state == TravelConstant.TRAVEL_STATE_COMPLETED
//				|| EBikeTravelData.travel_state == TravelConstant.TRAVEL_STATE_NONE) {
//			// 开始同步
//			syncData(syncHandler);
//		}
	}

	/** 判断Ble是否链接 */
	public void bleConnect(String message ,String leftText,String rightText) {
		if (BlueToothService.ble_state != BlueToothConstants.BLE_STATE_CONNECTED) {// 未绑定
			AlertUtil.getInstance(context).alertChoice( message,leftText,rightText, new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertUtil.getInstance(context).dismiss();
					toBindBleActivity(context,BLEScanConnectActivity.HANDLE_SCAN);
				}
			}, new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertUtil.getInstance(context).dismiss();
				}
			});

		}
	}

	/** 退出服务 */
	public void exit() {
		if (scanHandler != null) {
			context.unregisterReceiver(mScanReceiver);
		}
		if (sendDataHandler != null) {
			context.unregisterReceiver(mSendDataReceiver);
		}
		context.unregisterReceiver(mStateReceiver);
	}

	/** 停止服务，一般不需要调用 */
	public void stop() {
		exit();
		Intent service = new Intent(context, BlueToothService.class);
		context.stopService(service);
	}

	/** 去到绑定设备页面 */
	public static void toBindBleActivity(Context context, int handle) {
		Intent intent = new Intent(context, BLEScanConnectActivity.class);
		intent.putExtra(BLEScanConnectActivity.HANDLE_EXTRA, handle);
		context.startActivity(intent);
	}

	/**
	 * @param context
	 *            void
	 * @Description 控制服务
	 */
	private void handleService(int flag, String data) {
		if (isLebAvailable()) {
			Intent intent = new Intent(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER);
			intent.putExtra(BlueToothConstants.EXTRA_HANDLE_TYPE, flag);
			intent.putExtra(BlueToothConstants.EXTRA_DATA, data);
			context.sendBroadcast(intent);
		}
	}

	/** 设置travel状态 */
	public void setBikeState(Context context, int control, int flag) {
		EBikeStatus.getInstance(context).setBikeStatus(control, flag);
	}

	/** 接收发送数据返回 */
	public void receiveSendData(Handler sendDataHandler) {
		this.sendDataHandler = sendDataHandler;
		IntentFilter filter = new IntentFilter(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SEND_DATA);
		context.registerReceiver(mSendDataReceiver, filter);
		handleService(BlueToothConstants.HANDLE_SERVER_SEND_DATA, null);
	}

	/** 接收扫描设备返回 */
	public void scanDevice(Handler scanHandler) {
		this.scanHandler = scanHandler;
		IntentFilter filter = new IntentFilter(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SCAN_DEVICE);
		context.registerReceiver(mScanReceiver, filter);
		handleService(BlueToothConstants.HANDLE_SERVER_SCAN, null);
	}

	/**发送数据*/
	public void sendData(byte[] packData) {
		Gson gson=new Gson();
		String mapString=gson.toJson(packData);
		handleService(BlueToothConstants.HANDLE_SERVER_SEND_DATA, mapString);
	}
	/** 同步数据 */
	/*
	 * public void syncData(Handler syncHandler) { this.syncHandler =
	 * syncHandler; IntentFilter filter = new IntentFilter(
	 * BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SYNC_DATA);
	 * context.registerReceiver(mSyncReceiver, filter);
	 * handleService(BlueToothConstants.HANDLE_SERVER_SYNC, null); }
	 */

	/** 链接蓝牙服务 */
	public void connectBLE(String address) {
		handleService(BlueToothConstants.HANDLE_SERVER_CONNECT, address);
	}

	/**
	 * 蓝牙或者骑行状态改变
	 */
	private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			int state = intent.getIntExtra(TravelConstant.EXTRA_STATE, 0);
			if (BlueToothConstants.BLE_SERVER_STATE_CHANAGE.equals(action)) {
				// LogUtils.i(TAG, "接收到蓝牙状态的改变"+state);
				if (bleStateHandler != null) {
					bleStateHandler.sendEmptyMessage(state);
				}
			}else if (TravelConstant.ACTION_UI_SERICE_TRAVEL_STATE_CHANGE.equals(action)) {// 状态改变
				if (travelStateHandler != null) {
					travelStateHandler.sendEmptyMessage(state);
				}
			}
		}
	};
	/**
	 * 扫描设备返回
	 */
	private final BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BlueToothConstants.EXTRA_DATA);
			if (scanHandler != null) {
				Message msg = Message.obtain();
				msg.obj = device;
				msg.what = intent.getIntExtra(BlueToothConstants.EXTRA_STATUS, BlueToothConstants.RESULT_FAIL);
				scanHandler.sendMessage(msg);
			}
		}
	};
	/**
	 * 同步数据返回
	 */
	/*
	 * private final BroadcastReceiver mSyncReceiver = new BroadcastReceiver() {
	 * 
	 * @Override public void onReceive(Context context, Intent intent) { // 同步完成
	 * LogUtils.i(TAG, "sync completed---" + EBikeHistoryData.data_id); if
	 * (scanHandler == null && EBikeHistoryData.data_id > 0) {// 说明有读到数据 Travel
	 * travel = new Travel();
	 * travel.setAltitude(EBikeHistoryData.travel_altitude);
	 * travel.setAvgSpeed(EBikeHistoryData.travel_avgSpeed);
	 * travel.setCadence(EBikeHistoryData.travel_cadence);
	 * travel.setCalorie(EBikeHistoryData.travel_calorie);
	 * travel.setDistance(EBikeHistoryData.travel_distance);
	 * travel.setEndTime(EBikeHistoryData.travel_endTime);
	 * travel.setMaxSpeed(EBikeHistoryData.travel_maxSpeed);
	 * travel.setSpendTime(EBikeHistoryData.travel_spendTime);
	 * travel.setStartTime(EBikeHistoryData.travel_startTime);
	 * DBHelper.getInstance(context).insertTravel(travel); if (syncHandler !=
	 * null) { syncHandler
	 * .sendEmptyMessage(BlueToothConstants.RESULT_SUCCESS);// 1完成。0同步失败 } } }
	 * };
	 */
	/**
	 * 发送数据返回
	 */
	private final BroadcastReceiver mSendDataReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (sendDataHandler != null) {
				sendDataHandler.sendEmptyMessage(BlueToothConstants.RESULT_SUCCESS);// 更新
			}
		}
	};

}
