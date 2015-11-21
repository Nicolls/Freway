/**
 * 
 */
package com.freway.ebike.utils;

import android.util.Log;

/**
 * 日志工具类
 * 
 * @author mengjk
 * 
 *         2015年5月14日
 */
public class LogUtils {
	private static boolean isOpenLog = false;

	public static void i(String tag, String message) {
		if (isOpenLog) {
			Log.i(tag, message);
		}
	}

	public static void d(String tag, String message) {
		if (isOpenLog) {
			Log.d(tag, message);
		}
	}

	public static void e(String tag, String message) {
		if (isOpenLog) {
			Log.e(tag, message);
		}
	}

	public static void i(String tag, boolean isOpenLog, String message) {
		if (isOpenLog) {
			Log.i(tag, message);
		}
	}

	public static void d(String tag, boolean isOpenLog, String message) {
		if (isOpenLog) {
			Log.d(tag, message);
		}
	}

	public static void e(String tag, boolean isOpenLog, String message) {
		if (isOpenLog) {
			Log.e(tag, message);
		}
	}
	
	public static void e(String tag,  String message,Exception e) {
		e.printStackTrace();
		if (isOpenLog) {
			Log.e(tag, message);
		}
	}

	/** java系统打印 */
	public static void systemOut(String message) {
		if (isOpenLog) {
			System.out.println(message);
		}
	}

	public static boolean isOpenLog() {
		return isOpenLog;
	}

	/** 是否打印输出日志 */
	public static void setOpenLog(boolean isOpenLog) {
		LogUtils.isOpenLog = isOpenLog;
	}

}
