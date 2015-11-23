package com.freway.ebike.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.freway.ebike.R;
import com.freway.ebike.adapter.BleScanAdapter;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.protocol.CommandCode;
import com.freway.ebike.protocol.ProtocolByteHandler;
import com.freway.ebike.protocol.ProtocolTool;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class BlueToothActivity extends BaseActivity {

	private View readyView;
	private View initView;
	private TextView tv_state;
	private EditText etSend;
	private TextView tvSend;
	private TextView tvReceive;
	private TextView tvReceiveFormat;
	private ListView listView;
	private BleScanAdapter adapter;
	private List<BluetoothDevice> data = new ArrayList<BluetoothDevice>();
	private BlueToothUtil mBlueToothUtil;
	private String address;
	private String name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		readyView=findViewById(R.id.ble_ll_ready);
		initView=findViewById(R.id.ble_ll_init);
		readyView.setVisibility(View.GONE);
		initView.setVisibility(View.VISIBLE);
		tv_state=(TextView) findViewById(R.id.ble_tv_state);
		etSend=(EditText) findViewById(R.id.ble_et_send);
		tvSend=(TextView) findViewById(R.id.ble_tv_send);
		tvReceive=(TextView) findViewById(R.id.ble_tv_receive);
		tvReceiveFormat=(TextView) findViewById(R.id.ble_tv_receive_format);
		listView=(ListView) findViewById(R.id.ble_listview);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position<data.size()){
					address= data.get(position).getAddress();
					name=data.get(position).getName();
					tv_state.setText("正在连接到："+name+"--地址："+address);
					mBlueToothUtil.connectBLE(address);
				}
			}
		});
		adapter = new BleScanAdapter(this);
		adapter.setData(data);
		listView.setAdapter(adapter);
		mBlueToothUtil = new BlueToothUtil(this, stateHandler,null);
		mBlueToothUtil.receiveSendData(sendDataHandler);
		name=SPUtils.getEBkieName(this);
		address=SPUtils.getEBkieAddress(this);
	}
	
	private Handler stateHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int state = msg.what;
			String text="";
			switch (state) {
			case BlueToothConstants.BLE_STATE_NONE:
				text="蓝牙未初始化";
				break;
			case BlueToothConstants.BLE_STATE_CONNECTED:
				text="已连接到 "+name;
				break;
			case BlueToothConstants.BLE_STATE_CONNECTTING:
				text="正在连接……";
				break;
			case BlueToothConstants.BLE_STATE_DISCONNECTED:
				text="连接已断开";
				break;
			default:
				break;
			}
			if(state==BlueToothConstants.BLE_STATE_CONNECTED){
				readyView.setVisibility(View.VISIBLE);
				initView.setVisibility(View.GONE);
			}else{
				readyView.setVisibility(View.GONE);
				initView.setVisibility(View.VISIBLE);
			}
			text=text+"----";
			tv_state.setText(text);
		}
		
	};
	
	private Handler searchHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==BlueToothConstants.RESULT_COMPLETED){
				ToastUtils.toast(getApplicationContext(), getString(R.string.ble_scan_compledted));
			}else if(msg.what==BlueToothConstants.RESULT_SUCCESS){
				BluetoothDevice device = (BluetoothDevice) msg.obj;
				if (device!=null) {
					data.add(device);
					adapter.notifyDataSetChanged();
				}
			}
		}

	};

	private Handler sendDataHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ProtocolByteHandler.parseData(BlueToothActivity.this, ProtocolByteHandler.packData);
			tvReceive.setText("收到的数据："+ProtocolTool.bytesToHexString(ProtocolByteHandler.packData));
			tvReceiveFormat.setText("格式化收到的数据："+EBikeTravelData.getInstance(BlueToothActivity.this).getControlValueText());
		}
		
	};
	
	public void onClearReceive(View view){
		tvReceive.setText("收到的数据：");
	}
	
	public void onClearFormat(View view){
		tvReceiveFormat.setText("格式化收到的数据：");
	}
	
	public void onCreateData(View view){//生成发送的数据
		String str=etSend.getText().toString();
		if(!TextUtils.isEmpty(str)){
			byte[]data=CommonUtil._16String2ByteArray(str);
			if(data.length>0){
				int cmd=data[0];
				byte[]sendData=new byte[data.length-1];
				for(int i=0;i<sendData.length;i++){
					sendData[i]=data[i+1];
				}
				byte[] send = ProtocolByteHandler.command(cmd, sendData);
				String tv="发送出去的数据："+ProtocolTool.bytesToHexString(send);
				tvSend.setText(tv);
			}
		}
	}
	public void onSearch(View view){//搜索
		data.clear();
		adapter.notifyDataSetChanged();
		mBlueToothUtil.scanDevice(searchHandler);
	}
	
	
	public void onSend(View view){//发送
		String str=etSend.getText().toString();
		if(!TextUtils.isEmpty(str)){
			byte[]data=CommonUtil._16String2ByteArray(str);
			mBlueToothUtil.sendData(data);
		}
		
	}
	
	

	@Override
	protected void onDestroy() {
		mBlueToothUtil.exit();
		super.onDestroy();
	}
	@Override
	public void dateUpdate(int id, Object obj) {
		
		
	}

}
