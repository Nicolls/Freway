package com.freway.ebike.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.utils.SPUtils;

public class BlueToothScanActivity extends BaseActivity {

	private TextView tv;
	private ListView listView;
	private SimpleAdapter adapter;
	private List<HashMap<String,String>> data=new ArrayList<HashMap<String,String>>();
	private BlueToothUtil mBlueToothUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_scan);
		super.initCommonView();
		mBlueToothUtil=new BlueToothUtil(this);
		tv=(TextView) findViewById(R.id.bluetooth_tv);
		listView=(ListView) findViewById(R.id.bluetooth_listview);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String sn=data.get(position).get("address");
				SPUtils.setEBikeAddress(getApplicationContext(), sn);
				mBlueToothUtil.handleService(BlueToothScanActivity.this, BlueToothConstants.HANDLE_SERVER_CONNECT, sn);
			}
		});
		adapter=new SimpleAdapter(this, data, R.layout.item_bluetooth_device, new String[]{"name"}, new int[]{R.id.device_title});
		listView.setAdapter(adapter);
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		
		
	}
	public void onSearch(View view){
		mBlueToothUtil.startScanLeb(searchHandler);
	}
	
	private Handler searchHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			HashMap<String,String> map=(HashMap<String, String>) msg.obj;
        	if(adapter!=null){
    			data.add(map);
    			adapter.notifyDataSetChanged();
    		}
		}
		
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mBlueToothUtil.exitScanLeb();
	}
	

}
