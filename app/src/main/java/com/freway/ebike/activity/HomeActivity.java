package com.freway.ebike.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapLayer;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.freway.ebike.R;
import com.freway.ebike.bluetooth.BlueToothConstants;
import com.freway.ebike.bluetooth.BlueToothUtil;
import com.freway.ebike.bluetooth.EBikeTravelData;
import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.map.MapUtil;
import com.freway.ebike.map.TravelConstant;

public class HomeActivity extends HomeUiActivity implements OnClickListener {

	private static final String TAG=HomeActivity.class.getSimpleName();
	private TextView tvMessage;
	private TextView tvBle;
	private AlertDialog syncDialog=null;//历史记录

	private LocationClient mLocClient;
	private MapView mMapView;
	private BaiduMap mBaiduMap;

	private BitmapDescriptor bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.battery_pic_day);

	@Override
	protected void uiInitCompleted(View mapContent) {
		initMapBle(mapContent);
	}
	
	private void initMapBle(View mapContent) {
		tvBle=(TextView) mapContent.findViewById(R.id.home_tv_ble);
		tvMessage=(TextView) mapContent.findViewById(R.id.home_tv_message);
		mBlueToothUtil=new BlueToothUtil(this,blueHandler,travelStateHandler,updateUiHandler,syncHandler);
		mBlueToothUtil.initBle(this);

		// 地图初始化
		mMapView = (MapView) mapContent.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mMapUtil = new MapUtil(this,mMapView,mBaiduMap);
		final MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

		RadioGroup group = (RadioGroup)mapContent.findViewById(R.id.radioGroup);
		RadioGroup.OnCheckedChangeListener radioButtonListener = new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.defaulticon) {
					// 传入null则，恢复默认图标
					mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, null));
				}
				if (checkedId == R.id.customicon) {
					int accuracyCircleFillColor = 0xAAFFFF88;
					int accuracyCircleStrokeColor = 0xAA00FF00;
					// 修改为自定义图层
					BitmapDescriptor currentMarker = BitmapDescriptorFactory.fromResource(R.drawable.battery_pic_night);
					mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, currentMarker,
							accuracyCircleFillColor, accuracyCircleStrokeColor));
					currentMarker.recycle();
				}
			}
		};
		group.setOnCheckedChangeListener(radioButtonListener);
		// 定位初始化
		initLocation();
		// 地图定位图标点击事件监听
		mBaiduMap.setOnMyLocationClickListener(new BaiduMap.OnMyLocationClickListener() {
			@Override
			public boolean onMyLocationClick() {
				Toast.makeText(HomeActivity.this,"点击定位图标", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				Toast.makeText(HomeActivity.this,"点击Marker图标", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
	}

	/**
	 * 定位初始化
	 */
	public void initLocation(){
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mMapUtil.registerListener(mLocClient);
		LocationClientOption option = new LocationClientOption();
		// 打开gps
		option.setOpenGps(true);
		// 设置坐标类型
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}


	/**
	 * 切换指定图层的顺序
	 */
	public void switchLayerOrder(View view){
		if (mBaiduMap == null){
			return;
		}
		mBaiduMap.switchLayerOrder(MapLayer.MAP_LAYER_LOCATION, MapLayer.MAP_LAYER_OVERLAY);
	}

	/**
	 * 关闭定位图层点击事件
	 */
	public void setLayerClickable(View view){
		if (mBaiduMap == null){
			return;
		}
		CheckBox checkBox = (CheckBox) view;
		if (checkBox.isChecked()){
			// 设置指定的图层是否可以点击
			mBaiduMap.setLayerClickable(MapLayer.MAP_LAYER_LOCATION,false );
		} else {
			mBaiduMap.setLayerClickable(MapLayer.MAP_LAYER_LOCATION,true );
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时必须调用mMapView. onResume ()
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时必须调用mMapView. onPause ()
		mMapView.onPause();
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
		bitmapA.recycle();
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		// 在activity执行onDestroy时必须调用mMapView.onDestroy()
		mMapView.onDestroy();

		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.setClassLoader(getClassLoader());
		super.onSaveInstanceState(outState);
	}

}
