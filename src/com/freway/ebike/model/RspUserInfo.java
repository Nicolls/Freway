package com.freway.ebike.model;

/**
 * @author Nicolls
 * @Description 用户信息返回实体
 * @date 2015年10月31日
 */
public class RspUserInfo extends EBResponse {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields data 用户信息
	 */
	private User data;
	public User getData() {
		return data;
	}
	public void setData(User data) {
		this.data = data;
	}
}
