package com.freway.ebike.activity;

import com.freway.ebike.R;
import com.freway.ebike.utils.FontUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements OnClickListener {

	private ImageView iconButton;
	private ImageView leftButton;
	private TextView rightButton;
	private TextView titleTv;
	private ImageView genderImage;
	private TextView nameTv;
	private TextView mileageValueTv;
	private TextView mileageTitleTv;
	private TextView timeValueTv;
	private TextView timeTitleTv;
	private View recordsLL;
	private View gradesLL;
	private View newsLL;
	private View tutorialLL;
	private TextView recordsTv;
	private TextView gradesTv;
	private TextView newsTv;
	private TextView tutorialTv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		initView();
		initFontStyle();
		intClick();
		initData();
	}
	
	
	private void initView(){
		iconButton=(ImageView) findViewById(R.id.top_bar_right_icon);
		leftButton=(ImageView) findViewById(R.id.top_bar_left);
		rightButton=(TextView) findViewById(R.id.top_bar_right);
		titleTv=(TextView) findViewById(R.id.top_bar_title);
		genderImage=(ImageView) findViewById(R.id.profile_gender_pic);
		nameTv=(TextView) findViewById(R.id.profile_name);
		mileageValueTv=(TextView) findViewById(R.id.profile_total_mileage_value);
		mileageTitleTv=(TextView) findViewById(R.id.profile_total_mileage_title);
		timeValueTv=(TextView) findViewById(R.id.profile_total_time_value);
		timeTitleTv=(TextView) findViewById(R.id.profile_total_time_title);
		recordsLL=findViewById(R.id.profile_btn_records);
		gradesLL=findViewById(R.id.profile_btn_grades);
		newsLL=findViewById(R.id.profile_btn_news);
		tutorialLL=findViewById(R.id.profile_btn_tutorial);
		recordsTv=(TextView) findViewById(R.id.profile_tv_records);
		gradesTv=(TextView) findViewById(R.id.profile_tv_grades);
		newsTv=(TextView) findViewById(R.id.profile_tv_news);
		tutorialTv=(TextView) findViewById(R.id.profile_tv_tutorial);
	}
	
	/** 设置字体风格 */
	private void initFontStyle() {
		rightButton.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		titleTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		nameTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mileageValueTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mileageTitleTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		timeValueTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		timeTitleTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		recordsTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		gradesTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		newsTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		tutorialTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
	}

	private void intClick() {
		iconButton.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		recordsLL.setOnClickListener(this);
		gradesLL.setOnClickListener(this);
		newsLL.setOnClickListener(this);
		tutorialLL.setOnClickListener(this);
	}

	private void initData() {
		titleTv.setText(getString(R.string.profile));
		rightButton.setText("");
		rightButton.setVisibility(View.GONE);
		iconButton.setImageResource(R.drawable.icon_settings);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_bar_right_icon:
			onSetting();
			break;
		case R.id.top_bar_left:
			finish();
			break;
		case R.id.profile_btn_records:
			onRecord();
			break;
		case R.id.profile_btn_grades:
			onGrade();
			break;
		case R.id.profile_btn_news:
			onNews();
			break;
		case R.id.profile_btn_tutorial:
			onTutorial();
			break;
		default:
			break;
		}
	}

	public void onSetting() {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}

	public void onRecord() {
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra("url", "http://www.ifreway.com/app/index.php/history/");
		startActivity(intent);
	}

	public void onGrade() {
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra("url", "http://www.ifreway.com/app/index.php/grades/");
		startActivity(intent);
	}

	public void onNews() {
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra("url", "http://www.ifreway.com/app/index.php/news/");
		startActivity(intent);
	}

	public void onTutorial() {
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra("url", "http://www.ifreway.com/app/index.php/tutorial");
		startActivity(intent);
	}

}