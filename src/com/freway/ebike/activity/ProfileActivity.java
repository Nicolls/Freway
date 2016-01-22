package com.freway.ebike.activity;

import java.math.BigDecimal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.common.EBConstant;
import com.freway.ebike.model.RspUserInfo;
import com.freway.ebike.model.User;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.EBikeViewUtils;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;
import com.freway.ebike.view.HeadPicView;

public class ProfileActivity extends BaseActivity implements OnClickListener {

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
	private HeadPicView mHeadView;
	private User user;
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
		mHeadView=(HeadPicView) findViewById(R.id.profile_head_view);
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
		mHeadView.setEnabled(true);
		titleTv.setText(getString(R.string.profile));
		rightButton.setText("");
		rightButton.setVisibility(View.GONE);
		iconButton.setImageResource(R.drawable.icon_settings);
		mEBikeRequestService.userInfo(SPUtils.getToken(this));
	}
	//更新profile
	private void initProfile(User user){
		nameTv.setText(user.getUsername());
		if(TextUtils.equals(user.getGender(), User.GENDER_FEMALE)){//女
			genderImage.setImageResource(R.drawable.profile_icon_woman);
		}else{//男
			genderImage.setImageResource(R.drawable.profile_icon_man);
		}
		if(!TextUtils.isEmpty(user.getPhoto())){
			EBikeViewUtils.displayPhoto(this, mHeadView, user.getPhoto());
		}
		try {
			float distance=Float.parseFloat(user.getTotal_distance());//km
			if (SPUtils.getUnitOfDistance(this) == EBConstant.DISTANCE_UNIT_MPH) {//迈
				distance = distance * 0.6f;// km->mi
				mileageTitleTv.setText(R.string.total_miles);
			}else{
				mileageTitleTv.setText(R.string.total_km);
			}
			
			float time=Float.parseFloat(user.getTotal_time());//秒
			mileageValueTv.setText(CommonUtil.formatFloatAccuracy(distance, 1)+"");
			timeValueTv.setText(CommonUtil.formatFloatAccuracy(time/3600,1)+"h");
		} catch (Exception e) {
			LogUtils.e(tag, "parsefloat error"+e.getMessage());
		}
		
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
		Intent intent = new Intent(this, EBikeWebViewActivity.class);
		intent.putExtra("url", formatUrl(EBConstant.HTML5_URL_RECORDS));
		startActivity(intent);
	}

	public void onGrade() {
		Intent intent = new Intent(this, EBikeWebViewActivity.class);
		intent.putExtra("url", formatUrl(EBConstant.HTML5_URL_GRADES));
		startActivity(intent);
	}

	public void onNews() {
		Intent intent = new Intent(this, EBikeWebViewActivity.class);
		intent.putExtra("url",formatUrl( EBConstant.HTML5_URL_NEWS));
		startActivity(intent);
	}

	public void onTutorial() {
		Intent intent = new Intent(this, EBikeWebViewActivity.class);
		intent.putExtra("url", formatUrl(EBConstant.HTML5_URL_TUTORIAL));
		startActivity(intent);
	}


	@Override
	public void dateUpdate(int id, Object obj) {
		if(id==EBikeRequestService.ID_USERINFO){
			RspUserInfo info=(RspUserInfo) obj;
			if(info!=null&&info.getData()!=null){
				user=CommonUtil.updateUserProfile(this,info.getData());
				initProfile(user);
			}
		}
	}



	@Override
	protected void onResume() {
		super.onResume();
		user=SPUtils.getUser(this);
		initProfile(user);
	}
	
	private String formatUrl(String link){
		//token="31aayFqdhmtoiFxS1pudmYdzWmJgam5llpWTamxSaZeeZmaeaZWGtg==";
		return link+"?token="+SPUtils.getToken(this)+"&unit="+SPUtils.getUnitOfDistance(this);
	}
	
	/**请求出错*/
	protected void requestError(int id){
		ToastUtils.toast(this,getString(R.string.request_server_error));
		hideLoading();
	}
}
