package com.freway.ebike.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.freway.ebike.R;
import com.freway.ebike.model.TravelLocation;
import com.freway.ebike.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapUtil  {
	private static final String TAG = MapUtil.class.getSimpleName();
	/** 纠正角度偏差 */
//	private static final double ANGLE_OFFSET = 0.0000007;
	private static final double ANGLE_OFFSET = 0;
	private final static int CAMERA_INIT_ZOOM = 15;// 初始缩放
	private final static int CAMERA_MAX_ZOOM = 18;// 最大缩放
	private final static int POLY_LINE_WIDTH = 3;// 线宽

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// 是否首次定位
	boolean isFirstLoc = true;
	private Marker mMarker;
	private float zoom = CAMERA_INIT_ZOOM;
	private boolean isChinaGps = false;
	private Context context;
	private TravelLocation mLastLocation;
	private BitmapDescriptor bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.battery_pic_day);

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// MapView 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())// 设置定位数据的精度信息，单位：米
					.direction(location.getDirection()) // 此处设置开发者获取到的方向信息，顺时针0-360
					.latitude(location.getLatitude())
					.longitude(location.getLongitude())
					.build();
			// 设置定位数据, 只有先允许定位图层后设置数据才会生效
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatus.Builder builder = new MapStatus.Builder();
				builder.target(latLng).zoom(20.0f);
				mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
				addMarker(latLng);
			}
			if (mMarker != null){
				mMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
			}
		}
	}

	public MapUtil(Context context, MapView mapView,BaiduMap mBaiduMap) {
		this.context = context;
		this.mMapView = mapView;
		this.mBaiduMap = mBaiduMap;
	}

	public void registerListener(LocationClient mLocClient){
		MyLocationListenner myListener = new MyLocationListenner();
		mLocClient.registerLocationListener(myListener);
		init();
	}

	/**
	 * 添加marker
	 *
	 * @param latLng 经纬度
	 */
	public void addMarker(LatLng latLng){
		if (latLng.latitude == 0.0 || latLng.longitude == 0.0){
			return;
		}
		MarkerOptions markerOptionsA = new MarkerOptions().position(latLng).yOffset(30).icon(bitmapA).draggable(true);
		mMarker = (Marker) mBaiduMap.addOverlay(markerOptionsA);
	}


	/** 初始化 */
	private void init() {
		// 判断语言环境
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		// LogUtils.i(TAG, "当前语言是:"+language);
		if (language.endsWith("zh")) {
			isChinaGps = true;
		} else {
			isChinaGps = false;
		}
	}

	/** 清空地图 */
	public void clearMap() {
		if(mBaiduMap!=null){
			mBaiduMap.clear();
		}
	}

	/** 注册广播 */
	public void startMapService() {
		// 注册广播
		IntentFilter filter = new IntentFilter(TravelConstant.ACTION_MAP_SERVICE_LOCATION_CHANGE);
		context.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(TravelConstant.ACTION_MAP_SERVICE_LOCATION_START);
		context.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(TravelConstant.ACTION_MAP_SERVICE_LOCATION_END);
		context.registerReceiver(mReceiver, filter);
		Intent intent = new Intent(context, MapService.class);
		context.startService(intent);
	}

	/** 退出服务 */
	public void exit() {
		context.unregisterReceiver(mReceiver);
	}
	
	/** 停止服务，一般不需要调用 */
	public void stop() {
		exit();
		Intent service = new Intent(context, MapService.class);
		context.stopService(service);
	}


	/** 在地图上画两个点 */
	public void drawPolyLine(TravelLocation paintFromLocation, TravelLocation paintToLocation,
			boolean showSpeedColor) {
			/*PolylineOptions poly = new PolylineOptions();
			LatLng from = new LatLng(paintFromLocation.getLocation().getLatitude() - ANGLE_OFFSET,
					paintFromLocation.getLocation().getLongitude() - ANGLE_OFFSET);
			LatLng to = new LatLng(paintToLocation.getLocation().getLatitude(),
					paintToLocation.getLocation().getLongitude());
			if(paintFromLocation.isPause()){
				poly.add(from, to).width(POLY_LINE_WIDTH).color(Color.parseColor("#9B0000"));
			}else{
				poly.add(from, to).width(POLY_LINE_WIDTH).color(Color.parseColor("#e10019"));
			}
			if(mBaiduMap!=null){
				mBaiduMap
				mGoogleMap.addPolyline(poly);
			}*/
	}
	
	/** 在地图上画两个点 */
