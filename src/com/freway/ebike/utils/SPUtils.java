/**
 * 
 */
package com.freway.ebike.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.freway.ebike.common.EBConstant;
import com.freway.ebike.model.EBRequest;
import com.freway.ebike.model.User;
import com.google.gson.Gson;

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
	public static final String SP_HTTP_KEY = "SP_HTTP_KEY";

	// app相关
	public static final String SP_APP = "SP_APP";
	public static final String SP_APP_ENTER = "SP_APP_ENTER";
	public static final String SP_APP_EBIKE_ADDRESS = "SP_APP_EBIKE_ADDRESS";
	public static final String SP_APP_EBIKE_NAME = "SP_APP_EBIKE_NAME";
	
	// 用户相关
	public static final String SP_USER = "SP_USER";
	public static final String SP_USER_AUTO_LOGIN = "SP_USER_AUTO_LOGIN";
	public static final String SP_USER_SAFE_CODE = "SP_USER_SAFE_CODE";
	public static final String SP_USER_SAFE_CODE_SWITCH = "SP_USER_SAFE_CODE_SWITCH";
	public static final String SP_USER_TOKEN = "SP_USER_TOKEN";
//	public static final String SP_USER_TRAVEL_ID = "SP_USER_TRAVEL_ID";//行程ID

	public static final String SP_USER_SIGNIN_TYPE = "SP_USER_SIGNIN_TYPE";
	public static final String SP_USER_UI_MODEL = "SP_USER_UI_MODEL";
	public static final String SP_USER_DATA = "SP_USER_DATA";
	public static final String SP_USER_UNIT_OF_DISTANCE = "SP_USER_UNIT_OF_DISTANCE";
	public static final String SP_USER_TRAVEL_MAP = "SP_USER_TRAVEL_MAP";//地图行程数据


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
	
	
	/** 获取私钥 */
	public static String getServerKey(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_HTTP, Context.MODE_PRIVATE);
		String key = sp.getString(SP_HTTP_KEY, EBConstant.DEFAULT_KEY);
		EBRequest.requestKey = key;
		return key;
	}

	/** 设置私钥 */
	public static boolean setServerKey(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(SP_HTTP, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_HTTP_KEY, key).commit();
		if (isOk) {
			EBRequest.requestKey = key;
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

	/** 获取ebike name */
	public static String getEBkieName(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
		String name = sp.getString(SP_APP_EBIKE_NAME, null);
		return name;
	}

	/** 保存ebike name */
	public static boolean setEBikeName(Context context, String name) {
		SharedPreferences sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_APP_EBIKE_NAME, name).commit();
		return isOk;
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

	/** 获取登录token */
	public static String getToken(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		String token = sp.getString(SP_USER_TOKEN, null);
		return token;
	}

	/** 保存登录token */
	public static boolean setToken(Context context, String token) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_USER_TOKEN, token).commit();
		return isOk;
	}
	
	/** 获取 travelId*/
//	public static long getTravelId(Context context) {
//		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
//		long travelId = sp.getLong(SP_USER_TRAVEL_ID,-1);
//		return travelId;
//	}

	/** 保存travelId*/
//	public static boolean setTravelId(Context context, long travelId) {
//		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
//		boolean isOk = sp.edit().putLong(SP_USER_TRAVEL_ID, travelId).commit();
//		return isOk;
//	}
	
	/** 获取登录类型 */
	public static int getSigninType(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		int signinType = sp.getInt(SP_USER_SIGNIN_TYPE, 0);
		return signinType;
	}

	/** 保存登录类型 */
	public static boolean setSigninType(Context context, int signinType) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putInt(SP_USER_SIGNIN_TYPE, signinType).commit();
		return isOk;
	}
	/** 获取行程单位 */
	public static int getUnitOfDistance(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		int unitOfDistance = sp.getInt(SP_USER_UNIT_OF_DISTANCE, 0);
		return unitOfDistance;
	}

	/** 保存行程显示模式 */
	public static boolean setUnitOfDistance(Context context, int unitOfDistance) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putInt(SP_USER_UNIT_OF_DISTANCE, unitOfDistance).commit();
		return isOk;
	}
	/** 获取UI显示模式 */
	public static int getUiModel(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		int model = sp.getInt(SP_USER_UI_MODEL, 0);
		return model;
	}

	/** 保存UI显示模式 */
	public static boolean setUiModel(Context context, int model) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putInt(SP_USER_UI_MODEL, model).commit();
		return isOk;
	}
	
	/** 获取User */
	public static User getUser(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		String data = sp.getString(SP_USER_DATA, "");
		data=EncryptUtils.decryptBase64(data);
		User user=new User();
		if(!TextUtils.isEmpty(data)){
			Gson gson=new Gson();
			user=gson.fromJson(data, User.class);
		}
		return user;
	}

	/** 保存User Base64压缩*/
	public static boolean setUser(Context context, User user) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		Gson gson=new Gson();
		String data=gson.toJson(user);
		data=EncryptUtils.encryptBase64(data);
		boolean isOk = sp.edit().putString(SP_USER_DATA, data).commit();
		return isOk;
	}
	
	/** 获取last travel*/
//	public static String getTravelLast(Context context) {
//		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
//		String travel = sp.getString(SP_USER_TRAVEL_MAP,null);
//		return travel;
//	}

	/** 保存last travel*/
//	public static boolean setTravelLast(Context context, String travel) {
//		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
//		boolean isOk = sp.edit().putString(SP_USER_TRAVEL_MAP, travel).commit();
//		return isOk;
//	}
	
}
