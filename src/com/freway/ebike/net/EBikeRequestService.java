package com.freway.ebike.net;
/**
 * @author Nicolls
 * @Description 请求服务器接口类
 * @date 2015年10月25日
 */
public interface EBikeRequestService {

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
	
	
	/**
	 * @param dataUpdateListener 监听器
	 * @return void
	 * @Description 监听请求返回
	 */
	void setUptateListener(DataUpdateListener dataUpdateListener);

	/**
	 * @param username 用户名
	 * @param password 密码
	 * @Description 用户在APP上填写写户名密码登录时使用该接口
	 */
	void login(String username,String password);
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
	 * @param birthday 可为空  如 1988-10-12
	 * @param photo 可为空 户头像地址
	 * @param email 可为空 户邮箱地址
	 * @Description 用户在APP使用第三方Facebook登录，当从第三方成功登录后，使用该接口注册登录。 注：该接口会检查用户名，如果用户名未注册，则会为该用户注册； 如果用户已注册，则直接返回登录成功。
	 */
	void loginFaceBook(String userid,String username,String gender,String birthday,String photo,String email);
	/**
	 * @param userid (必须) 第三⽅的UserID
	 * @param username (必须) 用户名
	 * @param gender 可为空 性别 female 或male
	 * @param birthday 可为空  如 1988-10-12
	 * @param photo 可为空 户头像地址
	 * @param email 可为空 户邮箱地址
	 * @Description 用户在APP使用第三方Twitter登录，当从第三方成功登录后，使用该接口注册登录。 注：该接口会检查用户名，如果用户名未注册，则会为该用户注册； 如果用户已注册，则直接返回登录成功。
	 */
	void loginTwitter(String userid,String username,String gender,String birthday,String photo,String email);
	
	/**
	 * @param token 登录态token,从注册/登录接口中获得
	 * @return void
	 * @Description 获取⽤户信息，用于用户信息修改和显示
	 */
	void userInfo(String token);
	/**
	 * @param token 登录态token ,从注册/登录接口中获得
	 * @param username 用户名
	 * @param password 用户密码
	 * @param gender 性别 female 或male
	 * @param birthday 生日 如 1988-10-12
	 * @param email 用户邮箱地址
	 * @return void
	 * @Description 用户修改信息
	 */
	void updateUserInfo(String token,String username,String password,String gender,String birthday,String email);
	
}
