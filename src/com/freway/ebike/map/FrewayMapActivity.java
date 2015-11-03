package com.freway.ebike.map;

import com.freway.ebike.R;
import com.freway.ebike.bluetooth.BlueToothConstants;
import com.freway.ebike.bluetooth.BlueToothUtil;
import com.freway.ebike.bluetooth.EBikeTravelData;
import com.freway.ebike.bluetooth.EBikeStatus;
import com.freway.ebike.common.BaseApplication;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class FrewayMapActivity extends FragmentActivity {
	private static final String TAG = "FrewayMapActivity2";
	private TextView tvMap;// 消息
	private TextView tvBlueTooth;// 消息
	private MapUtil mMapUtil;
	private BlueToothUtil mBlueToothUtil;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_map_travel);
		tvMap = (TextView) findViewById(R.id.message_map);
		tvBlueTooth = (TextView) findViewById(R.id.message_bluetooth);
		init();
	}

	private void init() {
		SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mMapUtil=new MapUtil(this, supportMapFragment);
		mMapUtil.startMapService(handler);
		mBlueToothUtil=new BlueToothUtil(this,blueHandler);
		if(EBikeTravelData.travel_state==TravelConstant.TRAVEL_STATE_STOP||EBikeTravelData.travel_state==TravelConstant.TRAVEL_STATE_COMPLETED||EBikeTravelData.travel_state==TravelConstant.TRAVEL_STATE_NONE){
			//开始同步
			mBlueToothUtil.handleSyncData(new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);//在这里去链接发送数据
					if(msg.what==1){
						mBlueToothUtil.handleSendData(new Handler(){

							@Override
							public void handleMessage(Message msg) {
								super.handleMessage(msg);//在这里更新UI
								
								
							}
							
						});
					}
					
				}
			});
		}
	}
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
					tvMap.setText("ble not init");
					break;
				case BlueToothConstants.BLE_STATE_CONNECTED:
					tvMap.setText("ble connected");
					break;
				case BlueToothConstants.BLE_STATE_CONNECTTING:
					tvMap.setText("ble connectting");
					break;
				case BlueToothConstants.BLE_STATE_DISCONNECTED:
					tvMap.setText("ble disconnected");
					break;
				default:
					break;
				}
		}
		
	};
	
	public void onSport(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(EBikeStatus.BIKING_SPORT, flag);
	}
	public void onElc(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(EBikeStatus.BIKING_ELEC, flag);
	}
	public void onPower1(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(EBikeStatus.BIKING_HELP_POWER_1, flag);
	}
	public void onPower3(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(EBikeStatus.BIKING_HELP_POWER_3, flag);
	}
	public void onLightFront(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(EBikeStatus.BIKE_FRONT_LIGHT, flag);
	}
	public void onLightBack(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(EBikeStatus.BIKE_BACK_LIGHT, flag);
	}
	public void onCall(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(EBikeStatus.PHONE_CALL, flag);
	}
	public void onMessage(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		mBlueToothUtil.setBikeState(EBikeStatus.RECEIVE_MESSAGE, flag);
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
