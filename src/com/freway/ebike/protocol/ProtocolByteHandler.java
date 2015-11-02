package com.freway.ebike.protocol;
import java.util.HashMap;

import com.freway.ebike.bluetooth.EBikeData;
import com.freway.ebike.bluetooth.EBikeStatus;
import com.freway.ebike.utils.LogUtils;

/**
 * @author Nicolls
 * @Description 协议数据操作类，包括装包，解包
 * @date 2015年10月25日
 */
public class ProtocolByteHandler {
	
	/**
	 * @Fields mEBikeData 返回的蓝牙数据实体
	 */
	public static EBikeData mEBikeData=new EBikeData();
	private static final String TAG="CreateByteCommand";
	/**
	 * @Fields EXTRA_CODE 解析数据包时，装入HashMap中的结果码
	 */
	public static final String EXTRA_CODE="EXTRA_CODE";
	/**
	 * @Fields EXTRA_CMD 解析数据包时，装入HashMap中的命令Key
	 */
	public static final String EXTRA_CMD="EXTRA_CMD";
	
	/**
	 * @Fields EXTRA_DATA 解析数据包时，装入HashMap中的数据值byte[]
	 */
	public static final String EXTRA_DATA="EXTRA_DATA";
	/**
	 * @param cmdCode 操作码 @see com.freway.protocol.CommandCode
	 * @param data 命令发出的数据 ，如果不需要传递参数，则设置为空
	 * @return byte[]
	 * @Description 通过命令枚举，和参数，构造要发送的数据
	 */
	public static byte[] command(int cmdCode,byte[] data) {
		String sendstr = "";
		if(data!=null){
			sendstr=new String(data);
		}
		switch(cmdCode)
		{
		case CommandCode.SURVEY:
			
			break;
		}
		LogUtils.d(TAG,"发送的数据: " + sendstr);
		Protocol mProtocol = new Protocol();
		return mProtocol.pack(cmdCode,data);
	}
	
	/** 对接收到的数据进行解析
	 * @param receiveData 接收到的数据
	 * @return HashMap<String,Object> 
	 * @Description 返回的key有:
	 * 操作码ProtocolByteHandler.EXTRA_CMD
	 * 对应的值ProtocolByteHandler.EXTRA_DATA 数据块的值@see com.freway.ebike.bluetooth.EBikeData
	 * 操作码如果为CommandCode.ERROR则表示出错，出错信息在对应的值中
	 * 
	 * */
	public static HashMap<String,Object> parseData(byte[]receiveData) {
		Protocol mProtocol = new Protocol();
		mProtocol.parseBytes(receiveData);
		HashMap<String,Object> map=new HashMap<String, Object>();
		map.put(EXTRA_CMD, ProtocolTool.byteArrayToInt(mProtocol.getCommandCode()));
		map.put(EXTRA_CODE, mProtocol.getResultCode());
		if(mProtocol.getResultCode()!=Protocol.RESULT_OK){//有错误
			map.put(EXTRA_DATA, mProtocol.getResultMessage());
		}else{
			byte[]data=mProtocol.getParamData();
			mEBikeData.setEBike(data);
			map.put(EXTRA_DATA, mEBikeData);
		}
		return map;
	}
}
