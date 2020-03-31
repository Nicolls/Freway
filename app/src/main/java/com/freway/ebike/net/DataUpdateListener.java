/**
 * 
 */
package com.freway.ebike.net;

/**
 * HTTP请求返回数据会更新监听类
 * 
 * @author Nicolls
 */
public interface DataUpdateListener {
	/**
	 * 更新数据方法
	 * 
	 * @param id
	 *            请求数据时传入的id
	 * @param obj
	 *            返回的实体类，如不能确定为所要的实体，请先做instance判断
	 */
	void update(int id, Object obj);
}
