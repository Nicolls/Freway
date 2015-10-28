package com.freway.ebike.map;

import java.io.Serializable;

/**记录每一个坐标*/
public class TravelLocation implements Serializable{
	private static final long serialVersionUID = 1L;
	public double latitude;//纬度
	public double longitude;//经度
	public double altitude;//海拔
	public float speed;//速度
	public boolean isPause;//是否在这个点暂停
}
