package com.freway.ebike.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBNaviStatusListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.adapter.IBTTSPlayer;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteLine;
import com.freway.ebike.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author: mengjiankang created on  2020/11/8
 */
public class BikeMap {

    private static final String TAG = "BikeMap";
    private BitmapDescriptor bdStart = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_start);
    private BitmapDescriptor bdEnd = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_end);
    private BitmapDescriptor bdBike = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_bike);

    private LatLng startPt = new LatLng(39.925283,116.235801);
    private LatLng endPt = new LatLng(39.927884,116.259228);
    private LatLng bikePt = new LatLng(39.926777,116.253874);
    private List<LatLng> points = new ArrayList<>();
    private MapView mapView;
    private BaiduMap baiduMap;

    public BikeMap() {
        init();
    }

    private void init() {
        points.add(startPt);
        points.add(new LatLng(39.925338,116.237166));
        points.add(new LatLng(39.925394,116.238495));
        points.add(new LatLng(39.925394,116.239178));
        points.add(new LatLng(39.925449,116.239789));
        points.add(new LatLng(39.925394,116.242807));
        points.add(new LatLng(39.925421,116.243885));
        points.add(new LatLng(39.925753,116.244712));
        points.add(new LatLng(39.926971,116.24694));
        points.add(new LatLng(39.927745,116.248413));
        points.add(new LatLng(39.927773,116.249239));
        points.add(new LatLng(39.927607,116.250138));
        points.add(new LatLng(39.927275,116.251108));
        points.add(new LatLng(39.926943,116.252222));
        points.add(new LatLng(39.926805,116.253048));
        points.add(new LatLng(39.926722,116.255168));
        points.add(new LatLng(39.927109,116.256929));
        points.add(new LatLng(39.927773,116.258869));
        points.add(endPt);
    }

    public void onCreate(final MapView mapView) {
        Log.d(TAG, "onCreate");
        this.mapView = mapView;
        this.baiduMap = mapView.getMap();
        this.mapView.showZoomControls(false);
//        paintResult();
        paintBiking();
    }

    private void paintResult() {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(startPt).zoom(18);
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        baiduMap.setMyLocationEnabled(false);

        MarkerOptions ooA = new MarkerOptions().position(startPt).icon(bdStart)
                .zIndex(9).draggable(true);
        baiduMap.addOverlay(ooA);

        MarkerOptions ooB = new MarkerOptions().position(endPt).icon(bdEnd)
                .zIndex(5);
        baiduMap.addOverlay(ooB);

        PolylineOptions polylineOptions = new PolylineOptions()
                .points(points)
                .width(6)
                .color(0x99FF0000)
                .zIndex(9);
        baiduMap.addOverlay(polylineOptions);
    }

    private void paintBiking() {
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(bikePt).zoom(18);
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


        MarkerOptions ooA = new MarkerOptions().position(bikePt).icon(bdBike)
                .zIndex(9).draggable(true);
        baiduMap.addOverlay(ooA);

        PolylineOptions polylineOptions = new PolylineOptions()
                .points(points)
                .width(6)
                .color(0x99FF0000)
                .zIndex(9);
        baiduMap.addOverlay(polylineOptions);
    }

    public void onResume() {
        // 在activity执行onResume时必须调用mMapView. onResume ()
        mapView.onResume();
    }

    public void onPause() {
        // 在activity执行onPause时必须调用mMapView. onPause ()
        mapView.onPause();
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mapView.onDestroy();
    }

}
