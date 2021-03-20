/**
 * 
 */
package com.freway.ebike.utils;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.freway.ebike.R;

/**
 * 自定义Toast工具类
 * 
 * @author mengjk
 *
 *         2015年5月15日
 */
public class ToastUtils {
	public static final String TAG = ToastUtils.class.getSimpleName();
	private static boolean isToast = true;

	/** 全局设置是否显示Toast */
	public void setToastEnable(boolean enable) {
		isToast = enable;
	}

	/** Toast一个消息，默认为短时间显示 */
	public static void toast(Context context, String message) {
		if (isToast) {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Toast一个消息
	 * 
	 * @param time
	 *            显示的时长方式，0为Toast.LENGTH_SHORT,1为Toast.LENGTH_LONG
	 * */
	public static void toast(Context context, String message, int time) {
		if (isToast) {
			if (time == 1) {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/** Toast一个消息，是否强制显示此消息，并以短时长显示 */
	public static void toast(Context context, String message, boolean isToast) {
		if (isToast) {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Toast一个消息，是否强制显示此消息
	 * 
	 * @param time
	 *            显示的时长方式，0为Toast.LENGTH_SHORT,1为Toast.LENGTH_LONG
	 * */
	public static void toast(Context context, String message, boolean isToast, int time) {
		if (isToast) {
			if (time == 1) {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
