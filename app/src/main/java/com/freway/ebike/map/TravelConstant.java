package com.freway.ebike.map;

public class TravelConstant {
	public static final int TRAVEL_TYPE_IM = 0;//app即时 记录
	public static final int TRAVEL_TYPE_HISTORY = 1;//控制器历史记录
	
	public static final int TRAVEL_SYNC_TRUE = 1;//app即时 记录
	public static final int TRAVEL_SYNC_FALSE = 0;//控制器历史记录
	
	public static final int TRAVEL_STATE_NONE = 0;
	public static final int TRAVEL_STATE_START = 1;
	public static final int TRAVEL_STATE_PAUSE = 2;
	public static final int TRAVEL_STATE_RESUME = 3;
	public static final int TRAVEL_STATE_COMPLETED = 4;
	public static final int TRAVEL_STATE_STOP = 5;
	public static final int TRAVEL_STATE_FAKE_PAUSE = 6;//伪暂停，用于当得到的速度为0，持续几秒时。让UI显示为暂停状态，其他均不改变。而到速度变为正数时，则去掉伪暂停
//	public static final int TRAVEL_STATE_EXIT = 7;//退出应用
	// 用于UI与服务之间传递状态改变的广播
	public static final String ACTION_UI_SERICE_TRAVEL_STATE_CHANGE = "ACTION_UI_SERICE_TRAVEL_STATE_CHANGE";
	public static final String ACTION_UI_SERICE_QUIT_APP = "ACTION_UI_SERICE_QUIT_APP";//退出app发送的广播
	public static final String EXTRA_STATE = "EXTRA_STATE";
	

	// MapService发送给UI的广播action
	public static final String ACTION_MAP_SERVICE_LOCATION_START = "ACTION_MAP_SERVICE_LOCATION_START";
	public static final String ACTION_MAP_SERVICE_LOCATION_END = "ACTION_MAP_SERVICE_LOCATION_END";
	public static final String ACTION_MAP_SERVICE_LOCATION_CHANGE = "ACTION_MAP_SERVICE_LOCATION_CHANGE";
	public static final String EXTRA_LOCATION_CURRENT = "EXTRA_LOCATION_CURRENT";
	public static final String EXTRA_LOCATION_FROM = "EXTRA_LOCATION_FROM";
	public static final String EXTRA_LOCATION_TO = "EXTRA_LOCATION_TO";
	public static final String EXTRA_TRAVEL_ID = "EXTRA_TRAVEL_ID";
}
