package com.freway.ebike.activity;

import android.os.Bundle;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;

public class MainActivity extends BaseActivity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		super.initCommonView();
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		
		
	}

}
