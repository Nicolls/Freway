package com.freway.ebike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.bluetooth.BlueToothConstants;
import com.freway.ebike.bluetooth.BlueToothService;
import com.freway.ebike.bluetooth.BlueToothUtil;
import com.freway.ebike.bluetooth.EBikeTravelData;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.common.EBConstant;
import com.freway.ebike.map.MapUtil;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.utils.AlertUtil;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.TimeUtils;
import com.freway.ebike.view.BatteryView;
import com.freway.ebike.view.ClickImageButton;
import com.freway.ebike.view.ClickImageButton.ClickListener;
import com.freway.ebike.view.FlickTextView;
import com.freway.ebike.view.SpeedView;

public abstract class HomeUiActivity extends BaseActivity implements OnClickListener {

	protected MapUtil mMapUtil;
	protected BlueToothUtil mBlueToothUtil;
	/** 导航条 */
	private View topBarView;
	/** 车况view */
	private View bikeStateView;
	/** 车速view */
	private View speedStateView;
	private View speedStateContent;
	/** 电池view */
	private View batteryStateView;
	/** 骑行状态view */
	private View travelStateView;
	/** 分隔线用于显示电池view,点击第一次显示，点击第二次隐藏 */
	private View lineBikeStateView;
	/** 分隔线用于显示速度view */
	private View lineTravelStateView;
	/** 分隔线用于显示速度view */
	private View lineBatteryStateView;
	private ImageButton mModelBtn;
	private ImageView mProfileView;
	private ImageView mLogo;
	// 车况
	private BatteryView mBikeStateBatteryView;
	private TextView mBikeStateBatteryPercent;
	private TextView mBikeStateBatteryRemaindValue;
	private TextView mBikeStateBatteryRemaindTitle;
	private ImageView mBikeStateGearIcon;
	private TextView mBikeStateGearTitle;
	private TextView mBikeStateGearValue;
	private ImageView mBikeStateLightFront;
	private ImageView mBikeStateLightBack;

	// 骑行状态
	private TextView mTravelStateSpendTime;
	private TextView mTravelStateCalValue;
	private TextView mTravelStateCalUnit;
	private TextView mTravelStateSpeedValue;
	private TextView mTravelStateSpeedUnit;

	private TextView mTravelStateDistanceTitle;
	private TextView mTravelStateDistanceValue;
	private TextView mTravelStateDistanceUnit;

	private TextView mTravelStateAvgSpeedTitle;
	private TextView mTravelStateAvgSpeedValue;
	private TextView mTravelStateAvgSpeedUnit;

	private TextView mTravelStateAslTitle;
	private TextView mTravelStateAslValue;
	private TextView mTravelStateAslUnit;

	private TextView mTravelStateCadenceTitle;
	private TextView mTravelStateCadenceValue;
	private TextView mTravelStateCadenceUnit;
	// 速度
	private View mSpeedStateArrowTopView;
	private ImageView mSpeedStateArrowBottomView;
	private ImageView mSpeedStateCicleHolder;
	private SpeedView mSpeedStateSpeedView;
	private TextView mSpeedStateSpeedText;
	private FlickTextView mSpeedStateTipText;
	private ImageButton mSpeedStateSpeedButton;
	private TextView mSpeedStateCalValue;
	private TextView mSpeedStateCalUnit;
	private TextView mSpeedStateSpendTimeValue;

	private TextView mSpeedStateDistanceTitle;
	private TextView mSpeedStateDistanceValue;
	private TextView mSpeedStateDistanceUnit;

	private TextView mSpeedStateAvgSpeedTitle;
	private TextView mSpeedStateAvgSpeedValue;
	private TextView mSpeedStateAvgSpeedUnit;

	private TextView mSpeedStateAslTitle;
	private TextView mSpeedStateAslValue;
	private TextView mSpeedStateAslUnit;

	private TextView mSpeedStateCadenceTitle;
	private TextView mSpeedStateCadenceValue;
	private TextView mSpeedStateCadenceUnit;

	// 电池
	private BatteryView mBatteryStateBatteryView;
	private TextView mBatteryStateBatteryPercent;
	private TextView mBatteryStateRemaindTip;
	private TextView mBatteryStateRemaindValue;
	private TextView mBatteryStateRemaindUnit;
	private ImageView mBatteryStateLightFront;
	private ImageView mBatteryStateLightBack;
	private TextView mBatteryStateGearText;

	/** 右下角进入个人中心按钮 */
	private ImageView mProfile;

	// 内存中要保存的值
	private int model = EBConstant.MODEL_DAY;// 模式
	private int distanUnit = EBConstant.DISTANCE_UNIT_MPH;

	// UI的样式
	private static final int UI_STYLE_MAP = 0;
	private static final int UI_STYLE_SPEED = 1;
	private static final int UI_STYLE_BATTERY = 2;
	private int uiStyle = UI_STYLE_SPEED;

	//动画
	private AlphaAnimation alphaHideAnim;
	private AlphaAnimation alphaShowAnim;
	private ScaleAnimation scaleHideAnim;
	private ScaleAnimation scaleShowAnim;
	private AnimationSet hideAnimSet;
	private AnimationSet showAnimSet;
	private ScaleAnimation speedScaleAnim;
	private static final int ANIM_DURATION = 300;
	
