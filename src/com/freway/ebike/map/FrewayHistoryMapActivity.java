/*
 * Copyright (C) 2012 The Android Open Source Project
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
import java.util.List;

import com.freway.ebike.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class FrewayHistoryMapActivity extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mGoogleMap;
	private BikeTravel bikeTravel;
	private final static int POLY_LINE_WIDTH = 10;// 线宽

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_history);
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		bikeTravel = (BikeTravel) getIntent().getSerializableExtra("bikeTravel");
	}

	@Override
	public void onMapReady(GoogleMap map) {
		mGoogleMap = map;
		if (bikeTravel != null) {
			showHistory();
		}
	}

	/** 导入 */
	public void showHistory() {
		List<LatLng> list = new ArrayList<LatLng>();
		PolylineOptions poly = null;
		// 画点
		for (int i = 0; i < bikeTravel.getTravelRoute().size(); i++) {
			TravelLocation travelStart = bikeTravel.getTravelRoute().get(i);
			LatLng start = new LatLng(travelStart.latitude, travelStart.longitude);
			list.add(start);
			if ((i + 1) < bikeTravel.getTravelRoute().size()) {// 有两个点可以画
				TravelLocation travelEnd = bikeTravel.getTravelRoute().get(i + 1);
				LatLng end = new LatLng(travelEnd.latitude, travelEnd.longitude);
				if (mGoogleMap != null && !travelEnd.isPause) {
					poly = new PolylineOptions();
					poly.add(start, end).width(POLY_LINE_WIDTH).color(Color.RED);
					mGoogleMap.addPolyline(poly);
				}
			}
		}
		// 移动到包含所有点的位置
		// Create bounds that include all locations of the map
		LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
		for (LatLng latlng : list) {
			boundsBuilder.include(latlng);
		}
		// Move camera to show all markers and locations
		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),
				getWindow().getWindowManager().getDefaultDisplay().getWidth(),
				getWindow().getWindowManager().getDefaultDisplay().getHeight(), 10));

	
	}

}
