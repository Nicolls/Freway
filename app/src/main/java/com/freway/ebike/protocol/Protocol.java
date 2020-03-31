package com.freway.ebike.protocol;

import com.freway.ebike.utils.LogUtils;
/**
 * @author Nicolls
 * @Description 通讯协议
 * @date 2015年10月25日
 */
public class Protocol {
	/**数据解析正确*/
	public static final int RESULT_OK=0;
	/**数据为空*/
	public static final int RESULT_DATA_EMPTY=1;
	/**数据不完整*/
	public static final int RESULT_DATA_NO_COMPLETE=2;
	/**校验失败*/
	public static final int RESULT_CRC16_FAIL=3;
	/**数据错误*/
	public static final int RESULT_DATA_WRONG=4;
	/**数据最大长度*/
	private static final int MAX_DATA_LENGTH=250;
	/**蓝牙发送的数据包最大长度，如果一个数据超过这个长度那么蓝牙就会把他给分包发送，比如这个数据包长度为28，就会先发前面的20个数据再发剩下的8个数据。除去别的数据，如果一个数据长度大于13那么这个包就必须得分包发送*/
	private static final int MAX_PACK_DATA=13;
	/**标记*/
	private static final String TAG="Protocol";
	
	/**起始帧*/
	private static final byte START_SYMBOL=(byte) 0xFE;
	/**结束帧*/
	private static final byte END_SYMBOL=(byte) 0xBB;
	/**解析数据时的代码*/
	private int resultCode=RESULT_OK;

	/**错误信息*/
	private String resultMessage;

	/**接收到的原始数据包，打包完成后的数据*/
	private byte[] packetBytes;

	
	/**帧头*/
	private byte[] beginSymbol;
	/**命令字*/
	private byte[] commandCode;

	/**数据长度<250*/
	private byte[] dataLength;

	/**参数*/
	private byte[]paramData;
	/**CRC16校验码*/
	private byte[]crc16Code;
	/**帧尾*/
	private byte[] endSymbol;

	/**分包数据，用于存储需要分包的数据*/
	private static byte[]separate;//分包发送
	/**
	 * create a new instance Protocol
	 */
	public Protocol() {
		this.beginSymbol = new byte[2];//帧头2个字节 0xFE,0xFE
		this.commandCode = new byte[1];//命令1个字节
		this.dataLength = new byte[1];//数据长度1个字节，值小于MAX_DATA_LENGTH
		this.paramData = new byte[0];//参数，数组大小要根据数据长度来计算
		this.crc16Code = new byte[2];//检验码2个字节 crc_L crc_H
		this.endSymbol = new byte[1];//结束帧1个字节 0xBB
	}
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public byte[] getPacketBytes() {
		return packetBytes;
	}
	public byte[] getBeginSymbol() {
		return beginSymbol;
	}
	public void setBeginSymbol(byte[] beginSymbol) {
		this.beginSymbol = beginSymbol;
	}
	public byte[] getCommandCode() {
		return commandCode;
	}
	public void setCommandCode(byte[] commandCode) {
		this.commandCode = commandCode;
	}
	public byte[] getDataLength() {
		return dataLength;
	}
	public void setDataLength(byte[] dataLength) {
		this.dataLength = dataLength;
	}
	public byte[] getParamData() {
		return paramData;
	}
	public void setParamData(byte[] paramData) {
		this.paramData = paramData;
	}
	
	public byte[] getCrc16Code() {
		return crc16Code;
	}
	public void setCrc16Code(byte[] crc16Code) {
		this.crc16Code = crc16Code;
	}
	
