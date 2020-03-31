package com.freway.ebike.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 引导页面适配器
 * 
 * @author mengjk
 * 
 * */
public class GuidePagerAdapter extends PagerAdapter {

	private ArrayList<View> viewList = new ArrayList<View>();// ViewPager页面集合

	public GuidePagerAdapter(ArrayList<View> viewList) {
		this.viewList = viewList;
	}

	@Override
	public int getCount() {
		int count = viewList.size();
		return count;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(viewList.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		container.addView(viewList.get(position));
		return viewList.get(position);
	}

}
