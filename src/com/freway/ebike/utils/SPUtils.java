/**
 * 
 */
package com.freway.ebike.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.freway.ebike.common.EBConstant;
import com.freway.ebike.model.EBRequest;

/**
 * 存储shareprefrence数据工具类
 * 
 * @author mengjk
 * 
 *         2015年5月15日
 */
public class SPUtils {
	// 服务器相关
	public static final String SP_HTTP = "SP_HTTP";
	public static final String SP_HTTP_HOST = "SP_HTTP_HOST";
	public static final String SP_HTTP_PORT = "SP_HTTP_PORT";

	// app相关
	public static final String SP_APP = "SP_APP";
	public static final String SP_APP_ENTER = "SP_APP_ENTER";
	public static final String SP_APP_EBIKE_ADDRESS = "SP_APP_EBIKE_ADDRESS";
	
	// 用户相关
	public static final String SP_USER = "SP_USER";
	public static final String SP_USER_USERNAME = "SP_USER_USERNAME";
	public static final String SP_USER_PASSWORD = "SP_USER_PASSWORD";
	public static final String SP_USER_AUTO_LOGIN = "SP_USER_AUTO_LOGIN";
	public static final String SP_USER_SAFE_CODE = "SP_USER_SAFE_CODE";
	public static final String SP_USER_SAFE_CODE_SWITCH = "SP_USER_SAFE_CODE_SWITCH";

	/** 获取登录用户名 */
	public static String getUsername(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		String name = sp.getString(SP_USER_USERNAME, "");
		return name;
	}

	/** 设置登录用户名 */
	public static boolean setUsername(Context context, String name) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_USER_USERNAME, name).commit();
		return isOk;
	}

	/** 获取登录密码,解BASE64压缩 */
	public static String getPassword(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		String password = sp.getString(SP_USER_PASSWORD, "");
		if (!TextUtils.isEmpty(password)) {
			password = EncryptUtils.decryptBase64(password);
		}
		return password;
	}

	/** 设置登录密码，BASE64压缩存储 */
	public static boolean setPassword(Context context, String password) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_USER_PASSWORD, EncryptUtils.encryptBase64(password))
				.commit();
		return isOk;
	}

	/** 获取服务器地址 */
	public static String getServerHost(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_HTTP, Context.MODE_PRIVATE);
		String host = sp.getString(SP_HTTP_HOST, EBConstant.DEFAULT_HOST);
		EBRequest.requestHost = host;
		return host;
	}

	/** 设置服务器地址 */
	public static boolean setServerHost(Context context, String host) {
		SharedPreferences sp = context.getSharedPreferences(SP_HTTP, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_HTTP_HOST, host).commit();
		if (isOk) {
			EBRequest.requestHost = host;
		}
		return isOk;
	}

	/** 获取服务器端口 */
	public static int getServerPort(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_HTTP, Context.MODE_PRIVATE);
		int port = sp.getInt(SP_HTTP_PORT, EBConstant.DEFAULT_PORT);
		EBRequest.requestPort = port;
		return port;
	}

	/** 设置服务器端口 */
	public static boolean setServerPort(Context context, int port) {
		SharedPreferences sp = context.getSharedPreferences(SP_HTTP, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putInt(SP_HTTP_PORT, port).commit();
		if (isOk) {
			EBRequest.requestPort = port;
		}
		return isOk;
	}

	/** 设置是否自动登录 */
	public static boolean setAutoLoginState(Context context, boolean isAuto) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putBoolean(SP_USER_AUTO_LOGIN, isAuto).commit();
		return isOk;
	}

	/** 获取是否是动登录,一获取到就把自动登录设置为true */
	public static boolean getAutoLoginState(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		boolean isOk = sp.getBoolean(SP_USER_AUTO_LOGIN, true);
		return isOk;
	}

	/** 获取是否是第一次进入APP */
	public static boolean hasEnterBefore(Context context) {
		boolean isEnter = false;
		SharedPreferences sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
		isEnter = sp.getBoolean(SP_APP_ENTER, false);
		return isEnter;
	}

	/** 设置是否第一次登录 */
	public static boolean setEnterBefore(Context context, boolean isEnter) {
		SharedPreferences sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putBoolean(SP_APP_ENTER, isEnter).commit();
		return isOk;
	}

	/** 获取safe code */
	public static String getSafeCode(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		String code = sp.getString(SP_USER_SAFE_CODE, null);
		return code;
	}

	/** 保存safe code */
	public static boolean setSafeCode(Context context, String code) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_USER_SAFE_CODE, code).commit();
		return isOk;
	}

	/** 清除本地存储的数据 */
	public static void cleanLocalData(Context context) {
		context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE).edit().clear().commit();
	}

	/** 获取ebike address */
	public static String getEBkieAddress(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
		String code = sp.getString(SP_APP_EBIKE_ADDRESS, null);
		return code;
	}

	/** 保存ebike address */
	public static boolean setEBikeAddress(Context context, String address) {
		SharedPreferences sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_APP_EBIKE_ADDRESS, address).commit();
		return isOk;
	}

	
	
}
