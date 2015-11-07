package com.freway.ebike.bluetooth;

import java.io.Serializable;

import com.freway.ebike.db.DBHelper;
import com.freway.ebike.db.Travel;
import com.freway.ebike.db.TravelSpeed;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.protocol.ProtocolTool;
import com.freway.ebike.utils.NetUtil;

import android.content.Context;

public class EBikeHistoryData implements Serializable {
	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @Fields WHEEL_VALUE 轮径
	 */
	private static final int WHEEL_VALUE = 2180;
	// 行程data
	/**
	 * @Fields travelId 行程ID
	 */
	public  long travelId=-1;
	/**
	 * @Fields data_id 控制器data
	 */
	public static int dataId = 0;
	/**
	 * @Fields type 骑行类别，即时
	 */
	public int type = TravelConstant.TRAVEL_TYPE_HISTORY;
	/**
	 * @Fields avgSpeed 平均速度 单位米/s或者是迈
	 */
	public int avgSpeed;
	/**
	 * @Fields maxSpeed 最大速度 单位米/s或者是迈
	 */
	public int maxSpeed;
	/**
	 * @Fields spendTime 行程时间 毫秒
	 */
	public long spendTime;
	/**
	 * @Fields distance 行程距离 单位 米
	 */
	public int distance;
	/**
	 * @Fields calorie 卡路里 单位cal
	 */
	public int calorie;

	/**
	 * @Fields cadence 踏频量（圈）单位次
	 */
	public int cadence;

	// 用于计算当前行程
	private long cal_startTime;
	private long cal_tempSpendTime;
	private long cal_endTime;
	private long cal_startDistance;
	private long cal_tempDistance;
	private long cal_endDistance;
	private long cal_startCalorie;
	private long cal_tempCalorie;
	private long cal_endCalorie;
	private long cal_startCadence;
	private long cal_tempCadence;
	private long cal_endCadence;
	private boolean isNewTravel = true;
	private Context context;
	private static EBikeHistoryData mEBikeTravelData;

	private EBikeHistoryData(Context context) {
		this.context = context;
	}

	public static EBikeHistoryData getInstance(Context context) {
		if (mEBikeTravelData == null) {
			mEBikeTravelData = new EBikeHistoryData(context);
		}
		return mEBikeTravelData;
	}
	/**开始同步*/
	public void start() {
		isNewTravel = true;
	}
	/**停止取消同步*/
	public void stop() {
		isNewTravel = false;
		DBHelper.getInstance(context).deleteTravel(travelId);
	}

	/**
	 * @param controlState
	 * @param data
	 * @Description 格式化数据
	 */
	public void parseHistoryData(byte[] data) {
		if (data != null && data.length >= 8) {
			byte[] id = { data[0], data[1] };
			byte[] time = { data[2], data[3] };
			byte[] step = { data[4], data[5] };
			byte[] mileage = { data[6], data[7] };
			
			//在这里要判断是不是有可能是从头数据，他回来的是不是dataId=0如果是，后面要加判断
			
			dataId = ProtocolTool.byteArrayToInt(id);
			if (dataId > 0) {
				cal_tempDistance = ProtocolTool.byteArrayToInt(mileage);
				cal_tempCadence = ProtocolTool.byteArrayToInt(step);
				cal_tempSpendTime = ProtocolTool.byteArrayToInt(time);

				cal_tempCalorie = cal_tempCadence / 10 * WHEEL_VALUE * 655 / 21000000;
				cal_tempDistance = cal_tempDistance * WHEEL_VALUE / 1000; // 需要考虑溢出(m)

				if (isNewTravel) {// 新的骑行
					Travel travel=new Travel();
					travel.setType(type);
					travel.setSync(0);
					DBHelper.getInstance(context).insertTravel(travel);
					travelId=travel.getId();
					avgSpeed=0;
					maxSpeed=0;
					spendTime=0;
					distance=0;
					calorie=0;
					cadence=0;
					cal_startTime=cal_tempSpendTime;
					cal_endTime=cal_tempSpendTime;
					cal_startDistance=cal_tempDistance;
					cal_startCalorie=cal_tempCalorie;
					cal_startCadence=cal_tempCadence;
					isNewTravel=false;
				} else {

					cal_endTime = cal_tempSpendTime;
					cal_endDistance = cal_tempDistance;
					cal_endCalorie = cal_tempCalorie;
					cal_endCadence = cal_tempCadence;

					spendTime += (cal_endTime - cal_startTime);// 时长
					distance += (cal_endDistance - cal_startDistance);// 距离
					avgSpeed = (int) (distance / spendTime);// 平均
					if (avgSpeed > maxSpeed) {// 最大
						maxSpeed = avgSpeed;
					}
					calorie += (cal_endCalorie - cal_startCalorie);// 卡路里
					cadence += (cal_endCadence - cal_startCadence);// 踏频
				}
				
				//记录是每一百米一次，所以每次都插入一个记录速度
				TravelSpeed travelSpeed=new TravelSpeed();
				travelSpeed.setTravelId(travelId);
				travelSpeed.setSpeed(avgSpeed);
				DBHelper.getInstance(context).insertTravelSpeed(travelSpeed);
			}else{
				if(!isNewTravel){//说明有数据 //dataId为0说明读完了，保存起来
				Travel travel = new Travel();
				travel.setId(travelId);
				travel.setAvgSpeed(avgSpeed);
				travel.setCadence(cadence);
				travel.setCalorie(calorie);
				travel.setDistance(distance);
				travel.setMaxSpeed(maxSpeed);
				travel.setSpendTime(spendTime);
				DBHelper.getInstance(context).updateTravel(travel);
				NetUtil.getInstance(context).uploadLocalRecord();//插入数据后，上传
				}
			}
		} else {
			dataId = 0;
		}
	}

	public String getValueText() {
		String value = "踏频量（圈）:" + cadence + "累积骑行里程:" + distance + 
				// "循环次数(次)"+cycle_times+
				"卡路里:" + calorie;
		return value;
	}
}
