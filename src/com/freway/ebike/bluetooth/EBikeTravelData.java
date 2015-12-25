package com.freway.ebike.bluetooth;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;

import android.content.Context;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.db.DBHelper;
import com.freway.ebike.db.Travel;
import com.freway.ebike.db.TravelSpeed;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.protocol.ProtocolTool;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.NetUtil;
import com.freway.ebike.utils.ToastUtils;

public class EBikeTravelData implements Serializable {
	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private static final String TAG = "EBikeTravelData";
	/**
	 * @Fields WHEEL_VALUE 轮径
	 */
	private static final int WHEEL_VALUE = 2180;
	/**
	 * @Fields RECORD_TIME_FRE 每一百秒记录一次平均速度点用于描绘速度曲线
	 */
	private static final int RECORD_TIME_FRE = 10;// 每0秒记录一次
	/**
	 * @Fields MUST_MIN_TRAVEL 最短行程，要记录的行程至少要大于最短行程，否则丢弃
	 */
	public static final float MUST_MIN_TRAVEL = 0.1f;// 单位km 最短行程0.1km-->100米
	// 行程data
	/**
	 * @Fields travelId 行程ID
	 */
	public long travelId = -1;

	/**
	 * @Fields travelPhoto 行程地图缩略图
	 */
	public String travelPhoto;
	/**
	 * @Fields type 骑行类别，即时
	 */
	public int type = TravelConstant.TRAVEL_TYPE_IM;
	/**
	 * @Fields startTime 开始时间 单位毫秒
	 */
	public long startTime;
	/**
	 * @Fields endTime 结束时间 单位毫秒
	 */
	public long endTime;
	/**
	 * @Fields avgSpeed 平均速度 单位km/h
	 */
	public float avgSpeed;
	/**
	 * @Fields insSpeed 瞬时速度 单位km/h
	 */
	public float insSpeed;
	/**
	 * @Fields maxSpeed 最大速度 单位km/h
	 */
	public float maxSpeed;
	/**
	 * @Fields spendTime 行程时间单位秒
	 */
	public long spendTime;
	/**
	 * @Fields distance 行程距离 单位 km
	 */
	public float distance;
	/**
	 * @Fields calorie 卡路里 单位cal
	 */
	public float calorie;

	/**
	 * @Fields cadence 踏频量（圈）单位 圈/每分钟
	 */
	public float cadence;
	/**
	 * @Fields altitude 海拔 单位km
	 */
	public double altitude;
	/**
	 * @fields messageNoticeGet 短信提醒标志接收完成
	 */
	public int messageNoticeGet;
	/**
	 * @fields phoneCallGet 电话呼叫标志接收完成
	 */
	public int phoneCallGet;
	/**
	 * @fields batConnect 电池包连接标志
	 */
	public int batConnect;
	/**
	 * @fields ctrlerOvercp 控制器过流保护
	 */
	public int ctrlerOvercp;
	/**
	 * @fields ctrlerLowvp 控制器欠压保护
	 */
	public int ctrlerLowvp;
	/**
	 * @fields energyCycle 能量回收状态
	 */
	public int energyCycle;
	/**
	 * @fields ctrlerErr 控制器故障
	 */
	public int ctrlerErr;
	/**
	 * @fields backLed 后灯状态
	 */
	public int backLed;
	/**
	 * @fields frontLed 前灯状态
	 */
	public int frontLed;
	/**
	 * @fields sportMode 运动模式
	 */
	public int sportMode;
	/**
	 * @fields assisMode 助力模式
	 */
	public int assisMode;

	/**
	 * @fields elecMode 电动模式
	 */
	public int elecMode;
	// /**
	// * @fields biking_speed 骑行速度，需要根据车轮大小和控制器返回的速度进行计算
	// */
	// public int biking_speed;

	// /**
	// * @fields accumulated_mileage 累积骑行里程(km
	// */
	// public int accumulated_mileage;

	/**
	 * @fields batteryAh 电池的安时数(100mah)
	 */
	public int batteryAh;