	//触摸事件 
	private ViewTouch viewTouch;
	private float downY;
	private float upY;
	private float moveDistance;//down-up产生的距离
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initView();
		initAnim();
		initFontStyle();
		intClick();
		initViewTouch();
		changeUIStyle();// 默认UI显示状态
		// initData();//这个已经由onResume来做了
		uiInitCompleted();
	}

	private void initView() {
		// 导航条
		topBarView = findViewById(R.id.home_top_bar);
		bikeStateView = findViewById(R.id.home_bike_state);
		speedStateView = findViewById(R.id.home_speed_view);
		speedStateContent = findViewById(R.id.speed_state_content);
		batteryStateView = findViewById(R.id.home_battery_view);
		travelStateView = findViewById(R.id.home_travel_state);
		lineBikeStateView = findViewById(R.id.home_view_line_bike_state);
		lineTravelStateView = findViewById(R.id.home_view_line_travel_state);
		lineBatteryStateView = findViewById(R.id.home_view_line_battery_state);
		mProfileView = (ImageView) findViewById(R.id.home_ib_profile);
		mModelBtn = (ImageButton) findViewById(R.id.home_top_bar_model);
		mLogo = (ImageView) findViewById(R.id.home_top_bar_logo);
		// 车况
		mBikeStateBatteryView = (BatteryView) findViewById(R.id.bike_state_battery_view);
		mBikeStateBatteryPercent = (TextView) findViewById(R.id.bike_state_battery_value);
		mBikeStateBatteryRemaindValue = (TextView) findViewById(R.id.bike_state_battery_remaind_value);
		mBikeStateBatteryRemaindTitle = (TextView) findViewById(R.id.bike_state_battery_remaind_title);
		mBikeStateGearIcon = (ImageView) findViewById(R.id.bike_state_gear_icon);
		mBikeStateGearTitle = (TextView) findViewById(R.id.bike_state_gear_title);
		mBikeStateGearValue = (TextView) findViewById(R.id.bike_state_gear_value);
		mBikeStateLightFront = (ImageView) findViewById(R.id.bike_state_light_front_icon);
		mBikeStateLightBack = (ImageView) findViewById(R.id.bike_state_light_back_icon);

		// 骑行状态
		mTravelStateSpendTime = (TextView) findViewById(R.id.travel_state_spend_time);
		mTravelStateCalValue = (TextView) findViewById(R.id.travel_state_cal_value);
		mTravelStateCalUnit = (TextView) findViewById(R.id.travel_state_cal_unit);
		mTravelStateSpeedValue = (TextView) findViewById(R.id.travel_state_speed);
		mTravelStateSpeedUnit = (TextView) findViewById(R.id.travel_state_speed_unit);

		mTravelStateDistanceTitle = (TextView) findViewById(R.id.travel_state_distance_title);
		mTravelStateDistanceValue = (TextView) findViewById(R.id.travel_state_distance_value);
		mTravelStateDistanceUnit = (TextView) findViewById(R.id.travel_state_distance_unit);

		mTravelStateAvgSpeedTitle = (TextView) findViewById(R.id.travel_state_avg_title);
		mTravelStateAvgSpeedValue = (TextView) findViewById(R.id.travel_state_avg_value);
		mTravelStateAvgSpeedUnit = (TextView) findViewById(R.id.travel_state_avg_unit);
		mTravelStateAslTitle = (TextView) findViewById(R.id.travel_state_asl_title);
		mTravelStateAslValue = (TextView) findViewById(R.id.travel_state_asl_value);
		mTravelStateAslUnit = (TextView) findViewById(R.id.travel_state_asl_unit);
		mTravelStateCadenceTitle = (TextView) findViewById(R.id.travel_state_cadence_title);
		mTravelStateCadenceValue = (TextView) findViewById(R.id.travel_state_cadence_value);
		mTravelStateCadenceUnit = (TextView) findViewById(R.id.travel_state_cadence_unit);
		// 速度
		mSpeedStateArrowTopView = findViewById(R.id.speed_state_arrow_top);
		mSpeedStateArrowBottomView = (ImageView) findViewById(R.id.speed_state_arrow_bottom);
		mSpeedStateCicleHolder = (ImageView) findViewById(R.id.speed_state_cicle_view);
		mSpeedStateSpeedView = (SpeedView) findViewById(R.id.speed_state_speed_view);
		mSpeedStateSpeedText = (TextView) findViewById(R.id.speed_state_speed_text);
		mSpeedStateTipText = (FlickTextView) findViewById(R.id.speed_state_tip_text);
		mSpeedStateSpeedButton = (ImageButton) findViewById(R.id.speed_state_btn);
		mSpeedStateCalValue = (TextView) findViewById(R.id.speed_state_cal_value);
		mSpeedStateCalUnit = (TextView) findViewById(R.id.speed_state_cal_unit);
		mSpeedStateSpendTimeValue = (TextView) findViewById(R.id.speed_state_spend_time);

		mSpeedStateDistanceTitle = (TextView) findViewById(R.id.speed_state_distance_title);
		mSpeedStateDistanceValue = (TextView) findViewById(R.id.speed_state_distance_value);
		mSpeedStateDistanceUnit = (TextView) findViewById(R.id.speed_state_distance_unit);

		mSpeedStateAvgSpeedTitle = (TextView) findViewById(R.id.speed_state_avg_title);
		mSpeedStateAvgSpeedValue = (TextView) findViewById(R.id.speed_state_avg_value);
		mSpeedStateAvgSpeedUnit = (TextView) findViewById(R.id.speed_state_avg_unit);

		mSpeedStateAslTitle = (TextView) findViewById(R.id.speed_state_asl_title);
		mSpeedStateAslValue = (TextView) findViewById(R.id.speed_state_asl_value);
		mSpeedStateAslUnit = (TextView) findViewById(R.id.speed_state_asl_unit);

		mSpeedStateCadenceTitle = (TextView) findViewById(R.id.speed_state_cadence_title);
		mSpeedStateCadenceValue = (TextView) findViewById(R.id.speed_state_cadence_value);
		mSpeedStateCadenceUnit = (TextView) findViewById(R.id.speed_state_cadence_unit);

		// 电池
		mBatteryStateBatteryView = (BatteryView) findViewById(R.id.battery_state_battery_view);
		mBatteryStateBatteryPercent = (TextView) findViewById(R.id.battery_state_battery_percent);
		mBatteryStateRemaindTip = (TextView) findViewById(R.id.battery_state_remaind_tip);
		mBatteryStateRemaindValue = (TextView) findViewById(R.id.battery_state_remaind_value);
		mBatteryStateRemaindUnit = (TextView) findViewById(R.id.battery_state_remaind_unit);
		mBatteryStateLightFront = (ImageView) findViewById(R.id.battery_state_light_front);
		mBatteryStateLightBack = (ImageView) findViewById(R.id.battery_state_light_back);
		mBatteryStateGearText = (TextView) findViewById(R.id.battery_state_gear);

	}

	/** 设置字体风格 */
	private void initFontStyle() {
		// 车况
		mBikeStateBatteryPercent.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mBikeStateBatteryRemaindValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mBikeStateBatteryRemaindTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_MEDIUM));
		mBikeStateGearTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_MEDIUM));
		mBikeStateGearValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		// 骑行状态
		mTravelStateSpendTime.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mTravelStateCalValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mTravelStateCalUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_MEDIUM));
		mTravelStateSpeedValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mTravelStateSpeedUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));

		mTravelStateDistanceTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mTravelStateDistanceValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mTravelStateDistanceUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_MEDIUM));

		mTravelStateAvgSpeedTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mTravelStateAvgSpeedValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mTravelStateAvgSpeedUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_MEDIUM));

		mTravelStateAslTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mTravelStateAslValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mTravelStateAslUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_MEDIUM));

		mTravelStateCadenceTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mTravelStateCadenceValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mTravelStateCadenceUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_MEDIUM));

		// 速度
		mSpeedStateSpeedText.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mSpeedStateTipText.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mSpeedStateCalValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mSpeedStateCalUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mSpeedStateSpendTimeValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));

		mSpeedStateDistanceTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mSpeedStateDistanceValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mSpeedStateDistanceUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));

		mSpeedStateAvgSpeedTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mSpeedStateAvgSpeedValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mSpeedStateAvgSpeedUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));

		mSpeedStateAslTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mSpeedStateAslValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mSpeedStateAslUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));

		mSpeedStateCadenceTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		mSpeedStateCadenceValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mSpeedStateCadenceUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));

		// 电池
		mBatteryStateBatteryPercent.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mBatteryStateRemaindTip.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mBatteryStateRemaindValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mBatteryStateRemaindUnit.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
		mBatteryStateGearText.setTypeface(FontUtil.get(this, FontUtil.STYLE_DINCOND_BOLD));
	}

	private void intClick() {
		mModelBtn.setOnClickListener(this);
		mProfileView.setOnClickListener(this);
		mBikeStateLightFront.setOnClickListener(this);
		mBikeStateLightBack.setOnClickListener(this);
		mBatteryStateLightFront.setOnClickListener(this);
		mBatteryStateLightBack.setOnClickListener(this);
//
		lineBikeStateView.setOnClickListener(this);
		lineTravelStateView.setOnClickListener(this);
		lineBatteryStateView.setOnClickListener(this);

		mSpeedStateArrowTopView.setOnClickListener(this);
		mSpeedStateArrowBottomView.setOnClickListener(this);
		mSpeedStateSpeedButton.setOnClickListener(this);
		mSpeedStateSpeedText.setOnClickListener(this);
	}

	/** 初始化 */
	private void initData() {
		// 单位
		distanUnit = SPUtils.getUnitOfDistance(this);
		if (distanUnit == EBConstant.DISTANCE_UNIT_MPH) {
			mSpeedStateCicleHolder.setImageResource(R.drawable.speed_state_view_cicle_mph);
		} else {
			mSpeedStateCicleHolder.setImageResource(R.drawable.speed_state_view_cicle_mi);
		}
		// 模式
		model = SPUtils.getUiModel(this);
		if (SPUtils.getUiModel(this) == EBConstant.MODEL_DAY) {
			mModelBtn.setSelected(false);
		} else {
			mModelBtn.setSelected(true);
		}
		modelChange();
		// 骑行状态
		travelStateHandler.sendEmptyMessage(BaseApplication.travelState);
		// 蓝牙状态
		bleStateChange(BlueToothService.ble_state);
		updateUiValue();
	}

	protected abstract void uiInitCompleted();

	/** 改变状态 */
	protected Handler travelStateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int state = msg.what;// 所有的行程操作都必须是链接上BLE了才能有作用，因为没有链接的话，显示一定是灰色的
			if (state == TravelConstant.TRAVEL_STATE_PAUSE||state == TravelConstant.TRAVEL_STATE_FAKE_PAUSE) {// 暂停
				mSpeedStateSpeedButton.setVisibility(View.VISIBLE);
				mSpeedStateSpeedText.setVisibility(View.GONE);
				mSpeedStateSpeedButton.setImageResource(R.drawable.speed_state_view_btn_pause_enable);
				mSpeedStateTipText.showTip(getString(R.string.tip_travel_stop));
			} else if (BlueToothService.ble_state == BlueToothConstants.BLE_STATE_CONNECTED
					&& (state == TravelConstant.TRAVEL_STATE_START || state == TravelConstant.TRAVEL_STATE_RESUME)) {
				mSpeedStateSpeedButton.setImageResource(R.drawable.speed_state_view_btn_start_enable);
				mSpeedStateSpeedButton.setVisibility(View.GONE);
				mSpeedStateSpeedText.setVisibility(View.VISIBLE);
				mSpeedStateTipText.hideTip();
			} else if (BlueToothService.ble_state == BlueToothConstants.BLE_STATE_CONNECTED) {// 无，停止，完成，退出
				mSpeedStateSpeedButton.setVisibility(View.VISIBLE);
				mSpeedStateSpeedText.setVisibility(View.GONE);
				mSpeedStateSpeedButton.setImageResource(R.drawable.speed_state_view_btn_start_enable);
				mSpeedStateTipText.hideTip();
			}

		}

	};

	/** 更新UI的值 */
	public void updateUiValue() {
		// 单位格式化
		float speed = EBikeTravelData.getInstance(this).insSpeed;
		float avgSpeed = EBikeTravelData.getInstance(this).avgSpeed;
		float altitude = (float) EBikeTravelData.getInstance(this).altitude;
		float distance = EBikeTravelData.getInstance(this).distance;
		float calorie = EBikeTravelData.getInstance(this).calorie;
		float cadence = EBikeTravelData.getInstance(this).cadence;
		float remaindTravelCapacity = EBikeTravelData.getInstance(this).remaindTravelCapacity;
		String spendTime = TimeUtils.formatTimeSSToHMS(EBikeTravelData.getInstance(this).spendTime) + "";
		if (distanUnit == EBConstant.DISTANCE_UNIT_MPH) {
			speed = speed / 1.6f;// km/h->mph
			avgSpeed = avgSpeed / 1.6f;// km/h->mph
			altitude = altitude * 0.6f;// km->mi
			distance = distance * 0.6f;// km->mi
			remaindTravelCapacity = remaindTravelCapacity * 0.6f;// km->mi
		}

		// 格式化精度
		speed = CommonUtil.formatFloatAccuracy(speed, 1);
		avgSpeed = CommonUtil.formatFloatAccuracy(avgSpeed, 1);
		altitude = CommonUtil.formatFloatAccuracy(altitude, 1);
		distance = CommonUtil.formatFloatAccuracy(distance, 1);
		cadence = CommonUtil.formatFloatAccuracy(cadence, 0);
		calorie = CommonUtil.formatFloatAccuracy(calorie, 1);
		remaindTravelCapacity = CommonUtil.formatFloatAccuracy(remaindTravelCapacity, 1);

		// 车况
		mBikeStateBatteryView.onValueChange(EBikeTravelData.getInstance(this).batteryResidueCapacity, model,
				EBikeTravelData.getInstance(this).gear, false);
		mBikeStateBatteryPercent.setText(EBikeTravelData.getInstance(this).batteryResidueCapacity + "%");
		if (distanUnit == EBConstant.DISTANCE_UNIT_MPH) {
			mBikeStateBatteryRemaindValue.setText(remaindTravelCapacity + "" + getString(R.string.mi));
		} else {
			mBikeStateBatteryRemaindValue.setText(remaindTravelCapacity + "" + getString(R.string.km));
		}
		mBikeStateGearValue.setText(EBikeTravelData.getInstance(this).gear + "");
		if (EBikeTravelData.getInstance(this).frontLed == EBConstant.ON) {
			mBikeStateLightFront.setSelected(true);
		} else {
			mBikeStateLightFront.setSelected(false);
		}
		if (EBikeTravelData.getInstance(this).backLed == EBConstant.ON) {
			mBikeStateLightBack.setSelected(true);
		} else {
			mBikeStateLightBack.setSelected(false);
		}

		// 骑行状态
		mTravelStateSpendTime.setText(spendTime);
		mTravelStateCalValue.setText(calorie + "");
		mTravelStateSpeedValue.setText(speed + "");

		mTravelStateDistanceValue.setText(distance + "");

		mTravelStateAvgSpeedValue.setText(avgSpeed + "");
		mTravelStateAslValue.setText(altitude + "");
		mTravelStateCadenceValue.setText(cadence + "");
		if (distanUnit == EBConstant.DISTANCE_UNIT_MPH) {
			mSpeedStateSpeedView.onValueChange(speed, SpeedView.MAX_SPEED_MPH);
			mSpeedStateAvgSpeedUnit.setText(getString(R.string.mph));
			mTravelStateAvgSpeedUnit.setText(getString(R.string.mph));
			mTravelStateSpeedUnit.setText(getString(R.string.mph));
			mTravelStateAslUnit.setText(getString(R.string.mi));
			mTravelStateDistanceUnit.setText(getString(R.string.mi));
			mSpeedStateAslUnit.setText(getString(R.string.mi));
			mSpeedStateDistanceUnit.setText(getString(R.string.mi));
		} else {
			mSpeedStateSpeedView.onValueChange(speed, SpeedView.MAX_SPEED_KM_H);
			mSpeedStateAvgSpeedUnit.setText(getString(R.string.km_h));
			mTravelStateAvgSpeedUnit.setText(getString(R.string.km_h));
			mTravelStateSpeedUnit.setText(getString(R.string.km_h));
			mTravelStateAslUnit.setText(getString(R.string.km));
			mTravelStateDistanceUnit.setText(getString(R.string.km));
			mSpeedStateAslUnit.setText(getString(R.string.km));
			mSpeedStateDistanceUnit.setText(getString(R.string.km));
		}

		mSpeedStateSpeedText.setText(speed + "");
		mSpeedStateCalValue.setText(calorie + "");
		mSpeedStateSpendTimeValue.setText(spendTime);

		mSpeedStateDistanceValue.setText(distance + "");

		mSpeedStateAvgSpeedValue.setText(avgSpeed + "");

		mSpeedStateAslValue.setText(altitude + "");

		mSpeedStateCadenceValue.setText(cadence + "");

		// 电池
		mBatteryStateBatteryView.onValueChange(EBikeTravelData.getInstance(this).batteryResidueCapacity, model,
				EBikeTravelData.getInstance(this).gear, true);
		mBatteryStateBatteryPercent.setText(EBikeTravelData.getInstance(this).batteryResidueCapacity + "%");
		mBatteryStateRemaindValue.setText(remaindTravelCapacity + "");
		if (distanUnit == EBConstant.DISTANCE_UNIT_MPH) {
			mBatteryStateRemaindUnit.setText(getString(R.string.mi));
		} else {
			mBatteryStateRemaindUnit.setText(getString(R.string.km));
		}
		if (EBikeTravelData.getInstance(this).frontLed == EBConstant.ON) {
			mBatteryStateLightFront.setSelected(true);
		} else {
			mBatteryStateLightFront.setSelected(false);
		}
		if (EBikeTravelData.getInstance(this).backLed == EBConstant.ON) {
			mBatteryStateLightBack.setSelected(true);
		} else {
			mBatteryStateLightBack.setSelected(false);
		}
		mBatteryStateGearText.setText(getString(R.string.gear) + EBikeTravelData.getInstance(this).gear + "");
		// 判断电池包是否链接异常，正常为1，异常为0
		if (EBikeTravelData.getInstance(this).batConnect == EBConstant.OFF) {// 电池包链接异常
			// ToastUtils.toast(getApplicationContext(),
			// getString(R.string.battery_connect_exception));
			// AlertUtil.getInstance(this).alertConfirm(getString(R.string.battery_connect_exception),
			// getString(R.string.confirm), new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			//
			// AlertUtil.getInstance(HomeUiActivity.this).dismiss();
			// }
			// });
		}
	}

	/** BLE stateChange */
	public void bleStateChange(int state) {
		if (state == BlueToothConstants.BLE_STATE_CONNECTED) {
			// mSpeedStateSpeedButton.setClickable(true);
			travelStateHandler.sendEmptyMessage(BaseApplication.travelState);
		} else {
			LogUtils.i(tag, "ble stateChange==" + state);
			mSpeedStateSpeedButton.setVisibility(View.VISIBLE);
			mSpeedStateSpeedText.setVisibility(View.GONE);
			// mSpeedStateSpeedButton.setClickable(false);
			if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_PAUSE) {
				mSpeedStateSpeedButton.setImageResource(R.drawable.speed_state_view_btn_pause_disable);
			} else {
				mSpeedStateSpeedButton.setImageResource(R.drawable.speed_state_view_btn_start_disable);
			}
			mSpeedStateTipText.showTip(getString(R.string.tip_ebike_disconnect));

		}
	}

	/** 只是用来测试按钮的 */
	/*
	 * private void test(int state) { // 按钮 if (state ==
	 * TravelConstant.TRAVEL_STATE_PAUSE) {// 暂停
	 * mSpeedStateSpeedButton.setVisibility(View.VISIBLE);
	 * mSpeedStateSpeedText.setVisibility(View.INVISIBLE);
	 * mSpeedStateSpeedButton.setImageResource(R.drawable.
	 * speed_state_view_btn_pause); } else if (state ==
	 * TravelConstant.TRAVEL_STATE_START || state ==
	 * TravelConstant.TRAVEL_STATE_RESUME) {
	 * mSpeedStateSpeedButton.setVisibility(View.INVISIBLE);
	 * mSpeedStateSpeedText.setVisibility(View.VISIBLE);
	 * mSpeedStateSpeedButton.setImageResource(R.drawable.
	 * speed_state_view_btn_start); } else {// 无，停止，完成，退出
	 * mSpeedStateSpeedButton.setVisibility(View.VISIBLE);
	 * mSpeedStateSpeedButton.setImageResource(R.drawable.
	 * speed_state_view_btn_start);
	 * mSpeedStateSpeedText.setVisibility(View.INVISIBLE); } }
	 */
	/**保存骑行*/
	private void saveTravel(){
		LogUtils.i(tag, "保存骑行" + BaseApplication.travelState);
			if (EBikeTravelData.getInstance(HomeUiActivity.this).distance < EBikeTravelData.MUST_MIN_TRAVEL) {
				// 数据少，不存储
				AlertUtil.getInstance(HomeUiActivity.this).alertChoice(
						getString(R.string.travel_too_short_not_save), getString(R.string.yes),
						getString(R.string.no), new OnClickListener() {

							@Override
							public void onClick(View v) {
								BaseApplication.travelId = -1;
								AlertUtil.getInstance(HomeUiActivity.this).dismiss();
								BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this,
										TravelConstant.TRAVEL_STATE_STOP);
								if (mMapUtil != null) {
									mMapUtil.clearMap();
								}
								// test(TravelConstant.TRAVEL_STATE_COMPLETED);
							}
						}, new OnClickListener() {

							@Override
							public void onClick(View v) {
								AlertUtil.getInstance(HomeUiActivity.this).dismiss();
							}
						},true);
			} else {
				if (mMapUtil != null) {
					mMapUtil.snapshot(new Handler() {

						@Override
						public void handleMessage(Message msg) {
							super.handleMessage(msg);
							mMapUtil.clearMap();
							LogUtils.i(tag, "收到图片路径：" + msg.obj.toString());
							String photoPath = msg.obj.toString();
							if (!TextUtils.isEmpty(photoPath)) {// 图片与行程关联
								EBikeTravelData.getInstance(HomeUiActivity.this).travelPhoto = photoPath;
							}
							BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this,
									TravelConstant.TRAVEL_STATE_COMPLETED);
						}

					});
				}
			}

	}
	/**弹出行程 选择*/
	private void alertTravelChoice(){
		AlertUtil.getInstance(this).alertThree(getString(R.string.travel_save), getString(R.string.yes), getString(R.string.no), getString(R.string.tip_go_on_the_trial), 
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AlertUtil.getInstance(HomeUiActivity.this).dismiss();
						saveTravel();
					}
				}, 
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AlertUtil.getInstance(HomeUiActivity.this).dismiss();
						// test(TravelConstant.TRAVEL_STATE_RESUME);
						BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_STOP);
					}
				}, 
				new OnClickListener() {
					
					@Override
					public void onClick(View v) {//继续骑行
						AlertUtil.getInstance(HomeUiActivity.this).dismiss();
						// test(TravelConstant.TRAVEL_STATE_RESUME);
						BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_RESUME);
					}
				});
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_top_bar_model:
			onModel(v);
			break;
		case R.id.home_ib_profile:
			onProfile();
			break;
		case R.id.speed_state_btn:
			LogUtils.i(tag, "单击" + BaseApplication.travelState);
			if (BlueToothService.ble_state != BlueToothConstants.BLE_STATE_CONNECTED) {// 未连接
				mBlueToothUtil.bleConnect(getString(R.string.ble_not_connect), getString(R.string.yes),
						getString(R.string.no));
				return;
			}
			if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_PAUSE) {
				alertTravelChoice();
			}else if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_FAKE_PAUSE) {//如果是伪暂停要先把它设置为暂停
				BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_PAUSE);
				alertTravelChoice();
			} else if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_START
					|| BaseApplication.travelState == TravelConstant.TRAVEL_STATE_RESUME) {
				BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_PAUSE);
