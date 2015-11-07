package com.freway.ebike.bluetooth;

import java.io.Serializable;
import java.util.Calendar;

import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.db.DBHelper;
import com.freway.ebike.db.Travel;
import com.freway.ebike.db.TravelSpeed;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.protocol.ProtocolTool;
import com.freway.ebike.utils.NetUtil;
import com.freway.ebike.utils.ToastUtils;

import android.content.Context;

public class EBikeTravelData implements Serializable {
	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @Fields WHEEL_VALUE 轮径
	 */
	private static final int WHEEL_VALUE = 2180;
	/**
	 * @Fields RECORD_TIME_FRE 每一百秒记录一次平均速度点用于描绘速度曲线
	 */
	private static final int RECORD_TIME_FRE=100;//每100秒记录一次
	/** 
	 * @Fields MUST_MIN_TRAVEL 归短行程，要记录的行程至少要大于最短行程，否则丢弃
	 */
	private static final float MUST_MIN_TRAVEL = 10;// 最短行程10米
	//行程data
	/**
	 * @Fields travelId 行程ID
	 */
	public  long travelId=-1;
	/**
	 * @Fields type 骑行类别，即时
	 */
	public  int type=TravelConstant.TRAVEL_TYPE_IM;
	/**
	 * @Fields startTime 开始时间 单位毫秒
	 */
	public  long startTime;
	/**
	 * @Fields endTime 结束时间 单位毫秒
	 */
	public  long endTime;
	/**
	 * @Fields avgSpeed 平均速度  单位米/s或者是迈
	 */
	public  int avgSpeed;
	/**
	 * @Fields insSpeed 瞬时速度  单位米/s或者是迈
	 */
	public  int insSpeed;
	/**
	 * @Fields maxSpeed 最大速度 单位米/s或者是迈
	 */
	public  int maxSpeed;
	/**
	 * @Fields spendTime 行程时间 毫秒
	 */
	public  long spendTime; 
	/**
	 * @Fields distance 行程距离 单位 米
	 */
	public  int distance;
	/**
	 * @Fields calorie 卡路里 单位cal 
	 */
	public  int calorie;
	
	/**
	 * @Fields cadence 踏频量（圈）单位次
	 */
	public  int cadence;
	/**
	 * @Fields altitude 海拔 单位米
	 */
	public  double altitude;
	/**
	 * @fields messageNoticeGet 短信提醒标志接收完成
	 */
	public  int messageNoticeGet;
	/**
	 * @fields phoneCallGet 电话呼叫标志接收完成
	 */
	public  int phoneCallGet;
	/**
	 * @fields batConnect 电池包连接标志
	 */
	public  int batConnect;
	/**
	 * @fields ctrlerOvercp 控制器过流保护
	 */
	public  int ctrlerOvercp;
	/**
	 * @fields ctrlerLowvp 控制器欠压保护
	 */
	public  int ctrlerLowvp;
	/**
	 * @fields energyCycle 能量回收状态
	 */
	public  int energyCycle;
	/**
	 * @fields ctrlerErr 控制器故障
	 */
	public  int ctrlerErr;
	/**
	 * @fields backLed 后灯状态
	 */
	public  int backLed;
	/**
	 * @fields frontLed 前灯状态
	 */
	public  int frontLed;
	/**
	 * @fields sportMode 运动模式
	 */
	public  int sportMode;
	/**
	 * @fields assisMode 助力模式
	 */
	public  int assisMode;

	/**
	 * @fields elecMode 电动模式
	 */
	public  int elecMode;


//	/**
//	 * @fields biking_speed 骑行速度，需要根据车轮大小和控制器返回的速度进行计算
//	 */
//	public  int biking_speed;

//	/**
//	 * @fields accumulated_mileage 累积骑行里程(km
//	 */
//	public  int accumulated_mileage;

	/**
	 * @fields batteryAh 电池的安时数(100mah)
	 */
	public  int batteryAh;
	

	/**
	 * @fields biking_state_change 骑行状态改变标志
	 */
	public  int bikingStateChange;

	/**
	 * @fields batteryResidueCapacity 剩余容量 单位%
	 */
	public  int batteryResidueCapacity;

	/**
	 * @fields miCapacity 剩余里程
	 */
	public  int miCapacity;
	/**
	 * @fields temperature 温度(℃)
	 */
	public  int temperature;
	/**
	 * @fields cycle_times 循环次数(次) 1.0暂且没有，所以不用加
	 */
	// public int cycle_times;

//	/**
//	 * @Fields kcal_value 卡路里
//	 */
//	public static int kcal_value;

