package com.freway.ebike.view;

import com.freway.ebike.R;
import com.freway.ebike.utils.LogUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * @author mengjk
 *
 * 2015年8月18日
 */
public class HomeViewLayout extends RelativeLayout{
	private static final String TAG="HomeViewLayout-->";
	/** 车况view */
	private View bikeStateView;
	/** 车速view */
	private View speedStateView;
	private ImageView mSpeedStateArrowTopView;
	private ImageView mSpeedStateArrowBottomView;
	/** 电池view */
	private View batteryStateView;
	/** 骑行状态view */
	private View travelStateView;
	/** 分隔线用于显示电池view,点击第一次显示，点击第二次隐藏 */
	private View lineTopView;
	/** 分隔线用于显示速度view */
	private View lineBottomView;
	//判断值
	private boolean isInterceptionEvent=false;
	//各个view的大小
	private int bikeStateViewHeight;//车况view高度
	private int speedStateViewHeight;//车速view高度
	private int batteryStateViewHeight;//电池view高度
	private int travelStateViewHeight;//骑行状态view高度
	private int lineTopViewHeight;//上分隔线view高度
	private int lineBottomViewHeight;//下分隔线view高度
	//距离
	private int move;//拉动的距离
	float yDown=0;//事件down,y轴上的点
	float yMove=0;//事件move，y轴上的点
	float yUp=0;//事件up，y轴上的点
	
	//当前UI样式
	private static final int UI_MODEL_SPEED=0;
	private static final int UI_MODEL_MAP=1;
	private static final int UI_MODEL_BATTERY=2;
	private int uiModel=UI_MODEL_SPEED;
	private OnClick onClick;
	/**
	 * @param context
	 */
	public HomeViewLayout(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public HomeViewLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initView();
	}
	
	
	/**初始view*/
	private void initView(){
		onClick=new OnClick();
		/** 车况view */
		bikeStateView=findViewById(R.id.home_bike_state);
		/** 车速view */
		speedStateView=findViewById(R.id.home_speed_view);
		mSpeedStateArrowTopView=(ImageView) findViewById(R.id.speed_state_arrow_top);
		mSpeedStateArrowBottomView=(ImageView) findViewById(R.id.speed_state_arrow_bottom);
		/** 电池view */
		batteryStateView=findViewById(R.id.home_battery_view);
		/** 骑行状态view */
		travelStateView=findViewById(R.id.home_travel_state);
		/** 分隔线用于显示电池view,点击第一次显示，点击第二次隐藏 */
		lineTopView=findViewById(R.id.home_view_line_top);
		/** 分隔线用于显示速度view */
		lineBottomView=findViewById(R.id.home_view_line_bottom);
		
		bikeStateView.measure(0, 0);
		speedStateView.measure(0, 0);
		batteryStateView.measure(0, 0);
		travelStateView.measure(0, 0);
		lineTopView.measure(0, 0);
		lineBottomView.measure(0, 0);
		
		bikeStateViewHeight=bikeStateView.getMeasuredHeight();//车况view高度
		speedStateViewHeight=speedStateView.getMeasuredHeight();//车速view高度
		batteryStateViewHeight=batteryStateView.getMeasuredHeight();//电池view高度
		travelStateViewHeight=travelStateView.getMeasuredHeight();//骑行状态view高度
		lineTopViewHeight=lineTopView.getMeasuredHeight();//上分隔线view高度
		lineBottomViewHeight=lineBottomView.getMeasuredHeight();//下分隔线view高度
		
		//事件监听
		lineTopView.setOnClickListener(onClick);
		lineBottomView.setOnClickListener(onClick);
		mSpeedStateArrowTopView.setOnClickListener(onClick);
		mSpeedStateArrowBottomView.setOnClickListener(onClick);
	}
	/**监听触摸事件 */
	class OnClick implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.home_view_line_top:
				if(uiModel==UI_MODEL_MAP){//地图模式
					uiModel=UI_MODEL_BATTERY;
				}else{//电池
					uiModel=UI_MODEL_MAP;
				}
				break;
			case R.id.home_view_line_bottom:
				uiModel=UI_MODEL_SPEED;
				break;
			case R.id.speed_state_arrow_top:
				uiModel=UI_MODEL_BATTERY;
				break;
			case R.id.speed_state_arrow_bottom:
				uiModel=UI_MODEL_MAP;
				break;
				default:
					uiModel=UI_MODEL_MAP;
					break;
			}
			requestLayout();
		}
		
	}
	
	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		LogUtils.i(TAG, "onLayout"+"changed-left-top-right-bottom"+":"+changed+"-"+l+"-"+t+"-"+r+"-"+b);
		LogUtils.i(TAG, "onLayout"+"bikeStateViewHeight"+":"+bikeStateViewHeight);
		LogUtils.i(TAG, "onLayout"+"speedStateViewHeight"+":"+speedStateViewHeight);
		LogUtils.i(TAG, "onLayout"+"batteryStateViewHeight"+":"+batteryStateViewHeight);
		LogUtils.i(TAG, "onLayout"+"travelStateViewHeight"+":"+travelStateViewHeight);
		LogUtils.i(TAG, "onLayout"+"lineTopViewHeight"+":"+lineTopViewHeight);
		LogUtils.i(TAG, "onLayout"+"lineBottomViewHeight"+":"+lineBottomViewHeight);

		switch(uiModel){
		case UI_MODEL_MAP:
//			mHeadView.layout(l, move-headHegiht, r, move);
//			mContentView.layout(l, move, r, move+b);
//			mTailView.layout(l, b+move, r, b+move+tailHeight);
			LogUtils.i(TAG, "map-model");
			bikeStateView.layout(l, 0, r, bikeStateViewHeight);
			lineTopView.layout(l, bikeStateViewHeight, r, bikeStateViewHeight+lineTopViewHeight);
			travelStateView.layout(l, bikeStateViewHeight+lineTopViewHeight, r, bikeStateViewHeight+lineTopViewHeight+travelStateViewHeight);
			lineBottomView.layout(l, bikeStateViewHeight+lineTopViewHeight+travelStateViewHeight, r, bikeStateViewHeight+lineTopViewHeight+travelStateViewHeight+lineBottomViewHeight);
			speedStateView.layout(0, 0, 0, 0);
			batteryStateView.layout(0, 0, 0, 0);
			super.onLayout(changed, l, 0, r, 500);
			break;
		case UI_MODEL_SPEED:
			LogUtils.i(TAG, "speed-model");
			bikeStateView.layout(l, 0, r, bikeStateViewHeight);
			lineTopView.layout(0, 0, 0, 0);
			travelStateView.layout(0,0,0,0);
			lineBottomView.layout(0,0,0,0);
			speedStateView.layout(l, bikeStateViewHeight, r, bikeStateViewHeight+speedStateViewHeight);
			batteryStateView.layout(0, 0, 0, 0);
			break;
		case UI_MODEL_BATTERY:
			LogUtils.i(TAG, "battery-model");
			
			break;
			default:
				break;
		}
		
