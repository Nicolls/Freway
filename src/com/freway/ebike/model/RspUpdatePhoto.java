package com.freway.ebike.model;

/**
 * @author Nicolls
 * @Description 注册用户返回实体
 * @date 2015年10月31日
 */
public class RspUpdatePhoto extends EBResponse {
	private static final long serialVersionUID = 1L;
	private Object data;
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
}
