package com.freway.ebike.db;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class TravelLocation implements Parcelable{
	private long id;
	private long travelId;
	private Location location;
	private float speed;
	private double altitude;
	private boolean isPause;
	private String description;
	public TravelLocation(Location location){
		this.location=location;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(travelId);
		dest.writeParcelable(location, flags);
		dest.writeFloat(speed);
		dest.writeDouble(altitude);
		dest.writeInt(isPause ? 1 : 0);
		dest.writeString(description);
	}
	public static final Parcelable.Creator<TravelLocation> CREATOR = new Parcelable.Creator<TravelLocation>() {
		public TravelLocation createFromParcel(Parcel in) {
			return new TravelLocation(in);
		}

		public TravelLocation[] newArray(int size) {
			return new TravelLocation[size];
		}
	};
	private TravelLocation(Parcel in) {
		id=in.readLong();
		travelId=in.readLong();
		location = in.readParcelable(null);
		speed=in.readFloat();
		altitude=in.readDouble();
		isPause= in.readInt() != 0;
		description=in.readString();
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public boolean isPause() {
		return isPause;
	}
	public void setPause(boolean isPause) {
		this.isPause = isPause;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

}