	//用于计算当前行程
	private  long cal_startTime;
	private  long cal_tempTime;
	private  long cal_endTime;
	private  long cal_startDistance;
	private  long cal_tempDistance;
	private  long cal_endDistance;
	private  long cal_startCalorie;
	private  long cal_tempCalorie;
	private  long cal_endCalorie;
	private  long cal_startCadence;
	private  long cal_tempCadence;
	private  long cal_endCadence;
	private  boolean isNewTravel=true;
	private Context context;
	private static EBikeTravelData mEBikeTravelData;
	private EBikeTravelData(Context context){
		this.context=context;
	}
	
	public static EBikeTravelData getInstance(Context context){
		if(mEBikeTravelData==null){
			mEBikeTravelData=new EBikeTravelData(context);
		}
		return mEBikeTravelData;
	}
	
	public void start(long id) {
		travelId=id;
		isNewTravel=true;
		startTime=Calendar.getInstance().getTimeInMillis();
		endTime=startTime;
	}

	public  void pause() {
		insSpeed=0;
		cal_startTime=cal_endTime;
		cal_startDistance=cal_endDistance;
		cal_startCalorie=cal_endCalorie;
		cal_startCadence=cal_endCadence;
		isNewTravel=false;
	}

	public  void resume() {
		isNewTravel=false;
	}

	public  void stop() {
		isNewTravel=false;
		DBHelper.getInstance(context).deleteTravel(travelId);
	}

	public  void completed() {
		isNewTravel=false;
		if (distance < MUST_MIN_TRAVEL) {
			//数据少，不存储
			ToastUtils.toast(context, "travel is too short not save");
			BaseApplication.travelId=-1;
		}else{
			Travel travel=new Travel();
			travel.setId(travelId);
			travel.setType(type);
			travel.setSync(0);
			travel.setAltitude(altitude);
			travel.setAvgSpeed(avgSpeed);
			travel.setCadence(cadence);
			travel.setCalorie(calorie);
			travel.setDistance(distance);
			travel.setEndTime(endTime);
			travel.setMaxSpeed(maxSpeed);
			travel.setSpendTime(spendTime);
			travel.setStartTime(startTime);
			DBHelper.getInstance(context).updateTravel(travel);// 更新
			NetUtil.getInstance(context).uploadLocalRecord();//更新数据后，上传
		}
	}

