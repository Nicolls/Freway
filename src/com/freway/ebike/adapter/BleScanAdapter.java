package com.freway.ebike.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freway.ebike.R;

/**
 * 列表，Item两行数据适配器
 * 
 * @param <T>
 *            数据实体类
 * @author mengjk
 *
 *         2015年6月15日
 */
public  class BleScanAdapter extends EBBaseAdapter<BluetoothDevice> {
	private LayoutInflater inflater;
	private Context context;
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * */
	public BleScanAdapter(Context context) {
		super(context);
		this.context=context;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_bluetooth_device, null);
		}
		ImageView icon = (ImageView) convertView.findViewById(R.id.device_select);
		TextView title = (TextView) convertView.findViewById(R.id.device_title);
		TextView address = (TextView) convertView.findViewById(R.id.device_address);
		BluetoothDevice device=dataList.get(position);
		if(TextUtils.isEmpty(device.getName())){
			title.setText(context.getText(R.string.unknown));
		}else{
			title.setText(device.getName());
		}
		address.setText(device.getAddress());
		address.setVisibility(View.GONE);//mark 当用于蓝牙模块测试时，把它设置为visble，这样方便调试，发布时应该设置为gone
		convertView.setTag(dataList.get(position));
		return convertView;
	}



}
