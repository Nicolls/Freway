package com.freway.ebike.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.freway.ebike.R;

/**
 * 组列表适配器，包括标题列表和一个Layout容器列表适配
 * 
 * @author mengjk 2015年6月15日
 */
public class GridAdapter extends BaseAdapter {
	public boolean isShowCheckBox = false;
	private Context mContext;
	private List<HashMap<String, Object>> itemList;
	private LayoutInflater inflater;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * */
	public GridAdapter(Context context) {
		this.mContext = context;
		this.inflater = LayoutInflater.from(context);
	}

	/**
	 * 设置数据源
	 * 
	 * @param itemList
	 *            数据源集合
	 * */
	public void setData(List<HashMap<String, Object>> itemList) {
		this.itemList = itemList;
	}

	@Override
	public int getCount() {
		return itemList == null ? 0 : itemList.size();
	}

	@Override
	public HashMap<String, Object> getItem(int position) {
		return itemList == null ? null : itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

//		if (convertView == null) {
//			convertView = inflater.inflate(R.layout.item_grid, null);
//		}
//		convertView.setTag(itemList.get(position));
//		if ((Integer) itemList.get(position).get("icon") == 0) {
//			((TextView) convertView.findViewById(R.id.item_tv_title)).setText("");
//			((ImageView) convertView.findViewById(R.id.item_iv_icon))
//					.setImageResource(R.drawable.component_cpu);
//			((TextView) convertView.findViewById(R.id.item_tv_title)).setVisibility(View.INVISIBLE);
//			((ImageView) convertView.findViewById(R.id.item_iv_icon)).setVisibility(View.INVISIBLE);
//		} else {
//			((TextView) convertView.findViewById(R.id.item_tv_title)).setText((String) itemList
//					.get(position).get("name"));
//			((ImageView) convertView.findViewById(R.id.item_iv_icon))
//					.setImageResource((Integer) itemList.get(position).get("icon"));
//		}

		return convertView;
	}
}
