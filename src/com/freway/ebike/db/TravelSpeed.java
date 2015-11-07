package com.freway.ebike.db;

import android.os.Parcel;
import android.os.Parcelable;

public class TravelSpeed implements Parcelable{
	private long id;
	private long travelId;
	private int speed;
	public TravelSpeed(){}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(travelId);
		dest.writeInt(speed);
	}
	public static final Parcelable.Creator<TravelSpeed> CREATOR = new Parcelable.Creator<TravelSpeed>() {
		public TravelSpeed createFromParcel(Parcel in) {
			return new TravelSpeed(in);
		}

		public TravelSpeed[] newArray(int size) {
			return new TravelSpeed[size];
		}
	};
	private TravelSpeed(Parcel in) {
		id=in.readLong();
		travelId=in.readLong();
		speed=in.readInt();
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getTravelId() {
		return travelId;
	}
	public void setTravelId(long travelId) {
		this.travelId = travelId;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}

}