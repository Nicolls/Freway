/**
 * 
 */
package com.freway.ebike.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.text.TextUtils;
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
import com.freway.ebike.common.EBConstant;

/**
 * View工具
 * 
 * @author Nicolls
 *
 *         2015年7月1日
 */
public class EBikeViewUtils {

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

			ImageLoader imageLoader = new ImageLoader(mQueue, new ImageCache() {
				
				@Override
				public void putBitmap(String url, Bitmap bitmap) {
					FileUtils.saveBitmapByUrlOrName(url, bitmap);
				}
				
				@Override
				public Bitmap getBitmap(String url) {
					return FileUtils.getBitmapFromUrlOrName(url);
				}
			});

			ImageListener listener = ImageLoader.getImageListener(view, 0,
					0);
			imageLoader.get(url, listener);
			// 指定图片允许的最大宽度和高度
			// imageLoader.get("http://developer.android.com/images/home/aw_dac.png",listener,
			// 200, 200);
		}
	}
	
	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap getRoundBitmap(Bitmap bitmap) {
		if(bitmap!=null){
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float roundPx;
			float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
			if (width <= height) {
				roundPx = width / 2;

				left = 0;
				top = 0;
				right = width;
				bottom = width;

				height = width;

				dst_left = 0;
				dst_top = 0;
				dst_right = width;
				dst_bottom = width;
			} else {
				roundPx = height / 2;

				float clip = (width - height) / 2;

				left = clip;
				right = width - clip;
				top = 0;
				bottom = height;
				width = height;

				dst_left = 0;
				dst_top = 0;
				dst_right = height;
				dst_bottom = height;
			}

			Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final Paint paint = new Paint();
			final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
			final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
			final RectF rectF = new RectF(dst);

			paint.setAntiAlias(true);// 设置画笔无锯齿

			canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas

			// 以下有两种方法画圆,drawRounRect和drawCircle
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
			// canvas.drawCircle(roundPx, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
			canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle

			return output;
		}else{
			return null;
		}
		
	}
}