	/**
	 * @fields biking_state_change 骑行状态改变标志，也就是档位0运动，1电动2助力1.3助力2.4助力3
	 */
	public int gear;

	/**
	 * @fields batteryResidueCapacity 剩余容量 单位%
	 */
	public int batteryResidueCapacity;

	/**
	 * @fields miCapacity 剩余里程km
	 */
	public float remaindTravelCapacity;
	/**
	 * @fields temperature 温度(℃)
	 */
	public int temperature;
	/**
	 * @fields cycle_times 循环次数(次) 1.0暂且没有，所以不用加
	 */
	public int cycle_times;

	private static KcalCaculate kcalCacul;//卡路里计算
	// 用于计算当前行程
	// private long cal_startTime;
	// private long cal_tempTime;
	// private long cal_endTime;

	// private double cal_startAltitude;
	// public double cal_tempAltitude;// 这个值有地图来提供
	// private double cal_endAltitude;
	private float cal_startDistance;
	private float cal_tempDistance;
	private float cal_endDistance;
	private float cal_startCalorie;
	private float cal_tempCalorie;
	private float cal_endCalorie;
	private float cal_startCadence;
	private float cal_tempCadence;
	private float cal_endCadence;
	private float cal_recordCadence;// 记录的总踏频量
	private float[] cal_CadenceArray=null;//计算踏频量的数组
	private boolean isReInitCaculate = true;//是否重初始化计算变量 
	private Context context;
	private static EBikeTravelData mEBikeTravelData;
	// UI时间
	private boolean isCalUiTime = true;// 是否显示
	private boolean isPauseTime = true;// 是否计算
	private SpendTimeThread spendTimeThread = null;
	// 存储连续速度为0的点，每一秒存一个，如果连续为0的点超过3个就暂停骑行。
	private static final int MAX_LIMIT_ZERO_SPEED = 3;
	private int zeroSpeedCount = 0;// 速度为0的次数
	private NetUtil netUtil;
	// 历史记录
	/**
	 * @Fields data_id 控制器data
	 */
	public static int dataId = 0;
	private long cal_startTime;
	private long cal_tempSpendTime;
	private long cal_endTime;

	private EBikeTravelData(Context context) {
		this.context = context;
	}

	public static EBikeTravelData getInstance(Context context) {
		if (mEBikeTravelData == null) {
			mEBikeTravelData = new EBikeTravelData(context);
		}
		return mEBikeTravelData;
	}

	public void start(long id, int type) {
		this.type = type;
		cal_CadenceArray=null;
		kcalCacul=new KcalCaculate();
		zeroSpeedCount = 0;
		spendTime = 0;
		insSpeed = 0;
		avgSpeed = 0;
		maxSpeed = 0;
		distance = 0;
		calorie = 0;
		cadence = 0;
		altitude = 0;
		cal_recordCadence=0;
		
		travelId = id;
		isReInitCaculate = true;
		startTime = Calendar.getInstance().getTimeInMillis();
		endTime = startTime;
		isCalUiTime = true;
		isPauseTime = false;
		if (spendTimeThread == null) {
			if (type == TravelConstant.TRAVEL_TYPE_IM) {
				spendTimeThread = new SpendTimeThread();
				spendTimeThread.start();
			}
		}
	}

	public void pause() {
		zeroSpeedCount = 0;
		insSpeed = 0;
		// cal_startTime=cal_endTime;
		// cal_startAltitude = cal_endAltitude;
		cal_startDistance = cal_endDistance;
		cal_startCalorie = cal_endCalorie;
		cal_startCadence = cal_endCadence;
		isPauseTime = true;
	}
	
	public void fakePause() {
		zeroSpeedCount = 0;
		insSpeed = 0;
		// cal_startTime=cal_endTime;
		// cal_startAltitude = cal_endAltitude;
		cal_startDistance = cal_endDistance;
		cal_startCalorie = cal_endCalorie;
		cal_startCadence = cal_endCadence;
		isPauseTime = true;
	}

	public void resume() {
		zeroSpeedCount = 0;
		isPauseTime = false;
	}

