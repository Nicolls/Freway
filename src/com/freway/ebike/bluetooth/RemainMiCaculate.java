package com.freway.ebike.bluetooth;

import com.freway.ebike.protocol.ProtocolTool;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;

import android.content.Context;

public class RemainMiCaculate {
	private static RemainMiCaculate mRemindCaculate;
	private Context context;

	private RemainMiCaculate(Context context) {
		this.context = context;
		// 每初始化一次,调用一次
		eeprom_mileage_read();
	}

	public static RemainMiCaculate getInstance(Context context) {
		if (mRemindCaculate == null) {
			mRemindCaculate = new RemainMiCaculate(context);
		}
		return mRemindCaculate;
	}

	//
	// ======================剩余里程计算公式============================================
	// 传递值为骑行里程
	// 建立8个容器,每1%容量变化记录一次骑行里程,并加入电池容量变化以及衰减补偿
	//
	// 具体计算等其他完成了再详解
	//
	int[] remain_mil_buffer_zl = new int[8];
	int volume_zhuli = 200;// 剩余容量缓存，同时判断是否在助力，在切换成助力档位的时候赋容量值，运动模式赋200
	int mil_setp = 1; // 剩余里程存储步骤
	int first_mil = 0;// 第一次存储的里程
	int second_mil = 0;
	int third_mil = 0;
	int four_mil = 0;
	int five_mil = 0;
	int six_mil = 0;
	int seven_mil = 0;
	int eight_mil = 0;

	int first_setp = 0;// 第一次存储的剩余容量
	int second_setp = 0;
	int third_setp = 0;
	int four_setp = 0;
	int five_setp = 0;
	int six_setp = 0;
	int seven_setp = 0;
	int eight_setp = 0;

	int mil_zhuli = 0; // 剩余里程值

	// controller_flags.remainmileage_value //读取到的maH值
	// controller_flags.remaincap//剩余容量

