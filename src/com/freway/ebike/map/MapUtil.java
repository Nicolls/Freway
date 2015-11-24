package com.freway.ebike.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.db.DBHelper;
import com.freway.ebike.db.Travel;
import com.freway.ebike.db.TravelLocation;
import com.freway.ebike.utils.FileUtils;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.NetUtil;
import com.freway.ebike.utils.ScreenUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapUtil implements OnCameraChangeListener {
	private static final String TAG = MapUtil.class.getSimpleName();
	/** 国内定位纬度偏差 */
	private static final double LAT_OFFSET = 0.0012893886;
	/** 国内定位精度偏差 */
	private static final double LNG_OFFSET = 0.0061154366;
	/** 纠正角度偏差 */
//	private static final double ANGLE_OFFSET = 0.0000007;
	private static final double ANGLE_OFFSET = 0;
	private final static int CAMERA_INIT_ZOOM = 15;// 初始缩放
	private final static int CAMERA_MAX_ZOOM = 18;// 最大缩放
	private final static int POLY_LINE_WIDTH = 3;// 线宽
	private GoogleMap mGoogleMap;
	private float zoom = CAMERA_INIT_ZOOM;
	private boolean isChinaGps = false;
	private Context context;
	private Polygon polygon;
	private TravelLocation mLastLocation;
	public MapUtil(Context context, SupportMapFragment supportMapFragment) {
		this.context = context;
		init(supportMapFragment);
	}

	/** 初始化 */
	private void init(SupportMapFragment supportMapFragment) {
		// 判断语言环境
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		// LogUtils.i(TAG, "当前语言是:"+language);
		if (language.endsWith("zh")) {
			isChinaGps = true;
		} else {
			isChinaGps = false;
		}
		mLocationSource = new CustomerLocationSource();
		this.mGoogleMap=supportMapFragment.getMap();
		mGoogleMap.setMyLocationEnabled(true);
		mGoogleMap.setOnCameraChangeListener(this);
		mGoogleMap.setLocationSource(mLocationSource);
		mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(CAMERA_INIT_ZOOM));
	}

	/** 清空地图 */
	public void clearMap() {
		mGoogleMap.clear();
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

	/** 重新格式化位置为国内坐标 */
	private TravelLocation formatLocationWithChina(TravelLocation location) {
		if (location != null && isChinaGps) {
			location.getLocation().setLatitude(location.getLocation().getLatitude() + LAT_OFFSET);
			location.getLocation().setLongitude(location.getLocation().getLongitude() + LNG_OFFSET);
		}
		return location;
	}

	/** 在地图上画两个点 */
	public void drawPolyLine(GoogleMap map, TravelLocation paintFromLocation, TravelLocation paintToLocation,
			boolean showSpeedColor) {
		// ToastUtils.toast(this, "paint");
//		if (!paintFromLocation.isPause()) {
			PolylineOptions poly = new PolylineOptions();
			LatLng from = new LatLng(paintFromLocation.getLocation().getLatitude() - ANGLE_OFFSET,
					paintFromLocation.getLocation().getLongitude() - ANGLE_OFFSET);
			LatLng to = new LatLng(paintToLocation.getLocation().getLatitude(),
					paintToLocation.getLocation().getLongitude());
			if(paintFromLocation.isPause()){
				poly.add(from, to).width(POLY_LINE_WIDTH).color(Color.parseColor("#242b35"));
			}else{
				poly.add(from, to).width(POLY_LINE_WIDTH).color(Color.parseColor("#e10019"));
			}
			map.addPolyline(poly);
//		}
	}

	/** 完成行程 */
	public void completedTravel(long travelId) {
		LogUtils.i(TAG, "travelId==" + travelId);
		Intent intent = new Intent(context, FrewayHistoryMapActivity.class);
		intent.putExtra("travelId", travelId);
		context.startActivity(intent);
	}

	/** 移动相机 */
	public void moveCamera(TravelLocation location) {
		if (mGoogleMap != null && location != null) {
			// LogUtils.i(TAG, "移动相机"+location+"--zoom="+zoom);
			mLocationSource.changeLocation(location.getLocation());
			LatLng latlng = new LatLng(location.getLocation().getLatitude(), location.getLocation().getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng, this.zoom);
			mGoogleMap.animateCamera(update);
		}
	}
	
	/**截图*/
	public void snapshot(final Handler handler){
		if(mGoogleMap!=null){
			mGoogleMap.snapshot(new SnapshotReadyCallback() {
				
				@Override
				public void onSnapshotReady(Bitmap arg0) {
					String name=BaseApplication.travelId+".jpg";
					String filePath=FileUtils.saveBitmapByUrlOrName(name,arg0);
					if(!TextUtils.isEmpty(filePath)){
						LogUtils.i(TAG, "上传地图缩略图的图片路径为："+filePath);
						//mark 不为空要在这里把图片上传了。
						Message msg=Message.obtain();
						msg.obj=filePath;
						handler.sendMessage(msg);
					}
				}
			});
		}
	}
	/** 自定义定位 */
	private CustomerLocationSource mLocationSource = new CustomerLocationSource();

	@Override
	public void onCameraChange(CameraPosition position) {
		this.zoom = position.zoom;
		// LogUtils.i(TAG, "zoom"+zoom);
	}

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
					completedTravel(travelId);
				} else {
					LogUtils.i(TAG, "行程过短，不保存");
				}

			} else if (TravelConstant.ACTION_MAP_SERVICE_LOCATION_CHANGE.equals(action)) {
				if (current != null) {// 移动相机
					current = formatLocationWithChina(current);
					moveCamera(current);
				}
				if (from != null && to != null) {// 画图
					// LogUtils.i(TAG, to.getDescription()+"--");
					from = formatLocationWithChina(from);
					to = formatLocationWithChina(to);
					if(mLastLocation.isPause()){//如果是暂停,把暂停的这条画出来
						drawPolyLine(mGoogleMap, mLastLocation, from, false);
					}
					drawPolyLine(mGoogleMap, from, to, false);
				}
			}
		}
	};

	private OnCameraChangeListener historyCameraChange = new OnCameraChangeListener() {

		@Override
		public void onCameraChange(CameraPosition arg0) {
			LogUtils.i(TAG, "相机发言了");
			if (arg0.zoom > CAMERA_MAX_ZOOM) {
				mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(CAMERA_MAX_ZOOM));
			}
			drawScreenPolygon();
		}
	};

	/** 根据travelId显示地图轨迹记录 */
	public void showRoute(long travelId) {
		mGoogleMap.clear();
		// mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
		Travel travel = DBHelper.getInstance(context).findTravelById(travelId);
		List<TravelLocation> routes = DBHelper.getInstance(context).listTravelLocation(travelId);
		for (TravelLocation location : routes) {
			location = formatLocationWithChina(location);
		}
		LogUtils.i("Freway", "开始时间：" + travel.getStartTime() + "--结束时间：" + travel.getEndTime() + "-花费时间"
				+ travel.getSpendTime() + "距离：" + travel.getDistance());
				// List<LatLng> list = new ArrayList<LatLng>();

		// 画一个遮罩层
		mGoogleMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {

			@Override
			public void onMapLoaded() {
				LogUtils.i(TAG, "地图发言了");
				drawScreenPolygon();
				mGoogleMap.setOnCameraChangeListener(historyCameraChange);
			}
		});
		// 画轨迹
		for (int i = 0; i < routes.size(); i++) {
			TravelLocation travelStart = routes.get(i);
			if ((i + 1) < routes.size()) {// 有两个点可以画
				TravelLocation travelEnd = routes.get(i + 1);
				drawPolyLine(mGoogleMap, travelStart, travelEnd, true);
			}
		}
		// 画起终点标识
		if (routes.size() > 1) {
			markStartEndPoint(routes.get(0), routes.get(routes.size() - 1));
		}
		// 移动到包含所有点的位置
		cameraContainPoint(routes);
	}

	/** 画起始点跟终点 */
	private void markStartEndPoint(TravelLocation start, TravelLocation end) {
		MarkerOptions optionsStart = new MarkerOptions();
		LatLng startLatLng = new LatLng(start.getLocation().getLatitude(), start.getLocation().getLongitude());
		optionsStart.position(startLatLng).title("起始位置")
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		mGoogleMap.addMarker(optionsStart);
		MarkerOptions optionsEnd = new MarkerOptions();
		LatLng endLatLng = new LatLng(end.getLocation().getLatitude(), end.getLocation().getLongitude());
		optionsEnd.position(endLatLng).title("终点位置")
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		mGoogleMap.addMarker(optionsEnd);
	}

	/** 将地图移动到包含所有点的地方 */
	private void cameraContainPoint(List<TravelLocation> travelList) {
		List<LatLng> list = new ArrayList<LatLng>();
		for (TravelLocation l : travelList) {
			list.add(new LatLng(l.getLocation().getLatitude(), l.getLocation().getLongitude()));
		}
		if (list.size() > 0) {
			LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
			for (LatLng latlng : list) {
				boundsBuilder.include(latlng);
			}
			// Move camera to show all markers and locations
			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(),ScreenUtils.getScreenWidth(context),
					ScreenUtils.getScreenHeight(context),50));
		}
	}

	/** 画一个填满地图的polygon */
	private void drawScreenPolygon() {
		if (polygon != null) {
			polygon.remove();
		}
		LatLngBounds bounds = mGoogleMap.getProjection().getVisibleRegion().latLngBounds;
		LatLng nothEast = bounds.northeast;
		LatLng southWest = bounds.southwest;
		LatLng leftTop = new LatLng(nothEast.latitude, southWest.longitude);
		LatLng rightTop = nothEast;
		LatLng rightBottom = new LatLng(southWest.latitude, nothEast.longitude);
		LatLng leftBottom = southWest;
		PolygonOptions options = new PolygonOptions().add(leftTop, rightTop, rightBottom, leftBottom).strokeWidth(0)
				.fillColor(Color.argb(100, 0, 0, 0));
		polygon = mGoogleMap.addPolygon(options);
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

	public void setChinaGps(boolean isChinaGps) {
		this.isChinaGps = isChinaGps;
	}

}