	/**
	 * @param controlState
	 * @param data
	 * @Description 格式化数据
	 */
	public  void parseBikeData(byte[] data) {
		byte[] controlState = new byte[2];
		byte[] bikeData = new byte[data.length - 2];
		// 控制器状态
		for (int i = 0; i < controlState.length; i++) {
			controlState[i] = data[i];
		}
		int[] controlArray = ProtocolTool.byteToBitIntArray(controlState);
		messageNoticeGet = controlArray[11]; // 短信提醒标志接收完成
		if (messageNoticeGet == 1
				&& EBikeStatus.getInstance(context).getReceiveMessageStatus() == 1) {// 说明短信接收完成，将发送数据的状态短信设置为0表示已处理短信，暂时没有短信
			EBikeStatus.getInstance(context).setBikeStatus(EBikeStatus.RECEIVE_MESSAGE, 0);
		}
		phoneCallGet = controlArray[10]; // 电话呼叫标志接收完成
		if (phoneCallGet == 1 && EBikeStatus.getInstance(context).getPhoneCallStatus() == 1) {// 说明短信接收完成，将发送数据的状态短信设置为0表示已处理短信，暂时没有短信
			EBikeStatus.getInstance(context).setBikeStatus(EBikeStatus.PHONE_CALL, 0);
		}
		batConnect = controlArray[9]; // 电池包连接标志
		ctrlerOvercp = controlArray[8]; // 控制器过流保护
		ctrlerLowvp = controlArray[7]; // 控制器欠压保护
		energyCycle = controlArray[6]; // 能量回收状态
		ctrlerErr = controlArray[5]; // 控制器故障
		backLed = controlArray[4]; // 后灯状态
		frontLed = controlArray[3]; // 前灯状态
		sportMode = controlArray[2]; // 运动模式
		assisMode = controlArray[1]; // 助力模式
		elecMode = controlArray[0]; // 电动模式

		// 骑行数据
		for (int i = 0; i < bikeData.length; i++) {
			bikeData[i] = data[i + 2];
		}
		if (bikeData.length >= 10) {
			cal_tempTime = ProtocolTool.byteArrayToInt(new byte[] {
					bikeData[0], bikeData[1] });
			insSpeed = ProtocolTool.byteArrayToInt(new byte[] {
					bikeData[2], bikeData[3] });
			cal_tempDistance = ProtocolTool.byteArrayToInt(new byte[] {
					bikeData[4], bikeData[5] });
			batteryAh = ProtocolTool
					.byteArrayToInt(new byte[] { bikeData[6] });
			bikingStateChange = ProtocolTool
					.byteArrayToInt(new byte[] { bikeData[7] });
			batteryResidueCapacity = ProtocolTool
					.byteArrayToInt(new byte[] { bikeData[8] });
			temperature = ProtocolTool
					.byteArrayToInt(new byte[] { bikeData[9] });
			// cycle_times=ProtocolTool.byteArrayToInt(new
			// byte[]{bikeData[10],bikeData[11]});
		}

		cal_tempCalorie = cal_tempCadence / 10 * WHEEL_VALUE * 655 / 21000000;
		insSpeed = insSpeed * 1200 * WHEEL_VALUE / 1000/2400;//单位：m/s
		cal_tempDistance = cal_tempDistance * WHEEL_VALUE / 1000; // 单位：m
		miCapacity = batteryResidueCapacity * batteryAh*8;//公里（千米）
		if(isNewTravel){//新的骑行
			insSpeed=0;
			avgSpeed=0;
			maxSpeed=0;
			spendTime=0;
			distance=0;
			calorie=0;
			cadence=0;
			altitude=0;
			
			cal_startTime=startTime;
			cal_endTime=startTime;
			cal_startDistance=cal_tempDistance;
			cal_startCalorie=cal_tempCalorie;
			cal_startCadence=cal_tempCadence;
			isNewTravel=false;
		}else{
			cal_endTime=Calendar.getInstance().getTimeInMillis();
			cal_endDistance=cal_tempDistance;
			cal_endCalorie=cal_tempCalorie;
			cal_endCadence=cal_tempCadence;
			
			if(insSpeed>maxSpeed){//最大
				maxSpeed=insSpeed;
			}
			spendTime+=(cal_endTime-cal_startTime);//时长
			distance+=(cal_endDistance-cal_startDistance);//距离
			avgSpeed=(int)(distance/spendTime);//平均
			calorie+=(cal_endCalorie-cal_startCalorie);//卡路里
			cadence+=(cal_endCadence-cal_startCadence);//踏频
			altitude+=altitude;//海拔
			if(spendTime!=0&&spendTime%RECORD_TIME_FRE==0){//每百秒存储一个速度
				TravelSpeed travelSpeed=new TravelSpeed();
				travelSpeed.setTravelId(travelId);
				travelSpeed.setSpeed(avgSpeed);
				DBHelper.getInstance(context).insertTravelSpeed(travelSpeed);
			}
		}
	}

	public  String getControlValueText() {
		String value = "短信提醒标志接收完成" + messageNoticeGet + "电话呼叫标志接收完成"
				+ phoneCallGet + "电池包连接标志" + batConnect + "控制器过流保护"
				+ ctrlerOvercp + "控制器欠压保护" + ctrlerLowvp + "能量回收状态"
				+ energyCycle + "控制器故障" + ctrlerErr + "后灯状态" + backLed
				+ "前灯状态" + frontLed + "运动模式" + sportMode + "助力模式"
				+ assisMode + "电动模式" + elecMode + "踏频量（圈）"
				+ cadence + "骑行速度" + insSpeed + "累积骑行里程"
				+ distance + "电池的安时数(100mah)" + batteryAh
				+ "骑行状态改变标志" + bikingStateChange + "剩余容量%"
				+ batteryResidueCapacity + "剩余里程" + miCapacity + "温度(℃)"
				+ temperature +
				// "循环次数(次)"+cycle_times+
				"卡路里" + calorie;
		return value;
	}
	
	public  String getTravelValueText() {
		String value = "-travel_id:" + travelId + "-travel_startTime:" + startTime + " -travel_endTime:"
				+ endTime + " -travel_avgSpeed:" + avgSpeed + " -travel_insSpeed:"
				+ insSpeed + " -travel_maxSpeed:" + maxSpeed + " -travel_spendTime:" + spendTime
				+ " -travel_distance:" + distance + " -travel_calorie:" + calorie + " -travel_cadence:"
				+ cadence + " -travel_altitude:" + altitude ;
		return value;
	}

}
