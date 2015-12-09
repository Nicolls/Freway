/**
 * 
 */
package com.freway.ebike.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.freway.ebike.R;
import com.freway.ebike.model.EBErrorResponse;
import com.freway.ebike.model.EBResponse;
import com.freway.ebike.net.EBikeRequestService;

/**
 * 
 * 错误处理工具
 * 
 * @author Nicolls
 *
 *         2015年7月22日
 */
public class ErrorUtils {
	/**
	 * 发现处理后无错误，则返回到context页面 监听器
	 * */
	public interface SuccessListener {
		void successCompleted(int id, Object obj);
	}
	
	/**
	 * 有错误也可以返回的监听
	 * */
	public interface ErrorListener{
		void errorCompleted();
	}

	public static void handle(Activity context, int id, Object obj, SuccessListener lis,ErrorListener errorLis) {
		if(context!=null){
			if (id == EBikeRequestService.ID_REQUEST_ERROR) {
				ToastUtils.toast(context, context.getString(R.string.http_request_error));
				if(errorLis!=null){
					errorLis.errorCompleted();
				}
			} else if (obj instanceof EBErrorResponse) {// 登录或者请求出问题
				EBErrorResponse errorRes = (EBErrorResponse) obj;
				if (!TextUtils.equals(errorRes.getCode(), EBResponse.SUCCESS_CODE) ) {
					LogUtils.systemOut("登录或者请求出问题：" + errorRes.getText());
					if(errorLis!=null){
						errorLis.errorCompleted();
					}
					if(TextUtils.equals(errorRes.getCode(),EBResponse.TOKEN_INVALID)) {// 说明token失效
						EBikeActivityManager.getAppManager().reLogin(context, true);
					}else if(TextUtils.equals(errorRes.getCode(),EBResponse.USER_NAME_EXITS)){//用户存在
						EBikeActivityManager.getAppManager().reLogin(context, true);
					}
					ToastUtils.toast(context, errorRes.getMsg());
//					alertDialog(context);
				} else {
					if(errorLis!=null){
						errorLis.errorCompleted();
					}
				}

			} else if(!TextUtils.equals(((EBResponse) obj).getCode(),EBResponse.SUCCESS_CODE)) {// 说明有错误
				if(errorLis!=null){
					errorLis.errorCompleted();
				}
				ToastUtils.toast(context, ((EBResponse) obj).getMsg());
			}else if(TextUtils.equals(((EBResponse) obj).getCode(),EBResponse.TOKEN_INVALID)) {// 说明token失效
				EBikeActivityManager.getAppManager().reLogin(context, true);
			}
			else if (!context.isFinishing()) {// activity还存在
				lis.successCompleted(id, obj);
			}else{
				LogUtils.systemOut("数据正常返回但是activity不存在了!");
			}
		}else{
			LogUtils.systemOut("contxt为空!");
		}
	}

}
