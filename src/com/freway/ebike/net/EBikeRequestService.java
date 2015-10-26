package com.freway.ebike.net;
/**
 * @author Nicolls
 * @Description 请求服务器接口类
 * @date 2015年10月25日
 */
public interface EBikeRequestService {

	static final int ID_REQUEST_ERROR = -1;
	/** 方法ID **/
	/** 资源拒绝申请 */
	static final int ID_LOGIN=1;
	/** 资源拒绝申请 */
	static final int ID_CANCELAPPROVE=2;
	
	/** 资源通过申请 */
	static final int ID_AGREEAPPROVE=3;
	
	
	/** 方法名 **/
	/** 资源拒绝申请 */
	static final String METHOD_LOGIN="login.action";
	/** 资源拒绝申请 */
	static final String METHOD_CANCELAPPROVE="/job/cancelApprove.action";
	
	/** 资源通过申请 */
	static final String METHOD_AGREEAPPROVE="/job/agreeApprove.action";
	
	// 监听
	void setUptateListener(DataUpdateListener dataUpdateListener);

	/**
	 *登录
	 *
	 *@param userName 用户名
	 *@param password 密码
	 * 
	 * */
	void login(String userName,String password);
	/**
	 *资源审批拒绝
	 *
	 *@param appIds 要审批的资源id，多个id用","，隔开
	 *@param userIds 要审批的资源userId，多个id用","隔开
	 *@param reason 拒绝理由
	 * 
	 * */
	void cancelApprove(String appIds,String userIds,String reason);
	/**
	 *资源审批通过
	 *
	 *@param appIds 要审批的资源id，多个id用","，隔开
	 *@param userIds 要审批的资源userId，多个id用","隔开
	 * 
	 * */
	void agreeApprove(String appIds,String userIds);
}
