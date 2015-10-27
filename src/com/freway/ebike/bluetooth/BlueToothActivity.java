package com.freway.ebike.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.protocol.CommandCode;
import com.freway.ebike.protocol.ProtocolByteHandler;
import com.freway.ebike.utils.SPUtils;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

	private TextView tv;
	private ListView listView;
	private SimpleAdapter adapter;
	private List<HashMap<String,String>> data=new ArrayList<HashMap<String,String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		super.initCommonView();
		tv=(TextView) findViewById(R.id.bluetooth_tv);
		listView=(ListView) findViewById(R.id.bluetooth_listview);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String sn=data.get(position).get("address");
				SPUtils.setEBikeAddress(getApplicationContext(), sn);
				BlueToothService.handle(BlueToothActivity.this,BlueToothConstants.HANDLE_SERVER_CONNECT,sn);
			}
		});
		adapter=new SimpleAdapter(this, data, R.layout.item_bluetooth_device, new String[]{"name"}, new int[]{R.id.device_title});
		listView.setAdapter(adapter);
        IntentFilter filter = new IntentFilter(BlueToothConstants.BLUETOOTH_ACTION_SERVER_SCAN_RESULT);
        registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BlueToothConstants.BLUETOOTH_ACTION_SERVER_SEND_RESULT);
        registerReceiver(mReceiver, filter);
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		
		
	}
	public void onSport(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		EBikeStatus.setBikeStatus(EBikeStatus.BIKING_SPORT, flag);
	}
	public void onElc(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		EBikeStatus.setBikeStatus(EBikeStatus.BIKING_ELEC, flag);
	}
	public void onPower1(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		EBikeStatus.setBikeStatus(EBikeStatus.BIKING_HELP_POWER_1, flag);
	}
	public void onPower3(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		EBikeStatus.setBikeStatus(EBikeStatus.BIKING_HELP_POWER_3, flag);
	}
	public void onLightFront(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		EBikeStatus.setBikeStatus(EBikeStatus.BIKE_FRONT_LIGHT, flag);
	}
	public void onLightBack(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		EBikeStatus.setBikeStatus(EBikeStatus.BIKE_BACK_LIGHT, flag);
	}
	public void onCall(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		EBikeStatus.setBikeStatus(EBikeStatus.PHONE_CALL, flag);
	}
	public void onMessage(View view){
		CheckBox cb=(CheckBox) view;
		int flag=cb.isChecked()?1:0;
		EBikeStatus.setBikeStatus(EBikeStatus.RECEIVE_MESSAGE, flag);
	}
	
	
	 /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BlueToothConstants.BLUETOOTH_ACTION_SERVER_SCAN_RESULT.equals(action)) {
            	String name="";
            	String address="";
            	if(intent.hasExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DEVICE)){
            		BluetoothDevice device=(BluetoothDevice)intent.getParcelableExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DEVICE);
            		name=device.getName()+"--"+device.getAddress();
            		address=device.getAddress();
            	}
            	if(adapter!=null){
        			HashMap<String,String> map=new HashMap<String, String>();
        			map.put("name",name );
        			map.put("address",address);
        			data.add(map);
        			adapter.notifyDataSetChanged();
        		}
            }else if(BlueToothConstants.BLUETOOTH_ACTION_SERVER_SEND_RESULT.equals(action)){//when send data come back
            	EBikeData ebike=null;
            	int cmd=0;
            	if(intent.hasExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DATA)){
            		HashMap<String,Object> data=(HashMap<String, Object>) intent.getSerializableExtra(BlueToothConstants.BLUETOOTH_SERVER_EXTRA_DATA);
            		cmd=(int) data.get(ProtocolByteHandler.EXTRA_CMD);
            		ebike=(EBikeData) data.get(ProtocolByteHandler.EXTRA_DATA);
            	}
            	if(ebike!=null){
            		tv.setText("指令："+cmd+"-数据："+ebike.getValueText());
            	}
            }
        }
    };
    
    public void onStart(View view){
		BlueToothService.handle(getApplicationContext(),BlueToothConstants.HANDLE_SERVER_START,null);

    }
	
	public void onSearch(View view){
		SPUtils.setEBikeAddress(this, "");//重新搜索要设置为空
		BlueToothService.handle(getApplicationContext(),BlueToothConstants.HANDLE_SERVER_SCAN,null);
	}
	
	public void onReStart(View view){
		data.clear();
		BlueToothService.handle(getApplicationContext(),BlueToothConstants.HANDLE_SERVER_CONTINUTE,null);
	}
	
	public void onPause(View view){
		BlueToothService.handle(getApplicationContext(),BlueToothConstants.HANDLE_SERVER_PUASE,null);
	}
	
	public void onSend(View view){
		BlueToothService.sendData(getApplicationContext(),CommandCode.SURVEY,new byte[]{0});
	}
	
	public void onStop(View view){
		BlueToothService.handle(getApplicationContext(),BlueToothConstants.HANDLE_SERVER_STOP,null);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	

}
