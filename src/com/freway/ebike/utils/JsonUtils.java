/**
 * 
 */
package com.freway.ebike.utils;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Json工具类
 * 
 * @author mengjk
 *
 *         2015年5月14日
 */
public class JsonUtils {
	public static Object jsonString2ObjectWithClass(String str, Class<?> cls) {
		Gson gson = new Gson();
		return gson.fromJson(str, cls);
	}

	public static Object jsonString2ObjectWithType(String str, Type type) {
		Gson gson = new Gson();
		return gson.fromJson(str, type);

	}
	

	public static String objectToJson(Object obj, Class<?> cls) {
		Gson gson = new Gson();
		String result = gson.toJson(obj, cls);
		return result;
	}
	
	public static String objectToJson(Object obj, Type type) {
		Gson gson = new Gson();
		String result = gson.toJson(obj, type);
		return result;
	}


	public static String paramsToJson(Long[] params) {
		Gson gson = new Gson();
		String result = gson.toJson(params, Long[].class);
		return result;
	}

	public static String paramsToJson(String[] params) {
		Gson gson = new Gson();
		String result = gson.toJson(params, String[].class);
		// result=result.replaceAll(" ", "+");//转义空格
		// result=result.replaceAll("\\[", "\\\\[");//转义中括号
		// result=result.replaceAll("\\]", "\\\\]");//转义中括号
		// result=result.replaceAll("\"", "\\\\\"");//转义分号
		return result;
	}


	public static String paramsToJson(Map<String, String> params) {
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		return gson.toJson(params, type);
	}
}