	// //传进来的值
	// int cadence;//读到到的里程值
	// int mileage;//计算后行驶的里程值
	// int zhuli_lever;//档位
	// int remainmileage_value;//毫安时
	// int remaincap;//剩余容量
	int remainMi=-1;//剩余里程
	int remain_mileage_proc(byte[]res,int cadence,int mileage, int zhuli_lever, int remainmileage_value, int remaincap) // 传参：当前读取到的骑行里程值
	{
		int zhuli_min, zhuli_max; // 理论最大值和最小值，防止突变
		if (zhuli_lever > 1) {
			if (remaincap < volume_zhuli) {
				volume_zhuli = remaincap;

				switch (mil_setp) {
				case 1:
					first_mil = mileage; // 400
					first_setp = remaincap;// 99
					mil_setp = 2;
					break;
				case 2:
					second_mil = mileage;
					mil_setp = 3;
					second_setp = remaincap;
					break;
				case 3:
					third_mil = mileage;
					mil_setp = 4;
					third_setp = remaincap;
					break;
				case 4:
					four_mil = mileage;
					mil_setp = 5;
					four_setp = remaincap;
					break;
				case 5:
					five_mil = mileage;
					mil_setp = 6;
					five_setp = remaincap;
					break;
				case 6:
					six_mil = mileage;
					mil_setp = 7;
					six_setp = remaincap;
					break;
				case 7:
					seven_mil = mileage;
					mil_setp = 8;
					seven_setp = remaincap;
					break;
				case 8:
					eight_mil = mileage;
					mil_setp = 1;
					eight_setp = remaincap;
					break;
				default:
					mil_setp = 1;
					break;
				}
				if (remainmileage_value > 20) {
					zhuli_min = 3 * remainmileage_value;
					zhuli_max = 50 * remainmileage_value;
				} else {
					zhuli_min = 100;
					zhuli_max = 2000;
				}
				if ((first_setp - remaincap >= 1) && (first_setp > remaincap)) {
					if (mileage > first_mil) {
						first_mil = mileage - first_mil;
						first_mil = first_mil / (first_setp - remaincap);

						if ((first_mil < zhuli_min) || (first_mil > zhuli_max)) {
							if (first_mil < zhuli_min) {
								first_mil = zhuli_min + 50;
							} else {
								first_mil = zhuli_max - 50;
							}
						}
						// 电池曲线以37V为系数1点，电压越大，骑行的里程就越大，存入的数据就需要越小，反之更大
						remain_mil_buffer_zl[0] = (int) first_mil * bat_vol[remaincap] / 370;
						mil_zhuli = remain_mil_buffer_zl[0] + remain_mil_buffer_zl[1] + remain_mil_buffer_zl[2]
								+ remain_mil_buffer_zl[3] + remain_mil_buffer_zl[4] + remain_mil_buffer_zl[5]
								+ remain_mil_buffer_zl[6] + remain_mil_buffer_zl[7];
						eeprom_mileage_write(mil_zhuli);// 数据存储到EERPOM----自行兑换到APP相应的掉电存储器里.
					}
					first_setp = 0;
				}
				if ((second_setp - remaincap >= 1) && (second_setp > remaincap)) {
					if (mileage > second_mil) {
						second_mil = mileage - second_mil;
						second_mil = second_mil / (second_setp - remaincap);

						if ((second_mil < zhuli_min) || (second_mil > zhuli_max)) {
							if (second_mil < zhuli_min) {
								first_mil = zhuli_min + 50;
							} else {
								second_mil = zhuli_max - 50;
							}
						}
						remain_mil_buffer_zl[1] = (int) second_mil * bat_vol[remaincap] / 370;
						mil_zhuli = remain_mil_buffer_zl[0] + remain_mil_buffer_zl[1] + remain_mil_buffer_zl[2]
								+ remain_mil_buffer_zl[3] + remain_mil_buffer_zl[4] + remain_mil_buffer_zl[5]
								+ remain_mil_buffer_zl[6] + remain_mil_buffer_zl[7];
						eeprom_mileage_write(mil_zhuli);
					}
					second_setp = 0;
				}
				if ((third_setp - remaincap >= 1) && (third_setp > remaincap)) {
					if (mileage > third_mil) {
						third_mil = mileage - third_mil;
						third_mil = third_mil / (third_setp - remaincap);

						if ((third_mil < zhuli_min) || (third_mil > zhuli_max)) {
							if (third_mil < zhuli_min) {
								third_mil = zhuli_min + 50;
							} else {
								third_mil = zhuli_max - 50;
							}
						}
						remain_mil_buffer_zl[2] = (int) third_mil * bat_vol[remaincap] / 370;
						mil_zhuli = remain_mil_buffer_zl[0] + remain_mil_buffer_zl[1] + remain_mil_buffer_zl[2]
								+ remain_mil_buffer_zl[3] + remain_mil_buffer_zl[4] + remain_mil_buffer_zl[5]
								+ remain_mil_buffer_zl[6] + remain_mil_buffer_zl[7];
						eeprom_mileage_write(mil_zhuli);
					}
					third_setp = 0;
				}
				if ((four_setp - remaincap >= 1) && (four_setp > remaincap)) {
					if (mileage > four_mil) {
						four_mil = mileage - four_mil;
						four_mil = four_mil / (four_setp - remaincap);

						if ((four_mil < zhuli_min) || (four_mil > zhuli_max)) {
							if (four_mil < zhuli_min) {
								four_mil = zhuli_min + 50;
							} else {
								four_mil = zhuli_max - 50;
							}
						}
						remain_mil_buffer_zl[3] = (int) four_mil * bat_vol[remaincap] / 370;
						mil_zhuli = remain_mil_buffer_zl[0] + remain_mil_buffer_zl[1] + remain_mil_buffer_zl[2]
								+ remain_mil_buffer_zl[3] + remain_mil_buffer_zl[4] + remain_mil_buffer_zl[5]
								+ remain_mil_buffer_zl[6] + remain_mil_buffer_zl[7];
						eeprom_mileage_write(mil_zhuli);
					}
					four_setp = 0;
				}
				if ((five_setp - remaincap >= 1) && (five_setp > remaincap)) {
					if (mileage > five_mil) {
						five_mil = mileage - five_mil;
						five_mil = five_mil / (five_setp - remaincap);

						if ((five_mil < zhuli_min) || (five_mil > zhuli_max)) {
							if (five_mil < zhuli_min) {
								five_mil = zhuli_min + 50;
							} else {
								five_mil = zhuli_max - 50;
							}
						}
						remain_mil_buffer_zl[4] = (int) five_mil * bat_vol[remaincap] / 370;
						mil_zhuli = remain_mil_buffer_zl[0] + remain_mil_buffer_zl[1] + remain_mil_buffer_zl[2]
								+ remain_mil_buffer_zl[3] + remain_mil_buffer_zl[4] + remain_mil_buffer_zl[5]
								+ remain_mil_buffer_zl[6] + remain_mil_buffer_zl[7];
						eeprom_mileage_write(mil_zhuli);
					}
					five_setp = 0;
				}
				if ((six_setp - remaincap >= 1) && (six_setp > remaincap)) {
					if (mileage > six_mil) {
						six_mil = mileage - six_mil;
						six_mil = six_mil / (six_setp - remaincap);

						if ((six_mil < zhuli_min) || (six_mil > zhuli_max)) {
							if (six_mil < zhuli_min) {
								six_mil = zhuli_min + 50;
							} else {
								six_mil = zhuli_max - 50;
							}
						}
						remain_mil_buffer_zl[5] = (int) six_mil * bat_vol[remaincap] / 370;
						mil_zhuli = remain_mil_buffer_zl[0] + remain_mil_buffer_zl[1] + remain_mil_buffer_zl[2]
								+ remain_mil_buffer_zl[3] + remain_mil_buffer_zl[4] + remain_mil_buffer_zl[5]
								+ remain_mil_buffer_zl[6] + remain_mil_buffer_zl[7];
						eeprom_mileage_write(mil_zhuli);
					}
					six_setp = 0;
				}
				if ((seven_setp - remaincap >= 1) && (seven_setp > remaincap)) {
					if (mileage > seven_mil) {
						seven_mil = mileage - seven_mil;
						seven_mil = seven_mil / (seven_setp - remaincap);

						if ((seven_mil < zhuli_min) || (seven_mil > zhuli_max)) {
							if (seven_mil < zhuli_min) {
								seven_mil = zhuli_min + 50;
							} else {
								seven_mil = zhuli_max - 50;
							}
						}
						remain_mil_buffer_zl[6] = (int) seven_mil * bat_vol[remaincap] / 370;
						mil_zhuli = remain_mil_buffer_zl[0] + remain_mil_buffer_zl[1] + remain_mil_buffer_zl[2]
								+ remain_mil_buffer_zl[3] + remain_mil_buffer_zl[4] + remain_mil_buffer_zl[5]
								+ remain_mil_buffer_zl[6] + remain_mil_buffer_zl[7];
						eeprom_mileage_write(mil_zhuli);
					}
					seven_setp = 0;
				}
				if ((eight_setp - remaincap >= 1) && (eight_setp > remaincap)) {
					if (mileage > eight_mil) {
						eight_mil = mileage - eight_mil;
						eight_mil = eight_mil / (eight_setp - remaincap);

						if ((eight_mil < zhuli_min) || (eight_mil > zhuli_max)) {
							if (eight_mil < zhuli_min) {
								eight_mil = zhuli_min + 50;
							} else {
								eight_mil = zhuli_max - 50;
							}
						}
						remain_mil_buffer_zl[7] = (int) eight_mil * bat_vol[remaincap] / 370;
						mil_zhuli = remain_mil_buffer_zl[0] + remain_mil_buffer_zl[1] + remain_mil_buffer_zl[2]
								+ remain_mil_buffer_zl[3] + remain_mil_buffer_zl[4] + remain_mil_buffer_zl[5]
								+ remain_mil_buffer_zl[6] + remain_mil_buffer_zl[7];
						eeprom_mileage_write(mil_zhuli);

					}
					eight_setp = 0;
				}
			}
		} else {
			if (mil_setp > 1) {
				mil_setp = mil_setp - 1;
			}
			first_setp = 0;
			second_setp = 0;
			third_setp = 0;
			four_setp = 0;
			five_setp = 0;
			six_setp = 0;
			seven_setp = 0;
			eight_setp = 0;
		}
		int result = (int) remaincap * mil_zhuli / 8000;
		remainMi=result;//不记录数据，要记录数据，把此行代码注释掉
		if(remainMi!=result){//剩余里程改变的时候记录数据
			int batteryResidueCapacity=remaincap;
			if (batteryResidueCapacity > 2) {//对电量百分比进行数据调整
				batteryResidueCapacity = (batteryResidueCapacity - 3) * 100 / 97;
			} else {
				batteryResidueCapacity = 0;
			}
			int batteryAh=remainmileage_value;
			if (batteryAh <= 20) {
				batteryAh = 78;
			}
			String text = "cadence:" + cadence + " mileage:" + mileage + " zhuli_lever:" + zhuli_lever
					+ " remainmileage_value:" + remainmileage_value +" remaincap:" + remaincap +" 显示"+batteryResidueCapacity+ "%--->剩余里程:" + result+"km 迈显示"+CommonUtil.formatFloatAccuracy(result*0.6f, 1)+"mi"
					+ "\n";
			StringBuffer sb = new StringBuffer();
			sb.append("接收到的蓝牙数据byte[]:"+ProtocolTool.bytesToHexString(res)+"\n");
			sb.append(text);
			sb.append("计算完成后Buffer数组是:");
			sb.append("[");
			for (int i = 0; i < remain_mil_buffer_zl.length; i++) {
				sb.append(remain_mil_buffer_zl[i] + ",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
			sb.append("\n");
			LogUtils.writeLogtoFile("剩余里程计算日志", sb.toString());
		}
		remainMi=result;
		return result;

	}

	// 开机读取存储在设备里的剩余里程值
	void eeprom_mileage_read() {
		int i;
		// mil_zhuli = x;
		mil_zhuli = SPUtils.getMile(context);// 读取本地存储的里程值.如果没有值那就是8000
		for (i = 0; i < 8; i++) {
			remain_mil_buffer_zl[i] = mil_zhuli / 8;
		}
	}

	// 存储到本地
	void eeprom_mileage_write(int zhuli_mi) {
		// System.out.println("我现在存储到本地的值是:"+zhuli_mi);
		SPUtils.setMile(context, zhuli_mi);
	}

	// 剩余里程算法用到的电池曲线表
	int[] bat_vol = { 416, 415, 413, 412, 411, 410, 409, 407, 406, 405, 404, 403, 402, 401, 400, 398, 397, 396, 395,
			394, 393, 392, 391, 390, 389, 388, 387, 386, 385, 384, 383, 382, 381, 380, 378, 377, 376, 376, 375, 373,
			372, 371, 370, 369, 367, 366, 365, 364, 364, 363, 363, 362, 362, 361, 361, 361, 360, 360, 360, 360, 359,
			359, 359, 358, 358, 358, 358, 357, 357, 357, 356, 356, 356, 356, 355, 355, 355, 354, 354, 353, 353, 352,
			351, 351, 350, 349, 348, 347, 346, 344, 343, 343, 342, 341, 340, 339, 338, 336, 330, 316,

	};
}