	public byte[] getEndSymbol() {
		return endSymbol;
	}
	public void setEndSymbol(byte[] endSymbol) {
		this.endSymbol = endSymbol;
	}
	/**通过传入的参数进行协议打包，打包完成后，返回打包好后可以发送的数据包
	 * @param cmd 操作命令
	 * @param data 要发送的参数，数据
	 * @return byte[] 打包完成可以发送的数据包
	 * */
	public byte[] pack(int cmd,byte[] data){
		if(data!=null&&data.length>MAX_DATA_LENGTH){
			LogUtils.e(TAG, "数据长度超过"+MAX_DATA_LENGTH+"字节了");
		}
		//帧头/尾标记：使用0xFE 0xFE两个字节作为帧头, 使用0xBB一个字节作为帧尾。
		setBeginSymbol(new byte[] { START_SYMBOL, START_SYMBOL});
		setCommandCode(ProtocolTool.intToByteArray(cmd, this.commandCode.length));
		if(data!=null){
			setDataLength(ProtocolTool.intToByteArray(data.length,this.dataLength.length));
		}
		if(data!=null){
			setParamData(data);
		}
		//校验码
		//计算校验码
		int verLen=commandCode.length+dataLength.length+paramData.length;
		byte[] verByte=new byte[verLen];
		int j=0;
		for(int i=0;i<commandCode.length;i++){
			verByte[j++]=commandCode[i];
		}
		for(int i=0;i<dataLength.length;i++){
			verByte[j++]=dataLength[i];
		}
		for(int i=0;i<paramData.length;i++){
			verByte[j++]=paramData[i];
		}
		//校验值
		setCrc16Code(ProtocolTool.intToByteArray(ProtocolTool.getCRC16(verByte, verByte.length), crc16Code.length));
		LogUtils.d(TAG, "发送的数据校验码="+ProtocolTool.byteArrayToInt(this.crc16Code));
		//结束帧
		setEndSymbol(new byte[]{END_SYMBOL});
		
		initPacketBytes();
		return this.packetBytes;
	}
	/**
	 * @return byte[]
	 * @Description 调用这个方法，则需要完全初始化协议
	 */
	public byte[] initPacketBytes() {
		byte[] arr = new byte[getByteSize()];
		int j = 0;
		//起始帧
		for (int i = 0; i < beginSymbol.length; i++) {
			 arr[j++]=beginSymbol[i];
		}
		
		//命令字
		for (int i = 0; i < commandCode.length; i++) {
			 arr[j++]=commandCode[i];
		}
		//数据长度位
		for (int i = 0; i < dataLength.length; i++) {
			 arr[j++]=dataLength[i];
		}
		//参数
		for (int i = 0; i < paramData.length; i++) {
			arr[j++]=paramData[i];
		}
		//校验码
		for (int i = 0; i < crc16Code.length; i++) {
			arr[j++]=crc16Code[i];
		}
		//结束帧
		for (int i = 0; i < endSymbol.length; i++) {
			arr[j++]=endSymbol[i];
		}
		this.packetBytes=arr;
		return arr;
	}

	
	/**
	 * @return int
	 * @Description 数据长度，指的是整个数据包的长度包括起始祯头到祯尾的数据
	 */
	private int getByteSize() {
		int length=0;
		length=beginSymbol.length+commandCode.length+dataLength.length+paramData.length+crc16Code.length+endSymbol.length;
		return length;
	}
	
