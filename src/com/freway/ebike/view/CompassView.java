package com.freway.ebike.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.freway.ebike.R;

public class CompassView extends ImageView {
	private SensorManager sm = null;
	private Sensor aSensor = null;
	private Sensor mSensor = null;

	float[] accelerometerValues = new float[3];
	float[] magneticFieldValues = new float[3];
	float[] values = new float[3];
	float[] r = new float[9];
	float angle=0f;
	private Drawable compass;// 图片资源
	public CompassView(Context context) {
		super(context);
		init();
	}

	public CompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	final SensorEventListener myListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				accelerometerValues = event.values;
			}
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				magneticFieldValues = event.values;
			}
			// 调用getRotaionMatrix获得变换矩阵R[]
			SensorManager.getRotationMatrix(r, null, accelerometerValues, magneticFieldValues);
			SensorManager.getOrientation(r, values);
			// 经过SensorManager.getOrientation(R, values);得到的values值为弧度
			// 转换为角度
			values[0] = (float) Math.toDegrees(values[0]);
			angle=values[0];
			invalidate();
			
		}
	};

	
	
	private void init() {
		sm = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
		aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sm.registerListener(myListener, aSensor, SensorManager.SENSOR_DELAY_GAME);
		sm.registerListener(myListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	/** 释放罗盘 */
	public void releaseCompass() {
		sm.unregisterListener(myListener);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(compass == null){
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.daymabiao_compass_view);
			compass=new BitmapDrawable(getResources(),bitmap);
			compass.setBounds(0, 0, getWidth(), getHeight());// 图片资源在view的位置，此处相当于充满view
		}
		canvas.save();
		canvas.scale(0.9f, 0.9f,getWidth() / 2, getHeight() / 2);
		canvas.rotate(angle, getWidth() / 2, getHeight() / 2);// 绕图片中心点旋转，
		compass.draw(canvas);// 把旋转后的图片画在view上，即保持旋转后的样子
		canvas.restore();// 保存一下
	}
}
