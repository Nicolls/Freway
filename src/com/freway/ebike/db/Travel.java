package com.freway.ebike.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.freway.ebike.utils.LogUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Travel implements Parcelable{
	static SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
	private long id=-1;
	private Date startTime;
	private Date endTime;
	private long spendTime;
	private float distance;
	private int state;//行程状态，只用于行程状态判断，不入库
//	private List<TravelLocation> travelRoute;
	public Travel(){
		
	}
	
	public String formatTime(Date date){
		String result="";
		if(date!=null){
			result=format.format(date);
		}
		return result;
	}
	
	public Date parseTime(String time){
		Date date=null;
		if(!TextUtils.isEmpty(time)){
			try {
				date=format.parse(time);
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
		dest.writeString(format.format(startTime));
		dest.writeString(format.format(startTime));
		dest.writeLong(spendTime);
		dest.writeFloat(distance);
		dest.writeInt(state);
//		dest.writeList(travelRoute);
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
		id=in.readLong();
		try {
			startTime=format.parse(in.readString());
			endTime=format.parse(in.readString());
		} catch (ParseException e) {
			LogUtils.e("ParseException", e.getMessage());
		}
		spendTime=in.readLong();
		distance=in.readFloat();
		state=in.readInt();
//		travelRoute=in.readArrayList(TravelLocation.class.getClassLoader());
	}
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public long getSpendTime() {
		return spendTime;
	}
	public void setSpendTime(long spendTime) {
		this.spendTime = spendTime;
	}
//	public List<TravelLocation> getTravelRoute() {
//		return travelRoute;
//	}
//	public void setTravelRoute(List<TravelLocation> travelRoute) {
//		this.travelRoute = travelRoute;
//	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
