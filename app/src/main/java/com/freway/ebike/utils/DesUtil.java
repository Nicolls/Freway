package com.freway.ebike.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;

import android.util.Base64;

/**
 * DES加解密工具类
 * 
 * @author Nicolls
 *
 *         2015年6月2日
 */
public class DesUtil {

	static Key key;

	private static DesUtil desUtil;

	/**
	 * 获取加密工具实例
	 * 
	 * @param keyString
	 *            密钥
	 * */
	public static DesUtil getInstance(String keyString) {
		if (null == desUtil) {
			desUtil = new DesUtil();
			setKey(keyString); // 生成密匙
		}
		return desUtil;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * 根据参数生成 KEY
	 */
	public static void setKey(String strKey) {
		try {
			KeyGenerator generator = KeyGenerator.getInstance("DES");
			generator.init(new SecureRandom(strKey.getBytes()));
			key = generator.generateKey();
			generator = null;
		} catch (Exception e) {
			throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
		}
	}

	/**
	 * 加密 String 明文输入 ,String 密文输出
	 */
	public String encryptStr(String strMing) {
		byte[] byteMi = null;
		byte[] byteMing = null;
		String strMi = "";
		// BASE64Encoder base64en = new BASE64Encoder();
		try {
			byteMing = strMing.getBytes("UTF8");
			byteMi = this.encryptByte(byteMing);
			strMi = Base64.encodeToString(byteMi, Base64.DEFAULT);
			// strMi = base64en.encode(byteMi);
		} catch (Exception e) {
			throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
		} finally {
			// base64en = null ;
			byteMing = null;
			byteMi = null;
		}
		return strMi;
	}

	/**
	 * 解密 以 String 密文输入 ,String 明文输出
	 *
	 * @param strMi
	 * @return
	 */
	public String decryptStr(String strMi) {
		// BASE64Decoder base64De = new BASE64Decoder();
		byte[] byteMing = null;
		byte[] byteMi = null;
		String strMing = "";
		try {
			// byteMi = base64De.decodeBuffer(strMi);
			byteMi = Base64.decode(strMi, Base64.DEFAULT);
			byteMing = this.decryptByte(byteMi);
			strMing = new String(byteMing, "UTF8");
		} catch (Exception e) {
			throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
		} finally {
			// base64De = null ;
			byteMing = null;
			byteMi = null;
		}
		return strMing;
	}

	/**
	 * 加密以 byte[] 明文输入 ,byte[] 密文输出
	 *
	 * @param byteS
	 * @return
	 */
	private byte[] encryptByte(byte[] byteS) {
		byte[] byteFina = null;
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byteFina = cipher.doFinal(byteS);
		} catch (Exception e) {
			throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * 解密以 byte[] 密文输入 , 以 byte[] 明文输出
	 *
	 * @param byteD
	 * @return
	 */
	private byte[] decryptByte(byte[] byteD) {
		Cipher cipher;
		byte[] byteFina = null;
		try {
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byteFina = cipher.doFinal(byteD);
		} catch (Exception e) {
			throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
		} finally {
			cipher = null;
		}
		return byteFina;
	}

	/**
	 * 文件 file 进行加密并保存目标文件 destFile 中
	 *
	 * @param file
	 *            要加密的文件 如 c:/test/srcFile.txt
	 * @param destFile
	 *            加密后存放的文件名 如 c:/ 加密后文件 .txt
	 */
	public void encryptFile(String file, String destFile) throws Exception {
		Cipher cipher = Cipher.getInstance("DES");
		// cipher.init(Cipher.ENCRYPT_MODE, getKey());
		cipher.init(Cipher.ENCRYPT_MODE, this.key);
		InputStream is = new FileInputStream(file);
		OutputStream out = new FileOutputStream(destFile);
		CipherInputStream cis = new CipherInputStream(is, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = cis.read(buffer)) > 0) {
			out.write(buffer, 0, r);
		}
		cis.close();
		is.close();
		out.close();
	}

	/**
	 * 文件采用 DES 算法解密文件
	 *
	 * @param file
	 *            已加密的文件 如 c:/ 加密后文件 .txt *
	 *  destFile
	 *            解密后存放的文件名 如 c:/ test/ 解密后文件 .txt
	 */
	public void decryptFile(String file, String dest) throws Exception {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, this.key);
		InputStream is = new FileInputStream(file);
		OutputStream out = new FileOutputStream(dest);
		CipherOutputStream cos = new CipherOutputStream(out, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = is.read(buffer)) >= 0) {
			cos.write(buffer, 0, r);
		}
		cos.close();
		out.close();
		is.close();
	}

	public static void main(String[] args) throws Exception {
		String mine_key = "gridview-token";
		String encypt_content = "sugon123";
		DesUtil des = DesUtil.getInstance(mine_key);
		// DES 加密文件
		// des.encryptFile("G:/test.doc", "G:/ 加密 test.doc");
		// DES 解密文件
		// des.decryptFile("G:/ 加密 test.doc", "G:/ 解密 test.doc");
		String str1 = encypt_content;
		// DES 加密字符串
		String str2 = des.encryptStr(str1);
		// DES 解密字符串
		String deStr = des.decryptStr(str2);
		System.out.println(" 加密前： " + str1);
		System.out.println(" 加密后： " + str2);
		System.out.println(" 解密后： " + deStr);
	}
}