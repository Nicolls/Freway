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

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

/**
 * 自定义一个View继承TextView
 * 
 * 
 */
public class FlickTextView extends TextView {
	private Animation animation;
	public boolean isAnimating=false;
	public FlickTextView(Context context) {
		super(context);
	}

	public FlickTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlickTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
		
	};
	
	public void showTip(String title) {
		setText(title + "");
		if(getVisibility()!=View.VISIBLE){
			setVisibility(View.VISIBLE);
		}
		if(!isAnimating){
			if (animation == null) {
				animation = new AlphaAnimation(1, 0);
				animation.setDuration(400);
				animation.setRepeatCount(Animation.INFINITE);
				animation.setRepeatMode(Animation.REVERSE);

			}
			clearAnimation();
			startAnimation(animation);
			isAnimating=true;
		}
	}

	public void hideTip() {
		if(getVisibility()==View.VISIBLE){
			setVisibility(View.GONE);
		}
		if(isAnimating){
			clearAnimation();
			isAnimating=false;
		}
	}
}
