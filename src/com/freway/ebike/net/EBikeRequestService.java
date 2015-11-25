package com.freway.ebike.net;

import com.freway.ebike.model.User;

/**
 * @author Nicolls
 * @Description 请求服务器接口类
 * @date 2015年10月25日
 */
public interface EBikeRequestService {

	/**
	 * 接口测试
	 * http://www.ifreway.com/app/index.php/service/test
	 * */
	/**请求出错*/
	static final int ID_REQUEST_ERROR = -1;
	/** 方法ID **/
	/** 普通登录 */
	static final int ID_LOGIN=1;
	/** 普通注册 */
	static final int ID_REGISTER=2;
	/** 第三方登录注册facebook */
	static final int ID_LOGINFACEBOOK=3;
	/** 第三方登录注册twitter */
	static final int ID_LOGINTWITTER=4;
	/** 用户信息获取 */
	static final int ID_USERINFO=5;
	/** 修改用户信息 */
	static final int ID_UPDATEUSERINFO=6;
	/** 上传行程*/
	static final int ID_UPLOADTRAVEL=7;
	/**上传头像*/
	static final int ID_PHOTO=8;
	/**版本更新*/
	static final int ID_VERSION=9;
	
	/** 方法名 **/
	/** 普通登录 */
	static final String METHOD_LOGIN="/service/login";
	/** 普通注册 */
	static final String METHOD_REGISTER="/service/register";
	/** 第三方登录注册facebook */
	static final String METHOD_LOGINFACEBOOK="/service/login/facebook";
	/** 第三方登录注册twitter */
	static final String METHOD_LOGINTWITTER="/service/login/twiter";
	/** 用户信息获取 */
	static final String METHOD_USERINFO="/service/info";
	/** 修改用户信息 */
	static final String METHOD_UPDATEUSERINFO="/service/info/update";
	/** 上传行程*/
	static final String METHOD_UPLOADTRAVEL="/service/journey/create";
	/**上传头像*/
	static final String METHOD_PHOTO="/service/info/photo";
	/**版本更新*/
	static final String METHOD_VERSION="/service/version";
	/**
	 * @param dataUpdateListener 监听器
	 * @return void
	 * @Description 监听请求返回
	 */
	void setUptateListener(DataUpdateListener dataUpdateListener);

	/**
	 * @param email 用户邮箱
	 * @param password 密码
	 * @Description 用户在APP上填写写户名密码登录时使用该接口
	 */
	void login(String email,String password);
	/**
	 * @param email 邮箱
	 * @param username 用户名
	 * @param password 用户密码
	 * @Description 用户在APP上直接填信息注册时使用该接口
	 */
	void register(String email,String username,String password);
	/**
	 * @param userid (必须) 第三⽅的UserID
	 * @param username (必须) 用户名
	 * @param gender 可为空 性别 female 或male
	 * @param photo 可为空 户头像地址
	 * @param email 可为空 户邮箱地址
	 * @Description 用户在APP使用第三方Facebook登录，当从第三方成功登录后，使用该接口注册登录。 注：该接口会检查用户名，如果用户名未注册，则会为该用户注册； 如果用户已注册，则直接返回登录成功。
	 */
	void loginFaceBook(String userid,String username,String gender,String photo,String email);
	/**
	 * @param userid (必须) 第三⽅的UserID
	 * @param username (必须) 用户名
	 * @param gender 可为空 性别 female 或male
	 * @param photo 可为空 户头像地址
	 * @param email 可为空 户邮箱地址
	 * @Description 用户在APP使用第三方Twitter登录，当从第三方成功登录后，使用该接口注册登录。 注：该接口会检查用户名，如果用户名未注册，则会为该用户注册； 如果用户已注册，则直接返回登录成功。
	 */
	void loginTwitter(String userid,String username,String gender,String photo,String email);
	
	/**
	 * @param token 登录态token,从注册/登录接口中获得
	 * @return void
	 * @Description 获取⽤户信息，用于用户信息修改和显示
	 */
	void userInfo(String token);
	/**
	 * @param token 登录态token ,从注册/登录接口中获得
	 * @param user 用户信息
	 * @Description 用户修改信息
	 */
	void updateUserInfo(String token,User user);
	
	/**
	 * @param token 登录态token ,从注册/登录接口中获得
	 * @param type 行程类别，实时行程为0，历史行程为1
	 * @param stime 开始时间 ，历史行程开始时间为空 2015-10-10 12:31:21
	 * @param etime 结束时间，历史行程结束时间为空  2015-10-10 12:31:21
	 * @param distance * 单位:米 总距离
	 * @param time * 单位:秒 总时间
	 * @param cadence * 单位:次 总踏频量
	 * @param calories * 总消耗的卡路里
	 * @param speedList 当为历史行程是为每百米的平均速度，当为即时行程是为每百秒的平均速度 [10,20,30] 单位 m/s
	 * @param locationList 移动轨迹，历史行程时为空 [["x1","y1"],["x2","y2"],["x3","y3"]]
	 * @param topSpeed 最大速度 * 单位:米/秒
	 * @param avgSpeed 平均速度
	 * @param photo 行程缩略图
	 * @return void
	 * @Description 用户修改信息
	 */
	void upLoadTravel(String token,String type,String stime,String etime,String distance,String time,String cadence,String calories,String speedList,String locationList,String topSpeed,String avgSpeed,String photo);
	
	/**
	 * 
	 * @param token 登录token
	 * @param photoPath 本地头像路径
	 * @return void
	 * @Description 更新用户头像
	 */
	void updatePhoto(String token,String photoPath);
	
	/**
	 * @param type 设备类型 Android
	 * @param version 当前应用版本号，如1.0.0
	 * @return void
	 * @Description 查询更新
	 */
	void version(String type,String version);
}
