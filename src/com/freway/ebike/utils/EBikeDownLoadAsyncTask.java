/**
 * 
 */
package com.freway.ebike.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.freway.ebike.R;
import com.freway.ebike.common.EBConstant;
import com.freway.ebike.service.UpdateAPPService.UpdateAppListener;

/**
 * 安装包下载器
 * 
 * @author Nicolls
 *
 *         2015年7月24日
 */
public class EBikeDownLoadAsyncTask extends AsyncTask<String, Integer, String> {
	private static final int NOTIFYID = 2015072411;
	private Notification mNotification;
	private NotificationManager mNotificationManager;
	private File appFile;
	private String appName = EBConstant.APP_DOWNLOAD_APK_NAME;
	private Context context;
	private UpdateAppListener mUpdateAppListener;

	public EBikeDownLoadAsyncTask(Context context, UpdateAppListener updateAppListener) {
		this.context = context;
		this.mUpdateAppListener = updateAppListener;
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			URL url = new URL(params[0]);
			HttpURLConnection con;
			try {
				con = (HttpURLConnection) url.openConnection();
				con.connect();
				int code = con.getResponseCode();
				if (code == 200) {
					InputStream is = con.getInputStream();
					byte[] buf = new byte[1024];
					int length = con.getContentLength();
					int downloadCount = length / buf.length;
					int spacing = downloadCount / 100;
					int m = 0;
					FileOutputStream fos = new FileOutputStream(appFile);
					int len = 0;
					int value = 0;
					while ((len = is.read(buf)) != -1) {
						fos.write(buf, 0, len);
						if (m >= spacing) {
							m = 0;
							value++;
							this.onProgressUpdate(value);
						}
						m++;
					}
					fos.flush();
					fos.close(); // 使用流完毕之后,记得关闭流
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		String appPath = path + "/" + appName;
		appFile = new File(appPath);
		try {
			appFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		createAppUpdate();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		updateDownLoadProgress(-1);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		updateDownLoadProgress(values[0]);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	/** 更新下载进度 */
	private void updateDownLoadProgress(int progress) {
		if (progress == -1) {// 下载完成
			String fileName = appFile.getAbsolutePath();
			// String md5=MD5Tool.getFileMD5String(appFile);
			// log.i("验证下载包 本地文件验证md5="+md5+"服务器返回md5验证＝"+appMd5Code);
			// if(TextUtils.equals(md5, appMd5Code)){//验证成功
			if (mUpdateAppListener != null) {
				mUpdateAppListener.updateAppCompleted(context
						.getString(R.string.app_download_completed));
			}
			mNotificationManager.cancel(NOTIFYID);
			// Toast.makeText(this, "Gridview下载包验证成功",
			// Toast.LENGTH_SHORT).show();
			Uri uri = Uri.fromFile(new File(fileName));
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 启动新的activity
			intent.setDataAndType(uri, "application/vnd.android.package-archive");
			context.startActivity(intent);
			// }
			// else {
			// if (mUpdateAppListener != null) {
			// mUpdateAppListener.updateCompleted("安装包验证失败，请重新下载");
			// }
			// mNotification.contentView.setTextViewText(
			// R.id.update_app_remote_tv, "安装包验证失败，请重新下载");
			// mNotification.contentView.setProgressBar(
			// R.id.update_app_remote_progressbar, 100, 0, false);
			// mNotificationManager.notify(notifyId, mNotification);
			// }
			mNotification = null;
		} else if (progress < 100) {//
			mNotification.contentView.setTextViewText(R.id.update_app_remote_tv,
					context.getString(R.string.complete) + progress + "%");
			mNotification.contentView.setProgressBar(R.id.update_app_remote_progressbar, 100,
					progress, false);
			mNotificationManager.notify(NOTIFYID, mNotification);
		}
	}

	/** 新建一个APP下载,如果正在下载，则显示当前APP下载页面 */
	private void createAppUpdate() {
		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		RemoteViews remoteView = new RemoteViews(context.getPackageName(),
				R.layout.view_update_app_notify_remote);
		mNotification = builder.setContentTitle(context.getString(R.string.app_update))
				.setContentText(context.getString(R.string.app_downloading))
				.setSmallIcon(R.drawable.ic_launcher).setContent(remoteView).setAutoCancel(false)
				.build();
		mNotificationManager.notify(NOTIFYID, mNotification);
	}

}
