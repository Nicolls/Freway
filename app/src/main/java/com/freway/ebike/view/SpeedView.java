/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freway.ebike.view;

import com.freway.ebike.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 自定义一个View继承ImageView
 * 
 * 
 */
public class SpeedView extends android.support.v7.widget.AppCompatImageView {
	// 三个构造器
	/**迈*/
	public static final float MAX_SPEED_MPH=45f;
	/**千米每小时s*/
	public static final float MAX_SPEED_KM_H=72f;

	private float startAngle = 135f;
	private float sweepAngle;
	private float sweepEndAngle = 270f;
	private Bitmap bitmap;
	private Paint pnormal;
	private Paint paintSpeed;
	private Paint paintSpeedPoint;
	private RectF rectf;
	private float bitmapAngle;
	
	public SpeedView(Context context) {
		super(context);
		init();
	}

	public SpeedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SpeedView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		if (bitmap == null) {
			bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.speed_view_cicle_point);
		}
		pnormal = new Paint();
		pnormal.setAntiAlias(true);
		pnormal.setStyle(Paint.Style.STROKE);
		pnormal.setColor(Color.parseColor("#bababa"));
		pnormal.setStrokeWidth(2);

		paintSpeed = new Paint();
		paintSpeed.setAntiAlias(true);
		paintSpeed.setStyle(Paint.Style.STROKE);
		paintSpeed.setColor(Color.RED);
		paintSpeed.setStrokeWidth(4);

		paintSpeedPoint = new Paint(paintSpeed);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		//画表盘
		/*
		canvas.save();
		Paint pCicle=new Paint();
		pCicle.setAntiAlias(true);
		pCicle.setColor(getResources().getColor(R.color.red_deep));
		pCicle.setStrokeWidth(1);
		pCicle.setStyle(Paint.Style.STROKE);
		RectF unitCicleRectf=new RectF(0,getHeight()/2-15,30,getHeight()/2+15);
		canvas.rotate(-45,getWidth()/2,getHeight()/2);
		Paint pLine=new Paint();
		pLine.setAntiAlias(true);
		pLine.setColor(Color.GRAY);
		pLine.setStrokeWidth(1);
		pLine.setStyle(Paint.Style.STROKE);
		for(int i=0;i<10;i++){
			canvas.drawText(i+"", 0, getHeight()/2-15, pLine);
			canvas.drawArc(unitCicleRectf, 0, 360, false, pCicle);
			if(i!=9){
				canvas.save();
//				float cicleAngle = Math.abs((float) (unitCicleRectf.width() / 2 * 360 / (2 * Math.PI * (unitCicleRectf.width() / 2))));
				canvas.rotate(-3,getWidth()/2,getHeight()/2);
				for(int j=0;j<2;j++){
					canvas.rotate(10,getWidth()/2,getHeight()/2);
					canvas.drawLine(0, getHeight()/2-15, 20, getHeight()/2-15, pLine);
				}
				canvas.restore();
			}
			canvas.rotate(30,getWidth()/2,getHeight()/2);
		}
		canvas.restore();
		
		canvas.save();
		canvas.scale(0.8f, 0.8f, getWidth()/2,getHeight()/2);
		*/
		if(bitmap!=null){
			bitmapAngle = Math.abs((float) (bitmap.getWidth() / 2 * 360 / (2 * Math.PI * (getWidth() / 2))));
		}
		if (rectf == null&&bitmap!=null) {
			rectf = new RectF(bitmap.getWidth()/2,bitmap.getHeight()/2,getWidth()-bitmap.getWidth()/2,getHeight()-bitmap.getHeight()/2);
		}
		if(rectf!=null&&pnormal!=null&&paintSpeed!=null&&paintSpeedPoint!=null){
			canvas.drawArc(rectf, startAngle, sweepEndAngle, false, pnormal);
			canvas.drawArc(rectf, startAngle, sweepAngle , false, paintSpeed);
			canvas.save();
			canvas.rotate(sweepAngle-45+bitmapAngle, getWidth() / 2, getHeight() / 2);

			canvas.drawBitmap(bitmap, 0f, getHeight() / 2, paintSpeedPoint);
			canvas.restore();
		}
		canvas.save();
		canvas.restore();
	}
	/**速度改变*/
	public void onValueChange(float speed,float maxSpeed) {
		if (speed > maxSpeed) {
			speed = maxSpeed;
		}
		sweepAngle = 270f / maxSpeed * speed;
		invalidate();
	}

}
