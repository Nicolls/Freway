package com.freway.ebike.model;

/**
 * @author Nicolls
 * @Description 注册用户返回实体
 * @date 2015年10月31日
 */
public class RspUpdatePhoto extends EBResponse {
	private static final long serialVersionUID = 1L;
	private ResultData data;
	public class ResultData{
		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
	public ResultData getData() {
		return data;
	}
	public void setData(ResultData data) {
		this.data = data;
	}
	
}