	public void stop() {
		kcalCacul=null;
		isReInitCaculate=true;
		zeroSpeedCount = 0;
		if (spendTimeThread != null) {
			spendTimeThread.cancel();
		}
		isPauseTime = true;
		DBHelper.getInstance(context).deleteTravel(travelId);
	}

	public void completed() {
		kcalCacul=null;
		zeroSpeedCount = 0;
		isPauseTime = true;
		isReInitCaculate=true;
		endTime = Calendar.getInstance().getTimeInMillis();
		if (spendTimeThread != null) {
			spendTimeThread.cancel();
		}
		if (distance < MUST_MIN_TRAVEL) {
			// 数据少，不存储
			ToastUtils.toast(context, context.getString(R.string.travel_too_short_not_save));
			BaseApplication.travelId = -1;
		} else {
			formatFloat2OneAccuracy();
			Travel travel = new Travel();
			travel.setId(travelId);
			travel.setType(type);
			travel.setSync(0);
			travel.setAltitude(altitude);
			travel.setAvgSpeed(avgSpeed);
			travel.setCadence(cal_recordCadence);
			travel.setCalorie(calorie);
			travel.setDistance(distance);
			travel.setEndTime(endTime);
			travel.setMaxSpeed(maxSpeed);
			travel.setSpendTime(spendTime);
			travel.setStartTime(startTime);
			travel.setPhoto(travelPhoto);
			DBHelper.getInstance(context).updateTravel(travel);// 更新
			if (netUtil == null) {
				netUtil = new NetUtil(context);
			}
			netUtil.uploadLocalRecord();// 更新数据后，上传
		}
	}

