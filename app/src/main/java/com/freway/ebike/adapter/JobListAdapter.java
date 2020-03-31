package com.freway.ebike.adapter;

import com.freway.ebike.model.Job;

import android.content.Context;
import android.view.View;

/**
 * 作业列表适配器
 * 
 * @author mengjk
 *
 *         2015年6月15日
 */
public class JobListAdapter extends DoubleLineListAdapter<Job> {
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * */
	public JobListAdapter(Context context) {
		super(context);
	}

	@Override
	public void initData(Context context, DoubleLineListAdapter<Job>.DoubleLineHolder holder,
			Job item) {
		// 隐藏需要隐藏的
		holder.titleKind.setVisibility(View.GONE);
		holder.contentRight.setVisibility(View.GONE);
		holder.icon.setVisibility(View.GONE);
		// 填写数据
		holder.title.setText(item.getJobName());
		holder.contentLeft.setText(item.getJobOwner());
		holder.contentMiddle.setText(item.getJobStartTime());
		String status="";
		holder.contentKind.setText(status);

//		holder.contentLeft.setCompoundDrawablesWithIntrinsicBounds(R.drawable.item_head, 0, 0, 0);
//		holder.contentMiddle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.item_time, 0, 0, 0);
	}

}
