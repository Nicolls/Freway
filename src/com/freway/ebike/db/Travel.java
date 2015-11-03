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
	private long startTime;
	private long endTime;
	private int avgSpeed;
	private int maxSpeed;
	private long spendTime;
	private float distance;
	private float calorie;
	private long cadence;
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
		dest.writeLong(startTime);
		dest.writeLong(endTime);
		dest.writeInt(avgSpeed);
		dest.writeInt(maxSpeed);
		dest.writeLong(spendTime);
		dest.writeFloat(distance);
		dest.writeFloat(calorie);
		dest.writeLong(cadence);
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
		startTime = in.readLong();
		endTime = in.readLong();
		avgSpeed = in.readInt();
		maxSpeed = in.readInt();
		spendTime = in.readLong();
		distance = in.readFloat();
		calorie = in.readFloat();
		cadence = in.readLong();
		altitude = in.readDouble();
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
	public int getAvgSpeed() {
		return avgSpeed;
	}
	public void setAvgSpeed(int avgSpeed) {
		this.avgSpeed = avgSpeed;
	}
	public int getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(int maxSpeed) {
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
	public void setCadence(long cadence) {
		this.cadence = cadence;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

}
