package com.freway.ebike.bluetooth;

import java.io.Serializable;

import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.protocol.ProtocolTool;

public class EBikeHistoryData implements Serializable {
	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	public static final int WHEEL_VALUE = 2180;
	// 一次行程的Data
	public static long travel_id=-1;
	public static long travel_state=TravelConstant.TRAVEL_STATE_NONE;
	public static long travel_startTime;
	public static long travel_endTime;
	public static int travel_avgSpeed;
	public static int travel_insSpeed;
	public static int travel_maxSpeed;
	public static long travel_spendTime;
	public static float travel_distance;
	public static float travel_calorie;
	public static long travel_cadence;
	public static double travel_altitude;
	// 控制器data
	/**
	 * @fields message_notice_get 短信提醒标志接收完成
	 */
	public static int message_notice_get;
	/**
	 * @fields phone_call_get 电话呼叫标志接收完成
	 */
	public static int phone_call_get;
	/**
	 * @fields bat_connect 电池包连接标志
	 */
	public static int bat_connect;
	/**
	 * @fields ctrler_overcp 控制器过流保护
	 */
	public static int ctrler_overcp;
	/**
	 * @fields ctrler_lowvp 控制器欠压保护
	 */
	public static int ctrler_lowvp;
	/**
	 * @fields energy_cycle 能量回收状态
	 */
	public static int energy_cycle;
	/**
	 * @fields ctrler_err 控制器故障
	 */
	public static int ctrler_err;
	/**
	 * @fields back_led_on 后灯状态
	 */
	public static int back_led_on;
	/**
	 * @fields front_led_on 前灯状态
	 */
	public static int front_led_on;
	/**
	 * @fields sport_mode 运动模式
	 */
	public static int sport_mode;
	/**
	 * @fields assis_mode 助力模式
	 */
	public static int assis_mode;

	/**
	 * @fields elec_mode 电动模式
	 */
	public static int elec_mode;

	/**
	 * @fields steppedfrequency 踏频量（圈）
	 */
	public static int stepped_frequency;

	/**
	 * @fields biking_speed 骑行速度，需要根据车轮大小和控制器返回的速度进行计算
	 */
	public static int biking_speed;

	/**
	 * @fields accumulated_mileage 累积骑行里程(km
	 */
	public static int accumulated_mileage;

	/**
	 * @fields battery_ah 电池的安时数(100mah)
	 */
	public static int battery_ah;

	/**
	 * @fields biking_state_change 骑行状态改变标志
	 */
	public static int biking_state_change;

	/**
	 * @fields residual_capacity 剩余容量%
	 */
	public static int residual_capacity;

	/**
	 * @fields mi_capacity 剩余里程
	 */
	public static int mi_capacity;
	/**
	 * @fields temperature 温度(℃)
	 */
	public static int temperature;
	/**
	 * @fields cycle_times 循环次数(次) 1.0暂且没有，所以不用加
	 */
	// public int cycle_times;

	/**
	 * @Fields kcal_value 卡路里
	 */
	public static int kcal_value;

	public static void start(long id) {
		travel_id=id;
		travel_state=TravelConstant.TRAVEL_STATE_START;
	}

	public static void pause() {

	}

	public static void resume() {

	}

	public static void stop() {

	}

	public static void completed() {

	}

	/**
	 * @param controlState
	 * @param data
	 * @Description 格式化数据
	 */
	public static void parseBikeData(byte[] data) {
		byte[] controlState = new byte[2];
		byte[] bikeData = new byte[data.length - 2];
		// 控制器状态
		for (int i = 0; i < controlState.length; i++) {
			controlState[i] = data[i];
		}
		int[] controlArray = ProtocolTool.byteToBitIntArray(controlState);
		message_notice_get = controlArray[11]; // 短信提醒标志接收完成
		if (message_notice_get == 1
				&& EBikeStatus.getReceiveMessageStatus() == 1) {// 说明短信接收完成，将发送数据的状态短信设置为0表示已处理短信，暂时没有短信
			EBikeStatus.setBikeStatus(EBikeStatus.RECEIVE_MESSAGE, 0);
		}
		phone_call_get = controlArray[10]; // 电话呼叫标志接收完成
		if (phone_call_get == 1 && EBikeStatus.getPhoneCallStatus() == 1) {// 说明短信接收完成，将发送数据的状态短信设置为0表示已处理短信，暂时没有短信
			EBikeStatus.setBikeStatus(EBikeStatus.PHONE_CALL, 0);
		}
		bat_connect = controlArray[9]; // 电池包连接标志
		ctrler_overcp = controlArray[8]; // 控制器过流保护
		ctrler_lowvp = controlArray[7]; // 控制器欠压保护
		energy_cycle = controlArray[6]; // 能量回收状态
		ctrler_err = controlArray[5]; // 控制器故障
		back_led_on = controlArray[4]; // 后灯状态
		front_led_on = controlArray[3]; // 前灯状态
		sport_mode = controlArray[2]; // 运动模式
		assis_mode = controlArray[1]; // 助力模式
		elec_mode = controlArray[0]; // 电动模式

		// 骑行数据
		for (int i = 0; i < bikeData.length; i++) {
			bikeData[i] = data[i + 2];
		}
		if (bikeData.length >= 10) {
			stepped_frequency = ProtocolTool.byteArrayToInt(new byte[] {
					bikeData[0], bikeData[1] });
			biking_speed = ProtocolTool.byteArrayToInt(new byte[] {
					bikeData[2], bikeData[3] });
			accumulated_mileage = ProtocolTool.byteArrayToInt(new byte[] {
					bikeData[4], bikeData[5] });
			battery_ah = ProtocolTool
					.byteArrayToInt(new byte[] { bikeData[6] });
			biking_state_change = ProtocolTool
					.byteArrayToInt(new byte[] { bikeData[7] });
			residual_capacity = ProtocolTool
					.byteArrayToInt(new byte[] { bikeData[8] });
			temperature = ProtocolTool
					.byteArrayToInt(new byte[] { bikeData[9] });
			// cycle_times=ProtocolTool.byteArrayToInt(new
			// byte[]{bikeData[10],bikeData[11]});
		}

		kcal_value = stepped_frequency / 10 * WHEEL_VALUE * 655 / 21000000;
		biking_speed = biking_speed * 1200 * WHEEL_VALUE / 1000;
		accumulated_mileage = accumulated_mileage * WHEEL_VALUE / 1000; // 需要考虑溢出(m)
		mi_capacity = residual_capacity * 8;
		
	}

	public static String getValueText() {
		String value = "短信提醒标志接收完成" + message_notice_get + "电话呼叫标志接收完成"
				+ phone_call_get + "电池包连接标志" + bat_connect + "控制器过流保护"
				+ ctrler_overcp + "控制器欠压保护" + ctrler_lowvp + "能量回收状态"
				+ energy_cycle + "控制器故障" + ctrler_err + "后灯状态" + back_led_on
				+ "前灯状态" + front_led_on + "运动模式" + sport_mode + "助力模式"
				+ assis_mode + "电动模式" + elec_mode + "踏频量（圈）"
				+ stepped_frequency + "骑行速度" + biking_speed + "累积骑行里程"
				+ accumulated_mileage + "电池的安时数(100mah)" + battery_ah
				+ "骑行状态改变标志" + biking_state_change + "剩余容量%"
				+ residual_capacity + "剩余里程" + mi_capacity + "温度(℃)"
				+ temperature +
				// "循环次数(次)"+cycle_times+
				"卡路里" + kcal_value;
		return value;
	}

}
