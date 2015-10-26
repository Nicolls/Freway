package com.freway.ebike.model;

/**
 * 请求网络返回实体基类
 * 
 * @author mengjk
 *
 *         2015年5月14日
 */

public class EBErrorResponse extends EBResponse {
	private static final long serialVersionUID = 273842578285709923L;
	/** 回来的resultData */
	private String resultData;

	public String getResultData() {
		return resultData;
	}

	public void setResultData(String resultData) {
		this.resultData = resultData;
	}
}
