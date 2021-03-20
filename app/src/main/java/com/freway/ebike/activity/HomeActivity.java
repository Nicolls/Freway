package com.freway.ebike.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.freway.ebike.R;
import com.freway.ebike.adapter.ViewPagerAdapter;
import com.freway.ebike.bluetooth.BLEScanConnectActivity;
import com.freway.ebike.bluetooth.BlueToothUtil;
import com.freway.ebike.bluetooth.EBikeTravelData;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.common.EBConstant;
import com.freway.ebike.utils.AlertUtil;
import com.freway.ebike.utils.AlertUtil.AlertClick;
import com.freway.ebike.utils.BikeMap;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.utils.MainThreadUtil;
import com.freway.ebike.utils.TimeUtils;
import com.freway.ebike.utils.ToastUtils;
import com.freway.ebike.view.BatteryView;
import com.freway.ebike.view.FlickTextView;
import com.freway.ebike.view.FlickView;
import com.freway.ebike.view.SpeedView;
import com.freway.ebike.view.directionalviewpager.DirectionalViewPager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity
        implements OnClickListener, TravelDataModel.TravelChangedCallBack {

    /**
     * 整个view viewpager
     */
    private DirectionalViewPager ebikeHomePager;
    /**
     * 存储viewpager的list
     */
    private List<View> dataList = null;
    /**
     * 地图view
     */
    private View mapContentView;
    /**
     * 导航条
     */
    private View topBarView;
    /**
     * 车况view
     */
    private View bikeStateView;
    /**
     * 车速view
     */
    private View speedStateView;
    private View speedStateContent;
    /**
     * 电池view
     */
    private View batteryStateView;
    /**
     * 骑行状态view
     */
    private View travelStateView;
    /**
     * 分隔线用于显示电池view,点击第一次显示，点击第二次隐藏
     */
    private View lineBikeStateView;
    /**
     * 分隔线用于显示速度view
     */
    private View lineTravelStateView;
    /**
     * 分隔线用于显示速度view
     */
    private View lineBatteryStateView;
    private ImageButton mUiModelBtn;
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
    private FlickView mTravelTip;
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
    // view
    private LinearLayout bikeModel;
    private LinearLayout speedModel;
    private LinearLayout batteryModel;
    private ViewPagerAdapter<View> adapter;
    private final BikeMap bikeMap = new BikeMap();
    private BitmapDescriptor bitmapA = BitmapDescriptorFactory.fromResource(R.drawable.battery_pic_day);
    private TravelDataModel travelDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initFontStyle();
        intClick();
        initData();
    }

    private void initView() {
        mTravelTip = (FlickView) findViewById(R.id.home_travel_tip);
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
        speedModel.setBackgroundResource(R.drawable.speed_state_map_bg_day);

        batteryModel = new LinearLayout(this);
        batteryModel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        batteryModel.setOrientation(LinearLayout.VERTICAL);

        bikeModel.addView(travelStateView);
        bikeModel.addView(mapContentView);

        bikeStateView.findViewById(R.id.home_view_line_bike_state).setVisibility(View.GONE);
        speedModel.addView(bikeStateView);
        speedModel.addView(speedStateView);

        batteryModel.addView(batteryStateView);

        dataList.add(batteryModel);
        dataList.add(speedModel);
        dataList.add(bikeModel);
        adapter = new ViewPagerAdapter<View>();
        adapter.setData(dataList);
        ebikeHomePager.setAdapter(adapter);
        ebikeHomePager.setOrientation(DirectionalViewPager.VERTICAL);
        ebikeHomePager.setCurrentItem(1);
        ebikeHomePager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                System.out.println("seletec--" + arg0);
                if (arg0 == 2) {
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

                } else if (arg0 == 0) {
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
        mUiModelBtn = (ImageButton) findViewById(R.id.home_top_bar_model);
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
        // 地图初始化
        MapView mapView = mapContentView.findViewById(R.id.home_mapView);
        bikeMap.onCreate(mapView);
    }

    /**
     * 设置字体风格
     */
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
        mUiModelBtn.setOnClickListener(this);
        mProfileView.setOnClickListener(this);
        lineBikeStateView.setOnClickListener(this);
        lineTravelStateView.setOnClickListener(this);
        lineBatteryStateView.setOnClickListener(this);
        mSpeedStateArrowTopView.setOnClickListener(this);
        mSpeedStateArrowBottomView.setOnClickListener(this);
        mSpeedStateSpeedButton.setOnClickListener(this);
        mSpeedStateSpeedText.setOnClickListener(this);
        mTravelTip.setOnClickListener(this);
        mBikeStateLightFront.setOnClickListener(this);
        mBikeStateLightBack.setOnClickListener(this);

    }

    private void initData() {
        travelDataModel = TravelDataModel.getInstance();
        travelDataModel.setTravelChangedCallBack(this);
        travelDataModel.init();
    }

    public void onProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时必须调用mMapView. onResume ()
        bikeMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时必须调用mMapView. onPause ()
        bikeMap.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.sendQuitAppBroadCast(this);
        bitmapA.recycle();
        bikeMap.onDestroy();
        travelDataModel.exit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_top_bar_model:
                travelDataModel.setUiMode(travelDataModel.uiModel == EBConstant.MODEL_DAY ?
                        EBConstant.MODEL_NIGHT : EBConstant.MODEL_DAY);
                break;
            case R.id.bike_state_light_front_icon:
                // 在连接蓝牙状态下运行
                travelDataModel.setBlueToothConnect(true);
                break;
            case R.id.bike_state_light_back_icon:
                // 在脱机状态下运行
                travelDataModel.setBlueToothConnect(false);
                break;
            case R.id.home_travel_tip:
            case R.id.speed_state_btn:
            case R.id.speed_state_speed_text:// 速度状态下速度值显示text
                handleTravelClick();
                break;
            case R.id.home_view_line_bike_state:
                ebikeHomePager.setCurrentItem(0);
                break;
            case R.id.home_view_line_battery_state:
                ebikeHomePager.setCurrentItem(2);
                break;
            case R.id.home_view_line_travel_state:
                ebikeHomePager.setCurrentItem(1);
                break;
            case R.id.speed_state_arrow_top:
                ebikeHomePager.setCurrentItem(0);
                break;
            case R.id.speed_state_arrow_bottom:
                ebikeHomePager.setCurrentItem(2);
                break;
            case R.id.home_ib_profile:
                onProfile();
                break;
            default:
                break;
        }
    }

    /**
     * 处理点击骑行按钮
     */
    private void handleTravelClick() {
        String title = "";
        String leftMessage = "";
        String middleMessage = "";
        String rightMessage = "";
        AlertClick leftAlert = null;
        AlertClick middleAlert = null;
        AlertClick rightAlert = null;
        // 初始化，待骑行状态
        if (travelDataModel.travelState == TravelDataModel.TravelState.NONE) {
            // 骑行未开始
            if (travelDataModel.bluetoothConnect) { // 蓝牙已连接
                // 开始行程
                travelDataModel.start();
                return;
            } else { // 蓝牙未连接，则弹出确认框，是否要脱机运行
                title = "车载蓝牙未连接，连接后能提供更好的数据体验";
                leftMessage = "去连接";
                middleMessage = "继续骑行";
                rightMessage = "点错了";
                leftAlert = new AlertClick() {

                    @Override
                    public void onClick(AlertDialog dialog, View v) {
                        dialog.dismiss();
                        BlueToothUtil.toBindBleActivity(getApplicationContext(), BLEScanConnectActivity.HANDLE_SCAN);
                    }
                };
                middleAlert = new AlertClick() {

                    @Override
                    public void onClick(AlertDialog dialog, View v) {
                        dialog.dismiss();
                        travelDataModel.start();
                    }
                };
                rightAlert = new AlertClick() {

                    @Override
                    public void onClick(AlertDialog dialog, View v) {
                        dialog.dismiss();
                    }
                };
            }
        } else if (travelDataModel.travelState == TravelDataModel.TravelState.PAUSE) {
            // 骑行暂停状态
            title = "骑行已暂停，要结束骑行吗？";
            leftMessage = "恢复骑行";
            middleMessage = "结束骑行";
            rightMessage = "取消";
            leftAlert = new AlertClick() {

                @Override
                public void onClick(AlertDialog dialog, View v) {
                    dialog.dismiss();
                    travelDataModel.resume();
                }
            };
            middleAlert = new AlertClick() {

                @Override
                public void onClick(AlertDialog dialog, View v) {
                    dialog.dismiss();
                    saveTravel();
                }
            };
            rightAlert = new AlertClick() {

                @Override
                public void onClick(AlertDialog dialog, View v) {
                    dialog.dismiss();
                }
            };
        } else if (travelDataModel.travelState == TravelDataModel.TravelState.RUN) {
            // 正在骑行状态
            title = "结束骑行？";
            leftMessage = "暂停";
            middleMessage = "结束";
            rightMessage = "点错了";
            leftAlert = new AlertClick() {

                @Override
                public void onClick(AlertDialog dialog, View v) {
                    dialog.dismiss();
                    travelDataModel.pause();
                }
            };
            middleAlert = new AlertClick() {

                @Override
                public void onClick(AlertDialog dialog, View v) {
                    dialog.dismiss();
                    saveTravel();
                }
            };
            rightAlert = new AlertClick() {

                @Override
                public void onClick(AlertDialog dialog, View v) {
                    dialog.dismiss();
                }
            };
        }
        AlertUtil.getInstance().alertThree(this, title, leftMessage, middleMessage, rightMessage,
                leftAlert, middleAlert, rightAlert);
    }

    /**
     * 保存行程对话框
     */
    private void saveTravel() {
        String title = "是否要保存本次骑行";
        String left = "是";
        String right = "否";
        AlertClick leftAlert = new AlertClick() {

            @Override
            public void onClick(AlertDialog dialog, View v) {
                dialog.dismiss();
                travelDataModel.completed(true);
                MainThreadUtil.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        AlertClick rightAlert = new AlertClick() {

            @Override
            public void onClick(AlertDialog dialog, View v) {
                dialog.dismiss();
                travelDataModel.completed(false);
            }
        };
        AlertUtil.getInstance().alertChoice(this, title, left, right,
                leftAlert, rightAlert, true);
    }

    @Override
    public void onChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUi();
            }
        });
    }

    private void updateUi() {
        // 更新夜间，日间模式UI
        updateUiMode();
        // 更新骑行数据信息UI
        updateTravelValueUI();
        // 更新状态UI
        updateTravelStateUi();
    }

    private void updateUiMode() {
        int batteryResidueCapacity = travelDataModel.batteryResidueCapacity;
        if (batteryResidueCapacity > 2) {// 对电量进行数据调整
            batteryResidueCapacity = (batteryResidueCapacity - 3) * 100 / 97;
        } else {
            batteryResidueCapacity = 0;
        }
        mBatteryStateBatteryView.onValueChange(batteryResidueCapacity, travelDataModel.uiModel,
                travelDataModel.gear, true);
        if (travelDataModel.uiModel == EBConstant.MODEL_NIGHT) {// 由day到night
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
            // 整个view
            speedModel.setBackgroundResource(R.drawable.speed_state_map_bg_night);
            // speedModel.setBackgroundColor(getResources().getColor(R.color.model_night_view_background));
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
            // 整个view
            speedModel.setBackgroundResource(R.drawable.speed_state_map_bg_day);
            // speedModel.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
            batteryModel.setBackgroundColor(getResources().getColor(R.color.model_day_view_background));
        }
    }

    private void updateTravelValueUI() {
        float speed = travelDataModel.insSpeed;
        float avgSpeed = travelDataModel.avgSpeed;
        float altitude = (float) travelDataModel.altitude;
        float distance = travelDataModel.distance;
        float calorie = travelDataModel.calorie;
        float cadence = travelDataModel.cadence;
        float remaindTravelCapacity = travelDataModel.remaindTravelCapacity;
        int batteryResidueCapacity = travelDataModel.batteryResidueCapacity;
        String spendTime = TimeUtils.formatTimeSSToHMS(travelDataModel.spendTime) + "";
        if (batteryResidueCapacity > 2) {// 对电量进行数据调整
            batteryResidueCapacity = (batteryResidueCapacity - 3) * 100 / 97;
        } else {
            batteryResidueCapacity = 0;
        }

        // 格式化精度
        speed = CommonUtil.formatFloatAccuracy(speed, 1);
        avgSpeed = CommonUtil.formatFloatAccuracy(avgSpeed, 1);
        altitude = CommonUtil.formatFloatAccuracy(altitude, 1);
        distance = CommonUtil.formatFloatAccuracy(distance, 1, 1);
        cadence = CommonUtil.formatFloatAccuracy(cadence, 0);
        calorie = CommonUtil.formatFloatAccuracy(calorie, 1);
        remaindTravelCapacity = CommonUtil.formatFloatAccuracy(remaindTravelCapacity, 1);

        // 车况
        switch (travelDataModel.gear) {
            case 0:
                mBikeStateGearIcon.setImageResource(R.drawable.bike_state_gear0);
                break;
            case 1:
                mBikeStateGearIcon.setImageResource(R.drawable.bike_state_gear1);
                break;
            case 2:
                mBikeStateGearIcon.setImageResource(R.drawable.bike_state_gear2);
                break;
            case 3:
                mBikeStateGearIcon.setImageResource(R.drawable.bike_state_gear3);
                break;
            default:
                mBikeStateGearIcon.setImageResource(R.drawable.bike_state_gear0);
                break;
        }
        mBikeStateBatteryView.onValueChange(batteryResidueCapacity,
                travelDataModel.uiModel, travelDataModel.gear,
                false);
        mBikeStateBatteryPercent.setText(batteryResidueCapacity + "%");
        mBikeStateBatteryRemaindValue.setText(remaindTravelCapacity + "" + getString(R.string.km));
        mBikeStateGearValue.setText(travelDataModel.gear + "");
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
        mSpeedStateSpeedView.onValueChange(speed, SpeedView.MAX_SPEED_KM_H);
        mSpeedStateAvgSpeedUnit.setText(getString(R.string.km_h));
        mTravelStateAvgSpeedUnit.setText(getString(R.string.km_h));
        mTravelStateSpeedUnit.setText(getString(R.string.km_h));
        mTravelStateAslUnit.setText(getString(R.string.km));
        mTravelStateDistanceUnit.setText(getString(R.string.km));
        mSpeedStateAslUnit.setText(getString(R.string.km));
        mSpeedStateDistanceUnit.setText(getString(R.string.km));

        mSpeedStateSpeedText.setText(speed + "");
        mSpeedStateCalValue.setText(calorie + "");
        mSpeedStateSpendTimeValue.setText(spendTime);

        mSpeedStateDistanceValue.setText(distance + "");

        mSpeedStateAvgSpeedValue.setText(avgSpeed + "");

        mSpeedStateAslValue.setText(altitude + "");

        mSpeedStateCadenceValue.setText(cadence + "");
        mBatteryStateBatteryView.onValueChange(batteryResidueCapacity,
                travelDataModel.uiModel, travelDataModel.gear,
                true);
        mBatteryStateBatteryPercent.setText(batteryResidueCapacity + "%");
        mBatteryStateRemaindValue.setText(remaindTravelCapacity + "");
        mBatteryStateRemaindUnit.setText(getString(R.string.km));
        if (travelDataModel.frontLed == EBConstant.ON) {
            mBatteryStateLightFront.setSelected(true);
            mBikeStateLightFront.setSelected(true);
        } else {
            mBatteryStateLightFront.setSelected(false);
            mBikeStateLightFront.setSelected(false);
        }
        if (travelDataModel.backLed == EBConstant.ON) {
            mBatteryStateLightBack.setSelected(true);
            mBikeStateLightBack.setSelected(true);
        } else {
            mBatteryStateLightBack.setSelected(false);
            mBikeStateLightBack.setSelected(false);
        }
        mBatteryStateGearText.setText(getString(R.string.gear) + travelDataModel.gear + "");
    }

    private void updateTravelStateUi() {
        switch (travelDataModel.travelState) {
            case NONE:
                if (travelDataModel.bluetoothConnect) {
                    mSpeedStateTipText.hideTip(); // 蓝牙连接的状态
                    mSpeedStateSpeedButton.setImageResource(R.drawable.speed_state_view_btn_start_enable); // 状态图标
                    mSpeedStateSpeedButton.setVisibility(View.VISIBLE); // 状态按钮
                    mSpeedStateSpeedText.setVisibility(View.GONE); // 速度文字
                } else {
                    mSpeedStateTipText.showTip("蓝牙断开连接"); // 蓝牙连接的状态
                    mSpeedStateSpeedButton.setImageResource(R.drawable.speed_state_view_btn_start_disable); // 状态图标
                }
                mTravelTip.hideTip(); // 暂停的旗帜
                mSpeedStateSpeedButton.setVisibility(View.VISIBLE); // 状态按钮
                mSpeedStateSpeedText.setVisibility(View.GONE); // 速度文字
                break;
            case RUN:
                mSpeedStateTipText.hideTip(); // 蓝牙连接的状态
                mTravelTip.hideTip(); // 暂停的旗帜
                mSpeedStateSpeedButton.setVisibility(View.GONE); // 状态按钮
                mSpeedStateSpeedText.setVisibility(View.VISIBLE); // 速度文字
                break;
            case PAUSE:
                mSpeedStateTipText.hideTip(); // 蓝牙连接的状态
                mTravelTip.showTip(); // 暂停的旗帜
                mSpeedStateSpeedButton.setImageResource(R.drawable.speed_state_view_btn_pause_enable); // 状态图标
                mSpeedStateSpeedButton.setVisibility(View.VISIBLE); // 状态按钮
                mSpeedStateSpeedText.setVisibility(View.GONE); // 速度文字
                break;
        }
    }

}
