package com.freway.ebike.bluetooth;

import com.freway.ebike.utils.LogUtils;

/**骑行状态*/
public class EBikeStatus {
	private final static String TAG=EBikeStatus.class.getSimpleName();
	private static int bit_7 = 0;
	private static int bit_6 = 0;
	private static int bit_5 = 0;
	private static int bit_4 = 0;
	private static int bit_3 = 0;
	private static int bit_2 = 0;
	private static int bit_1 = 0;
	private static int bit_0 = 0;
	/**当前骑行状态数据*/
	private static byte bikeData;
	/**获取骑行数据*/
	public static byte getBikeData() {
		return bikeData;
	}
	/**存储状态的数组*/
	private final static int[] status = { bit_7, bit_6, bit_5, bit_4, bit_3,
			bit_2, bit_1, bit_0};
	/**运动*/
	public final static int BIKING_SPORT = 0;
	/**电动*/
	public final static int BIKING_ELEC = 1;
	/**助力1*/
	public final static int BIKING_HELP_POWER_1 = 2;
	/**助力2*/
	public final static int BIKING_HELP_POWER_2 = 3;
	/**助力3*/
	public final static int BIKING_HELP_POWER_3 = 4;
	/**车前灯*/
	public final static int BIKE_FRONT_LIGHT = 5;
	/**车后灯*/
	public final static int BIKE_BACK_LIGHT = 6;
	/**电话*/
	public final static int PHONE_CALL = 7;
	/**短信*/
	public final static int RECEIVE_MESSAGE = 8;
	/**
	 * @param control 控制命令
	 * @param flag 控制值 1 true,0 false
	 * 通过传入的控制和值设置骑行状态，并返回设置后的状态数据
	 * */
	public static byte setBikeData(int control,int flag) {
		byte result = 0;
		switch (control) {
		case BIKING_SPORT:
			bit_0=0;
			bit_1=0;
			bit_2=0;
			break;
		case BIKING_ELEC:
			bit_0=1;
			bit_1=0;
			bit_2=0;
			break;
		case BIKING_HELP_POWER_1:
			bit_0=0;
			bit_1=1;
			bit_2=0;
			break;
		case BIKING_HELP_POWER_2:
			bit_0=0;
			bit_1=0;
			bit_2=1;
			break;
		case BIKING_HELP_POWER_3:
			bit_0=0;
			bit_1=1;
			bit_2=1;
			break;
		case BIKE_FRONT_LIGHT:
			bit_3=flag;
			break;
		case BIKE_BACK_LIGHT:
			bit_4=flag;
			break;
		case PHONE_CALL:
			bit_5=flag;
			break;
		case RECEIVE_MESSAGE:
			bit_6=flag;
			break;
		default:
			break;
		}
		String s="";
		for(int i=0;i<status.length;i++){
			s+=status[i];
		}
		result=Byte.parseByte(s,2);
		bikeData=result;
		LogUtils.i(TAG, "根据控制要发出去的二进制数据："+s+"--二进制值："+result);
		return result;
	}
	
}
