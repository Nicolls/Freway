package com.freway.ebike.bluetooth;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.common.BaseService;
import com.freway.ebike.common.EBConstant;
import com.freway.ebike.db.DBHelper;
import com.freway.ebike.db.Travel;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.protocol.CommandCode;
import com.freway.ebike.protocol.Protocol;
import com.freway.ebike.protocol.ProtocolByteHandler;
import com.freway.ebike.protocol.ProtocolTool;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.JsonUtils;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BlueToothService extends BaseService {
	/**蓝牙状态*/
	public static int ble_state=BlueToothConstants.BLE_STATE_NONE;
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
	private static final int RECONNECT_SPACING = 3 * 1000;// 间隔时间，毫秒
	private boolean isReconnectRunning = true;// 开启断线重新链接
	// 获取数据心跳线程
	private RequestDataThread mRequestDataThread;// 每隔一段时间去获取数据
	private static final int REQUESTDATA_SPACING = 1 * 150;// 间隔时间，毫秒
	private boolean isRequestDataRunning = true;// 开启获取数据
	private boolean isRequestData = true;// 判断是否要获取数据

	// 获取历史数据线程
	private RequestHistoryDataThread mRequestHistoryDataThread;// 每隔一段时间去获取数据
	private static final int REQUESTHISTORYDATA_SPACING = 1 * 1000;// 间隔时间，毫秒
	private boolean isRequestHistoryDataRunning = true;// 开启获取数据
	// bluetooth gat
	private static BluetoothGattCharacteristic mReceiveCharacteristic; // 接收数据
	private static BluetoothGattCharacteristic mSendCharacteristic; // 发送数据

	// 蓝牙是否处于扫描状态
	private boolean isScanning = false;
	// 判断是否是应用初始化蓝牙
	private boolean isAppInitBluetooth = false;

	// 短信广播
	private static final String ACTION_SMS = "android.provider.Telephony.SMS_RECEIVED";
	// 电话广播
	private static final String ACTION_PHONE = TelephonyManager.ACTION_PHONE_STATE_CHANGED;

//	private int state = TravelConstant.TRAVEL_STATE_NONE;// 状态

	/** 监听来自UI的操作链接广播 */
	private final BroadcastReceiver mHandleReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER
					.equals(action)) {
				int handle = intent.getIntExtra(
						BlueToothConstants.EXTRA_HANDLE_TYPE, 0);
				switch (handle) {
				case BlueToothConstants.HANDLE_SERVER_SCAN:// 扫描
					SPUtils.setEBikeAddress(BlueToothService.this, "");// UI发送的重新扫描，则清除保存的设备
					SPUtils.setEBikeName(BlueToothService.this, "");
					mBluetoothDeviceAddress = "";
					mBlueToothConnction.close();
					startScanBluetoothDevice();
					break;
				case BlueToothConstants.HANDLE_SERVER_CONNECT:// 链接
					mBluetoothDeviceAddress = intent
							.getStringExtra(BlueToothConstants.EXTRA_DATA);
					connectDevice(mBluetoothDeviceAddress);
					break;
				case BlueToothConstants.HANDLE_SERVER_DISCONNECT:// 断开链接
					SPUtils.setEBikeAddress(BlueToothService.this, "");// UI发送的重新扫描，则清除保存的设备
					SPUtils.setEBikeName(BlueToothService.this, "");
					disconnect();
					break;
				case BlueToothConstants.HANDLE_SERVER_SYNC:// 同步
					// 默认只有链接上蓝牙才去同步
					if(mBlueToothConnction.getState()==BluetoothConnection.STATE_CONNECTED){
//						syncHistory();//mark  如果想同步历史记录，则打开此处
					}else{//没有连接就去扫描，并连接
						startScanBluetoothDevice();
					}
					break;
				case BlueToothConstants.HANDLE_SERVER_SEND_DATA:// 发送数据
//					simulate();
					Gson gson=new Gson();
					String str=intent.getStringExtra(BlueToothConstants.EXTRA_DATA);
					byte[]data=gson.fromJson(str, byte[].class);
					if(data!=null){
						if(data.length>0){
							int cmd=data[0];
							byte[]sendData=new byte[data.length-1];
							for(int i=0;i<sendData.length;i++){
								sendData[i]=data[i+1];
							}
							if(mBlueToothConnction!=null&&mReceiveCharacteristic!=null){//开启数据监听
								LogUtils.i(tag, "监听数据");
								mBlueToothConnction.setCharacteristicNotification(
										mReceiveCharacteristic, true);
							}
							//mark 加上这个，是因为，如果一设置好监听就发送请求数据包，将得不到数据，所以要做一个延迟处理，设置监听后500毫秒再发送请求数据包
							Message msg=Message.obtain();
							msg.what=cmd;
							msg.obj=sendData;
							sendDataControl.sendMessageDelayed(msg, 500);
						}
					}
					break;
				default:
					break;
				}
			}
		}
	};
	//模拟数据
