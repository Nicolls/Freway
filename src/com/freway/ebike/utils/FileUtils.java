/**
 * 
 */
package com.freway.ebike.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import com.freway.ebike.common.EBConstant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

/**
 * 文件操作类
 * 
 * @author Nicolls
 *
 * 2015年9月6日
 */
public class FileUtils {
	private static final String TAG=FileUtils.class.getSimpleName();
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
	/**save url image
	 * @return filePath
	 * */
	public static String saveBitmapByUrlOrName(String urlOrName,Bitmap bitmap){
		String filePath="";
		String fileName=urlOrName;
		fileName=fileName.replace("\\", "_");
		fileName=fileName.replace("/", "_");
		fileName=fileName.replace(":", "_");
		fileName=fileName.replace("*", "_");
		fileName=fileName.replace("?", "_");
		fileName=fileName.replace("\"", "_");
		fileName=fileName.replace("'", "_");
		fileName=fileName.replace("<", "_");
		fileName=fileName.replace(">", "_");
		LogUtils.i(TAG, "存储的文件名是:"+fileName);
		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+EBConstant.DIR_FREWAY);
        if (!file.exists())
            file.mkdirs();
        filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+EBConstant.DIR_FREWAY+"/"+fileName;
        file = new File(filePath);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            if(fileName.contains("png")){
            	bitmap.compress(CompressFormat.PNG, 80, fos);
            }else{
            	bitmap.compress(CompressFormat.JPEG, 80, fos);
            }
            fos.flush();
            fos.close();
        } catch (IOException e) {
            LogUtils.e(EBikeViewUtils.class.getSimpleName(), e.getMessage());
        }
        return filePath;
	}
	
	/**get url bitmap*/
	public static Bitmap getBitmapFromUrlOrName(String urlOrName){
		String fileName=urlOrName;
		fileName=fileName.replace("\\", "_");
		fileName=fileName.replace("/", "_");
		fileName=fileName.replace(":", "_");
		fileName=fileName.replace("*", "_");
		fileName=fileName.replace("?", "_");
		fileName=fileName.replace("\"", "_");
		fileName=fileName.replace("'", "_");
		fileName=fileName.replace("<", "_");
		fileName=fileName.replace(">", "_");
		LogUtils.i(TAG, "要获取的文件名是:"+fileName);
		String path=Environment.getExternalStorageDirectory().getAbsolutePath()+EBConstant.DIR_FREWAY+"/"+fileName;
		File file = new File(path);
		if(file.exists()){
			Bitmap bitmap=BitmapFactory.decodeFile(path);
			return bitmap;
		}else{
			return null;
		}
	}
}
