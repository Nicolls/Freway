package com.freway.ebike.common;

import java.io.Serializable;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.listener.OpenActivityResultListener;
import com.freway.ebike.net.DataUpdateListener;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.net.EBikeRequestServiceFactory;
import com.freway.ebike.utils.ErrorUtils;
import com.freway.ebike.utils.EBikeActivityManager;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.ErrorUtils.ErrorListener;
import com.freway.ebike.utils.ErrorUtils.SuccessListener;
import com.freway.ebike.view.LoadingDialog;

/**
 * Activity基类 实现了http请求返回监听接口DataUpdateListener 定义成抽象类是统一处理返回的数据，再交给子类来处理
 * 
 * @author mengjk
 *
 *         2015年5月15日
 */
public abstract class BaseActivity extends AppCompatActivity implements DataUpdateListener {
//	protected boolean isLog = true;
	protected EBikeRequestService mEBikeRequestService;
	protected TextView mTitleView;
	private LoadingDialog loading;
	public String tag = "BaseActivity";
	private OpenActivityResultListener mOpenActivityResultListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEBikeRequestService = EBikeRequestServiceFactory.getInstance(getApplicationContext(),
				EBikeRequestServiceFactory.REQUEST_VOLLEY);
		mEBikeRequestService.setUptateListener(this);
		tag = this.getClass().getSimpleName();
		EBikeActivityManager.getAppManager().addActivity(this);
		LogUtils.i(tag, "onCreate");
		setContentLayout();
	}
	
	public void setOpenActivityResultListener(OpenActivityResultListener lis){
		this.mOpenActivityResultListener=lis;
	}

	/** 设置布局文件 */
	protected void setContentLayout() {
	}

	public EBikeRequestService getService() {
		return mEBikeRequestService;
	}


	@Override
	protected void onStart() {
		super.onStart();
		LogUtils.i(tag, "onStart");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		LogUtils.i(tag,  "onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtils.i(tag,  "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtils.i(tag,  "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtils.i(tag,  "onStop");
	}

	@Override
	protected void onDestroy() {
		EBikeActivityManager.getAppManager().removeActivity(this);
		hideLoading();
		super.onDestroy();
		LogUtils.i(tag, "onDestroy");
	}


	@Override
	public void update(int id, Object obj) {
		ErrorUtils.handle(this, id, obj, new SuccessListener() {

			@Override
			public void successCompleted(int id, Object obj) {
				dateUpdate(id, obj);
			}
		},new ErrorListener() {
			
			@Override
			public void errorCompleted(int id) {
				hideLoading();
				requestError(id);
			}
		});
	}
	
	/**请求错误会调用这个方法*/
	protected void requestError(int id){
		
	}

	/** 显示加载框 */
	public void showLoading(boolean canCancel) {
		if (loading == null) {
			loading = new LoadingDialog(this);
		}
		loading.setCancelable(canCancel);
		loading.show();
	}

	/** 隐藏加载框 */
	public void hideLoading() {
		if (loading != null) {
			loading.dismiss();
		}
	}

	/** 抽象方法，用来通知activity数据已请求回来 */
	public abstract void dateUpdate(int id, Object obj);

	/** 菜单左按键点击 */
	public void onNavLeft(View view) {
		finish();
	}

	/** 菜单右按键点击 */
	public void onNavRight(View view) {

	}

	/** 打开某个Activity */
	public void openActivity(Class<?> cls, HashMap<String, Object> data, boolean isFinishSelf) {
		openActivity(cls, data, isFinishSelf, false, 0);
	}

	/** 打开某个Activity */
	public void openActivity(Class<?> cls, HashMap<String, Object> data, boolean isFinishSelf,
			boolean isStartForResult, int resultCode) {
		Intent intent = new Intent(this, cls);
		if (null != data) {
			for (String key : data.keySet()) {
				Object object = data.get(key);
				if (object instanceof Integer) {
					intent.putExtra(key, (Integer) object);
				} else if (object instanceof Integer[]) {
					intent.putExtra(key, (Integer[]) object);
				} else if (object instanceof String) {
					intent.putExtra(key, (String) object);
				} else if (object instanceof String[]) {
					intent.putExtra(key, (String[]) object);
				} else if (object instanceof Bundle) {
					intent.putExtra(key, (Bundle) object);
				} else if (object instanceof Parcelable) {
					intent.putExtra(key, (Parcelable) object);
				} else if (object instanceof Serializable) {
					intent.putExtra(key, (Serializable) object);
				}
			}
		}
		if (isStartForResult) {
			startActivityForResult(intent, resultCode);
		} else {
			startActivity(intent);
		}
		if (isFinishSelf) {
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(this.mOpenActivityResultListener!=null){
			this.mOpenActivityResultListener.onOpenActivityResult(requestCode,resultCode,data);
		}
	}
	
}
