package com.freway.ebike.bluetooth;

import java.util.HashMap;
import java.util.List;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseService;
import com.freway.ebike.protocol.CommandCode;
import com.freway.ebike.protocol.Protocol;
import com.freway.ebike.protocol.ProtocolByteHandler;
import com.freway.ebike.protocol.ProtocolTool;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;

public class BlueToothService extends BaseService {
	/**
	 * Bluetooth manager
	 * */
	private BluetoothManager mBluetoothManager = null;
	/**
	 * Local Bluetooth adapter
	 */
	private BluetoothAdapter mBluetoothAdapter = null;
	/**
	 * Name of the connected device
	 */
	private String mConnectedDeviceName = null;
	/**
	 * Member object for the chat services
	 */
	private BluetoothConnection mBlueToothConnction = null;
	// private Context context;
	private String mBluetoothDeviceAddress;
	// 重链
	private ReConnectThread mReConnectThread;// 每隔一段时间去链接
	private static final int RECONNECT_SPACING = 5 * 1000;// 间隔时间，毫秒
	private boolean isReconnectRunning = true;// 开启断线重新链接
	// 获取数据心跳线程
	private RequestDataThread mRequestDataThread;// 每隔一段时间去获取数据
	private static final int REQUESTDATA_SPACING = 1 * 1000;// 间隔时间，毫秒
	private boolean isRequestDataRunning = true;// 开启获取数据
	// bluetooth gat
	private BluetoothGattCharacteristic mReceiveCharacteristic; // 接收数据
	private BluetoothGattCharacteristic mSendCharacteristic; // 发送数据

	// 蓝牙是否处于扫描状态
	private boolean isScanning = false;
	// 判断是否是应用初始化蓝牙
	private boolean isAppInitBluetooth = false;

	// 短信广播
	private static final String ACTION_SMS = "android.provider.Telephony.SMS_RECEIVED";
	// 电话广播
	private static final String ACTION_PHONE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;

	/**
	 * @param context
	 *            void
	 * @Description 控制服务
	 */
	public static void handle(Context context, int flag, String data) {
		if (flag == BlueToothConstants.HANDLE_SERVER_START) {// 开启服务
			Intent service = new Intent(context, BlueToothService.class);
			context.startService(service);
		} else if (flag == BlueToothConstants.HANDLE_SERVER_STOP) {// 关闭服务
			Intent service = new Intent(context, BlueToothService.class);
			context.stopService(service);
		} else {// 操作服务
			Intent intent = new Intent(
					BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER);
			intent.putExtra(
					BlueToothConstants.BLUETOOTH_ACTION_HANDLE_EXTRA_FLAG, flag);
			intent.putExtra(
					BlueToothConstants.BLUETOOTH_ACTION_HANDLE_EXTRA_DATA, data);
			context.sendBroadcast(intent);
		}
	}