	/**
	 * @param controlState
	 * @param data
	 * @Description 格式化数据
	 */
	public synchronized void parseBikeData(byte[] data) {
		if (data != null && data.length >= 2) {
			byte[] controlState = new byte[2];
			byte[] bikeData = new byte[data.length - 2];
			// 控制器状态
			for (int i = 0; i < controlState.length; i++) {
				controlState[i] = data[i];
			}
			int[] controlArray = ProtocolTool.byteToBitIntArray(controlState);
			messageNoticeGet = controlArray[11]; // 短信提醒标志接收完成
			// if (messageNoticeGet == 1
			// && EBikeStatus.getInstance(context).getReceiveMessageStatus() ==
			// 1)
			// {// 说明短信接收完成，将发送数据的状态短信设置为0表示已处理短信，暂时没有短信
			// EBikeStatus.getInstance(context).setBikeStatus(EBikeStatus.RECEIVE_MESSAGE,
			// 0);
			// }
			phoneCallGet = controlArray[10]; // 电话呼叫标志接收完成
			// if (phoneCallGet == 1 &&
			// EBikeStatus.getInstance(context).getPhoneCallStatus() == 1) {//
			// 说明短信接收完成，将发送数据的状态短信设置为0表示已处理短信，暂时没有短信
			// EBikeStatus.getInstance(context).setBikeStatus(EBikeStatus.PHONE_CALL,
			// 0);
			// }
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
			float speedTemp = 0;
			int gearTemp=0;//档位
			for (int i = 0; i < bikeData.length; i++) {
				bikeData[i] = data[i + 2];
			}
			if (bikeData.length > 0) {
				if (bikeData.length >= 2) {
					cal_tempCadence = ProtocolTool.byteArrayToInt(new byte[] { bikeData[0], bikeData[1] });
				}
				if (bikeData.length >= 4) {
					speedTemp = ProtocolTool.byteArrayToInt(new byte[] { bikeData[2], bikeData[3] });
				}
				if (bikeData.length >= 6) {
					cal_tempDistance = ProtocolTool.byteArrayToInt(new byte[] { bikeData[4], bikeData[5] });
				}
				if (bikeData.length >= 7) {
					batteryAh = ProtocolTool.byteArrayToInt(new byte[] { bikeData[6] });
				}
				if (bikeData.length >= 8) {
					gearTemp = ProtocolTool.byteArrayToInt(new byte[] { bikeData[7] });
				}
				if (bikeData.length >= 9) {
					batteryResidueCapacity = ProtocolTool.byteArrayToInt(new byte[] { bikeData[8] });
				}
				if (bikeData.length >= 10) {
					temperature = ProtocolTool.byteArrayToInt(new byte[] { bikeData[9] });
				}
				if (bikeData.length >= 12) {
					System.out.println("有循环数");
					cycle_times = ProtocolTool.byteArrayToInt(new byte[] { bikeData[10], bikeData[11] });
				}
				// 下面对骑行状态进行转换。骑行状态：0-运动，1-电动 2-助力1,3-助力2,4-助力3
				if (gearTemp == 0) {
					gear = 0;
				} else if (gearTemp == 2) {
					gear = 1;
				} else if (gearTemp == 3) {
					gear = 2;
				} else if (gearTemp == 4) {
					gear = 3;
				}
			}
			if(kcalCacul==null){
				kcalCacul=new KcalCaculate();
			}
			cal_tempCalorie=kcalCacul.Kcale_Proc((long)cal_tempCadence,(long)speedTemp*1000,(byte)gearTemp);// 卡路里
			remaindTravelCapacity = RemainMiCaculate.getInstance(context).remain_mileage_proc((int) cal_tempDistance, gearTemp, batteryAh, batteryResidueCapacity);//batteryResidueCapacity * batteryAh * 12 / 780;// 公里（千米）
			speedTemp = speedTemp * 1200f * WHEEL_VALUE / 1000 / 1000;// 单位：km/h
			insSpeed = formatInsSpeed(speedTemp);// 在计算值之前，先用分段法处理一下得到的速度
			cal_tempDistance = cal_tempDistance * WHEEL_VALUE / 1000 / 1000; // 单位：km
			if (batteryAh <= 20) {
				batteryAh = 78;
			}
//			simulateData();// 模拟数据
			if (insSpeed != 0 && BaseApplication.travelState == TravelConstant.TRAVEL_STATE_FAKE_PAUSE) {// 当前是伪暂停，就resume
				BaseApplication.sendStateChangeBroadCast(context, TravelConstant.TRAVEL_STATE_RESUME);
			}
			if (isReInitCaculate) {// 重新计算值
				// cal_startTime=startTime;
				// cal_endTime=startTime;
				// cal_startAltitude = cal_tempAltitude;
				cal_startDistance = cal_tempDistance;
				cal_startCalorie = cal_tempCalorie;
				cal_startCadence = cal_tempCadence;
				isReInitCaculate = false;
			} else {
				// cal_endTime=Calendar.getInstance().getTimeInMillis();
				// cal_endAltitude = cal_tempAltitude;
				cal_endDistance = cal_tempDistance;
				cal_endCalorie = cal_tempCalorie;
				cal_endCadence = cal_tempCadence;
				
				//在这里添加溢出，或者是重启机器重计算的判断并处理
				if(cal_endDistance - cal_startDistance<0){
					cal_startDistance=cal_endDistance;//我们只要把上一次置为这次就可以了
				}
				if(cal_endCalorie - cal_startCalorie<0){
					cal_startCalorie=cal_endCalorie;//我们只要把上一次置为这次就可以了
				}
				if(cal_endCadence - cal_startCadence<0){
					cal_startCadence=cal_endCadence;//我们只要把上一次置为这次就可以了
				}
				//溢出处理完成
				if (insSpeed > maxSpeed) {// 最大
					maxSpeed = insSpeed;
				}
				// spendTime+=(cal_endTime-cal_startTime);//时长
				// if ((cal_endAltitude - cal_startAltitude) > 0) {// 上坡的时候，才计算值
				// altitude += (cal_endAltitude - cal_startAltitude);// 海拔
				// }
				distance += (cal_endDistance - cal_startDistance);// 距离
				if (spendTime != 0) {
					avgSpeed = distance / spendTime * 60 * 60;// 平均 km/h
				}
				calorie += (cal_endCalorie - cal_startCalorie);// 卡路里
//				cal_recordCadence += (cal_endCadence - cal_startCadence);// 踏频
				altitude += altitude;// 海拔
				if (insSpeed == 0) {// 只要速度为0那么踏频量就要为0
					cal_startCadence=cal_endCadence;
					cal_CadenceArray=null;
					cadenceArray(0);
				}

				// cal_startAltitude = cal_tempAltitude;
				cal_startDistance = cal_tempDistance;
				cal_startCalorie = cal_tempCalorie;
//				cal_startCadence = cal_tempCadence;
			}
		}

	}

