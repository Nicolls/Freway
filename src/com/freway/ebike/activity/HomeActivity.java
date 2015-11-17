package com.freway.ebike.activity;

import com.freway.ebike.R;
import com.freway.ebike.bluetooth.BlueToothConstants;
import com.freway.ebike.bluetooth.BlueToothUtil;
import com.freway.ebike.bluetooth.EBikeTravelData;
import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.map.MapUtil;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.utils.LogUtils;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HomeActivity extends HomeUiActivity implements OnClickListener {

	private static final String TAG=HomeActivity.class.getSimpleName();
	private TextView tvMessage;
	private TextView tvBle;
	@Override
	public void dateUpdate(int id, Object obj) {
		

	}
	@Override
	protected void uiInitCompleted() {
		initMapBle();
	}
	
	private void initMapBle() {
		tvBle=(TextView) findViewById(R.id.home_tv_ble);
		tvMessage=(TextView) findViewById(R.id.home_tv_message);
		SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mMapUtil=new MapUtil(this, supportMapFragment);
		mMapUtil.startMapService(travelStateHandler);
		mBlueToothUtil=new BlueToothUtil(this,blueHandler);
		mBlueToothUtil.initBle(updateUiHandler);
	}
	
	/**更新UI*/
	private Handler updateUiHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			//更新UI
			LogUtils.i(TAG, "update ui yes ");
			tvMessage.setText(EBikeTravelData.getInstance(getApplicationContext()).getTravelValueText());
			updateUiValue();
		}
	};
	
	private Handler blueHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
				int state = msg.what;
				bleStateChange(state);
				String text="";
				switch (state) {
				case BlueToothConstants.BLE_STATE_NONE:
					text="ble not init";
					break;
				case BlueToothConstants.BLE_STATE_CONNECTED:
					text="ble connected";
					break;
				case BlueToothConstants.BLE_STATE_CONNECTTING:
					text="ble connectting";
					break;
				case BlueToothConstants.BLE_STATE_DISCONNECTED:
					text="ble disconnected";
					break;
				default:
					break;
				}
				text=text+"----"+BaseApplication.travelState;
				tvBle.setText(text);
		}
		
	};

	@Override
	protected void onResume() {
		super.onResume();
		if(mMapUtil.checkGoogleServiceAvailable(this, 0)){//判断地图服务是否可用
//			ToastUtils.toast(this, "地图服务可用");
		}
		
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.sendQuitAppBroadCast(this);
		mMapUtil.exit();
		mBlueToothUtil.exit();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
