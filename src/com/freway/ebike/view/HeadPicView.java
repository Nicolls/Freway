package com.freway.ebike.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.freway.ebike.utils.EBikeViewUtils;

public class HeadPicView extends ImageView{
	public HeadPicView(Context context) {
		super(context);
		Bitmap bit=((BitmapDrawable)getDrawable()).getBitmap();
		setImageBitmap(bit);
	}

	public HeadPicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Bitmap bit=((BitmapDrawable)getDrawable()).getBitmap();
		setImageBitmap(bit);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(EBikeViewUtils.getRoundBitmap(bm));
	}
	
	
}
