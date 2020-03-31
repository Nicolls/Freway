package com.freway.ebike.model;

/**
 * @author Nicolls
 * @Description 更新用户信息返回实体
 * @date 2015年10月31日
 */
public class RspUpdateUserInfo extends EBResponse {
	private static final long serialVersionUID = 1L;
	private Object data;
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
