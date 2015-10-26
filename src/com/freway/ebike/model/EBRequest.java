/**
 * 
 */
package com.freway.ebike.model;

import com.freway.ebike.common.EBConstant;
import com.freway.ebike.net.BaseRequest;

/**
 * 一个完整请求实体 包含请求服务器地址，服务器端口等
 * 
 * @author mengjk
 *
 *         2015年5月14日
 */
public class EBRequest extends BaseRequest {
	public static final String REQUEST_HEAD_HTTP = "http://";
	public static final String REQUEST_HEAD_HTTPS = "https://";
	/** 请求服务器地址 */
	public static String requestHost = EBConstant.DEFAULT_HOST;
	/** 请求服务器端口 */
	public static int requestPort = EBConstant.DEFAULT_PORT;
	private static final long serialVersionUID = -1909392153140853242L;
	/** 协议类型 */
	private String httpHeadType = REQUEST_HEAD_HTTP;

	public EBRequest(String methodUrl) {
		String url = httpHeadType + requestHost + ":" + requestPort + methodUrl;
		super.setReqeustURL(url);
	}

	public String getHttpHeadType() {
		return httpHeadType;
	}

	public void setHttpHeadType(String httpHeadType) {
		this.httpHeadType = httpHeadType;
	}
}
