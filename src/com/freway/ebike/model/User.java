package com.freway.ebike.model;

import java.io.Serializable;

/**
 * @author Nicolls
 * @Description 用户信息
 * @date 2015年10月31日
 */
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * @Fields userid 用户ID只有第三方才存在
	 * */
	private String userid;
	/**
	 * @Fields username 名称
	 */
	private String username;
	/**
	 * @Fields gender 性别
	 */
	private String gender;
	
	/**
	 * @Fields birthday 生日
	 */
	private String birthday;
	
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
	/**总里程*/
	private String total_miles;
	/**总时间*/
	private String total_hour;
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
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
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
	public String getTotal_miles() {
		return total_miles;
	}
	public void setTotal_miles(String total_miles) {
		this.total_miles = total_miles;
	}
	public String getTotal_hour() {
		return total_hour;
	}
	public void setTotal_hour(String total_hour) {
		this.total_hour = total_hour;
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
}
