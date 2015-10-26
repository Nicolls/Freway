/**
 * 
 */
package com.freway.ebike.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * 文件操作类
 * 
 * @author Nicolls
 *
 * 2015年9月6日
 */
public class FileUtils {
	/**
	 * 通过传入的字符串在指定路径下创建一个txt文件，会阻塞线程
	 * */
	public static void createTXTFile(String fileName,String filePath,String txt){
		File dir=new File(filePath);
		dir.mkdirs();
		File file=new File(filePath+"/"+fileName);
		FileWriter fw=null;
		BufferedReader br=null;
		try {
			file.createNewFile();
			br=new BufferedReader(new StringReader(txt));
			
			fw=new FileWriter(file);
			char[] buf=new char[512];
			int len=0;
			while((len=br.read(buf))!=-1){
				fw.write(buf, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				fw.close();
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}