//				// test(TravelConstant.TRAVEL_STATE_PAUSE);
//				if (EBikeTravelData.getInstance(this).zeroSpeedCount > EBikeTravelData.MAX_LIMIT_ZERO_SPEED) {// 超过数值//说明界面已经是暂停的了
//					alertTravelChoice();
//				}
				
			} else {
				// test(TravelConstant.TRAVEL_STATE_START);
				BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_START);
			}
			break;
		case R.id.speed_state_speed_text:// 速度状态下速度值显示text
			// ToastUtils.toast(this, "speed text click");
			BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_PAUSE);
			// test(TravelConstant.TRAVEL_STATE_PAUSE);
			break;
		case R.id.bike_state_light_front_icon:

			break;
		case R.id.bike_state_light_back_icon:

			break;
		case R.id.battery_state_light_front:

			break;
		case R.id.battery_state_light_back:

			break;
		case R.id.home_view_line_bike_state:
			uiStyle = UI_STYLE_BATTERY;
			changeUIStyle();
			break;
		case R.id.home_view_line_battery_state:
			uiStyle = UI_STYLE_MAP;
			changeUIStyle();
			break;
		case R.id.home_view_line_travel_state:
			uiStyle = UI_STYLE_SPEED;
			changeUIStyle();
			break;
		case R.id.speed_state_arrow_top:
			uiStyle = UI_STYLE_BATTERY;
			changeUIStyle();
			break;
		case R.id.speed_state_arrow_bottom:
			uiStyle = UI_STYLE_MAP;
			changeUIStyle();
			break;
		default:
			break;
		}
	}

	
	/**初始化动画*/
	private void initAnim() {
		if(speedScaleAnim==null){
			speedScaleAnim = new ScaleAnimation(1, 1, 0, 1, 0.5f, 0.5f);
			speedScaleAnim.setDuration(ANIM_DURATION);
			speedScaleAnim.setFillAfter(true);
		}
		if (alphaHideAnim == null) {
			alphaHideAnim = new AlphaAnimation(1, 0);
			alphaHideAnim.setDuration(ANIM_DURATION);
			alphaHideAnim.setFillAfter(true);
		}
		if (alphaShowAnim == null) {
			alphaShowAnim = new AlphaAnimation(0, 1);
			alphaShowAnim.setDuration(ANIM_DURATION);
			alphaShowAnim.setFillAfter(true);
		}
		if (scaleHideAnim == null) {
			scaleHideAnim = new ScaleAnimation(1, 1, 1, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//			scaleHideAnim = new ScaleAnimation(1, 0, 1, 0, 0.5f, 0.5f);
			scaleHideAnim.setDuration(ANIM_DURATION);
			scaleHideAnim.setFillAfter(true);
		}
		if (scaleShowAnim == null) {
			scaleShowAnim = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//			scaleShowAnim = new ScaleAnimation(0, 1, 0, 1, 0.5f, 0.5f);
			scaleShowAnim.setDuration(ANIM_DURATION);
			scaleShowAnim.setFillAfter(true);
		}
		if (hideAnimSet == null) {
			hideAnimSet = new AnimationSet(true);
			hideAnimSet.setDuration(ANIM_DURATION);
			hideAnimSet.setFillAfter(true);
			hideAnimSet.addAnimation(alphaHideAnim);
			hideAnimSet.addAnimation(scaleHideAnim);
			hideAnimSet.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					bikeStateView.clearAnimation();
					travelStateView.clearAnimation();
					speedStateView.clearAnimation();
					batteryStateView.clearAnimation();
				}
			});
		}
		if (showAnimSet == null) {
			showAnimSet = new AnimationSet(true);
			showAnimSet.setDuration(ANIM_DURATION);
			showAnimSet.setFillAfter(true);
			showAnimSet.addAnimation(alphaShowAnim);
			showAnimSet.addAnimation(scaleShowAnim);
			showAnimSet.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					bikeStateView.clearAnimation();
					travelStateView.clearAnimation();
					speedStateView.clearAnimation();
					batteryStateView.clearAnimation();
				}
			});
		}
	}
	/**根据点击改变UI的显示样式*/
	private void changeUIStyle() {
		int bikeStateVisble = bikeStateView.getVisibility();
		switch (uiStyle) {
		case UI_STYLE_MAP:
			bikeStateView.setVisibility(View.VISIBLE);
			travelStateView.setVisibility(View.VISIBLE);
			speedStateView.setVisibility(View.GONE);
			batteryStateView.setVisibility(View.GONE);
			lineBikeStateView.setVisibility(View.VISIBLE);
			// lineTravelStateView.setVisibility(View.VISIBLE);

			// 动画
			if (bikeStateVisble == View.VISIBLE) {// 说明一定是从speed->map
				speedStateView.startAnimation(hideAnimSet);
				travelStateView.startAnimation(showAnimSet);
			} else {// battery->map
				bikeStateView.startAnimation(showAnimSet);
				batteryStateView.startAnimation(hideAnimSet);
			}
			break;
		case UI_STYLE_SPEED:
			bikeStateView.setVisibility(View.VISIBLE);
			travelStateView.setVisibility(View.GONE);
			speedStateView.setVisibility(View.VISIBLE);
			batteryStateView.setVisibility(View.GONE);
			lineBikeStateView.setVisibility(View.GONE);
			// lineTravelStateView.setVisibility(View.GONE);
			// 动画
			if (bikeStateVisble == View.VISIBLE) {// 说明一定是从map->speed
				speedStateView.startAnimation(speedScaleAnim);
				travelStateView.startAnimation(hideAnimSet);
			} else {// battery->speed
				speedStateView.startAnimation(showAnimSet);
				batteryStateView.startAnimation(hideAnimSet);
			}
			break;
		case UI_STYLE_BATTERY:
			bikeStateView.setVisibility(View.GONE);
			travelStateView.setVisibility(View.VISIBLE);
			speedStateView.setVisibility(View.GONE);
			batteryStateView.setVisibility(View.VISIBLE);
			lineBikeStateView.setVisibility(View.VISIBLE);
			// lineTravelStateView.setVisibility(View.VISIBLE);
			// 动画
			int speedStateVisble=speedStateView.getVisibility();
			if (speedStateVisble == View.VISIBLE) {// 说明一定是从speed->battery
				speedStateView.startAnimation(hideAnimSet);
				batteryStateView.startAnimation(showAnimSet);
			} else {// battery->speed
				batteryStateView.startAnimation(showAnimSet);
				bikeStateView.startAnimation(hideAnimSet);
			}
			break;
		default:
			break;
		}
	}

	public void onModel(View view) {
		view.setSelected(!view.isSelected());
		if (SPUtils.getUiModel(this) == EBConstant.MODEL_DAY) {// 由day到night
			SPUtils.setUiModel(this, EBConstant.MODEL_NIGHT);
		} else {
			SPUtils.setUiModel(this, EBConstant.MODEL_DAY);
		}
		model = SPUtils.getUiModel(this);
		modelChange();
	}

	private void modelChange() {
		mBatteryStateBatteryView.onValueChange(EBikeTravelData.getInstance(this).batteryResidueCapacity,
				SPUtils.getUiModel(this), EBikeTravelData.getInstance(this).gear, true);
		if (SPUtils.getUiModel(this) == EBConstant.MODEL_NIGHT) {// 由day到night
			// 电池
			mLogo.setImageResource(R.drawable.home_freway_logo_night);
			lineBikeStateView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			lineTravelStateView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			topBarView.setBackgroundColor(getResources().getColor(R.color.model_night_bar_background));
			bikeStateView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			speedStateContent.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			batteryStateView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			travelStateView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			mSpeedStateArrowTopView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			mSpeedStateArrowBottomView.setBackgroundResource(R.drawable.speed_state_view_arrow_bottom_night);
			mBatteryStateBatteryPercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.battery_pic_night, 0, 0, 0);
			// text
			mTravelStateSpendTime.setTextColor(getResources().getColor(R.color.white));
			mTravelStateCalValue.setTextColor(getResources().getColor(R.color.white));
			mTravelStateSpeedValue.setTextColor(getResources().getColor(R.color.white));
			mTravelStateDistanceValue.setTextColor(getResources().getColor(R.color.white));
			mTravelStateAvgSpeedValue.setTextColor(getResources().getColor(R.color.white));
			mTravelStateAslValue.setTextColor(getResources().getColor(R.color.white));
			mTravelStateCadenceValue.setTextColor(getResources().getColor(R.color.white));
			mSpeedStateSpeedText.setTextColor(getResources().getColor(R.color.white));
			mSpeedStateCalValue.setTextColor(getResources().getColor(R.color.white));
			mSpeedStateSpendTimeValue.setTextColor(getResources().getColor(R.color.white));
			mSpeedStateDistanceValue.setTextColor(getResources().getColor(R.color.white));
			mSpeedStateAvgSpeedValue.setTextColor(getResources().getColor(R.color.white));
			mSpeedStateAslValue.setTextColor(getResources().getColor(R.color.white));
			mSpeedStateCadenceValue.setTextColor(getResources().getColor(R.color.white));
			mBatteryStateBatteryPercent.setTextColor(getResources().getColor(R.color.text_gray));
			mBatteryStateRemaindValue.setTextColor(getResources().getColor(R.color.white));
			mBatteryStateGearText.setTextColor(getResources().getColor(R.color.white));
		} else {// 由night到day
			mLogo.setImageResource(R.drawable.home_freway_logo_day);
			lineBikeStateView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			lineTravelStateView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			topBarView.setBackgroundColor(getResources().getColor(R.color.model_day_bar_background));
			bikeStateView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			speedStateContent.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			batteryStateView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			travelStateView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			mSpeedStateArrowTopView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			mSpeedStateArrowBottomView.setBackgroundResource(R.drawable.speed_state_view_arrow_bottom_day);
			mBatteryStateBatteryPercent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.battery_pic_day, 0, 0, 0);

			// text
			mTravelStateSpendTime.setTextColor(getResources().getColor(R.color.black));
			mTravelStateCalValue.setTextColor(getResources().getColor(R.color.black));
			mTravelStateSpeedValue.setTextColor(getResources().getColor(R.color.black));
			mTravelStateDistanceValue.setTextColor(getResources().getColor(R.color.black));
			mTravelStateAvgSpeedValue.setTextColor(getResources().getColor(R.color.black));
			mTravelStateAslValue.setTextColor(getResources().getColor(R.color.black));
			mTravelStateCadenceValue.setTextColor(getResources().getColor(R.color.black));
			mSpeedStateSpeedText.setTextColor(getResources().getColor(R.color.black));
			mSpeedStateCalValue.setTextColor(getResources().getColor(R.color.black));
			mSpeedStateSpendTimeValue.setTextColor(getResources().getColor(R.color.black));
			mSpeedStateDistanceValue.setTextColor(getResources().getColor(R.color.black));
			mSpeedStateAvgSpeedValue.setTextColor(getResources().getColor(R.color.black));
			mSpeedStateAslValue.setTextColor(getResources().getColor(R.color.black));
			mSpeedStateCadenceValue.setTextColor(getResources().getColor(R.color.black));
			mBatteryStateRemaindValue.setTextColor(getResources().getColor(R.color.black));
			mBatteryStateGearText.setTextColor(getResources().getColor(R.color.black));
			mBatteryStateBatteryPercent.setTextColor(getResources().getColor(R.color.white));
		}
	}

	public void onProfile() {
		Intent intent = new Intent(this, ProfileActivity.class);
		startActivity(intent);
	}

	@Override
	public void dateUpdate(int id, Object obj) {

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initData();
	}
	/**初始化触摸事件*/
	private void initViewTouch() {
		viewTouch = new ViewTouch();
		/** 车况view */
		bikeStateView.setOnTouchListener(viewTouch);
//		/** 车速view */
//		speedStateView.setOnTouchListener(viewTouch);
		/** 电池view */
		batteryStateView.setOnTouchListener(viewTouch);
		/** 骑行状态view */
		travelStateView.setOnTouchListener(viewTouch);
		/** 分隔线用于显示电池view,点击第一次显示，点击第二次隐藏 */
		lineBikeStateView.setOnTouchListener(viewTouch);
		/** 分隔线用于显示速度view */
		lineTravelStateView.setOnTouchListener(viewTouch);
		/** 分隔线用于显示速度view */
		lineBatteryStateView.setOnTouchListener(viewTouch);
		// 速度view上边的箭头按钮
		mSpeedStateArrowTopView.setOnTouchListener(viewTouch);
		// 速度view下边的箭头按钮
		mSpeedStateArrowBottomView.setOnTouchListener(viewTouch);
	}

	// 触摸
	private class ViewTouch implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			LogUtils.i(tag, "view" + v.getId() + "--onTouchEvent");
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downY = event.getY();
//				LogUtils.i(tag, "downY=" + downY);
				break;
			case MotionEvent.ACTION_UP:
