package com.freway.ebike.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;

public class BLEScanConnectActivity extends BaseActivity implements OnItemClickListener {
	public static final String HANDLE_EXTRA = "HANDLE_EXTRA";
	public static final int HANDLE_CONNECT = 1;
	public static final int HANDLE_SCAN = 2;
	private static final int HANDLE_NOT_FOUND = 3;
	private ListView listView;
	private SimpleAdapter adapter;
	private List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	private BlueToothUtil mBlueToothUtil;
	private Button mBtnScan;
	private Button mBtnManual;
	private Button mBtnConfirm;
	private TextView mTvMessage;
	private TextView mTvTitle;
	private int handle = HANDLE_SCAN;
	private int bleState=BlueToothConstants.BLE_STATE_NONE;
	private String address;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ble_scan_connect);
		address=SPUtils.getEBkieAddress(this);
		handle = getIntent().getIntExtra(HANDLE_EXTRA, HANDLE_CONNECT);
		mBlueToothUtil = new BlueToothUtil(this, blueHandler);
		mBtnScan=(Button) findViewById(R.id.ble_bnt_scan);
		mBtnManual=(Button) findViewById(R.id.ble_bnt_manual);
		mBtnConfirm=(Button) findViewById(R.id.ble_bnt_confirm);
		mTvTitle=(TextView) findViewById(R.id.ble_tv_title);
		mTvMessage=(TextView) findViewById(R.id.ble_tv_message);
		listView = (ListView) findViewById(R.id.bluetooth_listview);
		listView.setOnItemClickListener(this);
		adapter = new SimpleAdapter(this, data, R.layout.item_bluetooth_device,
				new String[] { "name" }, new int[] { R.id.device_title });
		listView.setAdapter(adapter);
		handle("");
	}

	private void handle(String message) {
		mTvMessage.setText(message);
		if (handle == HANDLE_SCAN) {
			mTvTitle.setText("ebikie not bind ,search your ebike...");
			listView.setVisibility(View.VISIBLE);
			mBtnScan.setVisibility(View.GONE);
			mBtnManual.setVisibility(View.GONE);
			mBtnConfirm.setVisibility(View.VISIBLE);
			data.clear();
			mBlueToothUtil.scanDevice(searchHandler);
		}else if(handle==HANDLE_CONNECT){
			mTvTitle.setText("Not been bind ble ,start connectting");
			listView.setVisibility(View.GONE);
			mBtnScan.setVisibility(View.GONE);
			mBtnManual.setVisibility(View.VISIBLE);
			mBtnConfirm.setVisibility(View.VISIBLE);
			mBlueToothUtil.connectBLE(address);
		}else if(handle==HANDLE_NOT_FOUND){
			mTvTitle.setText("your ble not found");
			listView.setVisibility(View.GONE);
			mBtnScan.setVisibility(View.VISIBLE);
			mBtnManual.setVisibility(View.GONE);
			mBtnConfirm.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void dateUpdate(int id, Object obj) {

	}

	/**手动*/
	public void onManual(View view) {
		handle=HANDLE_SCAN;
		handle("");
	}
	/**搜索*/
	public void onScan(View view) {
		handle=HANDLE_SCAN;
		handle("");
	}
	
	/**确认，关闭窗口*/
	public void onConfirm(View view){
		finish();
	}
	private Handler searchHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==BlueToothConstants.RESULT_COMPLETED){
				if(data.size()<=0){
					handle=HANDLE_NOT_FOUND;
					handle("");
				}else{
					ToastUtils.toast(getApplicationContext(), "scan completed");
				}
			}else if(msg.what==BlueToothConstants.RESULT_SUCCESS){
				BluetoothDevice device = (BluetoothDevice) msg.obj;
				LogUtils.i("Blescanconnect", "看我扫到了什么－－"+device.getName());
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("name", device.getName());
				map.put("address", device.getAddress());
				if (device!=null) {
					data.add(map);
					adapter.notifyDataSetChanged();
				}
			}
		}

	};

	private Handler blueHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			bleState= msg.what;
			if(bleState==BlueToothConstants.BLE_STATE_CONNECTED){
				blueHandler.postDelayed(new Runnable() {//2秒之后如果还是链接状态，那就真正链接上了
					
					@Override
					public void run() {
						if(bleState==BlueToothConstants.BLE_STATE_CONNECTED){
							ToastUtils.toast(getApplicationContext(), "ble is connected");
							finish();
						}
					}
				}, 2000);
			}
		}

	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mBlueToothUtil.exit();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(position<data.size()){
			String address = data.get(position).get("address");
			this.address=address;
			handle=HANDLE_CONNECT;
			handle("");
		}
	}

}
