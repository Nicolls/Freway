package com.freway.ebike.map;

import com.freway.ebike.R;
import com.freway.ebike.bluetooth.BlueToothConstants;
import com.freway.ebike.bluetooth.BlueToothScanActivity;
import com.freway.ebike.bluetooth.BlueToothUtil;
import com.freway.ebike.bluetooth.EBikeStatus;
import com.freway.ebike.bluetooth.EBikeTravelData;
import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.utils.AlertUtil;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.google.android.gms.maps.SupportMapFragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class FrewayMapActivity extends FragmentActivity {
	private static final String TAG = "FrewayMapActivity2";
	private TextView tvMap;// 消息
	private TextView tvBlueToothData;// 消息
	private TextView tvBlueToothState;// 消息
	private MapUtil mMapUtil;
	private BlueToothUtil mBlueToothUtil;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_map_travel);
		tvMap = (TextView) findViewById(R.id.message_map);
		tvBlueToothState = (TextView) findViewById(R.id.message_bluetooth_state);
		tvBlueToothData = (TextView) findViewById(R.id.message_bluetooth_data);
		init();
	}

	private void init() {
		SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mMapUtil=new MapUtil(this, supportMapFragment);
		mMapUtil.startMapService(handler);
		mBlueToothUtil=new BlueToothUtil(this,blueHandler);
		mBlueToothUtil.initBle(updateUiHandler);
	}
	
	/**同步数据*/
	/*private Handler syncHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			//更新UI
			LogUtils.i(TAG, "sync data yes ");
			
		}
	};*/
	
	/**更新UI*/
	private Handler updateUiHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			//更新UI
			LogUtils.i(TAG, "update ui yes ");
			tvBlueToothData.setText(EBikeTravelData.getInstance(getApplicationContext()).getTravelValueText());
		}
	};
	/**改变状态*/
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case TravelConstant.TRAVEL_STATE_START:
				tvMap.setText("traveling");
				break;
			case TravelConstant.TRAVEL_STATE_PAUSE:
				tvMap.setText("pause travel");
				break;
			case TravelConstant.TRAVEL_STATE_RESUME:
				tvMap.setText("resume travel");
				break;
			case TravelConstant.TRAVEL_STATE_COMPLETED:
				tvMap.setText("completed travel");
				break;
			case TravelConstant.TRAVEL_STATE_STOP:
				tvMap.setText("user click stop ,none travel");
				break;
			case TravelConstant.TRAVEL_STATE_EXIT:
				tvMap.setText("exit app");
				break;
			default:
				break;
			}
		}
		
	};
	public void onStart(View view) {
		BaseApplication.sendStateChangeBroadCast(this, TravelConstant.TRAVEL_STATE_START);
	}
	public void onPause(View view) {
		BaseApplication.sendStateChangeBroadCast(this, TravelConstant.TRAVEL_STATE_PAUSE);
	}
	public void onResume(View view) {
		BaseApplication.sendStateChangeBroadCast(this, TravelConstant.TRAVEL_STATE_RESUME);
	}
	public void onCompleted(View view) {
		BaseApplication.sendStateChangeBroadCast(this, TravelConstant.TRAVEL_STATE_COMPLETED);
	}
	public void onStop(View view) {
		BaseApplication.sendStateChangeBroadCast(this, TravelConstant.TRAVEL_STATE_STOP);
	}
	
	
	private Handler blueHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
				int state = msg.what;
				switch (state) {
				case BlueToothConstants.BLE_STATE_NONE:
					tvBlueToothState.setText("ble not init");
					break;
				case BlueToothConstants.BLE_STATE_CONNECTED:
					tvBlueToothState.setText("ble connected");
					break;
				case BlueToothConstants.BLE_STATE_CONNECTTING:
					tvBlueToothState.setText("ble connectting");
					break;
				case BlueToothConstants.BLE_STATE_DISCONNECTED:
					tvBlueToothState.setText("ble disconnected");
					break;
				default:
					break;
				}
		}
		
	};
	
	public void onSport(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(this,EBikeStatus.BIKING_SPORT, flag);
	}
	public void onElc(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(this,EBikeStatus.BIKING_ELEC, flag);
	}
	public void onPower1(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(this,EBikeStatus.BIKING_HELP_POWER_1, flag);
	}
	public void onPower3(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(this,EBikeStatus.BIKING_HELP_POWER_3, flag);
	}
	public void onLightFront(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(this,EBikeStatus.BIKE_FRONT_LIGHT, flag);
	}
	public void onLightBack(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(this,EBikeStatus.BIKE_BACK_LIGHT, flag);
	}
	public void onCall(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(this,EBikeStatus.PHONE_CALL, flag);
	}
	public void onMessage(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(this,EBikeStatus.RECEIVE_MESSAGE, flag);
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.sendStateChangeBroadCast(this, TravelConstant.TRAVEL_STATE_EXIT);
		mMapUtil.exit();
		mBlueToothUtil.exit();
	}


	@Override
	protected void onResume() {
		super.onResume();
		if(mMapUtil.checkGoogleServiceAvailable(this, 0)){//判断地图服务是否可用
//			ToastUtils.toast(this, "地图服务可用");
		}
		
	}

}
