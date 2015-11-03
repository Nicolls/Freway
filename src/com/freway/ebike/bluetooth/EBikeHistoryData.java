package com.freway.ebike.bluetooth;

import java.io.Serializable;
import java.util.Calendar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.freway.ebike.db.DBHelper;
import com.freway.ebike.db.Travel;
import com.freway.ebike.protocol.ProtocolTool;

public class EBikeHistoryData implements Serializable {
	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private static final int WHEEL_VALUE = 2180;
	// 一次行程的Data
	public static  long travel_startTime;
	public static  long travel_endTime;
	public static  int travel_avgSpeed;
	public static  int travel_maxSpeed;
	public static  long travel_spendTime;
	public static  float travel_distance;
	public static  float travel_calorie;
	public static  long travel_cadence;
	public static  double travel_altitude;
	// 控制器data
	public static  int data_id=1;
	public static  int control_time;

	/**
	 * @fields steppedfrequency 踏频量（圈）
	 */
	public static  int stepped_frequency;


	/**
	 * @fields accumulated_mileage 累积骑行里程(km
	 */
	public static  int accumulated_mileage;

	/**
	 * @Fields kcal_value 卡路里
	 */
	private static  int kcal_value;

	//用于计算当前行程
	private static long cal_startTime;
	private static long cal_endTime;
	private static long cal_startDistance;
	private static long cal_endDistance;
	private static long cal_startCalorie;
	private static long cal_endCalorie;
	private static long cal_startCadence;
	private static long cal_endCadence;
	private static boolean isNewTravel=true;
	public static void start(){
		isNewTravel=true;
	}
	/**
	 * @param controlState
	 * @param data
	 * @Description 格式化数据
	 */
	public static void parseHistoryData(byte[] data) {
		byte[] id = {data[0],data[1]};
		byte[] time = {data[2],data[3]};
		byte[] step={data[4],data[5]};
		byte[] mileage={data[6],data[7]};
		data_id=ProtocolTool.byteArrayToInt(id);
		if(data_id>0){
			accumulated_mileage=ProtocolTool.byteArrayToInt(mileage);
			stepped_frequency=ProtocolTool.byteArrayToInt(step);
			control_time=ProtocolTool.byteArrayToInt(time);
			
			
			kcal_value = stepped_frequency / 10 * WHEEL_VALUE * 655 / 21000000;
			accumulated_mileage = accumulated_mileage * WHEEL_VALUE / 1000; // 需要考虑溢出(m)
			
			if(isNewTravel){//新的骑行
				travel_startTime=control_time;
				travel_endTime=control_time;
				travel_avgSpeed=0;
				travel_maxSpeed=0;
				travel_spendTime=0;
				travel_distance=0;
				travel_calorie=0;
				travel_cadence=0;
				travel_altitude=0;
				
				cal_startTime=travel_startTime;
				cal_endTime=travel_startTime;
				cal_startDistance=accumulated_mileage;
				cal_startCalorie=kcal_value;
				cal_startCadence=stepped_frequency;
				isNewTravel=false;
			}else{
				
				cal_endTime=control_time;
				cal_endDistance=accumulated_mileage;
				cal_endCalorie=kcal_value;
				cal_endCadence=stepped_frequency;
				
				travel_spendTime+=(cal_endTime-cal_startTime);//时长
				travel_distance+=(cal_endDistance-cal_startDistance);//距离
				travel_avgSpeed=(int)(travel_distance/travel_spendTime);//平均
				if(travel_avgSpeed>travel_maxSpeed){//最大
					travel_maxSpeed=travel_avgSpeed;
				}
				
				travel_calorie+=(cal_endCalorie-cal_startCalorie);//卡路里
				travel_cadence+=(cal_endCadence-cal_startCadence);//踏频
				travel_altitude+=travel_altitude;//海拔
			}
			
		}
	}
	

	public static String getValueText() {
		String value = "踏频量（圈）"
				+ stepped_frequency + "累积骑行里程"
				+ accumulated_mileage + "电池的安时数(100mah)"  +
				// "循环次数(次)"+cycle_times+
				"卡路里" + kcal_value;
		return value;
	}

}