	/**
	 * @param controlState
	 * @param data
	 * @Description 格式化数据
	 */
	public void parseHistoryData(byte[] data) {
		// mark 历史的，怕数组越界还没有处理
		if (data != null && data.length >= 8) {
			byte[] id = { data[0], data[1] };
			byte[] time = { data[2], data[3] };
			byte[] step = { data[4], data[5] };
			byte[] mileage = { data[6], data[7] };

			// 在这里要判断是不是有可能是从头数据，他回来的是不是dataId=0如果是，后面要加判断

			dataId = ProtocolTool.byteArrayToInt(id);
			if (dataId > 0) {
				cal_tempDistance = ProtocolTool.byteArrayToInt(mileage);
				cal_tempCadence = ProtocolTool.byteArrayToInt(step);
				spendTime = ProtocolTool.byteArrayToInt(time);
				
				if(kcalCacul==null){
					kcalCacul=new KcalCaculate();
				}
				
				cal_tempDistance = cal_tempDistance * WHEEL_VALUE / 1000 / 1000; // 单位：km
				if (isReInitCaculate) {// 新的骑行
					Travel travel = new Travel();
					travel.setType(TravelConstant.TRAVEL_TYPE_HISTORY);
					travel.setSync(0);
					DBHelper.getInstance(context).insertTravel(travel);
					travelId = travel.getId();
					avgSpeed = 0;
					maxSpeed = 0;
					spendTime = 0;
					distance = 0;
					calorie = 0;
					cadence = 0;
					cal_startTime = cal_tempSpendTime;
					cal_startDistance = cal_tempDistance;
					cal_startCalorie = cal_tempCalorie;
					cal_startCadence = cal_tempCadence;
					isReInitCaculate = false;
				} else {

					cal_endTime = cal_tempSpendTime;
					cal_endDistance = cal_tempDistance;
					cal_endCadence = cal_tempCadence;
					//在这里添加溢出，或者是重启机器重计算的判断并处理
					if(cal_endDistance - cal_startDistance<0){
						cal_startDistance=cal_endDistance;//我们只要把上一次置为这次就可以了
					}
					if(cal_endCadence - cal_startCadence<0){
						cal_startCadence=cal_endCadence;//我们只要把上一次置为这次就可以了
					}
					//溢出处理完成
					spendTime += (cal_endTime - cal_startTime);// 时长
					distance += (cal_endDistance - cal_startDistance);// 距离
					if (spendTime != 0) {
						avgSpeed = distance / spendTime * 60 * 60;// 平均 km/h
					}
					if (avgSpeed > maxSpeed) {// 最大
						maxSpeed = avgSpeed;
					}
					
					cal_tempCalorie=kcalCacul.Kcale_Proc((long)cal_tempCadence,(long)avgSpeed*1000,(byte)0);// 卡路里
					cal_endCalorie = cal_tempCalorie;
					if(cal_endCalorie - cal_startCalorie<0){
						cal_startCalorie=cal_endCalorie;//我们只要把上一次置为这次就可以了
					}
					calorie += (cal_endCalorie - cal_startCalorie);// 卡路里
					cal_recordCadence += (cal_endCadence - cal_startCadence);// 踏频

					cal_startDistance = cal_tempDistance;
					cal_startCalorie = cal_tempCalorie;
					cal_startCadence = cal_tempCadence;
					cal_startTime = cal_tempSpendTime;
				}
				// 记录是每一百米一次，所以每次都插入一个记录速度
				TravelSpeed travelSpeed = new TravelSpeed();
				travelSpeed.setTravelId(travelId);
				travelSpeed.setSpeed(CommonUtil.formatFloatAccuracy(avgSpeed, 1));
				DBHelper.getInstance(context).insertTravelSpeed(travelSpeed);
			} else {
				formatFloat2OneAccuracy();
				if (!isReInitCaculate) {// 说明有数据 //dataId为0说明读完了，保存起来
					Travel travel = new Travel();
					travel.setId(travelId);
					travel.setAvgSpeed(avgSpeed);
					travel.setCadence(cal_recordCadence);
					travel.setCalorie(calorie);
					travel.setDistance(distance);
					travel.setMaxSpeed(maxSpeed);
					travel.setSpendTime(spendTime);
					DBHelper.getInstance(context).updateTravel(travel);
					if (netUtil == null) {
						netUtil = new NetUtil(context);
					}
					netUtil.uploadLocalRecord();// 插入数据后，上传
				}
			}
		} else {
			dataId = 0;
		}
	}

