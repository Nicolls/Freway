package com.freway.ebike.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 基类适配器
 * 
 * @param <T>
 *            数据实体类
 * @author mengjk
 *
 *         2015年6月15日
 */
public class EBBaseAdapter<T> extends BaseAdapter {
	protected List<T> dataList;
	private Context context;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * */
	public EBBaseAdapter(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	/**
	 * 设置数据源
	 * 
	 * @param itemList
	 *            数据源集合
	 * */
	public void setData(List<T> itemList) {
		this.dataList = itemList;
	}

	@Override
	public int getCount() {
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public T getItem(int position) {
		return dataList == null ? null : dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		return convertView;
	}

}
