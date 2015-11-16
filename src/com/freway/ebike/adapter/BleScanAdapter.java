package com.freway.ebike.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
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

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * */
	public BleScanAdapter(Context context) {
		super(context);
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_bluetooth_device, null);
		}
		ImageView icon = (ImageView) convertView.findViewById(R.id.device_select);
		TextView title = (TextView) convertView.findViewById(R.id.device_title);
		BluetoothDevice device=dataList.get(position);
		title.setText(device.getName());
		convertView.setTag(dataList.get(position));
		return convertView;
	}



}
