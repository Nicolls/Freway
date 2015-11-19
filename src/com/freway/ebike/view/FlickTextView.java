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
import android.widget.TextView;

/**
 * 自定义一个View继承TextView
 * 
 * 
 */
public class FlickTextView extends TextView {
	private static final int FLICK_TIME=600;//闪烁文字的间隔时间毫秒
	public FlickTextView(Context context) {
		super(context);
	}

	public FlickTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlickTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void showTip(String title){
		setVisibility(View.VISIBLE);
		setText(title+"");
		handler.sendEmptyMessage(1);
	}
	public void hideTip(){
		setVisibility(View.GONE);
		handler.sendEmptyMessage(-1);
		handler.sendEmptyMessage(-1);
	}
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==-1){//hide
				handler.removeMessages(0);
				handler.removeMessages(1);
			}
			else if(msg.what==1){//visble
				setVisibility(View.VISIBLE);
				sendEmptyMessageDelayed(0, FLICK_TIME);
			}else{//invisible
				setVisibility(View.INVISIBLE);
				sendEmptyMessageDelayed(1, FLICK_TIME);
			}
		}
		
	};
}
