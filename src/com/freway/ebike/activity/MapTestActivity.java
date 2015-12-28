package com.freway.ebike.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.map.MapUtil;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.ToastUtils;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

public class MapTestActivity extends BaseActivity  {

	private static final String TAG=MapTestActivity.class.getSimpleName();
	private MapUtil mMapUtil;
	private Polyline mPolyline;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_map);
		initView();
	}

	private void initView() {
		if(CommonUtil.checkGoogleServiceAvailable(this, 100)){
			SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			mMapUtil=new MapUtil(this, supportMapFragment);
			mMapUtil.startMapService();
		}
	}
	
	public void onAdd(View view){
		ToastUtils.toast(this, "添加了");
		Location location=mMapUtil.getGoogleMap().getMyLocation();
		List<LatLng> list=new ArrayList<LatLng>();
		LatLng latlng=null;
		Random r = new Random ();
		for(int i=0;i<40;i++){
			latlng=new LatLng(location.getLatitude()+0.001f*(r.nextInt(15)+1),location.getLongitude()+0.001f*(r.nextInt(15)+1));
			list.add(latlng);
		}
		
		for(int i=0;i<list.size()-1;i++){
			
			mMapUtil.drawPolyLine(list.get(i), list.get(i+1));
		}
	}
	public void onRemove(View view){
		mPolyline.remove();
	}
	@Override
	public void dateUpdate(int id, Object obj) {
		
	}


}
