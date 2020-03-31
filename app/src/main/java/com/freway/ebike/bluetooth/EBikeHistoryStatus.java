package com.freway.ebike.bluetooth;


/**历史状态*/
public class EBikeHistoryStatus {
	private final static String TAG=EBikeHistoryStatus.class.getSimpleName();
	private static int bit_7 = 0;
	private static int bit_6 = 0;
	private static int bit_5 = 0;
	private static int bit_4 = 0;
	private static int bit_3 = 0;
	private static int bit_2 = 0;
	private static int bit_1 = 0;
	private static int bit_0 = 0;
	/**当前历史状态数据*/
	private static byte bikeStatus;
	public static byte getBikeStatus() {
		return bikeStatus;
	}
	
	/**获取从头开始*/
	public final static int DATA_INDEX = 0;
	/**获取下一条*/
	public final static int DATA_NEXT = 1;
	/**重复获取上一条*/
	public final static int DATA_REPEAT = 2;
	/**获取结束*/
	public final static int DATA_END = 3;
	/**
	 * @param control 控制命令
	 * @param flag 控制值 1 true,0 false
	 * 通过传入的控制和值设置历史状态，并返回设置后的状态数据
	 * */
	public static synchronized byte  setBikeStatus(int control,int flag) {
		byte result = 0;
		switch (control) {
		case DATA_INDEX:
			bit_0=0;
			bit_1=0;
			bit_2=0;
			break;
		case DATA_NEXT:
			bit_0=1;
			bit_1=0;
			bit_2=0;
			break;
		case DATA_REPEAT:
			bit_0=0;
			bit_1=1;
			bit_2=0;
			break;
		case DATA_END:
			bit_0=1;
			bit_1=1;
			bit_2=0;
			break;
		default:
			break;
		}
		/**存储状态的数组*/
		int[] status = { bit_7, bit_6, bit_5, bit_4, bit_3,
				bit_2, bit_1, bit_0};
		String s="";
		for(int i=0;i<status.length;i++){
			s+=status[i];
		}
		result=Byte.parseByte(s,2);
		bikeStatus=result;
//		LogUtils.i(TAG, "设置完成后的Ebike数据："+s+"--二进制值："+result);
//		LogUtils.i(TAG, "骑行状态："+bit_0+""+bit_1+""+bit_2+" 前灯："+bit_3+" 后灯："+bit_4+" 电话："+bit_5+" 短信："+bit_6);
		return result;
	}
	
}
