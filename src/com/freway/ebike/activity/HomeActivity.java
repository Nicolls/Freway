package com.freway.ebike.activity;

import com.freway.ebike.R;
import com.freway.ebike.bluetooth.BlueToothConstants;
import com.freway.ebike.bluetooth.BlueToothUtil;
import com.freway.ebike.bluetooth.EBikeTravelData;
import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.map.MapUtil;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.ToastUtils;
import com.google.android.gms.maps.SupportMapFragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeActivity extends HomeUiActivity implements OnClickListener {

	private static final String TAG=HomeActivity.class.getSimpleName();
	private TextView tvMessage;
	private TextView tvBle;
	private AlertDialog syncDialog=null;//历史记录
	@Override
	protected void uiInitCompleted(View mapContent) {
		initMapBle(mapContent);
	}
	
	private void initMapBle(View mapContent) {
		tvBle=(TextView) mapContent.findViewById(R.id.home_tv_ble);
		tvMessage=(TextView) mapContent.findViewById(R.id.home_tv_message);
		if(CommonUtil.checkGoogleServiceAvailable(this, 100)){
			SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			mMapUtil=new MapUtil(this, supportMapFragment);
			mMapUtil.startMapService();
		}
		mBlueToothUtil=new BlueToothUtil(this,blueHandler,travelStateHandler,updateUiHandler,syncHandler);
		mBlueToothUtil.initBle(this);
	}
	
	/**更新UI*/
	private Handler updateUiHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			//更新UI
			tvMessage.setText(EBikeTravelData.getInstance(getApplicationContext()).getTravelValueText());
			//必须是行程开始后才做UI变更，这里做限制是防止读取历史记录时UI上出现数据变化
			if(BaseApplication.travelState==TravelConstant.TRAVEL_STATE_COMPLETED
					||BaseApplication.travelState==TravelConstant.TRAVEL_STATE_NONE
					||BaseApplication.travelState==TravelConstant.TRAVEL_STATE_STOP){
				return;
			}
			updateUiValue();
		}
	};
	
	/**同步数据*/
	private Handler syncHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
				int state = msg.what;
				switch(state){
				case BlueToothConstants.SYNC_START:
					syncDialog(true);
					break;
				case BlueToothConstants.SYNC_END:
					syncDialog(false);
					break;
				case BlueToothConstants.SYNC_ERROR:
					syncDialog(false);
					break;
					default:
						break;
				}
		}
		
	};
	/**历史记录对话框*/
	private void syncDialog(boolean show){
		if(show){
			AlertDialog.Builder builder=new Builder(this);
			View view=LayoutInflater.from(this).inflate(R.layout.layout_dilog_sync_history, null);
			ProgressBar pb=(ProgressBar) view.findViewById(R.id.sync_progress);
			TextView title=(TextView) view.findViewById(R.id.dialog_title);
			syncDialog=builder.setView(view).create();
			syncDialog.show();
		}else{
			if(syncDialog!=null){
				syncDialog.dismiss();
			}
		}
	}
	
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
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.sendQuitAppBroadCast(this);
		if(mMapUtil!=null){
			mMapUtil.exit();
		}
		if(mBlueToothUtil!=null){
			mBlueToothUtil.exit();
		}
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.setClassLoader(getClassLoader());
		super.onSaveInstanceState(outState);
	}

}
