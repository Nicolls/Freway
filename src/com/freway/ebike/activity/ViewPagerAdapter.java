package com.freway.ebike.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

class ViewPagerAdapter extends PagerAdapter {
	protected static final String[] CONTENT = new String[] { "This", "Is Is", "A A A", "Test", };

	@Override
	public int getCount() {
		return CONTENT.length;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		
		return false;
	}

	
}