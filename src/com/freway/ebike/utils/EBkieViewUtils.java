/**
 * 
 */
package com.freway.ebike.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.freway.ebike.R;

/**
 * View工具
 * 
 * @author Nicolls
 *
 *         2015年7月1日
 */
public class EBkieViewUtils {

	/** 重新计算gridView的高度 */
	@SuppressLint("NewApi")
	public static void setGridViewHeightBasedOnChildren(GridView gridView) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, gridView);
			if (listItem != null) {
				listItem.measure(0, 0);
			}
			if ((i % gridView.getNumColumns()) == 0) {
				totalHeight += listItem.getMeasuredHeight();
			}
		}

		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight + (gridView.getVerticalSpacing() * (listAdapter.getCount() - 1));
		gridView.setLayoutParams(params);
	}

	/** 重新计算gridView的高度 */
	@SuppressLint("NewApi")
	public static void setGridViewWidthBasedOnChildren(GridView gridView) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int iCount = gridView.getNumColumns();
		if (listAdapter.getCount() <= gridView.getNumColumns()) {
			iCount = listAdapter.getCount();
		}

		int totalHeight = 0;
		for (int i = 0; i < iCount; i++) {
			View listItem = listAdapter.getView(i, null, gridView);
			if (listItem != null) {
				listItem.measure(0, 0);
			}
			totalHeight += listItem.getMeasuredWidth();
		}

		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.width = totalHeight + (gridView.getHorizontalSpacing() * (iCount - 1));
		gridView.setLayoutParams(params);
	}

	/** 隐藏软键盘 */
	public static void hideSystemKeyBoard(Context context, View v) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	/** 显示软键盘 */
	public static void showSystemKeyBoard(Context context, EditText v) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInputFromInputMethod(v.getWindowToken(), 0);
	}

	/** 获取屏幕宽高 */
	public static Point getDisplaySize(Context context) {
		Point p = new Point();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		p.x = width;
		p.y = height;
		return p;
	}

	/** 展示头像 */
	public static void displayPhoto(Context context, ImageView view, String url) {
		if (view != null && !TextUtils.isEmpty(url)) {
			RequestQueue mQueue = Volley.newRequestQueue(context.getApplicationContext());

			ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());

			ImageListener listener = ImageLoader.getImageListener(view, R.drawable.profile_pic_temp,
					R.drawable.profile_pic_temp);
			imageLoader.get(url, listener);
			// 指定图片允许的最大宽度和高度
			// imageLoader.get("http://developer.android.com/images/home/aw_dac.png",listener,
			// 200, 200);
		}
	}
}
