/**
 * 
 */
package com.freway.ebike.utils;

import java.io.UnsupportedEncodingException;

import android.util.Base64;

/**
 * 加解密工具类
 * 
 * @author Nicolls
 *
 *         2015年5月15日
 */
public class EncryptUtils {
	public static final String TAG = EncryptUtils.class.getSimpleName();

	public static String encryptBase64(String data) {
		return Base64.encodeToString(data.getBytes(), 0);
	}

	public static String decryptBase64(String data) {
		try {
			return new String(Base64.decode(data, 0), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtils.e(TAG, "decryptBase64出现错误－－" + e.getMessage());
			return "";
		}
	}
}
