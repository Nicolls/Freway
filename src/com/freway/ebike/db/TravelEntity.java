package com.freway.ebike.db;

import java.io.Serializable;

import android.provider.BaseColumns;

public class TravelEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**行程ID*/
	private int id; 
	/**里程*/
	private String mileage;
	/**最大速度*/// 
	private String maxSpeed; 
	/**最小速度*/
	private String minSpeed; 
	/**平均速度*/
	private String averageSpeed; 
	/**用时*/
	private String spendTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMileage() {
		return mileage;
	}
	public void setMileage(String mileage) {
		this.mileage = mileage;
	}
	public String getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(String maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public String getMinSpeed() {
		return minSpeed;
	}
	public void setMinSpeed(String minSpeed) {
		this.minSpeed = minSpeed;
	}
	public String getAverageSpeed() {
		return averageSpeed;
	}
	public void setAverageSpeed(String averageSpeed) {
		this.averageSpeed = averageSpeed;
	}
	public String getSpendTime() {
		return spendTime;
	}
	public void setSpendTime(String spendTime) {
		this.spendTime = spendTime;
	}

	public static final String TABLE_NAME = "tb_travel";
	public static final class TravelColumns implements BaseColumns{
		public static final String MILEAGE = "mileage";
		public static final String MAXSPEED = "maxSpeed";
		public static final String MINSPEED = "minSpeed";
		public static final String AVERAGESPEDD = "averageSpedd";
		public static final String SPENDTIME = "spendTime";
	}

}
