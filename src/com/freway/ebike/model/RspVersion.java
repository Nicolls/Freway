package com.freway.ebike.model;

/**
 * @author Nicolls
 * @Description 更新版本返回实体
 * @date 2015年10月31日
 */
public class RspVersion extends EBResponse {
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields data 用户信息
	 */
	private ResultData data;
	public ResultData getData() {
		return data;
	}
	public void setData(ResultData data) {
		this.data = data;
	}
	public class ResultData{
		private String newest;
		private String description;
		private String url;
		private String force_update;
		public String getNewest() {
			return newest;
		}
		public void setNewest(String newest) {
			this.newest = newest;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getForce_update() {
			return force_update;
		}
		public void setForce_update(String force_update) {
			this.force_update = force_update;
		}
	}
}
