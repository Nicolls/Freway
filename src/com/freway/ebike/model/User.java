package com.freway.ebike.model;

import java.io.Serializable;

/**
 * @author Nicolls
 * @Description 用户信息
 * @date 2015年10月31日
 */
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final String GENDER_MALE = "male";
	public static final String GENDER_FEMALE = "female";
	/**
	 * @Fields userid 用户ID只有第三方才存在
	 * */
	private String userid;
	/**
	 * @Fields username 名称
	 */
	private String username;
	/**
	 * @Fields password 密码
	 */
	private String password;
	/**
	 * @Fields gender 性别 可为空 性别 female 或male
	 */
	private String gender;
	
	/**
	 * @Fields photo 头像URL地址
	 */
	private String photo;
	/**
	 * @Fields email 邮箱
	 */
	private String email;
	
	/**
	 * 身高
	 */
	private String height;
	/**
	 * 体重
	 */
	private String weight;
	/**
	 * 年龄
	 */
	private String age;
	/**总里程，单位km*/
	private String total_distance="0";
	/**总时间，单位秒*/
	private String total_time="0";
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getTotal_distance() {
		return total_distance;
	}
	public void setTotal_distance(String total_distance) {
		this.total_distance = total_distance;
	}
	public String getTotal_time() {
		return total_time;
	}
	public void setTotal_time(String total_time) {
		this.total_time = total_time;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
