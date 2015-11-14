package com.freway.ebike.view;

import com.freway.ebike.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class BatteryView extends ImageView {
	public static final int MODEL_DAY = 0;
	public static final int MODEL_NIGHT = 1;

	public static final int GEAR0 = 0;
	public static final int GEAR1 = 1;
	public static final int GEAR2 = 2;
	public static final int GEAR3 = 3;
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
			percentCiclePaint.setColor(getResources().getColor(R.color.red_shallow));
			cicleRectF = new RectF(-cicleBitmapHolder.getWidth() / 2, -cicleBitmapHolder.getHeight() / 2,
					cicleBitmapHolder.getWidth() / 2, cicleBitmapHolder.getHeight() / 2);
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
		canvas.drawArc(cicleRectF, startAngle, sweepAngle, false, percentCiclePaint);
		canvas.restore();
	}

	public void onValueChange(float percent, int model, int gear) {
		Log.i("hudu", "p=" + percent);
		switch (gear) {
		case GEAR0:
			if (model == MODEL_NIGHT) {
				setImageResource(R.drawable.nightmabiao_battery_gear0);
			} else {
				setImageResource(R.drawable.daymabiao_battery_gear0);
			}
			break;
		case GEAR1:
			if (model == MODEL_NIGHT) {
				setImageResource(R.drawable.nightmabiao_battery_gear1);
			} else {
				setImageResource(R.drawable.daymabiao_battery_gear1);
			}
			break;
		case GEAR2:
			if (model == MODEL_NIGHT) {
				setImageResource(R.drawable.nightmabiao_battery_gear2);
			} else {
				setImageResource(R.drawable.daymabiao_battery_gear2);
			}
			break;
		case GEAR3:
			if (model == MODEL_NIGHT) {
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