/*	private void simulate(){
		new Thread(){
			public void run(){
				int m=0;
				while(true&&m<1000){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mHandler.sendEmptyMessage(BluetoothConnection.ACTION_DATA_AVAILABLE);
					m++;
				}
			}
		}.start();
	}*/
	
	private Handler sendDataControl=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			sendData(msg.what,(byte[])msg.obj);
		}
		
	};

	/** 监听UI发送的状态改变广播 */
	private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (TravelConstant.ACTION_UI_SERICE_TRAVEL_STATE_CHANGE
					.equals(action)) {
				if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_START) {// 开始
					if(mRequestHistoryDataThread!=null){
						mRequestHistoryDataThread.cancel();
					}
					startTravel();
				} else if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_PAUSE) {// 暂停
					isRequestData = false;
				} else if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_FAKE_PAUSE) {// 伪暂停
					//不做处理
				}else if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_RESUME) {// 恢复
					isRequestData = true;
				} else if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_COMPLETED) {// 完成
					stopTravel();
				} else if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_STOP) {// 停止
					stopTravel();
				} 
			}else if (TravelConstant.ACTION_UI_SERICE_QUIT_APP
					.equals(action)) {//退出app
				exitTravel();
			}
		}
	};

	/** 开始travel */
	private void startTravel() {
		LogUtils.i(tag, "蓝牙服务中开始travel了");
		if(mReceiveCharacteristic!=null){
			mBlueToothConnction.setCharacteristicNotification(
					mReceiveCharacteristic, true);
		}
		mRequestDataThread = new RequestDataThread();
		isRequestDataRunning = true;
		isRequestData = true;
		mRequestDataThread.start();
	}

	/** 停止travel */
	private void stopTravel() {
		if(mBlueToothConnction!=null&&mReceiveCharacteristic!=null){
			mBlueToothConnction.setCharacteristicNotification(
					mReceiveCharacteristic, false);
		}
		if (mRequestDataThread != null) {
			mRequestDataThread.cancel();
		}
	}

	/** 退出travel */
	private void exitTravel() {
		if (BaseApplication.travelState == BlueToothConstants.BLE_STATE_NONE
				|| BaseApplication.travelState == TravelConstant.TRAVEL_STATE_COMPLETED
				|| BaseApplication.travelState == TravelConstant.TRAVEL_STATE_STOP) {// travel停了
			stopService();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		startService();
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
			sendBleState(BlueToothConstants.BLE_STATE_DISCONNECTED);
		} else {// 已连接
			sendBleState(BlueToothConstants.BLE_STATE_CONNECTED);
		}
		LogUtils.i(tag, "isGoon" + isGoon);

		if (isGoon && stateEnableStep()) {
			// 客户端自动链接
			if (!TextUtils.isEmpty(mBluetoothDeviceAddress)) {
				sendBleState(BlueToothConstants.BLE_STATE_CONNECTTING);
				startScanBluetoothDevice();
			} else {
				sendBleState(BlueToothConstants.BLE_STATE_NONE);
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
		if (mBlueToothConnction != null&&mReceiveCharacteristic!=null) {
			mBlueToothConnction.setCharacteristicNotification(
					mReceiveCharacteristic, false);
		}
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
		unregisterReceiver(mStateReceiver);
	}

	/** 开启服务,如果已经开启，必须调用stopService关闭服务后，才可以再次调用 */
	private void startService() {
		// 注册接收来自UI的广播
		IntentFilter filter = new IntentFilter(
				BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER);
		registerReceiver(mHandleReceiver, filter);
		// 注册广播
		filter = new IntentFilter(
				TravelConstant.ACTION_UI_SERICE_TRAVEL_STATE_CHANGE);
		registerReceiver(mStateReceiver, filter);
		//退出app监听
		filter = new IntentFilter(
				TravelConstant.ACTION_UI_SERICE_QUIT_APP);
		registerReceiver(mStateReceiver, filter);
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

	/** 发送状态 */
	private void sendBleState(int state) {
		ble_state=state;
//		LogUtils.i(tag, "发送蓝牙链接状态"+state);
		Intent intent = new Intent(BlueToothConstants.BLE_SERVER_STATE_CHANAGE);
		intent.putExtra(BlueToothConstants.EXTRA_STATE, state);
		sendBroadcast(intent);
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
	private static final int SCAN_TIME = 7 * 1000;// 扫描时间为7秒

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
					//扫描结束
					broadCastData2UI(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SCAN_DEVICE, BlueToothConstants.RESULT_COMPLETED,null);
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
			printlnMessage("ble-->扫到设备:name:" + device.getName() + "-address:"
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
				if(!TextUtils.isEmpty(device.getAddress())){
					printlnMessage("返回扫描到的设备：" + device.getName());
					broadCastData2UI(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SCAN_DEVICE,BlueToothConstants.RESULT_SUCCESS, device);
				}
			}
		}

	};

	/** 通知UI对应操作的结果 */
	private void broadCastData2UI(String action,int status,Parcelable data) {
		Intent intent = new Intent(action);
		intent.putExtra(BlueToothConstants.EXTRA_STATUS,status);
		intent.putExtra(BlueToothConstants.EXTRA_DATA,data);
		sendBroadcast(intent);
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
					} else {
						sendBleState(BlueToothConstants.BLE_STATE_DISCONNECTED);
					}
				}
			} else if (ACTION_SMS.equals(action)) {// 短信
				sendData = "接收到短信";
				printlnMessage(sendData);
				EBikeStatus.getInstance(context).setBikeStatus(EBikeStatus.RECEIVE_MESSAGE, 1);

			} else if (ACTION_PHONE.equals(action)) {// 电话
				sendData = "接收到电话";
				printlnMessage(sendData);
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Service.TELEPHONY_SERVICE);

				switch (tm.getCallState()) {
				case TelephonyManager.CALL_STATE_RINGING:
					printlnMessage("来电");
					EBikeStatus.getInstance(context).setBikeStatus(EBikeStatus.PHONE_CALL, EBConstant.ON);
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					printlnMessage("挂机");
					EBikeStatus.getInstance(context).setBikeStatus(EBikeStatus.PHONE_CALL, EBConstant.OFF);
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
		if (mBlueToothConnction != null && !TextUtils.isEmpty(address)) {
			mBlueToothConnction.connect(address);
		}else{//为空则，要重新初始化链接
			mBlueToothConnction = new BluetoothConnection(this, mHandler);
			startScanBluetoothDevice();
		}
	}
	/**断开链接*/
	private void disconnect(){
		stopScanBluetoothDevice();// 链接停止当前扫描
		if (mBlueToothConnction != null) {
			mBlueToothConnction.disconnect();
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
				if(mBlueToothConnction!=null&&mReceiveCharacteristic!=null){//开启数据监听
					LogUtils.i(tag, "监听数据");
					mBlueToothConnction.setCharacteristicNotification(
							mReceiveCharacteristic, true);
				}
				sendBleState(BlueToothConstants.BLE_STATE_CONNECTED);
				break;
			case BluetoothConnection.ACTION_DATA_AVAILABLE:
				byte[] receiveData = (byte[]) msg.obj;
//				byte[] receiveData={(byte)0xFE,(byte)0xfe,(byte)0x30,(byte)0x0e,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x25,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x5a,(byte)0xa7,(byte)0xbb};
//				byte[] receiveData={(byte)0xfe,(byte)0xfe,(byte)0x30,(byte)0x0c,(byte)0x45,(byte)0x4f,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x32,(byte)0x19,(byte)0x87,(byte)0xec,(byte)0xbb};

				printlnMessage("收到数据："
						+ ProtocolTool.bytesToHexString(receiveData));
				HashMap<String, Object> map = ProtocolByteHandler
						.parseData(BlueToothService.this,receiveData);
				if(map!=null){//说明本次接收到的数据没有问题
					broadCastData2UI(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SEND_DATA,BlueToothConstants.RESULT_SUCCESS,null);// 提示UI更新   //mark 现改成，发送数据的时候就提示UI更新，因为需要流畅的时间显示
//					if(!isRequestData){//由于最后一次暂停 了，可能是没有发送出去。所以要把最后一次更新到UI上
//						broadCastData2UI(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SEND_DATA,BlueToothConstants.RESULT_SUCCESS,null);
//					}//mark 再次打开上面的广播UI，是因为如果是手动发送数据，就会出现，这次数据返回前，先更新UI，而数据回到这里后，没有更新UI，之前考虑到会耗性能，不过现在觉得一秒内发2，到3次广播，也是可以的。
				}
				break;
			case BluetoothConnection.ACTION_GATT_DISCONNECTED:
				if (mRequestDataThread != null) {
					mRequestDataThread.cancel();
				}
				printlnMessage("蓝牙服务链接断开");
				sendBleState(BlueToothConstants.BLE_STATE_DISCONNECTED);
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
					LogUtils.i(tag, "服务特征值已找到，"+BaseApplication.travelState);
					if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_START
							|| BaseApplication.travelState == TravelConstant.TRAVEL_STATE_RESUME|| BaseApplication.travelState == TravelConstant.TRAVEL_STATE_FAKE_PAUSE) {// 说明在运行中
						startTravel();
					}else if(BaseApplication.travelState != TravelConstant.TRAVEL_STATE_PAUSE){
//						syncHistory();//mark 同步历史数据
					}
				} else {
					mBlueToothConnction
							.setState(BluetoothConnection.STATE_DISCONNECTED);
					toastMessage(getString(R.string.bluetooth_service_init_fail));
					toastMessage(getString(R.string.bluetooth_service_re_init));
					isAppInitBluetooth = true;
					mBluetoothAdapter.disable();
					sendBleState(BlueToothConstants.BLE_STATE_DISCONNECTED);
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
		LogUtils.i(tag, "发送出去的数据包16进制为："+ProtocolTool.bytesToHexString(send));
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

	/** 开始读历史数据 */
	private void syncHistory() {
		if(mReceiveCharacteristic!=null){
			mBlueToothConnction.setCharacteristicNotification(
					mReceiveCharacteristic, true);
		}
		mRequestHistoryDataThread = new RequestHistoryDataThread();
		isRequestHistoryDataRunning = true;
		mRequestHistoryDataThread.start();
	}

	/**
	 * This thread runs while server is connect ,for to get bluetooth's data
	 */
	private class RequestHistoryDataThread extends Thread {
		public void run() {
			LogUtils.i(tag, "BEGIN RequestHistoryDataThread:");
			if (mBlueToothConnction.getState() != BluetoothConnection.STATE_CONNECTED) {
				toastMessage(getString(R.string.bluetooth_not_connect));
				connectDevice(mBluetoothDeviceAddress);
				// state=TravelConstant.TRAVEL_STATE_PAUSE;
				// BaseApplication.sendStateChangeBroadCast(BlueToothService.this,
				// state, false, mStateReceiver);
				return;
			} else {// 先发一个回头祯和一个数据
				EBikeTravelData.getInstance(BlueToothService.this).start(0,TravelConstant.TRAVEL_TYPE_HISTORY);
				sendData(CommandCode.HISTORY,
						new byte[] { EBikeHistoryStatus.setBikeStatus(
								EBikeHistoryStatus.DATA_INDEX, 0) });// 从头
				sendData(CommandCode.HISTORY,
						new byte[] { EBikeHistoryStatus.setBikeStatus(
								EBikeHistoryStatus.DATA_NEXT, 0) });// 下一条
			}
			setName("RequestHistoryDataThread");
			while (isRequestHistoryDataRunning
					&& BluetoothConnection.STATE_CONNECTED == mBlueToothConnction
							.getState()) {
				//先休眠再发送
				try {
					Thread.sleep(REQUESTHISTORYDATA_SPACING);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				printlnMessage("历史要发送出去的数据进进制值是："
						+ EBikeHistoryStatus.getBikeStatus()
						+ " 格式化8位是："
						+ ProtocolTool.byteToBitString(new byte[] { EBikeHistoryStatus
								.getBikeStatus() }));
				if (EBikeTravelData.dataId>0) {//没发完
					LogUtils.i(tag, "记录没有读取完");
					sendData(CommandCode.HISTORY,
							new byte[] { EBikeHistoryStatus.setBikeStatus(
									EBikeHistoryStatus.DATA_NEXT, 0) });// 下一条
				}else{
					LogUtils.i(tag, "记录读取完了");
					isRequestHistoryDataRunning=false;
					sendData(CommandCode.HISTORY,
							new byte[] { EBikeHistoryStatus.setBikeStatus(
									EBikeHistoryStatus.DATA_END, 0) });// 设置为读完了
					break;
				}
			}
			// Reset the ConnectThread because we're done
			synchronized (BlueToothService.this) {
				mRequestHistoryDataThread = null;
			}
		}

		public void cancel() {
			isRequestHistoryDataRunning = false;
		}
	}

	/**
	 * This thread runs while server is connect ,for to get bluetooth's data
	 */
	private class RequestDataThread extends Thread {
		public void run() {
			LogUtils.i(tag, "BEGIN RequestDataThread:");
			if (mBlueToothConnction.getState() != BluetoothConnection.STATE_CONNECTED) {
				toastMessage(getString(R.string.bluetooth_not_connect));
				connectDevice(mBluetoothDeviceAddress);
				// state=TravelConstant.TRAVEL_STATE_PAUSE;
				// BaseApplication.sendStateChangeBroadCast(BlueToothService.this,
				// state, false, mStateReceiver);
				return;
			}
			setName("RequestDataThread");
			while (isRequestDataRunning
					&& BluetoothConnection.STATE_CONNECTED == mBlueToothConnction
							.getState()) {
				printlnMessage("要发送出去的数据进进制值是："
						+ EBikeStatus.getInstance(getApplicationContext()).getBikeStatus()
						+ " 格式化8位是："
						+ ProtocolTool.byteToBitString(new byte[] { EBikeStatus.getInstance(getApplicationContext()).getBikeStatus()}));
				if (isRequestData) {// 如果是暂停，就不需要再发送获取数据的命令了
					broadCastData2UI(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SEND_DATA,BlueToothConstants.RESULT_SUCCESS,null);
					sendData(CommandCode.SURVEY,
							new byte[] { EBikeStatus.getInstance(getApplicationContext()).getBikeStatus()});// 获取蓝牙数据
				}
				try {
					Thread.sleep(REQUESTDATA_SPACING);
				} catch (InterruptedException e) {
					LogUtils.e(tag, e.getMessage());
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
//				 LogUtils.i(tag,
//				 "正在跑中"+mBluetoothAdapter+"-"+mBluetoothAdapter.isEnabled()+"--"+mBluetoothAdapter.isDiscovering()+"--"+mBlueToothConnction+"--"+mBlueToothConnction.getState());
				if (BaseApplication.travelState != TravelConstant.TRAVEL_STATE_NONE
						&& mBluetoothAdapter != null
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
