/**
 * 
 */
package com.freway.ebike.net;

import android.content.Context;

/**
 * 获取与服务器请求的工厂类 单例模式，请求通过getInstance方法来获取请求实例
 * 
 * @author Nicolls
 */
public class EBikeRequestServiceFactory {
	public static final int REQUEST_APACHE = 0;
	public static final int REQUEST_VOLLEY = 1;

	private EBikeRequestServiceFactory() {
	}

	public static EBikeRequestService getInstance(Context context) {
		return getInstance(context, REQUEST_APACHE);
	}

	public static EBikeRequestService getInstance(Context context, int requestMode) {
		EBikeRequestService mGvRequestService = null;
		switch (requestMode) {
		case REQUEST_APACHE:

			break;
		case REQUEST_VOLLEY:
			mGvRequestService = new EBikeRequestServiceVolleyImpl(context);
			break;
		default:
			mGvRequestService = new EBikeRequestServiceVolleyImpl(context);
			break;
		}
		return mGvRequestService;
	}
}
