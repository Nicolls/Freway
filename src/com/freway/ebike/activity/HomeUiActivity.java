package com.freway.ebike.activity;

import java.util.ArrayList;
import java.util.List;

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
import com.freway.ebike.view.FlickTextView;
import com.freway.ebike.view.SpeedView;
import com.freway.ebike.view.directionalviewpager.DirectionalViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class HomeUiActivity extends BaseActivity implements OnClickListener {

	protected MapUtil mMapUtil;
	protected BlueToothUtil mBlueToothUtil;
	/** 整个view viewpager */
	private DirectionalViewPager ebikeHomePager;
	/** 存储viewpager的list */
	private List<View> dataList = null;
	/** 地图view */
	private View mapContentView;
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

	// view
	private LinearLayout bikeModel;
	private LinearLayout speedModel;
	private LinearLayout batteryModel;
	private GVViewPagerAdapter<View> adapter;
	private int selectItem=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initView();
		initFontStyle();
		intClick();
		// initData();//这个已经由onResume来做了
		uiInitCompleted(mapContentView);
	}

	private void initView() {
		ebikeHomePager = (DirectionalViewPager) findViewById(R.id.home_ebike_pager);
		dataList = new ArrayList<View>();
		bikeStateView = LayoutInflater.from(this).inflate(R.layout.layout_home_bike_state, null);
		batteryStateView = LayoutInflater.from(this).inflate(R.layout.layout_home_battery_state, null);
		travelStateView = LayoutInflater.from(this).inflate(R.layout.layout_home_travel_state, null);
		speedStateView = LayoutInflater.from(this).inflate(R.layout.layout_home_speed_state, null);
		mapContentView = LayoutInflater.from(this).inflate(R.layout.layout_home_map, null);
		bikeModel = new LinearLayout(this);
		bikeModel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		bikeModel.setOrientation(LinearLayout.VERTICAL);

		speedModel = new LinearLayout(this);
		speedModel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		speedModel.setOrientation(LinearLayout.VERTICAL);

		batteryModel = new LinearLayout(this);
		batteryModel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		batteryModel.setOrientation(LinearLayout.VERTICAL);

		bikeModel.addView(travelStateView);
		bikeModel.addView(mapContentView);

		bikeStateView.findViewById(R.id.home_view_line_bike_state).setVisibility(View.GONE);
		speedModel.addView(bikeStateView);
		speedModel.addView(speedStateView);

		batteryModel.addView(batteryStateView);

		dataList.add(bikeModel);
		dataList.add(speedModel);
		dataList.add(batteryModel);
		adapter = new GVViewPagerAdapter<View>();
		adapter.setData(dataList);
		ebikeHomePager.setAdapter(adapter);
		ebikeHomePager.setOrientation(DirectionalViewPager.VERTICAL);
		ebikeHomePager.setCurrentItem(1);
		ebikeHomePager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				selectItem=arg0;
				System.out.println("seletec--" + arg0);
				if (arg0 == 0) {
					ViewGroup group = (ViewGroup) bikeStateView.getParent();
					if (group != null)
						group.removeView(bikeStateView);
					bikeStateView.findViewById(R.id.home_view_line_bike_state).setVisibility(View.VISIBLE);
					bikeModel.addView(bikeStateView, 0);
					ViewGroup group2 = (ViewGroup) travelStateView.getParent();
					if (group2 != null)
						group2.removeView(travelStateView);
					bikeModel.addView(travelStateView, 1);
				} else if (arg0 == 1) {
					ViewGroup group = (ViewGroup) bikeStateView.getParent();
					if (group != null)
						group.removeView(bikeStateView);
					bikeStateView.findViewById(R.id.home_view_line_bike_state).setVisibility(View.GONE);
					speedModel.addView(bikeStateView, 0);

				} else if (arg0 == 2) {
					ViewGroup group = (ViewGroup) travelStateView.getParent();
					if (group != null)
						group.removeView(travelStateView);
					batteryModel.addView(travelStateView);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		// 分隔线
		lineBikeStateView = bikeStateView.findViewById(R.id.home_view_line_bike_state);
		lineTravelStateView = travelStateView.findViewById(R.id.home_view_line_travel_state);
		lineBatteryStateView = batteryStateView.findViewById(R.id.home_view_line_battery_state);

		// 导航条
		topBarView = findViewById(R.id.home_top_bar);
		mProfileView = (ImageView) findViewById(R.id.home_ib_profile);
		mModelBtn = (ImageButton) findViewById(R.id.home_top_bar_model);
		mLogo = (ImageView) findViewById(R.id.home_top_bar_logo);
		// 车况
		mBikeStateBatteryView = (BatteryView) bikeStateView.findViewById(R.id.bike_state_battery_view);
		mBikeStateBatteryPercent = (TextView) bikeStateView.findViewById(R.id.bike_state_battery_value);
		mBikeStateBatteryRemaindValue = (TextView) bikeStateView.findViewById(R.id.bike_state_battery_remaind_value);
		mBikeStateBatteryRemaindTitle = (TextView) bikeStateView.findViewById(R.id.bike_state_battery_remaind_title);
		mBikeStateGearIcon = (ImageView) bikeStateView.findViewById(R.id.bike_state_gear_icon);
		mBikeStateGearTitle = (TextView) bikeStateView.findViewById(R.id.bike_state_gear_title);
		mBikeStateGearValue = (TextView) bikeStateView.findViewById(R.id.bike_state_gear_value);
		mBikeStateLightFront = (ImageView) bikeStateView.findViewById(R.id.bike_state_light_front_icon);
		mBikeStateLightBack = (ImageView) bikeStateView.findViewById(R.id.bike_state_light_back_icon);

		// 骑行状态
		mTravelStateSpendTime = (TextView) travelStateView.findViewById(R.id.travel_state_spend_time);
		mTravelStateCalValue = (TextView) travelStateView.findViewById(R.id.travel_state_cal_value);
		mTravelStateCalUnit = (TextView) travelStateView.findViewById(R.id.travel_state_cal_unit);
		mTravelStateSpeedValue = (TextView) travelStateView.findViewById(R.id.travel_state_speed);
		mTravelStateSpeedUnit = (TextView) travelStateView.findViewById(R.id.travel_state_speed_unit);

		mTravelStateDistanceTitle = (TextView) travelStateView.findViewById(R.id.travel_state_distance_title);
		mTravelStateDistanceValue = (TextView) travelStateView.findViewById(R.id.travel_state_distance_value);
		mTravelStateDistanceUnit = (TextView) travelStateView.findViewById(R.id.travel_state_distance_unit);

		mTravelStateAvgSpeedTitle = (TextView) travelStateView.findViewById(R.id.travel_state_avg_title);
		mTravelStateAvgSpeedValue = (TextView) travelStateView.findViewById(R.id.travel_state_avg_value);
		mTravelStateAvgSpeedUnit = (TextView) travelStateView.findViewById(R.id.travel_state_avg_unit);
		mTravelStateAslTitle = (TextView) travelStateView.findViewById(R.id.travel_state_asl_title);
		mTravelStateAslValue = (TextView) travelStateView.findViewById(R.id.travel_state_asl_value);
		mTravelStateAslUnit = (TextView) travelStateView.findViewById(R.id.travel_state_asl_unit);
		mTravelStateCadenceTitle = (TextView) travelStateView.findViewById(R.id.travel_state_cadence_title);
		mTravelStateCadenceValue = (TextView) travelStateView.findViewById(R.id.travel_state_cadence_value);
		mTravelStateCadenceUnit = (TextView) travelStateView.findViewById(R.id.travel_state_cadence_unit);
		// 速度
		speedStateContent = speedStateView.findViewById(R.id.speed_state_content);
		mSpeedStateArrowTopView = speedStateView.findViewById(R.id.speed_state_arrow_top);
		mSpeedStateArrowBottomView = (ImageView) speedStateView.findViewById(R.id.speed_state_arrow_bottom);
		mSpeedStateCicleHolder = (ImageView) speedStateView.findViewById(R.id.speed_state_cicle_view);
		mSpeedStateSpeedView = (SpeedView) speedStateView.findViewById(R.id.speed_state_speed_view);
		mSpeedStateSpeedText = (TextView) speedStateView.findViewById(R.id.speed_state_speed_text);
		mSpeedStateTipText = (FlickTextView) speedStateView.findViewById(R.id.speed_state_tip_text);
		mSpeedStateSpeedButton = (ImageButton) speedStateView.findViewById(R.id.speed_state_btn);
		mSpeedStateCalValue = (TextView) speedStateView.findViewById(R.id.speed_state_cal_value);
		mSpeedStateCalUnit = (TextView) speedStateView.findViewById(R.id.speed_state_cal_unit);
		mSpeedStateSpendTimeValue = (TextView) speedStateView.findViewById(R.id.speed_state_spend_time);

		mSpeedStateDistanceTitle = (TextView) speedStateView.findViewById(R.id.speed_state_distance_title);
		mSpeedStateDistanceValue = (TextView) speedStateView.findViewById(R.id.speed_state_distance_value);
		mSpeedStateDistanceUnit = (TextView) speedStateView.findViewById(R.id.speed_state_distance_unit);

		mSpeedStateAvgSpeedTitle = (TextView) speedStateView.findViewById(R.id.speed_state_avg_title);
		mSpeedStateAvgSpeedValue = (TextView) speedStateView.findViewById(R.id.speed_state_avg_value);
		mSpeedStateAvgSpeedUnit = (TextView) speedStateView.findViewById(R.id.speed_state_avg_unit);

		mSpeedStateAslTitle = (TextView) speedStateView.findViewById(R.id.speed_state_asl_title);
		mSpeedStateAslValue = (TextView) speedStateView.findViewById(R.id.speed_state_asl_value);
		mSpeedStateAslUnit = (TextView) speedStateView.findViewById(R.id.speed_state_asl_unit);

		mSpeedStateCadenceTitle = (TextView) speedStateView.findViewById(R.id.speed_state_cadence_title);
		mSpeedStateCadenceValue = (TextView) speedStateView.findViewById(R.id.speed_state_cadence_value);
		mSpeedStateCadenceUnit = (TextView) speedStateView.findViewById(R.id.speed_state_cadence_unit);

		// 电池
		mBatteryStateBatteryView = (BatteryView) batteryStateView.findViewById(R.id.battery_state_battery_view);
		mBatteryStateBatteryPercent = (TextView) batteryStateView.findViewById(R.id.battery_state_battery_percent);
		mBatteryStateRemaindTip = (TextView) batteryStateView.findViewById(R.id.battery_state_remaind_tip);
		mBatteryStateRemaindValue = (TextView) batteryStateView.findViewById(R.id.battery_state_remaind_value);
		mBatteryStateRemaindUnit = (TextView) batteryStateView.findViewById(R.id.battery_state_remaind_unit);
		mBatteryStateLightFront = (ImageView) batteryStateView.findViewById(R.id.battery_state_light_front);
		mBatteryStateLightBack = (ImageView) batteryStateView.findViewById(R.id.battery_state_light_back);
		mBatteryStateGearText = (TextView) batteryStateView.findViewById(R.id.battery_state_gear);

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

	protected abstract void uiInitCompleted(View mapContent);

	/** 改变状态 */
	protected Handler travelStateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int state = msg.what;// 所有的行程操作都必须是链接上BLE了才能有作用，因为没有链接的话，显示一定是灰色的
			if (state == TravelConstant.TRAVEL_STATE_PAUSE || state == TravelConstant.TRAVEL_STATE_FAKE_PAUSE) {// 暂停
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

	/** 保存骑行 */
	private void saveTravel() {
		LogUtils.i(tag, "保存骑行" + BaseApplication.travelState);
		if (EBikeTravelData.getInstance(HomeUiActivity.this).distance < EBikeTravelData.MUST_MIN_TRAVEL) {
			// 数据少，不存储
			AlertUtil.getInstance(HomeUiActivity.this).alertChoice(getString(R.string.travel_too_short_not_save),
					getString(R.string.yes), getString(R.string.no), new OnClickListener() {

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
					}, true);
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

	/** 弹出行程 选择 */
	private void alertTravelChoice() {
		AlertUtil.getInstance(this).alertThree(getString(R.string.travel_save), getString(R.string.yes),
				getString(R.string.no), getString(R.string.tip_go_on_the_trial), new OnClickListener() {

					@Override
					public void onClick(View v) {
						AlertUtil.getInstance(HomeUiActivity.this).dismiss();
						saveTravel();
					}
				}, new OnClickListener() {

					@Override
					public void onClick(View v) {
						AlertUtil.getInstance(HomeUiActivity.this).dismiss();
						// test(TravelConstant.TRAVEL_STATE_RESUME);
						BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_STOP);
					}
				}, new OnClickListener() {

					@Override
					public void onClick(View v) {// 继续骑行
						AlertUtil.getInstance(HomeUiActivity.this).dismiss();
						// test(TravelConstant.TRAVEL_STATE_RESUME);
						BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this,
								TravelConstant.TRAVEL_STATE_RESUME);
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
			bleStateChange(BlueToothService.ble_state);
			LogUtils.i(tag, "单击" + BaseApplication.travelState);
			if (BlueToothService.ble_state != BlueToothConstants.BLE_STATE_CONNECTED) {// 未连接
				mBlueToothUtil.bleConnect(getString(R.string.ble_not_connect), getString(R.string.yes),
						getString(R.string.no));
				return;
			}
			if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_PAUSE) {
				alertTravelChoice();
			} else if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_FAKE_PAUSE) {// 如果是伪暂停要先把它设置为暂停
				BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_PAUSE);
				alertTravelChoice();
			} else if (BaseApplication.travelState == TravelConstant.TRAVEL_STATE_START
					|| BaseApplication.travelState == TravelConstant.TRAVEL_STATE_RESUME) {
				BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_PAUSE);
				// // test(TravelConstant.TRAVEL_STATE_PAUSE);
				// if (EBikeTravelData.getInstance(this).zeroSpeedCount >
				// EBikeTravelData.MAX_LIMIT_ZERO_SPEED) {// 超过数值//说明界面已经是暂停的了
				// alertTravelChoice();
				// }

			} else {
				// test(TravelConstant.TRAVEL_STATE_START);
				BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_START);
			}
			break;
		case R.id.speed_state_speed_text:// 速度状态下速度值显示text
			bleStateChange(BlueToothService.ble_state);
			// ToastUtils.toast(this, "speed text click");
			BaseApplication.sendStateChangeBroadCast(HomeUiActivity.this, TravelConstant.TRAVEL_STATE_PAUSE);
			// test(TravelConstant.TRAVEL_STATE_PAUSE);
			break;
		case R.id.home_view_line_bike_state:
			ebikeHomePager.setCurrentItem(1);
			break;
		case R.id.home_view_line_battery_state:
			ebikeHomePager.setCurrentItem(0);
			break;
		case R.id.home_view_line_travel_state:
			System.out.println("--->"+selectItem);
			if(selectItem==0){
				System.out.println("set item 2");
				ebikeHomePager.setCurrentItem(2);
			}else {
				System.out.println("set item 1");
				ebikeHomePager.setCurrentItem(1);
			}
			break;
		case R.id.speed_state_arrow_top:
			ebikeHomePager.setCurrentItem(2);
			break;
		case R.id.speed_state_arrow_bottom:
			ebikeHomePager.setCurrentItem(0);
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
			lineBatteryStateView.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
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
			//整个view
			speedModel.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
			batteryModel.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));

		} else {// 由night到day
			mLogo.setImageResource(R.drawable.home_freway_logo_day);
			lineBikeStateView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			lineTravelStateView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			lineBatteryStateView.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
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
			//整个view
			speedModel.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
			batteryModel.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
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
		super.onResume();
		initData();
	}

}
