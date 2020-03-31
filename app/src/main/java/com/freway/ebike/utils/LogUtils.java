/**
 * 
 */
package com.freway.ebike.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

/**
 * 日志工具类
 * 
 * @author mengjk
 * 
 *         2015年5月14日
 */
public class LogUtils {
	private static boolean isOpenLog = true;

	public static void i(String tag, String message) {
		if (isOpenLog&&!TextUtils.isEmpty(message)) {
			Log.i(tag, message);
		}
	}

	public static void d(String tag, String message) {
		if (isOpenLog&&!TextUtils.isEmpty(message)) {
			Log.d(tag, message);
		}
	}

	public static void e(String tag, String message) {
		if (isOpenLog&&!TextUtils.isEmpty(message)) {
			Log.e(tag, message);
		}
	}

	public static void i(String tag, boolean isOpenLog, String message) {
		if (isOpenLog&&!TextUtils.isEmpty(message)) {
			Log.i(tag, message);
		}
	}

	public static void d(String tag, boolean isOpenLog, String message) {
		if (isOpenLog&&!TextUtils.isEmpty(message)) {
			Log.d(tag, message);
		}
	}

	public static void e(String tag, boolean isOpenLog, String message) {
		if (isOpenLog&&!TextUtils.isEmpty(message)) {
			Log.e(tag, message);
		}
	}
	
	public static void e(String tag,  String message,Exception e) {
		e.printStackTrace();
		if (isOpenLog&&!TextUtils.isEmpty(message)) {
			Log.e(tag, message);
		}
	}

	/** java系统打印 */
	public static void systemOut(String message) {
		if (isOpenLog&&!TextUtils.isEmpty(message)) {
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
	
	    private static int SDCARD_LOG_FILE_SAVE_DAYS = 0;// sd卡中日志文件的最多保存天数  
	    private static String MYLOGFILEName = "Log.txt";// 本类输出的日志文件名称  
	    private static SimpleDateFormat myLogSdf = new SimpleDateFormat(  
	            "yyyy-MM-dd HH:mm:ss");// 日志的输出格式  
	    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式  
	  /** 
     * 打开日志文件并写入日志 
     *  
     * @return 
     * **/  
    public static void writeLogtoFile(String tag, String text) {// 新建或打开日志文件  
        Date nowtime = new Date();  
        String needWriteFiel = logfile.format(nowtime);  
        String needWriteMessage = myLogSdf.format(nowtime) + " " + tag + "\n" + text;  
        File dir=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/freway/log");
        if(!dir.exists()){
        	dir.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/freway/log", needWriteFiel  
                + MYLOGFILEName);  
        if(!file.exists()){
        	try {
				file.createNewFile();
			} catch (IOException e) {
				LogUtils.e("LogUtils", "新建文件出错");
			}
        }
        try {  
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖  
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);  
            bufWriter.write(needWriteMessage);  
            bufWriter.newLine();  
            bufWriter.close();  
            filerWriter.close();  
        } catch (IOException e) {  
            LogUtils.e("LogUtils", e.getMessage()+""); 
        }  
    }  
  
    /** 
     * 删除制定的日志文件 
     * */  
    public static void delFile() {// 删除日志文件  
        String needDelFiel = logfile.format(getDateBefore());  
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/freway/log", needDelFiel + MYLOGFILEName);  
        if (file.exists()) {  
            file.delete();  
        }  
    }  
  
    /** 
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名 
     * */  
    private static Date getDateBefore() {  
        Date nowtime = new Date();  
        Calendar now = Calendar.getInstance();  
        now.setTime(nowtime);  
        now.set(Calendar.DATE, now.get(Calendar.DATE)  
                - SDCARD_LOG_FILE_SAVE_DAYS);  
        return now.getTime();  
    }  
}