	/**
	 * @param parseDataBytes 要解析的数据包
	 * @return boolean 返回解析状态，成功true，失败false
	 * @Description 解析接收到的数据，在解析之前一定要初始化好数据，给PacketBytes赋值
	 */
	public boolean parseBytes(byte[] parseDataBytes) {
		boolean isOk=true;
		this.packetBytes=parseDataBytes;
		if(packetBytes==null){//空
			separate=null;
			this.resultCode=RESULT_DATA_EMPTY;
			return false;
		}
		if(packetBytes.length==0){//空
			separate=null;
			this.resultCode=RESULT_DATA_EMPTY;
			return false;
		}
		
		//分包处理
		if(separate!=null){//说明上一个包是个分包
			LogUtils.i(TAG,"上一个是分包数据:"+ProtocolTool.bytesToHexString(separate));
			LogUtils.i(TAG,"这次收到的是剩下的分包数据："+ProtocolTool.bytesToHexString(parseDataBytes));
			byte[]temp=new byte[separate.length+parseDataBytes.length];
			for(int i=0;i<temp.length;i++){
				if(i<separate.length){
					temp[i]=separate[i];
				}else{
					temp[i]=parseDataBytes[i-separate.length];
				}
			}
			parseDataBytes=temp;
			this.packetBytes=parseDataBytes;
			LogUtils.i(TAG,"重新合回来后是："+ProtocolTool.bytesToHexString(parseDataBytes));
			separate=null;
		}
		//正常处理
		
		try {//只要解析有错误，都是数据有问题
			int j = 0;
			//起始帧
			for (int i = 0; i < beginSymbol.length; i++) {
				beginSymbol[i] = packetBytes[j++];
				if(beginSymbol[i]!=START_SYMBOL){
					this.resultCode=RESULT_DATA_WRONG;
					setResultMessage("数据起始帧头错误");
					return false;
				}
			}
			
			//命令字
			for (int i = 0; i < commandCode.length; i++) {
				commandCode[i] = packetBytes[j++];
			}
			//数据长度位
			for (int i = 0; i < dataLength.length; i++) {
				dataLength[i] = packetBytes[j++];
			}
			if(ProtocolTool.byteArrayToInt(dataLength)>MAX_DATA_LENGTH||ProtocolTool.byteArrayToInt(dataLength)<0){
				this.resultCode=RESULT_DATA_WRONG;
				setResultMessage("数据长度超出范围");
				return false;
			}
			//判断如果数据长度大于13说明得分包发送
			if(ProtocolTool.byteArrayToInt(dataLength)>MAX_PACK_DATA&&parseDataBytes.length<=20){//如果数据部分长度大于13，并且包总长度是20，就是分包发送，因为有可能这个包是我们后面组合好了的。
				System.out.println("数据长度大于13，分包发送");
				separate=parseDataBytes;
				return false;
			}else{
				separate=null;	
			}
			//参数
			int dataSize=dataLength[0];
			paramData=new byte[dataSize];
			for (int i = 0; i < dataSize; i++) {
				paramData[i] = packetBytes[j++];
			}
			//核验码
			//计算校验码
			int verLen=commandCode.length+dataLength.length+paramData.length;
			byte[] verByte=new byte[verLen];
			int startVer=beginSymbol.length;
			for(int i=0;i<verByte.length;i++){
				verByte[i]=packetBytes[startVer++];
			}
			//本地校验值  
			byte[]crc16CodeTemp=ProtocolTool.intToByteArray(ProtocolTool.getCRC16(verByte, verByte.length), crc16Code.length);
			for (int i = 0; i < crc16Code.length; i++) {
				crc16Code[i] = packetBytes[j++];//返回的数据校验值
			}
			LogUtils.d(TAG,"本地校验值="+ProtocolTool.byteArrayToInt(crc16CodeTemp)+ "接到的数据校验值="+ProtocolTool.byteArrayToInt(this.crc16Code));
			//校验
			if(crc16Code[0]!=crc16CodeTemp[0]||crc16Code[1]!=crc16CodeTemp[1]){//校验正确
				//校验失败
				this.resultCode=RESULT_CRC16_FAIL;
				return false;
			}
			//结束帧
			for (int i = 0; i < endSymbol.length; i++) {
				endSymbol[i] = packetBytes[j++];
				if(endSymbol[i]!=END_SYMBOL){
					this.resultCode=RESULT_DATA_WRONG;
					setResultMessage("数据结束帧有错误");
					return false;
				}
			}

		} catch (Exception e) {
			LogUtils.e(TAG, "解析错误",e);
			this.resultCode=RESULT_DATA_WRONG;
			isOk=false;
		}
		return isOk;

	}
}
