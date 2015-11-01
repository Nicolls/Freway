/**
 * 
 */
package com.freway.ebike.model;

import java.util.HashMap;

import com.freway.ebike.common.EBConstant;
import com.freway.ebike.net.BaseRequest;
import com.google.gson.Gson;

/**
 * 一个完整请求实体 包含请求服务器地址，服务器端口等
 * 
 * @author mengjk
 *
 *         2015年5月14日
 */
public class EBRequest extends BaseRequest {
	private static final long serialVersionUID = -1909392153140853242L;
	public static final String REQUEST_HEAD_HTTP = "http://";
	public static final String REQUEST_HEAD_HTTPS = "https://";
	private static Gson gson=new Gson();
	/** 请求服务器地址 */
	public static String requestHost = EBConstant.DEFAULT_HOST;
	/** 请求服务器端口 */
	public static int requestPort = EBConstant.DEFAULT_PORT;
	/** 请求服务器私钥 */
	public static String requestKey = EBConstant.DEFAULT_KEY;
	/** 协议类型 */
	private String httpHeadType = REQUEST_HEAD_HTTP;
	/**数据参数*/
	private HashMap<String, String> dataParam;
	/**
	 * 构造方法
	 * @param methodUrl 传入的接口名称地址。必须以"/"开始
	 * */
	public EBRequest(String methodUrl) {
		String url = "";
		if(requestPort>0){
			url = httpHeadType + requestHost + ":" + requestPort + methodUrl;
		}else{
			url = httpHeadType + requestHost + methodUrl;
		}
		super.setReqeustURL(url);
	}
	/** 添加参数跟值 */
	public void setDataParam(String key, String value) {
		if (null == this.dataParam) {
			this.dataParam = new HashMap<String, String>();
		}
		this.dataParam.put(key, value);
	}
	/** 添加参数跟值 */
	public String getDataParam() {
		String data=gson.toJson(this.dataParam);
		return data;
	}
	public String getHttpHeadType() {
		return httpHeadType;
	}

	public void setHttpHeadType(String httpHeadType) {
		this.httpHeadType = httpHeadType;
	}
}
