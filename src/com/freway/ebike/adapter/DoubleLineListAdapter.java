package com.freway.ebike.adapter;

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
public abstract class DoubleLineListAdapter<T> extends EBBaseAdapter<T> {
	private LayoutInflater inflater;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * */
	public DoubleLineListAdapter(Context context) {
		super(context);
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DoubleLineHolder holder;
//		if (convertView == null) {
//			convertView = inflater.inflate(R.layout.item_double_line_list, null);
//			holder = new DoubleLineHolder();
//			ImageView icon = (ImageView) convertView.findViewById(R.id.item_iv_icon);
//			ImageView arrow = (ImageView) convertView.findViewById(R.id.item_iv_arrow);
//			TextView title = (TextView) convertView.findViewById(R.id.item_tv_title);
//			TextView titleKind = (TextView) convertView.findViewById(R.id.item_tv_title_kind);
//			TextView contentLeft = (TextView) convertView.findViewById(R.id.item_tv_content_left);
//			TextView contentMiddle = (TextView) convertView
//					.findViewById(R.id.item_tv_content_middle);
//			TextView contentRight = (TextView) convertView.findViewById(R.id.item_tv_content_right);
//			TextView contentKind = (TextView) convertView.findViewById(R.id.item_tv_content_kind);
//			TextView contentNumLeft = (TextView) convertView
//					.findViewById(R.id.item_tv_content_left_num);
//			TextView contentNumMiddle = (TextView) convertView
//					.findViewById(R.id.item_tv_content_middle_num);
//			TextView contentNumRight = (TextView) convertView
//					.findViewById(R.id.item_tv_content_right_num);
//			holder.icon = icon;
//			holder.arrow = arrow;
//			holder.title = title;
//			holder.titleKind = titleKind;
//			holder.contentLeft = contentLeft;
//			holder.contentMiddle = contentMiddle;
//			holder.contentRight = contentRight;
//			holder.contentKind = contentKind;
//			holder.contentNumLeft = contentNumLeft;
//			holder.contentNumMiddle = contentNumMiddle;
//			holder.contentNumRight = contentNumRight;
//			convertView.setTag(holder);
//
//		} else {
//			holder = (DoubleLineHolder) convertView.getTag();
//		}
//		if (holder != null) {
//			initData(inflater.getContext(), holder, dataList.get(position));
//		}

		return convertView;
	}

	/**
	 * 由子类来初始化数据
	 * 
	 * @param holder
	 *            缓存数据的holder
	 * @param item
	 *            数据实体
	 * 
	 * */
	public abstract void initData(Context context, DoubleLineHolder holder, T item);

	/**
	 * 列表数据Holder用于缓存
	 *
	 */
	class DoubleLineHolder {
		/** Item项图标 */
		ImageView icon;
		/** Item右箭头 */
		ImageView arrow;
		/** 标题 */
		TextView title;
		/** 标题种类 */
		TextView titleKind;
		/** 内容左项 */
		TextView contentLeft;
		/** 内容中项 */
		TextView contentMiddle;
		/** 内容右项 */
		TextView contentRight;
		/** 内容种类 */
		TextView contentKind;
		/** 内容左项数字 */
		TextView contentNumLeft;
		/** 内容中项数字 */
		TextView contentNumMiddle;
		/** 内容右项数字 */
		TextView contentNumRight;
	}

}
