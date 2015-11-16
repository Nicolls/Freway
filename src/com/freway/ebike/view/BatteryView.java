package com.freway.ebike.view;

import com.freway.ebike.R;
import com.freway.ebike.common.EBConstant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class BatteryView extends ImageView {

	private float startAngle = 90;
	private float sweepAngle = 90;
	private Paint percentCiclePaint;
	private Bitmap cicleBitmapHolder;
	private Drawable viewDrawable;
	private RectF cicleRectF;

	public BatteryView(Context context) {
		super(context);
	}

	public BatteryView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void init(Canvas canvas) {
		if(cicleBitmapHolder==null){
			cicleBitmapHolder = BitmapFactory.decodeResource(getResources(), R.drawable.battery_state_percent_bg);
			percentCiclePaint = new Paint();
			percentCiclePaint.setAntiAlias(true);
			percentCiclePaint.setColor(Color.parseColor("#da4129"));
//			percentCiclePaint.setColor(getResources().getColor(R.color.red_shallow));
			if(cicleBitmapHolder!=null){
				cicleRectF = new RectF(-cicleBitmapHolder.getWidth() / 2, -cicleBitmapHolder.getHeight() / 2,
						cicleBitmapHolder.getWidth() / 2, cicleBitmapHolder.getHeight() / 2);
			}
		}
		if(viewDrawable==null){
			viewDrawable=getDrawable();
			viewDrawable.setBounds(0, 0, getWidth(), getHeight());
		}
		viewDrawable.draw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		init(canvas);
		canvas.save();
		//test
//		float width = getHeight() / 2 - 80;
//		cicleRectF = new RectF(-width, -width, width, width);
//		percentCiclePaint = new Paint();
//		percentCiclePaint.setAntiAlias(true);
//		percentCiclePaint.setColor(Color.parseColor("#da4129"));
		canvas.translate(getWidth() / 2, getHeight() / 2);
		if(cicleRectF!=null&&percentCiclePaint!=null){
			canvas.drawArc(cicleRectF, startAngle, sweepAngle, false, percentCiclePaint);
		}
		canvas.restore();
	}

	public void onValueChange(int percent, int model, int gear) {
		Log.i("hudu", "p=" + percent);
		switch (gear) {
		case EBConstant.GEAR0:
			if (model == EBConstant.MODEL_NIGHT) {
				setImageResource(R.drawable.nightmabiao_battery_gear0);
			} else {
				setImageResource(R.drawable.daymabiao_battery_gear0);
			}
			break;
		case EBConstant.GEAR1:
			if (model == EBConstant.MODEL_NIGHT) {
				setImageResource(R.drawable.nightmabiao_battery_gear1);
			} else {
				setImageResource(R.drawable.daymabiao_battery_gear1);
			}
			break;
		case EBConstant.GEAR2:
			if (model == EBConstant.MODEL_NIGHT) {
				setImageResource(R.drawable.nightmabiao_battery_gear2);
			} else {
				setImageResource(R.drawable.daymabiao_battery_gear2);
			}
			break;
		case EBConstant.GEAR3:
			if (model == EBConstant.MODEL_NIGHT) {
				setImageResource(R.drawable.nightmabiao_battery_gear3);
			} else {
				setImageResource(R.drawable.daymabiao_battery_gear3);
			}
			break;
			default:
				break;
		}
		viewDrawable=getDrawable();
		viewDrawable.setBounds(0, 0, getWidth(), getHeight());
		if (percent > 50) {
			startAngle = 360f - ((90f / 50) * (percent - 50));
			sweepAngle = 180f + 2 * ((90f / 50) * (percent - 50));
		} else {
			startAngle = 90f - ((90f / 50) * percent);
			sweepAngle = 2 * ((90f / 50) * percent);
		}
		invalidate();
	}
}
