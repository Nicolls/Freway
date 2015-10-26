package com.freway.ebike.protocol;

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
    	for(int i=0;i<bytes.length;i++){
    		byte temp=bytes[i];
    		for(int j=0;j<8;j++){
    			result=result+(byte) ((temp >>7-j) & 0x1) ;
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
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
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
		if(src.length>3){
			for (int i = 3; i >= 0; i--) {
				int v = src[i] & 0xFF;
				String hv = Integer.toHexString(v);
				if (hv.length() < 2) {
					stringBuilder.append(0);
				}
				stringBuilder.append(hv);
			}
		}
		return stringBuilder.toString();
	}
}
