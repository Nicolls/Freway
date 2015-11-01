/**
 * 
 */
package com.freway.ebike.net;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.freway.ebike.model.EBErrorResponse;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * 自定义volley请求Gson解析返回数据类
 * 
 * @param <T>
 *            泛型，用于解析不同的数据返回模型
 * @author Nicolls
 *
 *         2015年6月15日
 */
public class VolleyGsonRequest<T> extends Request<T> {
	private static final String TAG = VolleyGsonRequest.class.getSimpleName();
//	private static final String KEY = "eb-token";
//	private static final String ENCYPT_CONTENT = "123";
//	private static final String EB_TOKEN = "OzSBLmLbGPXR0kWccMmFLA==";
	private static List<String> cookies;
	private static Map<String, String> headers = null;
	private final Gson gson = new Gson();
	private final Type type;
	private final Map<String, String> params;
	private final Listener<T> listener;
	private Context context;

	// private static final String
	// EB_TOKEN=DesUtil.getInstance(KEY).encryptStr(ENCYPT_CONTENT);
	/**
	 * Make a GET request and return a parsed object from JSON.
	 *
	 * @param url
	 *            URL of the request to make
	 * @param clazz
	 *            Relevant class object, for Gson's reflection
	 * @param params
	 *            Map of request params
	 */
	public VolleyGsonRequest(Context context, BaseRequest bRquest, Type type, Listener<T> listener,
			ErrorListener errorListener) {
		super(bRquest.getReqeustSubmitType(), bRquest.getReqeustURL(), errorListener);
		String requestType="";
		if(bRquest.getReqeustSubmitType()==1){
			requestType="Post";
		}else if(bRquest.getReqeustSubmitType()==0){
			requestType="Get";
		}
		LogUtils.systemOut(requestType+"请求URL:" + bRquest.getReqeustURL());
		this.context = context;
		this.type = type;
		this.params = bRquest.getReqeustParam();
		this.listener = listener;
		this.setTag(bRquest.getReqeustURL());
	}

	/** 清除cookie */
	public void clearCookie() {
		cookies = null;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		if (headers == null) {
			headers = new HashMap<String, String>();
//			headers.put("Client-Type", "Gridview Android");
//			headers.put("Client-Version", CommonUtil.getAppVersion(context));
//			headers.put("Device-Id", CommonUtil.getPhoneImei(context));
//			headers.put("Gridview-Token", EB_TOKEN);
		}
		StringBuffer cookieBuf = new StringBuffer();
		if (cookies != null) {
			for (String value : cookies) {
				cookieBuf.append(value);
			}
			headers.put("Cookie", cookieBuf.toString());
		}
		return headers;
	}

	@Override
	protected void deliverResponse(T response) {
		listener.onResponse(response);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		String json = "";
		try {
			if (response.headers.containsKey("Set-Cookie")) {
				String cookie = response.headers.get("Set-Cookie");
				if (cookie.contains("JSESSIONID")) {// 如果有sessionID说明重新组织会话，要把cookie删除掉
					cookies = null;
				}
				if (cookies == null) {
					cookies = new ArrayList<String>();
				}
				if (!cookies.contains(cookie)) {
					cookies.add(cookie);
				}
			}
			// String rawCookies = response.headers.get("Set-Cookie");
			// cookie=rawCookies.substring(0, rawCookies.indexOf(";"));

			json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			LogUtils.systemOut("返回的数据：" + json);
			// Type type = new TypeToken<ListResource<Room>>(){}.getType();
			T object = gson.fromJson(json, type);
			((BaseResponse) object).setText(json);
			return (Response<T>) Response.success(object,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			LogUtils.e(TAG, "UnsupportedEncodingException解析错误请查看返回数据" + e.getMessage());
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			LogUtils.e(TAG,"解析数据出问题了JsonSyntaxException，但是数据可能是完整的");
			EBErrorResponse br = null;
			Response<T> rsp = null;
			try {
				br = (EBErrorResponse) gson.fromJson(json, EBErrorResponse.class);
				((BaseResponse) br).setText(json);
				rsp = (Response<T>) Response.success(br,
						HttpHeaderParser.parseCacheHeaders(response));
			} catch (Exception e2) {
				rsp = Response.error(new ParseError(e));
				LogUtils.e(TAG,
						"JsonSyntaxException解析错误,请查看返回数据：" + json + "--错误信息:" + e.getMessage());
			}
			return rsp;
		}
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		// TODO Auto-generated method stub
		if (params != null) {
			LogUtils.systemOut("请求的参数:" + params.toString());
		}
		return params == null ? super.getParams() : params;
	}
}