package com.freway.ebike.bluetooth;

import com.freway.ebike.R;
import com.freway.ebike.utils.ToastUtils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

/**蓝牙工具类*/
public class BlueToothTools {
	private Context context;
	private long visbleTime=0L;
	public BlueToothTools(Context context){
		this.context=context;
	}
	/**设置蓝牙可见时间,默认为一直可见*/
	public void setVisbleTime(long time){
		visbleTime=time;
	}
	
	/**判断蓝牙是否可用*/
	public  boolean isBlueToothSupport(){
		boolean isSupport=true;
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(bluetoothAdapter==null){
			isSupport=false;
		}
		return isSupport;
	}
	
	/**判断蓝牙是否可见 */
	public boolean isBlueToothDiscove(){
		boolean isDiscove=false;
		if (isBlueToothSupport()&&isBlueToothEnable()&&BluetoothAdapter.getDefaultAdapter().getScanMode() ==
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			isDiscove=true;
		}
		return isDiscove;
	}
	/**判断蓝牙是否打开 */
	public boolean isBlueToothEnable(){
		if(isBlueToothSupport()){
			return BluetoothAdapter.getDefaultAdapter().isEnabled();
		}else{
			ToastUtils.toast(context, context.getString(R.string.not_support_bluetooth));
			return false;
		}
	}
	
	
	/**打开蓝牙*/
	public  void enableBlueTooth(){
		if(isBlueToothSupport()&&!isBlueToothEnable()){
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			enableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(enableIntent);
		}
	}
	
	/**设置蓝牙可见*/
	public  void enableBluetoothBeDiscove(){
		if (!isBlueToothDiscove()) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, visbleTime);
            context.startActivity(discoverableIntent);
        }
	}
	
	/**扫描设备*/
	public void startDiscove(){
		if(isBlueToothEnable()&&!BluetoothAdapter.getDefaultAdapter().isDiscovering()){
			 if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
				 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		        }
			BluetoothAdapter.getDefaultAdapter().startDiscovery();
		}
	}
	
	/**停止扫描*/
	public void stopDiscove(){
		if(isBlueToothSupport()){
			if(BluetoothAdapter.getDefaultAdapter().isDiscovering()){
				BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
			}
		}
	}

}
