package com.freway.ebike.map;

import com.freway.ebike.R;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

public class FrewayMapActivity extends FragmentActivity {
	private static final String TAG = "FrewayMapActivity2";
	private TextView tv;// 消息
	private MapUtil mMapUtil;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_map_travel);
		tv = (TextView) findViewById(R.id.message_text);
		init();
	}

	private void init() {
		SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mMapUtil=new MapUtil(this, supportMapFragment,null);
		mMapUtil.start(handler);
	}
	/**改变状态*/
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case TravelConstant.TRAVEL_STATE_NONE:
				tv.setText("无骑行");
				break;
			case TravelConstant.TRAVEL_STATE_START:
				tv.setText("正在骑行");
				break;
			case TravelConstant.TRAVEL_STATE_PAUSE:
				tv.setText("已暂停骑行");
				break;
			case TravelConstant.TRAVEL_STATE_RESUME:
				tv.setText("已恢复骑行");
				break;
			case TravelConstant.TRAVEL_STATE_COMPLETED:
				tv.setText("行程完成");
				break;
			case TravelConstant.TRAVEL_STATE_STOP:
				tv.setText("用户点击停止，无骑行");
				break;
			case TravelConstant.TRAVEL_STATE_EXIT:
				tv.setText("退出应用");
				break;
			default:
				break;
			}
		}
		
	};
	// 开始
	public void onStart(View view) {
		if (mMapUtil.getReady()) {
			mMapUtil.sendState(TravelConstant.TRAVEL_STATE_START);
		}
	}
	// 暂停
	public void onPause(View view) {
		if (mMapUtil.getReady()) {
			mMapUtil.sendState(TravelConstant.TRAVEL_STATE_PAUSE);
		}
	}
	// 恢复
	public void onResume(View view) {
		if (mMapUtil.getReady()) {
			mMapUtil.sendState(TravelConstant.TRAVEL_STATE_RESUME);
		}
	}
	// 完成
	public void onCompleted(View view) {
		if (mMapUtil.getReady()) {
			mMapUtil.sendState(TravelConstant.TRAVEL_STATE_COMPLETED);
		}
	}
	// 停止
	public void onStop(View view) {
		if (mMapUtil.getReady()) {
			mMapUtil.sendState(TravelConstant.TRAVEL_STATE_STOP);
			mMapUtil.clearMap();
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapUtil.stop();
	}


	@Override
	protected void onResume() {
		super.onResume();
		if(mMapUtil.checkGoogleServiceAvailable(this, 0)){//判断地图服务是否可用
//			ToastUtils.toast(this, "地图服务可用");
		}
		
	}

}
