package com.freway.ebike.activity;

import com.freway.ebike.R;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.view.HeadPicView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity implements OnClickListener {

	private ImageView iconButton;
	private ImageView leftButton;
	private TextView rightButton;
	private TextView titleTv;
	private TextView nameTitle;
	private TextView nameValue;
	private TextView emailTitle;
	private TextView emailValue;
	private TextView genderTitle;
	private ImageView genderView;
	private TextView ageTitle;
	private TextView ageValue;
	private TextView heightTitle;
	private TextView heightValue;
	private TextView weightTitle;
	private TextView weightValue;
	private HeadPicView mHeadView;
	private TextView myEbike;
	private TextView snValue;
	private TextView languageTitle;
	private TextView languageValue;
	private TextView unitDistanceTitle;
	private TextView unitDistanceValue;
	private TextView aboutTitle;
	private TextView aoubtValue;
	private TextView exitTitle;

	private View snLL;
	private View languageLL;
	private View unitDistanceLL;
	private View aboutLL;
	private View exitLL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initView();
		initFontStyle();
		intClick();
		initData();
	}

	private void initView() {
		iconButton=(ImageView) findViewById(R.id.top_bar_right_icon);
		leftButton = (ImageView) findViewById(R.id.top_bar_left);
		rightButton = (TextView) findViewById(R.id.top_bar_right);
		titleTv = (TextView) findViewById(R.id.top_bar_title);
		nameTitle = (TextView) findViewById(R.id.setting_name_title);
		nameValue = (TextView) findViewById(R.id.setting_name_value);
		emailTitle = (TextView) findViewById(R.id.setting_email_title);
		emailValue = (TextView) findViewById(R.id.setting_email_value);
		genderTitle = (TextView) findViewById(R.id.setting_gender_title);
		genderView = (ImageView) findViewById(R.id.setting_gender_view);
		ageTitle = (TextView) findViewById(R.id.setting_age_title);
		ageValue = (TextView) findViewById(R.id.setting_age_value);
		heightTitle = (TextView) findViewById(R.id.setting_height_title);
		heightValue = (TextView) findViewById(R.id.setting_height_value);
		weightTitle = (TextView) findViewById(R.id.setting_weight_title);
		weightValue = (TextView) findViewById(R.id.setting_weight_value);
		mHeadView = (HeadPicView) findViewById(R.id.profile_head_view);
		myEbike = (TextView) findViewById(R.id.setting_my_ebike);
		snValue = (TextView) findViewById(R.id.setting_sn);
		languageTitle = (TextView) findViewById(R.id.setting_language_title);
		languageValue = (TextView) findViewById(R.id.setting_language_value);
		unitDistanceTitle = (TextView) findViewById(R.id.setting_unit_distance_title);
		unitDistanceValue = (TextView) findViewById(R.id.setting_unit_distance_value);
		aboutTitle = (TextView) findViewById(R.id.setting_about_title);
		aoubtValue = (TextView) findViewById(R.id.setting_about_value);
		exitTitle = (TextView) findViewById(R.id.setting_tv_exit);

		snLL = findViewById(R.id.setting_ll_sn);
		languageLL = findViewById(R.id.setting_ll_language);
		unitDistanceLL = findViewById(R.id.setting_ll_unit_distance);
		aboutLL = findViewById(R.id.setting_ll_about);
		exitLL = findViewById(R.id.setting_ll_exit);
	}

	/** 设置字体风格 */
	private void initFontStyle() {
		rightButton.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		titleTv.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		nameTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		nameValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		emailTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		emailValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		genderTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		ageTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		ageValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		heightTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		heightValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		weightTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		weightValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		myEbike.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		snValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		languageTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		languageValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		unitDistanceTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		unitDistanceValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		aboutTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		aoubtValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		exitTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
	}

	private void intClick() {
		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		genderView.setOnClickListener(this);
		mHeadView.setOnClickListener(this);
		snLL.setOnClickListener(this);
		languageLL.setOnClickListener(this);
		unitDistanceLL.setOnClickListener(this);
		aboutLL.setOnClickListener(this);
		exitLL.setOnClickListener(this);
	}

	private void initData() {
		iconButton.setVisibility(View.GONE);
		titleTv.setText(getString(R.string.settings)+"");
		rightButton.setText(getString(R.string.edit)+"");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_bar_left:
			finish();
			break;
		case R.id.top_bar_right:

			break;
		case R.id.setting_gender_view:

			break;
		case R.id.profile_head_view:

			break;
		case R.id.setting_ll_sn:

			break;
		case R.id.setting_ll_language:

			break;
		case R.id.setting_ll_unit_distance:

			break;
		case R.id.setting_ll_about:

			break;
		case R.id.setting_ll_exit:

			break;
		default:
			break;
		}
	}

}
