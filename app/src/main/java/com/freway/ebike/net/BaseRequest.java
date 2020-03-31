package com.freway.ebike.net;

import java.io.Serializable;
import java.util.HashMap;

/** 请求实体基类 */
public class BaseRequest implements Serializable {
	private static final long serialVersionUID = -1745945259352926432L;
	// 是否加密
	private boolean isEncrypt = false;

	// 请求提交的类型，默认为get
	private int reqeustSubmitType = Method.POST;
	// 请求返回的数据类型，默认为文本数据
	private ResultDataType resultDataType = ResultDataType.OBJECT;

	// 请求URL
	private String reqeustURL;
	// 请求参数
	private HashMap<String, String> reqeustParam;

	/** http请求头 */
	private HashMap<String, String> httpHeaders;

	/**
	 * 默认构造函数
	 * */
	public BaseRequest() {

	}

	/**
	 * 构造函数
	 * 
	 * @param requestUrl
	 *            请求Url
	 * */
	public BaseRequest(String requestUrl) {
		this.reqeustURL = requestUrl;
	}

	/** 返回数据类型枚举 */
	public enum ResultDataType {
		OBJECT, TEXT, INPUTSTREAM
	}

	/**
	 * Supported request methods.
	 */
	public interface Method {
		int DEPRECATED_GET_OR_POST = -1;
		int GET = 0;
		int POST = 1;
		int PUT = 2;
		int DELETE = 3;
		int HEAD = 4;
		int OPTIONS = 5;
		int TRACE = 6;
		int PATCH = 7;
	}

	public HashMap<String, String> getHttpHeaders() {
		return httpHeaders;
	}

	public void setHttpHeaders(HashMap<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public ResultDataType getResultDataType() {
		return resultDataType;
	}

	public void setResultDataType(ResultDataType resultDataType) {
		this.resultDataType = resultDataType;
	}

	public String getReqeustURL() {
		String url = reqeustURL;
		StringBuffer sb = new StringBuffer();
		switch (reqeustSubmitType) {
		case Method.GET:
			if (reqeustParam != null) {
				for (String name : reqeustParam.keySet()) {
					sb.append(name + "=" + reqeustParam.get(name) + "&");
				}
				url = url + "?" + sb.deleteCharAt(sb.length() - 1).toString();// 把最后一个&删除
			}
			break;
		case Method.POST:

			break;
		default:

			break;
		}
		return url;
	}

	public void setReqeustURL(String reqeustURL) {
		this.reqeustURL = reqeustURL;
	}

	public HashMap<String, String> getReqeustParam() {
		return reqeustParam;
	}

	/** 添加参数跟值 */
	public void setReqeustParam(String key, String value) {
		if (null == this.reqeustParam) {
			this.reqeustParam = new HashMap<String, String>();
		}
		this.reqeustParam.put(key, value);
	}

	public boolean isEncrypt() {
		return isEncrypt;
	}

	public void setEncrypt(boolean isEncrypt) {
		this.isEncrypt = isEncrypt;
	}

	public int getReqeustSubmitType() {
		return reqeustSubmitType;
	}

	public void setReqeustSubmitType(int reqeustSubmitType) {
		this.reqeustSubmitType = reqeustSubmitType;
	}
}
