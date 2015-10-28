/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freway.ebike.map;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.ToastUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class MapService extends Service implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener, OnMapReadyCallback,
		OnCameraChangeListener, OnMyLocationButtonClickListener {
	private final static String TAG = MapService.class.getSimpleName();
	// These settings are the same as the settings for the map. They will in
	// fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5 * 1000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	private final IBinder mBinder = new LocalBinder();
	private GoogleApiClient mGoogleApiClient;
	private CustomerLocationSource mLocationSource;
	private ArrayList<TravelLocation> travelRoute = new ArrayList<TravelLocation>();
	private Location startLocation;// 起始位置
	private Location endLocation;// 终点位置
	private Location paintFromLocation;// 开始画位置
	private Location paintToLocation;// 结束画位置
	private GoogleMap mGoogleMap;
	private boolean isRecord = false;
	private SupportMapFragment mapFragment;
	private float zoom = CAMERA_INIT_ZOOM;
	private final static int CAMERA_INIT_ZOOM = 17;// 初始缩放
	private final static int POLY_LINE_WIDTH = 10;// 线宽
	private Marker startMarker;
	private Marker endMarker;
	private Date startTime;
	private Date endTime;
	private Date calFromTime;
	private Date calToTime;
	private long spendTime;
	private float distance;
	private final static double LAT_OFFSET=0.0012893886;
	private final static double LNG_OFFSET=0.0061154366;
	private final static float RECORD_DISTANCE=5;//两点记录大于RECORD_DISTANCE米则记录
	private MapServiceCallback mMapServiceCallback;
	private boolean isChinaGpsEnable=false;
	public interface MapServiceCallback {
		void mapReady();
		void geocoder(String address);
	}

	public void setMapServiceCallback(MapServiceCallback mapServiceCallback) {
		this.mMapServiceCallback = mapServiceCallback;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.i(TAG, TAG + "onCreate");
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
		mLocationSource = new CustomerLocationSource();
		Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        LogUtils.i(TAG, "当前语言是:"+language);
        if (language.endsWith("zh")){
        	isChinaGpsEnable=true;
        }else{
        	isChinaGpsEnable=false;
        }
		ToastUtils.toast(this, "onCreate");
	}

	public class LocalBinder extends Binder {
		MapService getService() {
			return MapService.this;
		}
	}

	public void setMapFragment(SupportMapFragment mapFragment) {
		this.mapFragment = mapFragment;
		this.mapFragment.getMapAsync(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		LogUtils.i(TAG, TAG + "onBind");
		mGoogleApiClient.connect();
		ToastUtils.toast(this, "onBind");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		LogUtils.i(TAG, TAG + "onUnBind");
		mGoogleApiClient.disconnect();
		ToastUtils.toast(this, "onUnBind");
		return super.onUnbind(intent);
	}

	@Override
	public void onMapReady(GoogleMap map) {
		mGoogleMap = map;
		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.setOnCameraChangeListener(this);
		if(isChinaGpsEnable){
			mGoogleMap.setLocationSource(mLocationSource);
		}else{
			mGoogleMap.setLocationSource(null);
		}
		mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(CAMERA_INIT_ZOOM));
		if (mMapServiceCallback != null) {
			mMapServiceCallback.mapReady();
		}
	}

	@Override
	public boolean onMyLocationButtonClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCameraChange(CameraPosition position) {
//		LogUtils.i(TAG, "zoom=" + position.zoom);
		this.zoom = position.zoom;
	}

	@Override
	public void onLocationChanged(Location location) {
		if(isChinaGpsEnable&&mLocationSource!=null){
			location.setLatitude(location.getLatitude()+LAT_OFFSET);
			location.setLongitude(location.getLongitude()+LNG_OFFSET);
			mLocationSource.onMapChange(location);
		}
//		LogUtils.i(TAG, "locationchange了啦" + location.getLatitude() + "--"
//				+ location.getLongitude());
		// ToastUtils.toast(this,
		// "位置改变"+location.getAltitude()+"--"+location.getLongitude());
//		LatLng latlng = new LatLng(location.getLatitude(),
//				location.getLongitude());
		if (paintFromLocation != null) {
			paintFromLocation = paintToLocation;
			paintToLocation = location;
		} else {
			paintFromLocation = location;
			paintToLocation = location;
			startLocation = paintFromLocation;
		}
		recordPolyLine();
		move();
		if (isRecord) {//正在记录
			TravelLocation travel=new TravelLocation();
			travel.altitude=location.getAltitude();
			travel.latitude=location.getLatitude();
			travel.longitude=location.getLongitude();
			travel.speed=location.getSpeed();
			travelRoute.add(travel);
			distance+=paintFromLocation.distanceTo(paintToLocation);
		}
	}

	
	private void move() {
		if (mGoogleMap != null && paintToLocation != null) {
			LatLng latlng=new LatLng(paintToLocation.getLatitude(), paintToLocation.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
					latlng, this.zoom);
			mGoogleMap.animateCamera(update);
		}
	}

	public Location getCurrentLocation() {
		return mGoogleMap == null ? null : mGoogleMap.getMyLocation();
	}

	private Marker addMark(MarkerOptions options) {
		if (mGoogleMap != null) {
			return mGoogleMap.addMarker(options);
		} else {
			return null;
		}
	}
	
	public void setChinaGpsEnable(boolean enable){
		isChinaGpsEnable=enable;
		if(enable){
			mGoogleMap.setLocationSource(mLocationSource);
		}else{
			mGoogleMap.setLocationSource(null);
		}
	}

	public void geocoderCurrentAddress() {
		String result = "";
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		Location location = mGoogleMap.getMyLocation();
		List<Address> addresses = null;

		try {
			addresses = geocoder.getFromLocation(location.getLatitude(),
					location.getLongitude(),
					1);
		} catch (Exception exception) {
			LogUtils.e(TAG, exception.getMessage());
		}
		if (addresses == null || addresses.size() == 0) {
			LogUtils.i(TAG, "没有解析到地址");
		} else {
			Address address = addresses.get(0);
			ArrayList<String> addressFragments = new ArrayList<String>();
			for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
				addressFragments.add(address.getAddressLine(i));
				result += address.getAddressLine(i);
			}
			LogUtils.i(TAG, "解析到地址是：" + result);
		}
		if(mMapServiceCallback!=null){
			mMapServiceCallback.geocoder(result);
		}
	}

	/** 开始 */
	public void start() {
		isRecord = true;
		startLocation = paintToLocation;
		startTime=Calendar.getInstance().getTime();
		spendTime=0;
		endTime=startTime;
		calFromTime=startTime;
		calToTime=startTime;
		travelRoute.clear();
	}

	/** 暂停 */
	public void pause() {
		isRecord = false;
		if(travelRoute.size()>0){//如果暂停，将最后那一个设置为暂停标识
			travelRoute.get(travelRoute.size()-1).isPause=true;;
		}
		calToTime=Calendar.getInstance().getTime();
		long time=calToTime.getTime()-calFromTime.getTime();
		spendTime+=time;
		
	}

	/** 恢复 */
	public void resume() {
		isRecord = true;
		calFromTime=Calendar.getInstance().getTime();
	}

	/** 完成 */
	public BikeTravel completed() {
		isRecord = false;
		endLocation = paintToLocation;
		endTime=Calendar.getInstance().getTime();
		calToTime=endTime;
		long time=calToTime.getTime()-calFromTime.getTime();
		spendTime+=time;
		BikeTravel travel=new BikeTravel();
		travel.setStartTime(startTime);
		travel.setEndTime(endTime);
		travel.setSpendTime(spendTime);
		travel.setTravelRoute(travelRoute);
		travel.setDistance(distance);
		MarkerOptions optionsStart = new MarkerOptions();
		LatLng startLatLng=new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
		optionsStart
				.position(startLatLng)
				.title("起始位置")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		startMarker = addMark(optionsStart);

		MarkerOptions optionsEnd = new MarkerOptions();
		LatLng endLatLng=new LatLng(endLocation.getLatitude(), endLocation.getLongitude());
		optionsEnd
				.position(endLatLng)
				.title("终点位置")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		endMarker = addMark(optionsEnd);
		return travel;
	}


	/** 截图 */
	public void takePicture() {

	}

	private void recordPolyLine() {
		// ToastUtils.toast(this, "paint");
		if (mGoogleMap != null && isRecord) {
			PolylineOptions poly = new PolylineOptions();
			LatLng from=new LatLng(paintFromLocation.getLatitude(), paintFromLocation.getLongitude());
			LatLng to=new LatLng(paintToLocation.getLatitude(), paintToLocation.getLongitude());
			poly.add(from, to).width(POLY_LINE_WIDTH)
					.color(Color.YELLOW);
			mGoogleMap.addPolyline(poly);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		ToastUtils.toast(this, "onConnected");
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, REQUEST, this); // 位置变化监听
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ToastUtils.toast(this, "onDestroy");
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 用于自定义定位
	 */
	private static class CustomerLocationSource implements LocationSource {
		private OnLocationChangedListener mListener;

		/**
		 * Flag to keep track of the activity's lifecycle. This is not strictly
		 * necessary in this case because onMapLongPress events don't occur
		 * while the activity containing the map is paused but is included to
		 * demonstrate best practices (e.g., if a background service were to be
		 * used).
		 */
		private boolean mPaused;

		@Override
		public void activate(OnLocationChangedListener listener) {
			mListener = listener;
		}

		@Override
		public void deactivate() {
			mListener = null;
		}

		public void onMapChange(Location location) {
			if (mListener != null && !mPaused) {
				mListener.onLocationChanged(location);
			}
		}

		public void onPause() {
			mPaused = true;
		}

		public void onResume() {
			mPaused = false;
		}
	}
}
