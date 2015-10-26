package com.freway.ebike.utils;

import java.util.HashMap;
import java.util.Set;

import com.freway.ebike.activity.LoginActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * 
 */
public class EBikeActivityManager {
	public static HashMap<String, Activity> activitys;
	private static EBikeActivityManager instance;
	private long clickCurrenTime = 0L;

	private EBikeActivityManager() {
	}

	/**
	 * 单一实例
	 */
	public static EBikeActivityManager getAppManager() {
		if (instance == null) {
			instance = new EBikeActivityManager();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (activitys == null) {
			activitys = new HashMap<String, Activity>();
		}

		String key = activity.getClass().getSimpleName();
		activitys.put(key, activity);
		LogUtils.i(EBikeActivityManager.class.getSimpleName(), "将"
				+ activity.getClass().getSimpleName() + "添加到管理栈中");
	}

	/**
	 * 结束指定的Activity
	 */
	public void removeActivity(Activity activity) {
		if (activity != null) {
			String key = activity.getClass().getSimpleName();
			// if(activity.getClass() ==MainFragmentActivity.class){//点击多次，退出应用
			// Long currenTime=System.currentTimeMillis();
			// if(currenTime-clickCurrenTime<1500){//1.5秒内点击有用
			// activitys.remove(key);
			// activity.finish();
			// activity = null;
			// }else{
			// Toast.makeText(activity, "再按一次，退出应用", 0).show();
			// clickCurrenTime=currenTime;
			// }
			// }else{
			LogUtils.i(EBikeActivityManager.class.getSimpleName(), "从管理栈中移除"
					+ activity.getClass().getSimpleName());
			activitys.remove(key);
			// }
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void removeActivity(Class<?> cls) {
		Set<String> set = activitys.keySet();
		for (String key : set) {
			if (cls.getSimpleName().equals(key)) {
				activitys.remove(key);
			}
		}

	}

	/**
	 * 结束所有Activity
	 */
	public void removeAllActivity() {
		Set<String> set = activitys.keySet();
		for (String key : set) {
			Activity a = activitys.get(key);
			if (a != null && !a.isFinishing()) {
				a.finish();
			}
		}
		activitys.clear();
	}

	/** 重新登录 */
	public void reLogin(Context context, boolean isCleanData) {
		SPUtils.setEnterBefore(context, true);
		if (isCleanData) {
			SPUtils.cleanLocalData(context);
		}
		SPUtils.setAutoLoginState(context, false);
		Intent intent = new Intent(context, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		context.startActivity(intent);

	}

	/**
	 * 退出应用程序
	 */
	public void appExit(Context context) {
		// try {
		removeAllActivity();
		System.exit(0);
		// } catch (Exception e) {
		// }
	}
}