package com.freway.ebike.utils;

import java.util.Hashtable;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

public class FontUtil {
		private static final String TAG = "FontUtil";
		public static final int STYLE_DIN_LIGHT=0;
		public static final int STYLE_DIN_MEDIUM=1;
		public static final int STYLE_DINCOND_BOLD=2;
		private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();
		/**获取字体类型
		 * @param style 字体风格
		 * */
		public static Typeface get(Context c, int style) {
			String assetPath="font/DIN-Light.otf";
			switch(style){
			case STYLE_DIN_LIGHT:
				assetPath="font/DIN-Light.otf";
				break;
			case STYLE_DIN_MEDIUM:
				assetPath="font/DIN-Medium.otf";
				break;
			case STYLE_DINCOND_BOLD:
				assetPath="font/DINCond-Bold.otf";
				break;
				default:
					break;
			}
			synchronized (cache) {
				if (!cache.containsKey(assetPath)) {
					try {
						Typeface t = Typeface.createFromAsset(c.getAssets(),
								assetPath);
						cache.put(assetPath, t);
					} catch (Exception e) {
						Log.e(TAG, "Could not get typeface '" + assetPath
								+ "' because " + e.getMessage());
						return null;
					}
				}
				return cache.get(assetPath);
			}
		}
}
