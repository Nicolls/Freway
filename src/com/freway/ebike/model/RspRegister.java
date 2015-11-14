package com.freway.ebike.model;

import com.freway.ebike.model.RspLogin.Result;

/**
 * @author Nicolls
 * @Description 注册用户返回实体
 * @date 2015年10月31日
 */
public class RspRegister extends EBResponse {
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
