/**
 *
 */
package com.freway.ebike.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.freway.ebike.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * APP通用工具类
 *
 * @author Nicolls
 *
 *         2015年5月14日
 */
public class CommonUtil {
	/**
	 * 得到imei号
	 *
	 * @param activity
	 *            上下文
	 * */
	public static String getPhoneImei(Activity activity) {
		TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			LogUtils.i("CommonUtils","getPhoneImei need to request phone permission");
			activity.requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},10086);
			return "";
		}
		return tm.getDeviceId();// String
	}

	/**
	 * 得到版本号
	 * 
	 * @param context
	 *            上下文
	 * */
	public static String getAppVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			LogUtils.e(context.getClass().getName(), "找不到包名！");
			return "0.0.0";
		}
	}

	/** 格式化html数据，嵌入script代码 */
	public static String formatScriptHtml(Context context, String scriptCode, String htmlFileName) {
		StringBuffer sb = new StringBuffer();
		InputStream is = null;
		try {
			is = context.getAssets().open(htmlFileName);
			int len = 0;
			byte[] buf = new byte[512];
			while ((len = is.read(buf, 0, buf.length)) != -1) {
				sb.append(new String(buf, 0, len, "UTF-8"));
			}
		} catch (IOException e) {
			LogUtils.e("CommonUtil", "读取assets目录下的html文件出现错误：" + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return scriptCode + "\n" + sb.toString();
	}

	// 判断是否为手机号
	public static boolean isPhone(String inputText) {
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(inputText);
		return m.matches();
	}

	// 判断格式是否为email
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}
	/**
	 * 判断是否APN列表中某个渠道处于连接状态
	 * 
	 * @return
	 */
	public static boolean isMobile(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	/**
	 * 通过判断wifi和mobile两种方式是否能够连接网络
	 */
	public static boolean checkNetWork(Context context) {
		boolean isWIFI = isWIFI(context);
		boolean isMobile = isMobile(context);

		// 如果两个渠道都无法使用，提示用户设置网络信息
		if (!isWIFI && !isMobile) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否WIFI处于连接状态
	 * 
	 * @return
	 */
	public static boolean isWIFI(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}
	
	/** 判断谷歌服务是否可用 */
	public static boolean checkGoogleServiceAvailable(Activity context, int RQS_GooglePlayServices) {
		return false;
	}

	/**
	 * 格式化浮点数为某一精度，默认为4舍5入,
	 * formatType=0 4舍5入
	 * formatType=1 小数都不进位
	 * formatType=2 小数都进位
	 * 
	 * @param f
	 *            要格式化浮点值
	 * @param accuracy
	 *            精度数,2表示保留两位小数
	 * */
	public static float formatFloatAccuracy(float f, int accuracy) {
		int multiple=1;
		for(int i=0;i<accuracy;i++){
			multiple*=10;
		}
		f=Math.round(f*multiple);
		f=f/multiple;
		return formatFloatAccuracy(f,accuracy,0);
	}
	
	/**
	 * 格式化浮点数为某一精度，默认为4舍5入,
	 * formatType=0 4舍5入
	 * formatType=1 小数都不进位
	 * formatType=2 小数都进位
	 * 
	 * @param f
	 *            要格式化浮点值
	 * @param accuracy
	 *            精度数,2表示保留两位小数
	 * */
	public static float formatFloatAccuracy(float f, int accuracy,int formatType) {
		int multiple=1;
		for(int i=0;i<accuracy;i++){
			multiple*=10;
		}
		if(formatType==0){
			f=Math.round(f*multiple);
		}else if(formatType==1){
			f=(int) Math.floor(f*multiple);
		}else if(formatType==2){
			f=(int) Math.ceil(f*multiple);
		}
		f=f/multiple;
		return f;
	}
	
	/**将一个16进制字符串转成byte[]数组，字符串中不包含0x*/
	public static byte[] _16String2ByteArray(String data){
		byte[] buf=new byte[0];
		if(data!=null){
			String[]sAry=data.split(",");
			buf=new byte[sAry.length];
			
			for(int i=0;i<buf.length;i++){
				buf[i]=(byte) Integer.parseInt(sAry[i], 16);
			}
		}
		return buf;
	}

	/**更新User从服务器返回的值*/
	public static User updateUserProfile(Context context,User user){
		User localUser=SPUtils.getUser(context);
		localUser.setAge(user.getAge());
		localUser.setUsername(user.getUsername());
		localUser.setGender(user.getGender());
		localUser.setHeight(user.getHeight());
		localUser.setWeight(user.getWeight());
		localUser.setPhoto(user.getPhoto());
		localUser.setEmail(user.getEmail());
		localUser.setTotal_distance(user.getTotal_distance());
		localUser.setTotal_time(user.getTotal_time());
		SPUtils.setUser(context, localUser);
		return localUser;
	}
	
	/**判断是否需要更新*/
	public static boolean isNeed2UpdateApp(Context context,String serverVersion){
		boolean isNeed=false;
		String nowVersion=getAppVersion(context);
		try {
			int localVersion=Integer.parseInt(nowVersion.replace(".", ""));
			int version=Integer.parseInt(serverVersion.replace(".", ""));
			if(version>localVersion){
				isNeed=true;
			}
		} catch (Exception e) {
			LogUtils.e("CommonUtils", "isNeed="+isNeed);
		}
		return isNeed;
	}
	
	/**判断字符串是否为整数*/
	public static boolean isNumber(String input){
		boolean isNumber=true;
		Matcher mer = Pattern.compile("^[0-9]+$").matcher(input);  
		isNumber=mer.find();  
		return isNumber;
	}
}
