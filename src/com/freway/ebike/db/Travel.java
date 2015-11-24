package com.freway.ebike.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.freway.ebike.utils.LogUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Travel implements Parcelable {
	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private long id = -1;
	
	/**
	 * @Fields type 行程类别 实时行程0，历史行程1
	 */
	private int type;
	/**
	 * @Fields sync 是否同步到云端 是为1，否为0
	 */
	private int sync;
	
	/**
	 * @Fields startTime 开始时间 ，历史行程开始时间为空  上传时需用格式化成2015-10-10 12:31:21
	 */
	private long startTime;
	/**
	 * @Fields endTime 结束时间，历史行程结束时间为空  上传时，需要格式化成2015-10-10 12:31:21
	 */
	private long endTime;
	/**
	 * @Fields avgSpeed 平均速度 单位 m/s
	 */
	private float avgSpeed; 
	/**
	 * @Fields maxSpeed 最大速度 单位 m/s
	 */
	private float maxSpeed;
	/**
	 * @Fields spendTime 用时 毫秒 需要格式化成 xx天xx时xx分xx秒
	 */
	private long spendTime;
	/**
	 * @Fields distance 距离 米
	 */
	private float distance;
	/**
	 * @Fields calorie 卡路里
	 */
	private float calorie;
	/**
	 * @Fields cadence 踏频量
	 */
	private float cadence;
	/**
	 * @Fields altitude 海拔
	 */
	private double altitude;
	public Travel() {}
	public String formatTime(Date date) {
		String result = "";
		if (date != null) {
			result = format.format(date);
		}
		return result;
	}

	public Date parseTime(String time) {
		Date date = null;
		if (!TextUtils.isEmpty(time)) {
			try {
				date = format.parse(time);
			} catch (ParseException e) {
				LogUtils.e("Travel", e.getMessage());
			}
		}
		return date;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeInt(type);
		dest.writeInt(sync);
		dest.writeLong(startTime);
		dest.writeLong(endTime);
		dest.writeFloat(avgSpeed);
		dest.writeFloat(maxSpeed);
		dest.writeLong(spendTime);
		dest.writeFloat(distance);
		dest.writeFloat(calorie);
		dest.writeFloat(cadence);
		dest.writeDouble(altitude);
	}

	public static final Parcelable.Creator<Travel> CREATOR = new Parcelable.Creator<Travel>() {
		public Travel createFromParcel(Parcel in) {
			return new Travel(in);
		}

		public Travel[] newArray(int size) {
			return new Travel[size];
		}
	};

	private Travel(Parcel in) {
		id = in.readLong();
		type=in.readInt();
		sync=in.readInt();
		startTime = in.readLong();
		endTime = in.readLong();
		avgSpeed = in.readFloat();
		maxSpeed = in.readFloat();
		spendTime = in.readLong();
		distance = in.readFloat();
		calorie = in.readFloat();
		cadence = in.readFloat();
		altitude = in.readDouble();
	}
	
	@Override
	public String toString() {
		String result="id="+id+",type="+type+",sync="+sync+",startTime="+startTime+
				",endTime="+endTime+",avgSpeed="+avgSpeed+",maxSpeed="+maxSpeed+
				",spendTime="+spendTime+",distance="+distance+",calorie="+calorie+",cadence="+cadence+",altitude="+altitude;
		return result;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public float getAvgSpeed() {
		return avgSpeed;
	}
	public void setAvgSpeed(float avgSpeed) {
		this.avgSpeed = avgSpeed;
	}
	public float getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public long getSpendTime() {
		return spendTime;
	}
	public void setSpendTime(long spendTime) {
		this.spendTime = spendTime;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
	public float getCalorie() {
		return calorie;
	}
	public void setCalorie(float calorie) {
		this.calorie = calorie;
	}
	public float getCadence() {
		return cadence;
	}
	public void setCadence(float cadence) {
		this.cadence = cadence;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getSync() {
		return sync;
	}
	public void setSync(int sync) {
		this.sync = sync;
	}

}
