/**
 * 
 */
package com.freway.ebike.net;

import java.lang.reflect.Type;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.freway.ebike.model.EBRequest;
import com.freway.ebike.model.EBResponse;
import com.freway.ebike.utils.LogUtils;

/**
 * 通过volley框架来实现的与服务器交互接口的请求类
 * 
 * @author Nicolls

 */
public class EBikeRequestServiceVolleyImpl implements EBikeRequestService {
	protected DataUpdateListener dataUpdateListener;
	private Context context;

	public EBikeRequestServiceVolleyImpl(Context context) {
		this.context = context;
	}

	@Override
	public void setUptateListener(DataUpdateListener dataUpdateListener) {
		this.dataUpdateListener = dataUpdateListener;
	}

	private void dataNotify(int what, Object obj) {
		if (dataUpdateListener != null) {
			dataUpdateListener.update(what, obj);
		}
	}

	/** 集成发送方法 */
	private <T extends EBResponse> void sendRequest(EBRequest gvRequest, final int methodId,
			Type type) {

		VolleyGsonRequest<T> request = new VolleyGsonRequest<T>(context, gvRequest, type,
				new Response.Listener<T>() {

					@Override
					public void onResponse(T response) {
						dataNotify(methodId, response);
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						LogUtils.e(EBikeRequestServiceVolleyImpl.class.getSimpleName(), "请求服务器异常:"
								+ error.getMessage());
						dataNotify(ID_REQUEST_ERROR, null);
					}

				});
		if (methodId == EBikeRequestService.ID_LOGIN) {// 登录的请求要把所有值还原，比如cookie
			request.clearCookie();
		}
		VolleyRequestQueue.getInstance(context).addToRequestQueue(request);
	}


	@Override
	public void login(String userName, String password) {
		EBRequest gvr = new EBRequest(EBikeRequestService.METHOD_LOGIN);
		gvr.setReqeustSubmitType(Method.POST);
		gvr.setReqeustParam("userName", userName);
		gvr.setReqeustParam("password", password);
		sendRequest(gvr, EBikeRequestService.ID_LOGIN, EBResponse.class);
	}

	@Override
	public void cancelApprove(String appIds,String userIds,String reason) {
		EBRequest gvr = new EBRequest(EBikeRequestService.METHOD_CANCELAPPROVE);
		gvr.setReqeustSubmitType(Method.POST);
		gvr.setReqeustParam("appIds", appIds);
		gvr.setReqeustParam("userIds", userIds);
		if(!TextUtils.isEmpty(reason)){
			gvr.setReqeustParam("reason", reason);
		}
		sendRequest(gvr, EBikeRequestService.ID_CANCELAPPROVE, EBResponse.class);
	}

	@Override
	public void agreeApprove(String appIds,String userIds) {
		EBRequest gvr = new EBRequest(EBikeRequestService.METHOD_AGREEAPPROVE);
		gvr.setReqeustSubmitType(Method.POST);
		gvr.setReqeustParam("appIds", appIds);
		gvr.setReqeustParam("userIds", userIds);
		sendRequest(gvr, EBikeRequestService.ID_AGREEAPPROVE, EBResponse.class);
	}

}