//				LogUtils.i(tag, "upY=" + upY);
				upY = event.getY();
				moveDistance = upY - downY;
				if (Math.abs(moveDistance) > ANIM_SPACING) {
					handleTouch(v.getId(),moveDistance );
				}else{
					onClick(v);
				}
				break;
			default:
				break;
			}
			return true;
		}
	}

	private static final int ANIM_SPACING = 100;

	private void handleTouch(int id, float distance) {
		switch (id) {
		case R.id.home_bike_state:
				if (distance > 0) {// 向下滑
					uiStyle = UI_STYLE_BATTERY;
					changeUIStyle();
				}
			break;
		case R.id.home_battery_view:
				if (distance < 0) {// 向上滑
					uiStyle = UI_STYLE_MAP;
					changeUIStyle();
				}
			break;
		case R.id.home_travel_state:
				if (distance > 0) {// 向下滑
					uiStyle = UI_STYLE_SPEED;
					changeUIStyle();
				}
			break;
		case R.id.speed_state_arrow_top:
				if (distance > 0) {// 向下滑
					uiStyle = UI_STYLE_BATTERY;
					changeUIStyle();
				}
			break;
		case R.id.speed_state_arrow_bottom:
				if (distance < 0) {// 向上滑
					uiStyle = UI_STYLE_MAP;
					changeUIStyle();
				}
			break;
		default:
			break;

		}
	}
}