//		mHeadView.layout(l, move-headHegiht, r, move);
//		mContentView.layout(l, move, r, move+b);
//		mTailView.layout(l, b+move, r, b+move+tailHeight);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return isInterceptionEvent;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return isInterceptionEvent;
	}

//	@Override
//	public boolean dispatchTouchEvent(MotionEvent event) {
//		switch(event.getAction()){
//		case MotionEvent.ACTION_DOWN:
//			yDown=event.getY();
//			break;
//		case MotionEvent.ACTION_MOVE:
//			yMove=event.getY();
//			move=(int) (yMove-yDown);
//			if(move>0){//下拉，下拉的时候判断contentView是否是最顶部，只就可以开启下拉刷新
//				isInterceptionEvent=true;
//				requestLayout();
//				isNeed2Back=true;
//			}else if(move<0){//上拉的时候判断contentView是否是最底部，是就可以开启上拉加载 
//				isInterceptionEvent=true;
//				requestLayout();
//				isNeed2Back=true;
//			}
//			break;
//		case MotionEvent.ACTION_UP:
//			yUp=event.getY();
//			releaseLenght=(int) (yUp-yDown);
//			if(isNeed2Back){
//				isInterceptionEvent=false;
//				isNeed2Back=false;
//				back();
//			}
//			break;
//		}
//		
//		
//		return super.dispatchTouchEvent(event);
//	}
//	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			requestLayout();
		}
		
	};
	
	
	private void off(){
				while(move!=0){
					if(move>0){
						move-=3;
						if(move<0){
							move=0;
						}
					}else{
						move+=3;
						if(move>0){
							move=0;
						}
					}
					handler.sendEmptyMessage(0);
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	}

//	private void back(){
//		new Thread(){
//			public void run(){
//				while(move!=0){
//					if(move>0){
//						move-=3;
//						if(move<=freshHeight&&Math.abs(releaseLenght)>freshHeight){//到这里停住刷新完成后，再继续
//							try {
//								Thread.sleep(2000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							off();
//						}else{
//							
//							if(move<0){
//								move=0;
//							}
//						}
//					}else{
//						move+=3;
//						if(move>=-loadMoreHeight&&Math.abs(releaseLenght)>loadMoreHeight){
//							try {
//								Thread.sleep(2000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							off();
//						}
//						if(move>0){
//							move=0;
//						}
//					}
//					handler.sendEmptyMessage(0);
//					try {
//						Thread.sleep(1);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();
//	}
//

}
