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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.freway.ebike.bluetooth.BlueToothConstants;
import com.freway.ebike.bluetooth.EBikeTravelData;
import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.db.DBHelper;
import com.freway.ebike.db.Travel;
import com.freway.ebike.db.TravelLocation;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

public class MapService extends Service implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener {
	private final static String TAG = MapService.class.getSimpleName();
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5 * 1000) // 5
									// seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	private GoogleApiClient mGoogleApiClient;
	private static final float RECORD_MIN_DISTANCE = 2;// 达到记录的最短距离2米
	private TravelLocation fromLocation;// 开始画位置
	private TravelLocation toLocation;// 结束画位置
	private boolean isRecord = false;
	private TravelLocation currentLocation;// 当前的位置

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.i(TAG, TAG + "onCreate");
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
		// 注册广播
		IntentFilter filter = new IntentFilter(
				TravelConstant.ACTION_UI_SERICE_TRAVEL_STATE_CHANGE);
		registerReceiver(mStateReceiver, filter);
		filter = new IntentFilter(BlueToothConstants.BLE_SERVER_STATE_CHANAGE);
		registerReceiver(mBleStateReceiver, filter);
		ToastUtils.toast(this, "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.i(TAG, "onStartCommand");
		ToastUtils.toast(this, "onStartCommand");
		mGoogleApiClient.connect();
		initData();
		return super.onStartCommand(intent, flags, startId);
	}

	/** 初始化数据,初始化完成，要把状态返回到UI， */
	private void initData() {
		if (EBikeTravelData.travel_id > 0) {
			if (EBikeTravelData.travel_state == TravelConstant.TRAVEL_STATE_START
					|| EBikeTravelData.travel_state == TravelConstant.TRAVEL_STATE_RESUME) {
				isRecord = true;
			} else {
				isRecord = false;
			}
			List<TravelLocation> routes = DBHelper.getInstance(this)
					.listTravelLocation(EBikeTravelData.travel_id);
			for (int i = 0; i < routes.size(); i++) {
				TravelLocation from = routes.get(i);
				TravelLocation to = null;
				if ((i + 1) < routes.size()) {
					to = routes.get(i + 1);
					// to.setDescription("这是我初始化从数据库拿的");
					broadCastLocation(
							TravelConstant.ACTION_MAP_SERVICE_LOCATION_CHANGE,
							toLocation, from, to);
				}
			}
		}
	}

	/** 监听UI发送的广播 */
	private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (TravelConstant.ACTION_UI_SERICE_TRAVEL_STATE_CHANGE
					.equals(action)) {
				int state = intent.getIntExtra(TravelConstant.EXTRA_STATE, 0);
				if (state == TravelConstant.TRAVEL_STATE_START) {// 开始
					start();
				} else if (state == TravelConstant.TRAVEL_STATE_PAUSE) {// 暂停
					pause();
				} else if (state == TravelConstant.TRAVEL_STATE_RESUME) {// 恢复
					resume();
				} else if (state == TravelConstant.TRAVEL_STATE_COMPLETED) {// 完成
					completed();
				} else if (state == TravelConstant.TRAVEL_STATE_STOP) {// 停止
					stop();
				} else if (state == TravelConstant.TRAVEL_STATE_EXIT) {// 退出应用
					exit();
				}
			}
		}
	};

	/**
	 * The BroadcastReceiver that listens for discovered devices and changes the
	 * title when discovery is finished
	 */
	private final BroadcastReceiver mBleStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BlueToothConstants.BLE_SERVER_STATE_CHANAGE.equals(action)) {
				int bleState = intent.getIntExtra(BlueToothConstants.EXTRA_STATE,
						0);
				switch (bleState) {
				case BlueToothConstants.BLE_STATE_NONE:

					break;
				case BlueToothConstants.BLE_STATE_CONNECTED:
					LogUtils.i(TAG, "map service BLE_STATE_CONNECTED "+EBikeTravelData.travel_state);
					if (EBikeTravelData.travel_state == TravelConstant.TRAVEL_STATE_PAUSE) {
						BaseApplication.sendStateChangeBroadCast(context, TravelConstant.TRAVEL_STATE_RESUME);
					}
					break;
				case BlueToothConstants.BLE_STATE_CONNECTTING:

					break;
				case BlueToothConstants.BLE_STATE_DISCONNECTED:
					if (EBikeTravelData.travel_state == TravelConstant.TRAVEL_STATE_START
							|| EBikeTravelData.travel_state == TravelConstant.TRAVEL_STATE_RESUME) {
						BaseApplication.sendStateChangeBroadCast(context, TravelConstant.TRAVEL_STATE_PAUSE);
					}
					break;
				default:
					break;
				}
			}
		}
	};

	@Override
	public void onLocationChanged(Location location) {
//		LogUtils.i(TAG, "onLocationChanged" + location.getLatitude() + "--"
//				+ location.getLongitude());
		TravelLocation travelLocation = new TravelLocation(location);
		travelLocation.setTravelId(EBikeTravelData.travel_id);
		travelLocation.setSpeed(EBikeTravelData.travel_insSpeed);
		travelLocation.setAltitude(location.getAltitude());
		float pointDistance = 0;
		if (fromLocation != null) {
			pointDistance = fromLocation.getLocation().distanceTo(location);
		}
		broadCastLocation(TravelConstant.ACTION_MAP_SERVICE_LOCATION_CHANGE,
				travelLocation, null, null);// 当前位置
		if (fromLocation != null && pointDistance > RECORD_MIN_DISTANCE) {// 当两点距离值大于最小记录距离值，才算是发生距离改变
			fromLocation = toLocation;
			toLocation = travelLocation;
		} else {
			fromLocation = travelLocation;
			toLocation = travelLocation;
		}
		if (isRecord && pointDistance > RECORD_MIN_DISTANCE) {// 正在记录，并且两点的距离必须要大于最小记录距离值才记录
			EBikeTravelData.travel_altitude = travelLocation.getLocation()
					.getAltitude();
			// 判断是开始的位置
			if (currentLocation == null) {// 如果是开始，则通知行程开始
				broadCastLocation(
						TravelConstant.ACTION_MAP_SERVICE_LOCATION_START,
						travelLocation, null, null);// 当前位置
			}
			currentLocation = travelLocation;
			// 向UI发送位置改变，画数据
			broadCastLocation(
					TravelConstant.ACTION_MAP_SERVICE_LOCATION_CHANGE,
					travelLocation, fromLocation, toLocation);// 当前位置
			// 记录数据
			travelLocation.setTravelId(EBikeTravelData.travel_id);
			DBHelper.getInstance(this).insertTravelLocation(currentLocation);
		}
	}

	/** 广播地址变化 */
	private void broadCastLocation(String action, TravelLocation current,
			TravelLocation from, TravelLocation to) {
		Intent intent = new Intent(action);
		intent.putExtra(TravelConstant.EXTRA_LOCATION_CURRENT, current);
		intent.putExtra(TravelConstant.EXTRA_LOCATION_FROM, from);
		intent.putExtra(TravelConstant.EXTRA_LOCATION_TO, to);
		sendBroadcast(intent);
	}

	/** 开始 */
	public void start() {
		fromLocation = null;
		toLocation = null;
		currentLocation = null;
		isRecord = true;
		LogUtils.i(TAG, "行程ID－－" + EBikeTravelData.travel_id);
	}

	/** 暂停 */
	public void pause() {
		isRecord = false;
		if (currentLocation != null) {
			currentLocation.setPause(true);
			DBHelper.getInstance(this).updateTravelLocation(currentLocation);
		}
	}

	/** 恢复 */
	public void resume() {
		isRecord = true;
	}

	/** 完成 */
	public void completed() {
		isRecord = false;
	}

	/** 停止 */
	public void stop() {
		isRecord = false;
		fromLocation = null;
		toLocation = null;
	}

	/** 退出应用 */
	public void exit() {
		if (EBikeTravelData.travel_state == TravelConstant.TRAVEL_STATE_NONE
				|| EBikeTravelData.travel_state == TravelConstant.TRAVEL_STATE_STOP
				|| EBikeTravelData.travel_state == TravelConstant.TRAVEL_STATE_COMPLETED) {// 这些情况将不需要记录并且开启定位功能了
			mGoogleApiClient.disconnect();
			unregisterReceiver(mStateReceiver);
			unregisterReceiver(mBleStateReceiver);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		LogUtils.i(TAG, "onConnectionFailed");
	}

	@Override
	public void onConnected(Bundle arg0) {
		ToastUtils.toast(this, "onConnected");
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, REQUEST, this); // 位置变化监听
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mGoogleApiClient.disconnect();
		unregisterReceiver(mStateReceiver);
		unregisterReceiver(mBleStateReceiver);
		LogUtils.i(TAG, "onDestroy");
		ToastUtils.toast(this, "onDestroy");
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		LogUtils.i(TAG, "onConnectionSuspended");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