//	public Polyline drawPolyLine(LatLng from, LatLng to) {
//		// ToastUtils.toast(this, "paint");
////		if (!paintFromLocation.isPause()) {
//		Polyline polyline=null;
//			PolylineOptions poly = new PolylineOptions();
//			from = new LatLng(from.latitude- ANGLE_OFFSET,
//					from.longitude - ANGLE_OFFSET);
//			to = new LatLng(to.latitude- ANGLE_OFFSET,
//					to.longitude - ANGLE_OFFSET);
//			poly.add(from, to).width(POLY_LINE_WIDTH).color(Color.parseColor("#e10019"));
//			if(mGoogleMap!=null){
//				polyline=mGoogleMap.addPolyline(poly);
//			}
//			return polyline;
////		}
//	}

	/** 完成行程 */
//	public void completedTravel(long travelId) {
//		LogUtils.i(TAG, "travelId==" + travelId);
//		Intent intent = new Intent(context, FrewayHistoryMapActivity.class);
//		intent.putExtra("travelId", travelId);
//		context.startActivity(intent);
//	}

	/** 移动相机 */
	public void moveCamera(TravelLocation location) {
//		if (mGoogleMap != null && location != null) {
//			// LogUtils.i(TAG, "移动相机"+location+"--zoom="+zoom);
//			mLocationSource.changeLocation(location.getLocation());
//			LatLng latlng = new LatLng(location.getLocation().getLatitude(), location.getLocation().getLongitude());
//			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, this.zoom);
//			mGoogleMap.animateCamera(update);
//		}
	}
	
	/**截图*/
	public void snapshot(long travelId,final Handler handler){
//		if(mGoogleMap!=null){
//			mGoogleMap.snapshot(new SnapshotReadyCallback() {
//
//				@Override
//				public void onSnapshotReady(Bitmap arg0) {
//					String name=BaseApplication.travelId+".jpg";
//					String filePath=FileUtils.saveBitmapByUrlOrName(name,arg0);
//					LogUtils.i(TAG, "上传地图缩略图的图片路径为："+filePath);
//					//mark 不为空要在这里把图片上传了。
//					Message msg=Message.obtain();
//					msg.obj=filePath;
//					handler.sendMessage(msg);
//				}
//			});
//		}else{
//			Message msg=Message.obtain();
//			msg.obj="";
//			handler.sendMessage(msg);
//		}
		
	}