	/** 格式化值为需求精度 */
	private void formatFloat2OneAccuracy() {
		insSpeed = CommonUtil.formatFloatAccuracy(insSpeed, 1);
		maxSpeed = CommonUtil.formatFloatAccuracy(maxSpeed, 1);
		avgSpeed = CommonUtil.formatFloatAccuracy(avgSpeed, 1);
		distance = CommonUtil.formatFloatAccuracy(distance, 3,1);//小数都去掉，不用4舍5入
		cadence = CommonUtil.formatFloatAccuracy(cadence, 0);
		calorie = CommonUtil.formatFloatAccuracy(calorie, 3);
		cal_recordCadence = CommonUtil.formatFloatAccuracy(cal_recordCadence, 0);
		remaindTravelCapacity = CommonUtil.formatFloatAccuracy(distance, 3);
	}

	public String getControlValueText() {
		String value = "短信标志:" + messageNoticeGet + "-电话呼叫标志:" + phoneCallGet + "-电池包连接标志:" + batConnect + "-控制器过流保护:"
				+ ctrlerOvercp + "-控制器欠压保护:" + ctrlerLowvp + "-能量回收状态:" + energyCycle + "-控制器故障:" + ctrlerErr + "-后灯状态:"
				+ backLed + "-前灯状态:" + frontLed + "\n-开始时间(毫秒)：" + startTime + "\n时长(秒)：" + spendTime + "\n-踏频量（圈/分钟）:"
				+ cadence + "\n-骑行速度(km/h):" + insSpeed + "\n-平均速度(km/h):" + avgSpeed + "\n-最大速度(km/h):" + maxSpeed
				+ "\n-累积骑行里程(km):" + distance + "\n-卡路里：" + calorie + "\n-电池的安时数(100mah):" + batteryAh + "\n-骑行状态改变标志:"
				+ gear + "\n-剩余容量%:" + batteryResidueCapacity + "\n-剩余里程(km):" + remaindTravelCapacity + "\n-温度(℃):"
				+ temperature + "\n循环次数:" + cycle_times + "\n\n";
		return value;
	}

	public String getTravelValueText() {
		String value = "-travel_id:" + travelId + "-travel_startTime:" + startTime + " -travel_endTime:" + endTime
				+ " -travel_avgSpeed:" + avgSpeed + " -travel_insSpeed:" + insSpeed + " -travel_maxSpeed:" + maxSpeed
				+ " -travel_spendTime:" + spendTime + " -travel_distance:" + distance + " -travel_calorie:" + calorie
				+ " -travel_cadence:" + cadence + " -travel_altitude:" + altitude;
		return value;
	}

	/** 数据模拟 */
	Random r = new Random();

	private void simulateData() {
		insSpeed = (3 + r.nextInt(10) + 1) * 1.0f;
		if (insSpeed > 9) {
			backLed = 0;
			frontLed = 1;
			gear = 1;
			batteryResidueCapacity = 50;
		} else {
			backLed = 1;
			frontLed = 0;
			gear = 0;
			batteryResidueCapacity = 35;
		}
		cal_tempCadence = r.nextInt(3) + 1 + cal_startCadence;// 一次n圈
		cal_tempDistance = (r.nextInt(5) + 1) / 1000f + cal_startDistance;// km
		cal_tempCalorie = cal_tempCadence / 10 * WHEEL_VALUE * 655 / 21000000f;// cal

	}

