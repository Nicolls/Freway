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
	public static final String SUCCESS_CODE ="0";
	private static final long serialVersionUID = 273842578285709923L;
	/** 错误代码，0为无错误 */
	private String errCode;
	/** 错误信息，如果为0，则错误信息为空，非0则会返回相应的错误信息 */
	private String errMsg;
	/** 成功，true或者false */
	private String success;

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}
}
