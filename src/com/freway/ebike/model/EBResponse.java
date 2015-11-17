package com.freway.ebike.model;

import com.freway.ebike.net.BaseResponse;

/**
 * 请求网络返回实体基类
 * 
 * @author mengjk
 *
 *         2015年5月14日
 */

public class EBResponse extends BaseResponse {
	/** 返回成功的代码 */
	public static final String SUCCESS_CODE = "0000";
	public static final String S_CODE_WRONG = "1001";
	public static final String TOKEN_INVALID = "1002";
	public static final String USER_NAME_EXITS = "1005";
	private static final long serialVersionUID = 273842578285709923L;
	/** code: 为0000时，为正确的业务返回，非0000时，为异常情况，参照具体接⼝异常说明，此时可以根据需要，向客户端提⽰msg的信息 */
	private String code;
	/** 请求返回的信息，如果code为0000，则错误信息为空，非0000则会返回相应的错误信息 */
	private String msg;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