	/** 分段显示速度法 ，在取得控制器的值后，要先调用一次用来格式化值 */
	private float formatInsSpeed(float speed_val) {
		float avg = 0;
		if (speed_val == 0) {
			insSpeed = 0;
			return 0;
		}
		if (insSpeed != speed_val) {
			if (speed_val > insSpeed) {
				avg = (speed_val - insSpeed) / 3;
				insSpeed += avg;
			} else {
				avg = (insSpeed - speed_val) / 3;
				insSpeed -= avg;
			}
		}
		return insSpeed;

	}

	/**设置踏频量数组的值*/
	private synchronized void cadenceArray(float value){
		float f=0;
		if(cal_CadenceArray==null){
			if(value>0){//没有给值，那么得当value>0才能给值
				cal_CadenceArray=new float[30];
				for(int i=0;i<cal_CadenceArray.length;i++){
					cal_CadenceArray[i]=value;
				}
			}
		}else {//说明已经给值过了
			float[]temp=new float[cal_CadenceArray.length];
			for(int i=0;i<temp.length-1;i++){
				temp[i]=cal_CadenceArray[i+1];
			}
			temp[temp.length-1]=value;
			cal_CadenceArray=temp;
		}
//		StringBuffer sb=new StringBuffer();
		if(cal_CadenceArray!=null){
			for(int i=0;i<cal_CadenceArray.length;i++){
				f+=cal_CadenceArray[i];
//				sb.append(""+cal_CadenceArray[i]+",");
			}
		}
//		System.out.println(sb.toString());
		cadence=f;
	}
	/**
	 * This thread runs while connect is interrupt attempting to reconnect
	 */
	private class SpendTimeThread extends Thread {
		public void run() {
			LogUtils.i(TAG, "BEGIN ReConnectThread:");
			setName("SpendTimeThread");
			while (isCalUiTime) {
				if (!isPauseTime) {
					spendTime += 1;
					if(spendTime%2==0){//计算踏频量
						cal_endCadence=cal_tempCadence;
//						System.out.println("start-end:"+cal_startCadence+"-"+cal_endCadence);
						if(cal_endCadence - cal_startCadence<0){//为负数，说明是重新开机或者什么的了
							cal_startCadence=cal_endCadence;
						}
						if (insSpeed == 0) {//当速度为0时，踏频就是0
							cal_startCadence=cal_endCadence;
							cal_CadenceArray=null;
							cadenceArray(0);
						}else{
							cal_recordCadence += (cal_endCadence - cal_startCadence);//总踏频
							cadenceArray(cal_endCadence-cal_startCadence);
							cal_startCadence = cal_endCadence;
						}
//						System.out.println("总的是："+cal_recordCadence);
//						System.out.println("踏频量："+cadence);
					}
					
					if (insSpeed == 0) {
						zeroSpeedCount++;
						if (zeroSpeedCount > MAX_LIMIT_ZERO_SPEED) {// 超过数值
							zeroSpeedCount = 0;
							isPauseTime = true;
							BaseApplication.sendStateChangeBroadCast(context, TravelConstant.TRAVEL_STATE_FAKE_PAUSE);
						}
					} else {
						zeroSpeedCount = 0;
					}
					
					if (spendTime != 0 && (spendTime % RECORD_TIME_FRE) == 0) {// 每10秒存储一个速度
						TravelSpeed travelSpeed = new TravelSpeed();
						travelSpeed.setTravelId(travelId);
						travelSpeed.setSpeed(CommonUtil.formatFloatAccuracy(avgSpeed, 1));
						DBHelper.getInstance(context).insertTravelSpeed(travelSpeed);
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			synchronized (EBikeTravelData.this) {
				spendTimeThread = null;
			}
		}

		public void cancel() {
			isCalUiTime = false;
		}
	}
}