//	@Override
//	public void onCameraChange(CameraPosition position) {
//		this.zoom = position.zoom;
//		// LogUtils.i(TAG, "zoom"+zoom);
//	}

	/** 监听MapService发送的广播 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			int state = intent.getIntExtra(TravelConstant.EXTRA_STATE, 0);
			long travelId = intent.getLongExtra(TravelConstant.EXTRA_TRAVEL_ID, 0);
			TravelLocation current = intent.getParcelableExtra(TravelConstant.EXTRA_LOCATION_CURRENT);
			TravelLocation from = intent.getParcelableExtra(TravelConstant.EXTRA_LOCATION_FROM);
			TravelLocation to = intent.getParcelableExtra(TravelConstant.EXTRA_LOCATION_TO);
			mLastLocation=to;
			if (TravelConstant.ACTION_MAP_SERVICE_LOCATION_START.equals(action)) {// 起点
				if (current != null)
					LogUtils.i(TAG,
							"起点：" + current.getLocation().getLatitude() + "--" + current.getLocation().getLongitude());
			} else if (TravelConstant.ACTION_MAP_SERVICE_LOCATION_END.equals(action)) {// 终点
				if (current != null) {
					LogUtils.i(TAG,
							"终点：" + current.getLocation().getLatitude() + "--" + current.getLocation().getLongitude());
//					completedTravel(travelId);
				} else {
					LogUtils.i(TAG, "行程过短，不保存");
				}

			} else if (TravelConstant.ACTION_MAP_SERVICE_LOCATION_CHANGE.equals(action)) {
				if (current != null) {// 移动相机
					moveCamera(current);
				}
				if (from != null && to != null) {// 画图
					// LogUtils.i(TAG, to.getDescription()+"--");
					if(mLastLocation.isPause()){//如果是暂停,把暂停的这条画出来
						drawPolyLine(mLastLocation, from, false);
					}
					drawPolyLine(from, to, false);
				}
			}
		}
	};

//	private OnCameraChangeListener historyCameraChange = new OnCameraChangeListener() {
//
//		@Override
//		public void onCameraChange(CameraPosition arg0) {
//			LogUtils.i(TAG, "相机改变了");
//			if (arg0.zoom > CAMERA_MAX_ZOOM) {
//				mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(CAMERA_MAX_ZOOM));
//			}
//			drawScreenPolygon();
//		}
//	};

	/** 根据travelId显示地图轨迹记录 */
	public void showRoute(long travelId) {
//		if(mGoogleMap!=null){
//			mGoogleMap.clear();
//			// mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
//			Travel travel = DBHelper.getInstance(context).findTravelById(travelId);
//			List<TravelLocation> routes = DBHelper.getInstance(context).listTravelLocation(travelId);
//			LogUtils.i("Freway", "开始时间：" + travel.getStartTime() + "--结束时间：" + travel.getEndTime() + "-花费时间"
//					+ travel.getSpendTime() + "距离：" + travel.getDistance());
//					// List<LatLng> list = new ArrayList<LatLng>();
//
//			// 画一个遮罩层
//			mGoogleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
//
//				@Override
//				public void onMapLoaded() {
//					LogUtils.i(TAG, "地图加载完成了");
//					drawScreenPolygon();
//					mGoogleMap.setOnCameraChangeListener(historyCameraChange);
//				}
//			});
//			// 画轨迹
//			for (int i = 0; i < routes.size(); i++) {
//				TravelLocation travelStart = routes.get(i);
//				if ((i + 1) < routes.size()) {// 有两个点可以画
//					TravelLocation travelEnd = routes.get(i + 1);
//					drawPolyLine(travelStart, travelEnd, true);
//				}
//			}
//			// 画起终点标识
//			if (routes.size() > 1) {
//				markStartEndPoint(routes.get(0), routes.get(routes.size() - 1));
//			}
//			// 移动到包含所有点的位置
//			cameraContainPoint(routes);
//		}
	}

	/** 画起始点跟终点 */
	private void markStartEndPoint(TravelLocation start, TravelLocation end) {
//		if(mGoogleMap!=null){
//			MarkerOptions optionsStart = new MarkerOptions();
//			LatLng startLatLng = new LatLng(start.getLocation().getLatitude(), start.getLocation().getLongitude());
//			optionsStart.position(startLatLng).title("起始位置")
//					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//			mGoogleMap.addMarker(optionsStart);
//			MarkerOptions optionsEnd = new MarkerOptions();
//			LatLng endLatLng = new LatLng(end.getLocation().getLatitude(), end.getLocation().getLongitude());
//			optionsEnd.position(endLatLng).title("终点位置")
//					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//			mGoogleMap.addMarker(optionsEnd);
//		}
	}

	/** 将地图移动到包含所有点的地方 */
	public void cameraContainPoint(List<TravelLocation> travelList) {
		List<LatLng> list = new ArrayList<LatLng>();
		for (TravelLocation l : travelList) {
			list.add(new LatLng(l.getLocation().getLatitude(), l.getLocation().getLongitude()));
		}
//		if (list.size() > 0) {
//			LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
//			for (LatLng latlng : list) {
//				boundsBuilder.include(latlng);
//			}
//			// Move camera to show all markers and locations
//			if(mGoogleMap!=null)
//			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),ScreenUtils.getScreenWidth(context),
//					ScreenUtils.getScreenHeight(context),170));
//		}
	}

	/** 画一个填满地图的polygon */
	private void drawScreenPolygon() {
//		if(mGoogleMap!=null){
//			if (polygon != null) {
//				polygon.remove();
//			}
//			LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
//			LatLng nothEast = bounds.northeast;
//			LatLng southWest = bounds.southwest;
//			LatLng leftTop = new LatLng(nothEast.latitude, southWest.longitude);
//			LatLng rightTop = nothEast;
//			LatLng rightBottom = new LatLng(southWest.latitude, nothEast.longitude);
//			LatLng leftBottom = southWest;
//			PolygonOptions options = new PolygonOptions().add(leftTop, rightTop, rightBottom, leftBottom).strokeWidth(0)
//					.fillColor(Color.argb(100, 0, 0, 0));
//			polygon = mGoogleMap.addPolygon(options);
//		}
	}

	/**
	 * 解析地址
	 * 
	 * @param handler
	 *            用于回调的handler
	 * @param latlng
	 *            要解析的经纬度，如果为空，则解析当前经纬度
	 * 
	 */
	public static void geocoderAddress(Context context, final Handler handler, final LatLng latlng) {
		final Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		final Message msg = Message.obtain();
		new Thread() {

			@Override
			public void run() {
				super.run();
				List<Address> addresses = null;
				String result = "";
				try {
					addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
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
				msg.obj = result;
				handler.sendMessage(msg);
			}

		}.start();
	}


}
