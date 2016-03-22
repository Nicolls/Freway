package com.freway.ebike.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.freway.ebike.R;
import com.freway.ebike.adapter.BleScanAdapter;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class BLEScanConnectActivity extends BaseActivity implements OnItemClickListener {
	public static final String HANDLE_EXTRA = "HANDLE_EXTRA";
	public static final int HANDLE_CONNECT = 1;
	public static final int HANDLE_SCAN = 2;
	private static final int HANDLE_NOT_FOUND = 3;
	private ListView listView;
	private BleScanAdapter adapter;
	private List<BluetoothDevice> data = new ArrayList<BluetoothDevice>();
	private BlueToothUtil mBlueToothUtil;
	private Button mBtnScan;
	private Button mBtnManual;
	private Button mBtnConfirm;
	private TextView mTvMessage;
	private TextView mTvTitle;
	private int handle = HANDLE_SCAN;
	private int bleState=BlueToothConstants.BLE_STATE_NONE;
	private String address;
	private ProgressBar pb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		android.view.WindowManager.LayoutParams p = getWindow().getAttributes(); 
		p.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.5
		p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.9
		getWindow().setAttributes(p);
		setContentView(R.layout.activity_ble_scan_connect);
		
		pb=(ProgressBar) findViewById(R.id.scan_progress);
		address=SPUtils.getEBkieAddress(this);
		handle = getIntent().getIntExtra(HANDLE_EXTRA, HANDLE_CONNECT);
		mBlueToothUtil = new BlueToothUtil(this, blueHandler,null,null);
		mBtnScan=(Button) findViewById(R.id.ble_bnt_scan);
		mBtnManual=(Button) findViewById(R.id.ble_bnt_manual);
		mBtnConfirm=(Button) findViewById(R.id.ble_bnt_confirm);
		mTvTitle=(TextView) findViewById(R.id.ble_tv_title);
		mTvMessage=(TextView) findViewById(R.id.ble_tv_message);
		listView = (ListView) findViewById(R.id.bluetooth_listview);
		listView.setOnItemClickListener(this);
		data.clear();
		adapter = new BleScanAdapter(this);
		adapter.setData(data);
		listView.setAdapter(adapter);
		handle("");
	}

	private void handle(String message) {
		data.clear();
		mTvMessage.setText(message);
		pb.setVisibility(View.VISIBLE);
		if (handle == HANDLE_SCAN) {
			mTvTitle.setText("ebikie not bind ,search your ebike...");
			listView.setVisibility(View.VISIBLE);
			mBtnScan.setVisibility(View.GONE);
			mBtnManual.setVisibility(View.GONE);
			mBtnConfirm.setVisibility(View.VISIBLE);
			mBlueToothUtil.scanDevice(searchHandler);
		}else if(handle==HANDLE_CONNECT){
			mTvTitle.setText("Not been bind ble ,start connectting");
			listView.setVisibility(View.GONE);
			mBtnScan.setVisibility(View.GONE);
			mBtnManual.setVisibility(View.VISIBLE);
			mBtnConfirm.setVisibility(View.VISIBLE);
			mBlueToothUtil.connectBLE(address);
		}else if(handle==HANDLE_NOT_FOUND){
			pb.setVisibility(View.GONE);
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
				pb.setVisibility(View.GONE);
				if(data.size()<=0){
					handle=HANDLE_NOT_FOUND;
					handle("");
				}else{
					ToastUtils.toast(getApplicationContext(), getString(R.string.ble_scan_compledted));
				}
			}else if(msg.what==BlueToothConstants.RESULT_SUCCESS){
				BluetoothDevice device = (BluetoothDevice) msg.obj;
				LogUtils.i("Blescanconnect", "看我扫到了什么－－"+device.getName());
				if (device!=null) {
					boolean isExit=false;
					for(BluetoothDevice d:data){
						if(TextUtils.equals(d.getAddress(), device.getAddress())){
							isExit=true;
							break;
						}
					}
					if(!isExit){
						data.add(device);
						adapter.notifyDataSetChanged();
					}
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
				pb.setVisibility(View.GONE);
				blueHandler.postDelayed(new Runnable() {//2秒之后如果还是链接状态，那就真正链接上了
					
					@Override
					public void run() {
						if(bleState==BlueToothConstants.BLE_STATE_CONNECTED){
							ToastUtils.toast(getApplicationContext(), getString(R.string.ble_connected));
							finish();
						}
					}
				}, 2000);
			}
		}

	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mBlueToothUtil!=null){
			mBlueToothUtil.exit();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(position<data.size()){
			String address = data.get(position).getAddress();
			this.address=address;
			SPUtils.setEBikeName(getApplicationContext(),
					data.get(position).getName());
			handle=HANDLE_CONNECT;
			handle("");
		}
	}

}
