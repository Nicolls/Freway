package com.freway.ebike.view;

import android.R.color;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;

import com.freway.ebike.R;

/***
 * 自定义基类加载进度圈
 * 
 * @author mengjk
 *
 *         2015年6月2日
 */
public class LoadingDialog extends Dialog {

	private Animation rotAnim;
	private ProgressBar mProgressBar;
	private boolean cancelable=true;
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 */
	public LoadingDialog(Context context) {
		super(context, R.style.GVLoadingDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.common_loading);
		mProgressBar = (ProgressBar) findViewById(R.id.common_dialog_loading);
		getWindow().setBackgroundDrawable(new ColorDrawable(color.transparent));
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(cancelable){
				dismiss();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void show(boolean canCancel) {
		super.show();
		cancelable=canCancel;
		setCancelable(canCancel);
		if (rotAnim == null) {
			rotAnim = new RotateAnimation(0, -359, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			rotAnim.setDuration(1000);
			rotAnim.setInterpolator(new LinearInterpolator());
			rotAnim.setRepeatCount(Animation.INFINITE);
		}

		if (mProgressBar != null) {
			mProgressBar.startAnimation(rotAnim);
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

}
