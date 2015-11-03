package com.freway.ebike.bluetooth;

import java.util.HashMap;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;

import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.protocol.ProtocolByteHandler;
import com.freway.ebike.utils.SPUtils;

public class BlueToothUtil {
	private Context context;
	private Handler searchHandler;
	private Handler dataHandler;
	public static final int HANDLER_DATA=0;
	public static final int HANDLER_STATE=1;
	public BlueToothUtil(Context context) {
		this.context = context;
		Intent service = new Intent(context, BlueToothService.class);
		context.startService(service);
	}
	
	/**判断是否支持蓝牙*/
	public boolean isLebAvailable(){
		boolean isOk=true;
		if(BluetoothAdapter.getDefaultAdapter()==null){
			isOk=false;
		}
		if (!context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {// 不支持低功耗蓝牙
			isOk=false;
		}
		return isOk;
	}
	
	/**开启服务监听*/
	public void startService(Handler dataHandler) {
			this.dataHandler=dataHandler;
			IntentFilter filter = new IntentFilter(BlueToothConstants.BLUETOOTH_ACTION_SERVER_SCAN_RESULT);
			context.registerReceiver(mDataReceiver, filter);
			filter = new IntentFilter(BlueToothConstants.BLE_SERVER_STATE_CHANAGE);
			context.registerReceiver(mBleStateReceiver, filter);
	}

	/** 退出服务 */
	public void exit() {
		context.unregisterReceiver(mDataReceiver);
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
	public void handleService(Context context, int flag, String data) {
		Intent intent = new Intent(
				BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER);
		intent.putExtra(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_EXTRA_FLAG,
				flag);
		intent.putExtra(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_EXTRA_DATA,
				data);
		context.sendBroadcast(intent);
	}

	public void setBikeState(int control, int flag) {
		EBikeStatus.setBikeStatus(EBikeStatus.BIKING_HELP_POWER_1, flag);
	}

	/** 开始搜索 */
	public void startScanLeb(Handler searchHandler) {
		this.searchHandler=searchHandler;
		IntentFilter filter = new IntentFilter(BlueToothConstants.BLUETOOTH_ACTION_SERVER_SCAN_RESULT);
		context.registerReceiver(mSearchReceiver, filter);
		SPUtils.setEBikeAddress(context, "");// 重新搜索要设置为空
		Intent intent = new Intent(
				BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER);
		intent.putExtra(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_EXTRA_FLAG,
				BlueToothConstants.HANDLE_SERVER_SCAN);
		intent.putExtra(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_EXTRA_DATA,
				"");
		context.sendBroadcast(intent);
	}
	/**退出搜索*/
	public void exitScanLeb(){
		context.unregisterReceiver(mSearchReceiver);
	}
	
	/**
	 * The BroadcastReceiver that listens for discovered devices and changes the
	 * title when discovery is finished
	 */
	private final BroadcastReceiver mBleStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BlueToothConstants.BLE_SERVER_STATE_CHANAGE
					.equals(action)) {// when send data come back
				int state=intent.getIntExtra(BlueToothConstants.EXTRA_STATE, 0);
				if(dataHandler!=null){
					
					Message msg=Message.obtain();
					msg.what=HANDLER_STATE;
					msg.obj=state;
					dataHandler.sendMessage(msg);
				}
			}
		}
	};
	/**
	 * The BroadcastReceiver that listens for discovered devices and changes the
	 * title when discovery is finished
	 */
	private final BroadcastReceiver mDataReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BlueToothConstants.BLUETOOTH_ACTION_SERVER_SEND_RESULT
					.equals(action)) {// when send data come back
//				EBikeTravelData ebike = null;
//				int cmd = 0;
//				if (intent
//						.hasExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DATA)) {
//					HashMap<String, Object> data = (HashMap<String, Object>) intent
//							.getSerializableExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DATA);
//					cmd = (int) data.get(ProtocolByteHandler.EXTRA_CMD);
//					ebike = (EBikeTravelData) data
//							.get(ProtocolByteHandler.EXTRA_DATA);
//				}
				Message msg=Message.obtain();
				msg.what=HANDLER_DATA;
//				msg.obj=ebike;
				if(dataHandler!=null){
					dataHandler.sendMessage(msg);
				}
			}
		}
	};

	/**
	 * The BroadcastReceiver that listens for discovered devices and changes the
	 * title when discovery is finished
	 */
	private final BroadcastReceiver mSearchReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BlueToothConstants.BLUETOOTH_ACTION_SERVER_SCAN_RESULT
					.equals(action)) {
				String name = "";
				String address = "";
				if (intent
						.hasExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DEVICE)) {
					BluetoothDevice device = (BluetoothDevice) intent
							.getParcelableExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DEVICE);
					name = device.getName() + "--" + device.getAddress();
					address = device.getAddress();
				}
				if(searchHandler!=null){
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("name", name);
					map.put("address", address);
					Message msg=Message.obtain();
					msg.obj=map;
					searchHandler.sendMessage(msg);
				}
			}
		}
	};

	/**
	 * 发送数据，提供给外界强制去发数据的通道，而如果只是需要开灯关灯，改变骑行状态等操作，只需要改变com.freway.ebike.bluetooth
	 * .EBikeStatus中的值即可
	 */
	public void sendData(Context context, int commandCode, byte[] data) {
		Intent intent = new Intent(
				BlueToothConstants.BLUETOOTH_ACTION_SERVER_SEND_DATA);
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(ProtocolByteHandler.EXTRA_CMD, commandCode);
		dataMap.put(ProtocolByteHandler.EXTRA_DATA, data);
		intent.putExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DATA, dataMap);
		context.sendBroadcast(intent);
	}
}
