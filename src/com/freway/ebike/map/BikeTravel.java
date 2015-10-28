package com.freway.ebike.map;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class BikeTravel implements Serializable{
	private static final long serialVersionUID = 1L;
	private Date startTime;
	private Date endTime;
	private long spendTime;
	private float distance;
	private List<TravelLocation> travelRoute;
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
	public List<TravelLocation> getTravelRoute() {
		return travelRoute;
	}
	public void setTravelRoute(List<TravelLocation> travelRoute) {
		this.travelRoute = travelRoute;
	}
	public float getDistance() {
		return distance;
	}
	public void setDistance(float distance) {
		this.distance = distance;
	}
}
