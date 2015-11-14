package com.freway.ebike.view;

import java.util.Calendar;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

public class ClickImageButton extends ImageButton{
	private static final int WHAT=1001;
	private long clickFirstTime=-1;
	private long clickSecondTime=-1;
	private ClickListener click;
	
	public interface ClickListener{
		void onClick();
		void onDoubleClick();
	}
	public void setOnClick(ClickListener click){
		this.click=click;
	}
	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(click!=null){
				click.onClick();	
			}
			clickFirstTime=-1;
			clickSecondTime=-1;
		}
		
	};
	public ClickImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ClickImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			
			break;
		case MotionEvent.ACTION_UP:
			long time=Calendar.getInstance().getTimeInMillis();
			if(clickFirstTime==-1){
				clickFirstTime=time;
				mHandler.sendEmptyMessageDelayed(WHAT, 300);
			}else{
				clickSecondTime=time;
				if(clickSecondTime-clickFirstTime<200){
					mHandler.removeMessages(WHAT);
					clickFirstTime=-1;
					clickSecondTime=-1;
					if(click!=null){
						click.onDoubleClick();
					}
				}else{
					clickFirstTime=-1;
					clickSecondTime=-1;
				}
			}
			
			break;
		}
		return super.onTouchEvent(event);
	}
	
	

}