	/**
	 * 发送数据，提供给外界强制去发数据的通道，而如果只是需要开灯关灯，改变骑行状态等操作，只需要改变com.freway.ebike.bluetooth
	 * .EBikeStatus中的值即可
	 */
	public static void sendData(Context context, int commandCode, byte[] data) {
		Intent intent = new Intent(
				BlueToothConstants.BLUETOOTH_ACTION_SERVER_SEND_DATA);
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put(ProtocolByteHandler.EXTRA_CMD, commandCode);
		dataMap.put(ProtocolByteHandler.EXTRA_DATA, data);
		intent.putExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DATA, dataMap);
		context.sendBroadcast(intent);
	}

	/** 监听来自UI的广播 */
	private final BroadcastReceiver mHandleReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BlueToothConstants.BLUETOOTH_ACTION_SERVER_SEND_DATA
					.equals(action)) {
				if (intent
						.hasExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DATA)) {
					HashMap<String, Object> dataMap = (HashMap<String, Object>) intent
							.getSerializableExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DATA);
					int cmd = (int) dataMap.get(ProtocolByteHandler.EXTRA_CMD);
					byte[] str = (byte[]) dataMap
							.get(ProtocolByteHandler.EXTRA_DATA);
					sendData(cmd, str);
				}
			} else if (BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER
					.equals(action)) {
				int handle = intent.getIntExtra(
						BlueToothConstants.BLUETOOTH_ACTION_HANDLE_EXTRA_FLAG,
						0);
				switch (handle) {
				case BlueToothConstants.HANDLE_SERVER_PUASE:// 暂停
					mBlueToothConnction.setCharacteristicNotification(
							mReceiveCharacteristic, false);// 不接收了
					break;
				case BlueToothConstants.HANDLE_SERVER_CONTINUTE:// 继续
					mBlueToothConnction.setCharacteristicNotification(
							mReceiveCharacteristic, true);// 重新接收
					break;
				case BlueToothConstants.HANDLE_SERVER_SCAN:// 扫描
					SPUtils.setEBikeAddress(BlueToothService.this, "");//UI发送的重新扫描，则清除保存的设备
					mBluetoothDeviceAddress="";
					mBlueToothConnction.close();
					startScanBluetoothDevice();
					break;
				case BlueToothConstants.HANDLE_SERVER_CONNECT:// 链接
					mBluetoothDeviceAddress = intent
							.getStringExtra(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_EXTRA_DATA);
					connectDevice(mBluetoothDeviceAddress);
					break;
				default:
					break;
				}
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {// 不支持蓝牙
			toastMessage(getString(R.string.not_support_bluetooth));
			stopSelf();
		} else if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {// 不支持低功耗蓝牙
			toastMessage(getString(R.string.not_supported_ble));
			stopSelf();
		} else {// OK
			startService();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mBluetoothDeviceAddress = SPUtils.getEBkieAddress(this);// 设备地址
		if (mBlueToothConnction == null) {
			mBlueToothConnction = new BluetoothConnection(this, mHandler);
		}
		boolean isGoon = false;
		if (mReConnectThread == null) {
			mReConnectThread = new ReConnectThread();
			mReConnectThread.start();
		}
		if (mBlueToothConnction.getState() != BluetoothConnection.STATE_CONNECTED) {// 只要不是在链接的情况下就要重新链接
			isGoon = true;
		}
		LogUtils.i(tag, "isGoon" + isGoon);

		if (isGoon && stateEnableStep()) {
			// 客户端自动链接
			if (!TextUtils.isEmpty(mBluetoothDeviceAddress)) {
				startScanBluetoothDevice();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopService();
	}

	/** tost一个message */
	private void toastMessage(String message) {
		try {
			ToastUtils.toast(this, message);
		} catch (Exception e) {
			LogUtils.e(tag, "toast message error");
		}
	}

	/** 统一打印数据 */
	private void printlnMessage(String message) {
		LogUtils.i(tag, message);
	}

	/** 停止服务 */
	private void stopService() {
		// Make sure we're not doing discovery anymore
		stopScanBluetoothDevice();
		isAppInitBluetooth = false;
		if (mReConnectThread != null) {
			mReConnectThread.cancel();
			mReConnectThread = null;
		}
		if (mRequestDataThread != null) {
			mRequestDataThread.cancel();
			mRequestDataThread = null;
		}
		if (mBlueToothConnction != null) {
			mBlueToothConnction.close();
		}
		// unregister
		unregisterReceiver(mReceiver);
		unregisterReceiver(mHandleReceiver);
	}

	/** 开启服务,如果已经开启，必须调用stopService关闭服务后，才可以再次调用 */
	private void startService() {
		// 注册接收来自UI的广播
		IntentFilter filter = new IntentFilter(
				BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER);
		registerReceiver(mHandleReceiver, filter);
		filter = new IntentFilter(
				BlueToothConstants.BLUETOOTH_ACTION_SERVER_SEND_DATA);
		registerReceiver(mHandleReceiver, filter);

		// 注册对蓝牙状态改变的广播

		// register for when device bluetooth enable
		filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(mReceiver, filter);

		// 注册短信和电话的广播
		filter = new IntentFilter(ACTION_SMS);
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter(ACTION_PHONE);
		registerReceiver(mReceiver, filter);

	}

	/** 判断当前蓝牙是否可用并打开状态，如果是则开启connection,默认开启服务，反之则打开蓝牙 */
	private boolean stateEnableStep() {
		boolean enable = mBluetoothAdapter.isEnabled();
		if (!enable) {
			mBluetoothAdapter.enable();
		}
		return enable;
	}

	/** 扫描蓝牙 */
	private Handler scanHandler = new Handler();
	private static final int SCAN_TIME = 10 * 1000;// 扫描时间为10秒

	/*
	 * 扫描蓝牙设备
	 */
	public void startScanBluetoothDevice() {
		if (stateEnableStep()) {
			if (isScanning) {// 正在扫描则停止当前扫描后再开启扫描
				stopScanBluetoothDevice();
			}
			scanHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					stopScanBluetoothDevice();
				}
			}, SCAN_TIME); // 10秒后停止扫描
			printlnMessage("开始扫描");
			isScanning = true;
			mBluetoothAdapter.startLeScan(bleScanCallback);
		} else {
			toastMessage(getString(R.string.bluetooth_not_open));
		}
	}

	/**
	 * 停止扫描蓝牙设备
	 */
	public void stopScanBluetoothDevice() {
		if (isScanning) {
			printlnMessage("停止扫描");
			isScanning = false;
			mBluetoothAdapter.stopLeScan(bleScanCallback);
		}
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback bleScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {
			printlnMessage("扫到设备:name:" + device.getName() + "-address:"
					+ device.getAddress());
			if (!TextUtils.isEmpty(mBluetoothDeviceAddress)
					&& device != null
					&& device.getAddress() != null
					&& TextUtils.equals(mBluetoothDeviceAddress,
							device.getAddress())) {// 不为空则匹配自动 链接
				printlnMessage("开始链接到：" + mBluetoothDeviceAddress);
				stopScanBluetoothDevice();// 停止扫描
				connectDevice(device.getAddress());
			} else {// 如果address为空，则返回广播到的设备
				printlnMessage("返回扫描到的设备：" + device.getName());
				Intent scanIntent = new Intent(
						BlueToothConstants.BLUETOOTH_ACTION_SERVER_SCAN_RESULT);
				scanIntent.putExtra(
						BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DEVICE,
						device);
				sendBroadcast(scanIntent);
			}
		}

	};

	/** 向UI返回收到的解析好了的数据 */
	private void broadCastData2UI(HashMap<String, Object> map) {
		Intent intent = new Intent(
				BlueToothConstants.BLUETOOTH_ACTION_SERVER_SEND_RESULT);
		if (map != null) {

			int resultCode = (int) map.get(ProtocolByteHandler.EXTRA_CODE);
			if (resultCode == Protocol.RESULT_OK) {// 数据正确
				intent.putExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DATA,
						map);
				sendBroadcast(intent);
			}
		}
	}

	/**
	 * The BroadcastReceiver that listens for discovered devices and changes the
	 * title when discovery is finished
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String sendData = "";
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {// 蓝牙状态
				if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0) == BluetoothAdapter.STATE_ON) {
					sendData = "蓝牙打开";
					printlnMessage(sendData);
					startScanBluetoothDevice();

				} else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0) == BluetoothAdapter.STATE_OFF) {
					sendData = "蓝牙关闭";
					printlnMessage(sendData);
					if (isAppInitBluetooth) {// 如果是应用自动关闭蓝牙进行初始化，则在关闭状态触发后，再次打开
						isAppInitBluetooth = false;
						mBluetoothAdapter.enable();
					}
				}
			} else if (ACTION_SMS.equals(action)) {// 短信
				sendData = "接收到短信";
				printlnMessage(sendData);
				EBikeStatus.setBikeStatus(EBikeStatus.RECEIVE_MESSAGE, 1);

			} else if (ACTION_PHONE.equals(action)) {// 电话
				sendData = "接收到电话";
				printlnMessage(sendData);
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);

				switch (tm.getCallState()) {
				case TelephonyManager.CALL_STATE_RINGING:
					printlnMessage("来电");
					EBikeStatus.setBikeStatus(EBikeStatus.PHONE_CALL, 1);
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					printlnMessage("挂机");
					break;

				case TelephonyManager.CALL_STATE_IDLE:
					printlnMessage("拨打");
					break;
				}
			}
		}
	};

	/**
	 * Establish connection with other divice
	 *
	 * @param data
	 *            An {@link Intent} with
	 *            {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
	 */
	private void connectDevice(String address) {
		stopScanBluetoothDevice();// 链接停止当前扫描
		if (mBlueToothConnction != null) {
			mBlueToothConnction.connect(address);
		}
	}

	/**
	 * Makes this device discoverable.
	 */
	private void ensureDiscoverable(int time) {
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * The Handler that gets information back from the BluetoothChatService
	 */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BluetoothConnection.ACTION_GATT_CONNECTED:
				printlnMessage("链接上蓝牙服务");
				SPUtils.setEBikeAddress(getApplicationContext(),
						mBluetoothDeviceAddress);
				break;
			case BluetoothConnection.ACTION_DATA_AVAILABLE:
				byte[] receiveData = (byte[]) msg.obj;
				printlnMessage("收到数据："
						+ ProtocolTool.bytesToHexString(receiveData));
				HashMap<String, Object> map = ProtocolByteHandler
						.parseData(receiveData);
				broadCastData2UI(map);// 发送解析的数据到UI
				break;
			case BluetoothConnection.ACTION_GATT_DISCONNECTED:
				if (mRequestDataThread != null) {
					mRequestDataThread.cancel();
				}
				printlnMessage("蓝牙服务链接断开");
				break;
			case BluetoothConnection.ACTION_GATT_SERVICES_DISCOVERED:
				printlnMessage("蓝牙服务发现完毕");
				mReceiveCharacteristic = null;
				mSendCharacteristic = null;
				List<BluetoothGattService> gattServices = mBlueToothConnction
						.getSupportedGattServices();
				String uuid = "";// 特征的uuid
				// Loops through available GATT Services.
				for (BluetoothGattService gattService : gattServices) {
					uuid = gattService.getUuid().toString();
					List<BluetoothGattCharacteristic> gattCharacteristics = gattService
							.getCharacteristics();
					if (uuid.contains("ffe0")) {// 监听数据服务
						for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
							uuid = gattCharacteristic.getUuid().toString();
							if (uuid.contains("ffe4")) {// 服务"ffe0"下的"ffe4"特征为打开监听数据特征
								printlnMessage("找到接收数据的特征值了--ffe4");
								mReceiveCharacteristic = gattCharacteristic;
							}
						}
					} else if (uuid.contains("ffe5")) {// 发送数据服务
						for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
							uuid = gattCharacteristic.getUuid().toString();
							if (uuid.contains("ffe9")) {// 服务"ffe5"下的"ffe9"特征为发送特征
								printlnMessage("找到发送数据的特征值了--ffe9");
								mSendCharacteristic = gattCharacteristic;
							}
						}
					}
				}

				if (mReceiveCharacteristic != null
						&& mSendCharacteristic != null) {// 初始化完毕，可以启动心跳包，发送数据了
					LogUtils.i(tag, "服务特征值已找到，现在开启自动发数据线程");
					mBlueToothConnction.setCharacteristicNotification(
							mReceiveCharacteristic, true);
					mRequestDataThread = new RequestDataThread();
					isRequestDataRunning = true;
					mRequestDataThread.start();
				} else {
					mBlueToothConnction
							.setState(BluetoothConnection.STATE_DISCONNECTED);
					toastMessage(getString(R.string.bluetooth_service_init_fail));
					toastMessage(getString(R.string.bluetooth_service_re_init));
					isAppInitBluetooth = true;
					mBluetoothAdapter.disable();
				}
				break;

			default:
				break;

			}
		}
	};

	/**
	 * Sends a message.
	 *
	 * @param message
	 *            A string of text to send.
	 */
	private synchronized void sendData(int commandCode, byte[] data) {
		byte[] send = ProtocolByteHandler.command(commandCode, data);
		// Check that we're actually connected before trying anything
		if (mBlueToothConnction.getState() != BluetoothConnection.STATE_CONNECTED) {
			toastMessage(getString(R.string.bluetooth_not_connect));
			return;
		}
		if (send != null) {
			// Get the message bytes and tell the BluetoothChatService to write
			if (mSendCharacteristic != null) {
				mBlueToothConnction.sendData(send, mSendCharacteristic);
			} else {
				printlnMessage("发送数据的特征值为空！");
			}

		}
	}

	/**
	 * This thread runs while server is connect ,for to get bluetooth's data
	 */
	private class RequestDataThread extends Thread {
		public void run() {
			LogUtils.i(tag, "BEGIN RequestDataThread:");
			setName("RequestDataThread");
			while (isRequestDataRunning&&BluetoothConnection.STATE_CONNECTED==mBlueToothConnction.getState()) {
				printlnMessage("要发送出去的数据进进制值是："
						+ EBikeStatus.getBikeStatus()
						+ " 格式化8位是："
						+ ProtocolTool.byteToBitString(new byte[] { EBikeStatus
								.getBikeStatus() }));
				sendData(CommandCode.SURVEY,
						new byte[] { EBikeStatus.getBikeStatus() });// 获取蓝牙数据
				try {
					Thread.sleep(REQUESTDATA_SPACING);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Reset the ConnectThread because we're done
			synchronized (BlueToothService.this) {
				mRequestDataThread = null;
			}
		}

		public void cancel() {
			isRequestDataRunning = false;
		}
	}

	/**
	 * This thread runs while connect is interrupt attempting to reconnect
	 */
	private class ReConnectThread extends Thread {
		public void run() {
			LogUtils.i(tag, "BEGIN ReConnectThread:");
			setName("ReConnectThread");
			while (isReconnectRunning) {
				// LogUtils.i(tag,
				// "正在跑中"+mBluetoothAdapter+"-"+mBluetoothAdapter.isEnabled()+"--"+mBluetoothAdapter.isDiscovering()+"--"+mBlueToothConnction+"--"+mBlueToothConnction.getState());
				if (mBluetoothAdapter != null
						&& mBluetoothAdapter.isEnabled()
						&& !isScanning
						&& mBlueToothConnction != null
						&& BluetoothConnection.STATE_DISCONNECTED == mBlueToothConnction
								.getState()) {// 可用
					LogUtils.i(tag, "条件符合断线重新链接");
					startScanBluetoothDevice();
				}
				try {
					Thread.sleep(RECONNECT_SPACING);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// Reset the ConnectThread because we're done
			synchronized (BlueToothService.this) {
				mReConnectThread = null;
			}
		}

		public void cancel() {
			isReconnectRunning = false;
		}
	}
}
