package com.freway.ebike.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
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

    /**
     * 初始化定位参数配置
     */

    private void initLocationOption(Context context) {
//定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        LocationClient locationClient = new LocationClient(context);
//声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
//注册监听函数
        locationClient.registerLocationListener(myLocationListener);
//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("gcj02");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
//可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
//可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
//可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
//可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
//可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
//开始定位
        locationClient.start();
    }
    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            location.getSpeed();
            location.getAltitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();

        }
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
