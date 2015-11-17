/**
 * 
 */
package com.freway.ebike.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 
 * 时间工具
 * 
 * @author mengjk
 *
 *         2015年7月1日
 */
public class TimeUtils {

	/**
	 * 转换时间字符串
	 * 
	 * */
	public static String switchTimeString(String time, String timeFormat, String newTimeFormat) {
		String result = time;
		SimpleDateFormat oFormat = new SimpleDateFormat(timeFormat, Locale.CHINA);
		SimpleDateFormat nFormat = new SimpleDateFormat(newTimeFormat, Locale.CHINA);
		try {
			Date date = oFormat.parse(time);
			result = nFormat.format(date);
		} catch (ParseException e) {
			LogUtils.e("TimeUtils", "格式化字符串不成功，错误信息：" + e.getMessage());
		}
		return result;
	}

	/**
	 * 获取当前时间格式化字符串
	 * 
	 * @param formatStyle
	 *            格式 类似:yyyy-MM-dd HH:mm:ss
	 * */
	public static String getFormatNowTime(String formatStyle) {
		SimpleDateFormat format = new SimpleDateFormat(formatStyle, Locale.CHINA);
		Date date = new Date();
		return format.format(date);
	}

	/**
	 * 获取指定时间的格式化字符串
	 * 
	 * @param date
	 *            时间date
	 * @param formatStyle
	 *            格式 类似:yyyy-MM-dd HH:mm:ss
	 * */
	public static String getFormatTime(Date date, String formatStyle) {
		SimpleDateFormat format = new SimpleDateFormat(formatStyle, Locale.CHINA);
		return format.format(date);
	}

	/**
	 * 获取从当前时间推算往前多久的时间点
	 * 
	 * @param time
	 *            往前的时间段，单位为毫秒
	 * @param formatStyle
	 *            格式 类似:yyyy-MM-dd HH:mm:ss
	 * */
	public static String getFormatTimeBeforNow(Long time, String formatStyle) {
		return getFormatTimeLocate(Calendar.getInstance().getTime(), 1, time, formatStyle);
	}

	/**
	 * 获取从某个时间点推算往前或者后多久的时间点
	 * 
	 * @param date
	 *            某个时间点
	 * @param derect
	 *            方向，往前推算1，往后推算2
	 * @param time
	 *            推算的时间段
	 * @param formatStyle
	 *            格式 类似:yyyy-MM-dd HH:mm:ss
	 * */
	public static String getFormatTimeLocate(Date date, int derect, Long time, String formatStyle) {
		SimpleDateFormat format = new SimpleDateFormat(formatStyle, Locale.CHINA);
		Long l = 0L;
		if (derect == 1) {
			l = date.getTime() - time;
		} else if (derect == 2) {
			l = date.getTime() + time;
		}
		Date result = new Date(l);
		return format.format(result);
	}
	
	/**将毫秒转化成时分秒*/
	public static String formatTimeMillisToHMS(long time){
		long hour = time/(60*60*1000); 
		long minute = (time - hour*60*60*1000)/(60*1000); 
		long second = (time - hour*60*60*1000 - minute*60*1000)/1000; 
		if(second >= 60 ) 
		{ 
		second = second % 60; 
		minute+=second/60; 
		} 
		if(minute>=60) 
		{ 
		minute = minute %60; 
		hour += minute/60; 
		} 
		String sh = " "; 
		String sm = " "; 
		String ss = " "; 
		if(hour <10) 
		{ 
		sh = "0" + String.valueOf(hour); 
		}else 
		{ 
		sh = String.valueOf(hour); 
		} 
		if(minute <10) 
		{ 
		sm = "0" + String.valueOf(minute); 
		}else 
		{ 
		sm = String.valueOf(minute); 
		} 
		if(second <10) 
		{ 
		ss = "0" + String.valueOf(second); 
		}else 
		{ 
		ss = String.valueOf(second); 
		} 
		return sh+":"+sm+":"+ss;
	}

}
