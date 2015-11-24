package com.freway.ebike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;

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
import com.freway.ebike.utils.ToastUtils;
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
	private View lineTopView;
	/** 分隔线用于显示速度view */
	private View lineBottomView;
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
	private ImageView mSpeedStateArrowTopView;
	private ImageView mSpeedStateArrowBottomView;
	private ImageView mSpeedStateCicleHolder;
	private SpeedView mSpeedStateSpeedView;
	private TextView mSpeedStateSpeedText;
	private FlickTextView mSpeedStateTipText;
	private ClickImageButton mSpeedStateSpeedButton;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initView();
		initFontStyle();
		intClick();
		defaultUIState();
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
		lineTopView = findViewById(R.id.home_view_line_top);
		lineBottomView = findViewById(R.id.home_view_line_bottom);
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
		mSpeedStateArrowTopView = (ImageView) findViewById(R.id.speed_state_arrow_top);
		mSpeedStateArrowBottomView = (ImageView) findViewById(R.id.speed_state_arrow_bottom);
		mSpeedStateCicleHolder = (ImageView) findViewById(R.id.speed_state_cicle_view);
		mSpeedStateSpeedView = (SpeedView) findViewById(R.id.speed_state_speed_view);
		mSpeedStateSpeedText = (TextView) findViewById(R.id.speed_state_speed_text);
		mSpeedStateTipText = (FlickTextView) findViewById(R.id.speed_state_tip_text);
		mSpeedStateSpeedButton = (ClickImageButton) findViewById(R.id.speed_state_btn);
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

		lineTopView.setOnClickListener(this);
		lineBottomView.setOnClickListener(this);

		mSpeedStateArrowTopView.setOnClickListener(this);
		mSpeedStateArrowBottomView.setOnClickListener(this);

		mSpeedStateSpeedButton.setOnClick(new ClickListener() {

			@Override
			public void onDoubleClick() {// 双击
				LogUtils.i(tag, "双击" + BaseApplication.travelState);
				if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_PAUSE) {
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
								if(mMapUtil!=null){
									mMapUtil.clearMap();
								}
								// test(TravelConstant.TRAVEL_STATE_COMPLETED);
							}
						}, new OnClickListener() {

							@Override
							public void onClick(View v) {
								AlertUtil.getInstance(HomeUiActivity.this).dismiss();
							}
						});
					} else {
						AlertUtil.getInstance(HomeUiActivity.this).alertChoice(getString(R.string.travel_save),
								getString(R.string.yes), getString(R.string.no), new OnClickListener() {

							@Override
							public void onClick(View v) {
								AlertUtil.getInstance(HomeUiActivity.this).dismiss();
								if(mMapUtil!=null){
									mMapUtil.snapshot(new Handler(){

										@Override
										public void handleMessage(Message msg) {
											super.handleMessage(msg);
											mMapUtil.clearMap();
											LogUtils.i(tag, "收到图片路径："+msg.obj.toString());
											BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this,
													TravelConstant.TRAVEL_STATE_COMPLETED);
										}
										
									});
								}
								// test(TravelConstant.TRAVEL_STATE_COMPLETED);
							}
						}, new OnClickListener() {

							@Override
							public void onClick(View v) {
								AlertUtil.getInstance(HomeUiActivity.this).dismiss();
								BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this,
										TravelConstant.TRAVEL_STATE_STOP);// 停止
								if(mMapUtil!=null){
									mMapUtil.clearMap();
								}
								// test(TravelConstant.TRAVEL_STATE_COMPLETED);
							}
						});
					}
				}

			}

			@Override
			public void onClick() {// 单击
				LogUtils.i(tag, "单击" + BaseApplication.travelState);
				if (BlueToothService.ble_state != BlueToothConstants.BLE_STATE_CONNECTED) {// 未连接
					mBlueToothUtil.bleConnect(getString(R.string.ble_not_connect), getString(R.string.yes),
							getString(R.string.no));
					return;
				}
				if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_PAUSE) {
					// test(TravelConstant.TRAVEL_STATE_RESUME);
					BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_RESUME);
				} else if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_START
						|| BaseApplication.travelState == TravelConstant.TRAVEL_STATE_RESUME) {
					// test(TravelConstant.TRAVEL_STATE_PAUSE);
					BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_PAUSE);
				} else {
					// test(TravelConstant.TRAVEL_STATE_START);
					BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_START);
				}

			}
		});
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

	/**默认的UI显示状态*/
	private void defaultUIState(){
		bikeStateView.setVisibility(View.VISIBLE);
		travelStateView.setVisibility(View.GONE);
		speedStateView.setVisibility(View.VISIBLE);
		batteryStateView.setVisibility(View.GONE);
		lineTopView.setVisibility(View.GONE);
		lineBottomView.setVisibility(View.GONE);
	}
	protected abstract void uiInitCompleted();

	/** 改变状态 */
	protected Handler travelStateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int state = msg.what;// 所有的行程操作都必须是链接上BLE了才能有作用，因为没有链接的话，显示一定是灰色的
			if (state == TravelConstant.TRAVEL_STATE_PAUSE) {// 暂停
				mSpeedStateSpeedButton.setVisibility(View.VISIBLE);
				mSpeedStateSpeedText.setVisibility(View.GONE);
				mSpeedStateSpeedButton.setImageResource(R.drawable.speed_state_view_btn_pause_enable);
				mSpeedStateTipText.showTip(getString(R.string.tip_click_twice_to_end_the_trial));
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
		String spendTime = TimeUtils.formatTimeSSToHMS(EBikeTravelData.getInstance(this).spendTime) + "";
		if (distanUnit == EBConstant.DISTANCE_UNIT_MPH) {
			speed = speed / 1.6f;
			avgSpeed = avgSpeed / 1.6f;
		}

		// 格式化精度
		speed = CommonUtil.formatFloatAccuracy(speed, 1);
		avgSpeed = CommonUtil.formatFloatAccuracy(avgSpeed, 1);
		altitude = CommonUtil.formatFloatAccuracy(altitude, 1);
		distance = CommonUtil.formatFloatAccuracy(distance, 3);
		cadence = CommonUtil.formatFloatAccuracy(cadence, 0);
		calorie = CommonUtil.formatFloatAccuracy(calorie, 0,BigDecimal.ROUND_UP);
		// 转换
		distance = distance * 1000;// 由km->m

		// 车况
		mBikeStateBatteryView.onValueChange(EBikeTravelData.getInstance(this).batteryResidueCapacity, model,
				EBikeTravelData.getInstance(this).gear, false);
		mBikeStateBatteryPercent.setText(EBikeTravelData.getInstance(this).batteryResidueCapacity + "%");
		mBikeStateBatteryRemaindValue
				.setText(EBikeTravelData.getInstance(this).remaindTravelCapacity + "" + getString(R.string.km));
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
		mTravelStateCadenceValue.setText(EBikeTravelData.getInstance(this).cadence + "");
		if (distanUnit == EBConstant.DISTANCE_UNIT_MPH) {
			mSpeedStateSpeedView.onValueChange(speed, SpeedView.MAX_SPEED_MPH);
			mSpeedStateAvgSpeedUnit.setText(getString(R.string.mph));
			mTravelStateAvgSpeedUnit.setText(getString(R.string.mph));
			mTravelStateSpeedUnit.setText(getString(R.string.mph));
		} else {
			mSpeedStateSpeedView.onValueChange(speed, SpeedView.MAX_SPEED_KM_H);
			mSpeedStateAvgSpeedUnit.setText(getString(R.string.km_h));
			mTravelStateAvgSpeedUnit.setText(getString(R.string.km_h));
			mTravelStateSpeedUnit.setText(getString(R.string.km_h));
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
		mBatteryStateRemaindValue.setText(EBikeTravelData.getInstance(this).remaindTravelCapacity + "");
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.home_top_bar_model:
			onModel(v);
			break;
		case R.id.home_ib_profile:
			onProfile();
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
		case R.id.home_view_line_top:
			v.setSelected(!v.isSelected());
			if (v.isSelected()) {
				bikeStateView.setVisibility(View.GONE);
				travelStateView.setVisibility(View.VISIBLE);
				speedStateView.setVisibility(View.GONE);
				batteryStateView.setVisibility(View.VISIBLE);
				lineTopView.setVisibility(View.VISIBLE);
				lineBottomView.setVisibility(View.VISIBLE);
			} else {
				bikeStateView.setVisibility(View.VISIBLE);
				travelStateView.setVisibility(View.VISIBLE);
				speedStateView.setVisibility(View.GONE);
				batteryStateView.setVisibility(View.GONE);
				lineTopView.setVisibility(View.VISIBLE);
				lineBottomView.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.home_view_line_bottom:
			bikeStateView.setVisibility(View.VISIBLE);
			travelStateView.setVisibility(View.GONE);
			speedStateView.setVisibility(View.VISIBLE);
			batteryStateView.setVisibility(View.GONE);
			lineTopView.setVisibility(View.GONE);
			lineBottomView.setVisibility(View.GONE);
			break;
		case R.id.speed_state_arrow_top:
			bikeStateView.setVisibility(View.GONE);
			travelStateView.setVisibility(View.VISIBLE);
			speedStateView.setVisibility(View.GONE);
			batteryStateView.setVisibility(View.VISIBLE);
			lineTopView.setVisibility(View.VISIBLE);
			lineBottomView.setVisibility(View.VISIBLE);
			break;
		case R.id.speed_state_arrow_bottom:
			bikeStateView.setVisibility(View.VISIBLE);
			travelStateView.setVisibility(View.VISIBLE);
			speedStateView.setVisibility(View.GONE);
			batteryStateView.setVisibility(View.GONE);
			lineTopView.setVisibility(View.VISIBLE);
			lineBottomView.setVisibility(View.VISIBLE);
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
			lineTopView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			lineBottomView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			topBarView.setBackgroundColor(getResources().getColor(R.color.model_night_bar_background));
			bikeStateView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			speedStateContent.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			batteryStateView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			travelStateView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			mSpeedStateArrowTopView.setBackgroundResource(R.drawable.speed_state_view_arrow_top_night);
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
			lineTopView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			lineBottomView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			topBarView.setBackgroundColor(getResources().getColor(R.color.model_day_bar_background));
			bikeStateView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			speedStateContent.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			batteryStateView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			travelStateView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			mSpeedStateArrowTopView.setBackgroundResource(R.drawable.speed_state_view_arrow_top_day);
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

}
