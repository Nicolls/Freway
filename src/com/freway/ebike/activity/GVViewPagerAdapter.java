/**
 * 
 */
package com.freway.ebike.activity;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * View左右滑动切换适配器
 * 
 * @param <T>
 *            继承于CommonView的实体做为数据源模型
 * @author mengjk
 *
 *         2015年5月18日
 */
public class GVViewPagerAdapter<T extends View> extends PagerAdapter {
	private List<T> dataList;

	/**
	 * 设置数据源
	 * 
	 * @param itemList
	 *            数据源集合
	 * */
	public void setData(List<T> dataList) {
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (dataList != null) {
			((ViewPager) container).removeView(dataList.get(position));
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		T t = dataList.get(position);
		((ViewPager) container).addView(t);
		return t;
	}

}
