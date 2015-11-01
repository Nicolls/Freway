package com.freway.ebike.model;

/**
 * @author Nicolls
 * @Description 用户登录返回实体
 * @date 2015年10月31日
 */
public class RspLogin extends EBResponse {
	private static final long serialVersionUID = 1L;
	private Result data;
	public class Result{
		private String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}
	public Result getData() {
		return data;
	}
	public void setData(Result data) {
		this.data = data;
	}
}
