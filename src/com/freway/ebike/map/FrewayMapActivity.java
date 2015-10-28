package com.freway.ebike.map;

import com.freway.ebike.R;
import com.freway.ebike.map.MapService.MapServiceCallback;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.ToastUtils;
import com.google.android.gms.maps.SupportMapFragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FrewayMapActivity extends FragmentActivity implements MapServiceCallback {
	private TextView tv;// 消息
	private MapService mMapService;
	private SupportMapFragment supportMapFragment;
	private boolean isReady=false;
	private BikeTravel bikeTravel;
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mMapService = ((MapService.LocalBinder) service).getService();
			if(supportMapFragment!=null){
				mMapService.setMapFragment(supportMapFragment);
			}
			mMapService.setMapServiceCallback(FrewayMapActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mMapService = null;
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_map_travel);
		tv = (TextView) findViewById(R.id.message_text);
		supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		// 开启服务
		Intent mapServiceIntent = new Intent(getApplicationContext(), MapService.class);
		getApplicationContext().bindService(mapServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	//我的位置
	public void onLocation(View view) {
		if(isReady){
			Location location=mMapService.getCurrentLocation();
			ToastUtils.toast(this,"纬度"+location.getLatitude()+ "精度："+location.getLongitude());
		}
	}
	//开始
	public void onStart(View view) {
		if(isReady){
			mMapService.start();
		}
	}
	//暂停/恢复
	public void onPauseResume(View view) {
		Button btn=(Button) view;
		btn.setSelected(!btn.isSelected());
		if(isReady){
			if(btn.isSelected()){
				btn.setText("pause");
				mMapService.resume();
			}else{
				btn.setText("resume");
				mMapService.pause();
			}
		}
	}
	//完成
	public void onCompleted(View view) {
		if(isReady){
			bikeTravel=mMapService.completed();
			LogUtils.i("Freway", "开始时间："+bikeTravel.getStartTime()+
					"--结束时间："+bikeTravel.getEndTime()+
					"-花费时间"+bikeTravel.getSpendTime()+
					"距离："+bikeTravel.getDistance());
			
			
		}
	}
	//导入
	public void onImport(View view) {
		if(isReady&&bikeTravel!=null){
			Intent intent=new Intent(this, FrewayHistoryMapActivity.class);
			intent.putExtra("bikeTravel", bikeTravel);
			startActivity(intent);
		}
	}
	//截图
	public void onPictrue(View view) {
		mMapService.takePicture();
	}

	@Override
	public void mapReady() {
		isReady=true;
	}

	@Override
	public void geocoder(String address) {
		// TODO Auto-generated method stub
		
	}
}
