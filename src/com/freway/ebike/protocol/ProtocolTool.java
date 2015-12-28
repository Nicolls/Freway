package com.freway.ebike.protocol;

import com.freway.ebike.utils.LogUtils;

import android.content.Context;
import android.provider.Settings.Secure;

/**
 * @author Nicolls
 * @Description 通讯协议工具类
 * @date 2015年10月25日
 */
public class ProtocolTool {
	private static final String TAG=ProtocolTool.class.getSimpleName();

	/**获取一个UUID*/
	public static String getUUID(Context context) {
		String android_id = "";
		android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		return android_id;
	}
	/**
	 * @param data_arr 要校验的数组
	 * @param data_len 数组的长度
	 * @return char 
	 * @Description 获取校验码
	 */
	public static char getCRC16(byte[] data_arr, int data_len)
	{
		char crc16 = 0;
		for(int i =0; i < (data_len); i++)
	    {
			crc16 = (char)(( crc16 >> 8) | (crc16 << 8));
			crc16 ^= data_arr[i]& 0xFF;
			crc16 ^= (char)(( crc16 & 0xFF) >> 4);
			crc16 ^= (char)(( crc16 << 8) << 4);
			crc16 ^= (char)((( crc16 & 0xFF) << 4) << 1);
		}
		return crc16;
	}

	/** 
     * 把byte转为字符串的bit 
     */  
    public static String byteToBitString(byte[] bytes) {  
    	String result="";
    	if(bytes!=null){
    		for(int i=0;i<bytes.length;i++){
        		byte temp=bytes[i];
        		for(int j=0;j<8;j++){
        			result=result+(byte) ((temp >>7-j) & 0x1) ;
        		}
        	}
    	}
    	return result;
    }  
    
    /** 
     * 把byte转为8位整形数组的 
     */  
    public static int[] byteToBitIntArray(byte[] bytes) {  
    	int[] result=new int[bytes.length*8];
    	int m=0;
    	for(int i=0;i<bytes.length;i++){
    		byte temp=bytes[i];
    		for(int j=0;j<8;j++){
    			result[m]=(byte) ((temp >>7-j) & 0x1) ;
    			m++;
    		}
    	}
    	return result;
    }  


	/**
	 * Convert hex string to byte[] 把为字符串转化为字节数组
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
    public static byte[] hexStringToBytes(String hexString) {
		byte[] result = null;
		if (hexString == null || hexString.equals("")) {
			LogUtils.e("ProtocalTolls", "字符串为空");
			return null;
		}
		hexString = hexString.replace(" ", "");
		hexString = hexString.replace("0x", "");
		hexString = hexString.toUpperCase();
		if (hexString.length() % 2 != 0) {
			LogUtils.e("ProtocalTolls", "字符串长度错误");
			return null;
		}
		result = new byte[hexString.length() / 2];
		String temp = "";
		for (int i = 0, j = 0; i < hexString.length() - 1; i += 2, j++) {
			temp = hexString.substring(i, i + 2);
			result[j] = (byte) Integer.parseInt(temp, 16);
		}
		return result;
	}

	/**
	 * int 转为byte
	 * @param iSource 需要转换的int
	 * @param iArrayLen 转成byte数组的长度
	 * @return
	 */
	public static byte[] intToByteArray(int iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}
	
	/**
	 * byte 转为int
	 * @param iSource 需要转换的int
	 * @param iArrayLen 转成byte数组的长度
	 * @return
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int iOutcome = 0;
	    byte bLoop;

	    for (int i = 0; i < bytes.length; i++) {
	        bLoop = bytes[i];
	        iOutcome += (bLoop & 0xFF) << (8 * i);
	    }
	    return iOutcome;
	}
	
	
	/**
	 * Convert byte[] to hex
	 * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src
	 *            byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		
		for(int i=0;i<src.length;i++){
			String temp=Integer.toHexString(src[i]);
			if(temp.length()<2){
				temp="0"+temp;
			}else if(temp.length()>2){
				temp=temp.substring(temp.length()-2,temp.length());
			}
			stringBuilder.append(temp);
		}
		return stringBuilder.toString();
	}
}
