package com.freway.ebike.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.bluetooth.BLEScanConnectActivity;
import com.freway.ebike.bluetooth.BlueToothConstants;
import com.freway.ebike.bluetooth.BlueToothUtil;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.common.BaseApplication;
import com.freway.ebike.common.EBConstant;
import com.freway.ebike.crop.BitmapUtil;
import com.freway.ebike.crop.CropHandler;
import com.freway.ebike.crop.CropHelper;
import com.freway.ebike.crop.CropParams;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.model.RspUpdatePhoto;
import com.freway.ebike.model.RspUserInfo;
import com.freway.ebike.model.RspVersion;
import com.freway.ebike.model.User;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.service.UpdateAPPService;
import com.freway.ebike.service.UpdateAPPService.UpdateAppListener;
import com.freway.ebike.utils.AlertUtil;
import com.freway.ebike.utils.AlertUtil.AlertClick;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.EBikeActivityManager;
import com.freway.ebike.utils.EBikeViewUtils;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;
import com.freway.ebike.view.HeadPicView;

public class SettingActivity extends BaseActivity implements OnClickListener, UpdateAppListener, CropHandler {
	private ImageView iconButton;
	private ImageView leftButton;
	private TextView rightButton;
	private TextView titleTv;
	private TextView nameTitle;
	private EditText nameValue;
	private TextView emailTitle;
	private EditText emailValue;
	private TextView genderTitle;
	private ImageView genderView;
	private TextView ageTitle;
	private EditText ageValue;
	private TextView heightTitle;
	private EditText heightValue;
	private TextView weightTitle;
	private EditText weightValue;
	private HeadPicView mHeadView;
	private TextView myEbike;
	private TextView snValue;
	private TextView languageTitle;
	private TextView languageValue;
	private TextView unitDistanceTitle;
	private TextView unitDistanceValue;
	private TextView versionTitle;
	private TextView versionValue;
	private TextView exitTitle;

	private View snLL;
	private View languageLL;
	private View unitDistanceLL;
	private View versionLL;
	private View exitLL;

	private CropParams mCropParams;
	private boolean isEdit = false;
	private User user;
	private String photoPath = "";
	private RspVersion version;
	private boolean isDownloading = false;

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
		iconButton = (ImageView) findViewById(R.id.top_bar_right_icon);
		leftButton = (ImageView) findViewById(R.id.top_bar_left);
		rightButton = (TextView) findViewById(R.id.top_bar_right);
		titleTv = (TextView) findViewById(R.id.top_bar_title);
		nameTitle = (TextView) findViewById(R.id.setting_name_title);
		nameValue = (EditText) findViewById(R.id.setting_name_value);
		emailTitle = (TextView) findViewById(R.id.setting_email_title);
		emailValue = (EditText) findViewById(R.id.setting_email_value);
		genderTitle = (TextView) findViewById(R.id.setting_gender_title);
		genderView = (ImageView) findViewById(R.id.setting_gender_view);
		ageTitle = (TextView) findViewById(R.id.setting_age_title);
		ageValue = (EditText) findViewById(R.id.setting_age_value);
		heightTitle = (TextView) findViewById(R.id.setting_height_title);
		heightValue = (EditText) findViewById(R.id.setting_height_value);
		weightTitle = (TextView) findViewById(R.id.setting_weight_title);
		weightValue = (EditText) findViewById(R.id.setting_weight_value);
		mHeadView = (HeadPicView) findViewById(R.id.profile_head_view);
		myEbike = (TextView) findViewById(R.id.setting_my_ebike);
		snValue = (TextView) findViewById(R.id.setting_sn);
		languageTitle = (TextView) findViewById(R.id.setting_language_title);
		languageValue = (TextView) findViewById(R.id.setting_language_value);
		unitDistanceTitle = (TextView) findViewById(R.id.setting_unit_distance_title);
		unitDistanceValue = (TextView) findViewById(R.id.setting_unit_distance_value);
		versionTitle = (TextView) findViewById(R.id.setting_version_title);
		versionValue = (TextView) findViewById(R.id.setting_version_value);
		exitTitle = (TextView) findViewById(R.id.setting_tv_exit);

		snLL = findViewById(R.id.setting_ll_sn);
		languageLL = findViewById(R.id.setting_ll_language);
		unitDistanceLL = findViewById(R.id.setting_ll_unit_distance);
		versionLL = findViewById(R.id.setting_ll_version);
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
		versionTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		versionValue.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
		exitTitle.setTypeface(FontUtil.get(this, FontUtil.STYLE_DIN_LIGHT));
	}

	private void intClick() {
		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		iconButton.setOnClickListener(this);
		genderView.setOnClickListener(this);
		mHeadView.setOnClickListener(this);
		snLL.setOnClickListener(this);
		languageLL.setOnClickListener(this);
		unitDistanceLL.setOnClickListener(this);
		versionLL.setOnClickListener(this);
		exitLL.setOnClickListener(this);
	}

	private void initData() {
		genderView.setEnabled(false);
		mCropParams = new CropParams(this);
		mHeadView.setEnabled(false);
		iconButton.setImageResource(R.drawable.settings_confirm_red);
		iconButton.setVisibility(View.GONE);
		titleTv.setText(getString(R.string.settings) + "");
		rightButton.setText(getString(R.string.edit) + "");
		user = SPUtils.getUser(this);
		updateUiUser(user);
		showLoading(true);
		mEBikeRequestService.userInfo(SPUtils.getToken(this));
	}

	private void updateUiUser(User user) {
		if (user != null) {
			nameValue.setText(user.getUsername());
			emailValue.setText(user.getEmail());
			if (TextUtils.equals(User.GENDER_FEMALE, user.getGender())) {// 女
				genderView.setSelected(true);
			} else {
				genderView.setSelected(false);
			}
			ageValue.setText(user.getAge());
			heightValue.setText(user.getHeight());
			weightValue.setText(user.getWeight());
			if (!TextUtils.isEmpty(user.getPhoto())) {
				EBikeViewUtils.displayPhoto(this, mHeadView, user.getPhoto());
			}
			if (!TextUtils.isEmpty(SPUtils.getEBkieAddress(this))) {
				snValue.setText(SPUtils.getEBkieName(this));
			} else {
				snValue.setText(getString(R.string.ble_not_bind));
			}

			// languageValue = (TextView)
			// findViewById(R.id.setting_language_value);
			if (SPUtils.getUnitOfDistance(this) == EBConstant.DISTANCE_UNIT_MPH) {
				unitDistanceValue.setText(getString(R.string.distance_unit_mph));
			} else {
				unitDistanceValue.setText(getString(R.string.distance_unit_mi));
			}
			versionValue.setText(CommonUtil.getAppVersion(this));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_bar_left:
			finish();
			break;
		case R.id.top_bar_right:
			if (isEdit == false) {
				rightButton.setVisibility(View.GONE);
				iconButton.setVisibility(View.VISIBLE);
				isEdit = true;
				setEditState(isEdit);
			}
			break;
		case R.id.top_bar_right_icon:
			if (completedEdit()) {
				rightButton.setVisibility(View.VISIBLE);
				iconButton.setVisibility(View.GONE);
				isEdit = false;
				setEditState(isEdit);
				mEBikeRequestService.updateUserInfo(SPUtils.getToken(this), user);
			}
			break;
		case R.id.setting_gender_view:
			v.setSelected(!v.isSelected());
			if (v.isSelected()) {// 女
				user.setGender(User.GENDER_FEMALE);
			} else {// 男
				user.setGender(User.GENDER_MALE);
			}
			break;
		case R.id.profile_head_view:
			mCropParams.refreshUri();
			AlertUtil.getInstance().alertChoice(this,getString(R.string.portrait_settings), getString(R.string.album),
					getString(R.string.camera), new AlertClick() {

						@Override
						public void onClick(AlertDialog dialog,View v) {
							// album
							dialog.dismiss();
							mCropParams.enable = true;
							mCropParams.compress = false;
							Intent intent = CropHelper.buildGalleryIntent(mCropParams);
							startActivityForResult(intent, CropHelper.REQUEST_CROP);
						}
					}, new AlertClick() {

						@Override
						public void onClick(AlertDialog dialog,View v) {
							// camera
							dialog.dismiss();
							mCropParams.enable = true;
							mCropParams.compress = false;
							Intent intent = CropHelper.buildCameraIntent(mCropParams);
							startActivityForResult(intent, CropHelper.REQUEST_CAMERA);

						}
					},true);
			break;
		case R.id.setting_ll_sn:
			if (TextUtils.isEmpty(SPUtils.getEBkieAddress(SettingActivity.this))) {
				BlueToothUtil.toBindBleActivity(this, BLEScanConnectActivity.HANDLE_SCAN);
			} else {
				AlertUtil.getInstance().alertChoice(SettingActivity.this,getString(R.string.tip_ebike_setting),
						getString(R.string.unbind_ebike), getString(R.string.scan_ebike),
						new AlertClick() {

							@Override
							public void onClick(AlertDialog dialog,View v) {
								dialog.dismiss();
								unbindBleCharge();
							}
						}, new AlertClick() {

							@Override
							public void onClick(AlertDialog dialog,View v) {
								dialog.dismiss();
								BlueToothUtil.toBindBleActivity(SettingActivity.this,
										BLEScanConnectActivity.HANDLE_SCAN);
							}
						},true);
			}
			break;
		case R.id.setting_ll_language:

			break;
		case R.id.setting_ll_unit_distance:
			AlertUtil.getInstance().alertChoice(this,getString(R.string.unit_distance_settings),
					getString(R.string.distance_unit_mph), getString(R.string.distance_unit_mi),
					new AlertClick() {

						@Override
						public void onClick(AlertDialog dialog,View v) {
							// mph
							dialog.dismiss();
							SPUtils.setUnitOfDistance(SettingActivity.this, EBConstant.DISTANCE_UNIT_MPH);
							unitDistanceValue.setText(getString(R.string.distance_unit_mph));
						}
					}, new AlertClick() {

						@Override
						public void onClick(AlertDialog dialog,View v) {
							// mi
							dialog.dismiss();
							SPUtils.setUnitOfDistance(SettingActivity.this, EBConstant.DISTANCE_UNIT_KM);
							unitDistanceValue.setText(getString(R.string.distance_unit_mi));
						}
					},true);
			break;
		case R.id.setting_ll_version:
			chargeUpdate(version);
			break;
		case R.id.setting_ll_exit:
			EBikeActivityManager.getAppManager().reLogin(this, true);
			break;
		default:
			break;
		}
	}
	/**解绑判断*/
	private void unbindBleCharge(){
		if(BaseApplication.travelState==TravelConstant.TRAVEL_STATE_COMPLETED||
				BaseApplication.travelState==TravelConstant.TRAVEL_STATE_NONE||
				BaseApplication.travelState==TravelConstant.TRAVEL_STATE_STOP){//没有在行程中
			unbindBle();
		}else{//如果正在行程中，则提示
			AlertUtil.getInstance().alertChoice(this,getString(R.string.unbind_ble_will_abandon),
					getString(R.string.yes), getString(R.string.no),
					new AlertClick() {

						@Override
						public void onClick(AlertDialog dialog,View v) {
							// mph
							dialog.dismiss();
							unbindBle();
						}
					}, new AlertClick() {

						@Override
						public void onClick(AlertDialog dialog,View v) {
							// mi
							dialog.dismiss();
						}
					},true);
		}
	}
	/**解绑蓝牙*/
	private void unbindBle(){
		SPUtils.setEBikeAddress(SettingActivity.this, "");
		snValue.setText(getString(R.string.ble_not_bind));
		BaseApplication.sendStateChangeBroadCast(SettingActivity.this,
				TravelConstant.TRAVEL_STATE_STOP);
		Intent intent = new Intent(BlueToothConstants.BLUETOOTH_ACTION_HANDLE_SERVER);
		intent.putExtra(BlueToothConstants.EXTRA_HANDLE_TYPE,
				BlueToothConstants.HANDLE_SERVER_DISCONNECT);
		intent.putExtra(BlueToothConstants.EXTRA_DATA, "");
		SettingActivity.this.sendBroadcast(intent);
		ToastUtils.toast(SettingActivity.this, getString(R.string.tip_unbind_ebike_success));
	}
	
	/** 版本更新 */
	private void updateApk(String downloadUrl) {
		isDownloading = true;
		// final String downloadUrl =
		// "http://www.saner5.com/index.aspx?appId=1&appDownLoadCount=55&appDownloadUrl=upload/app/2014_07_17_17_44_48ear.apk";
		ToastUtils.toast(SettingActivity.this, getString(R.string.start_download));
		versionValue.setText(getString(R.string.app_downloading));
		Intent intent = new Intent(UpdateAPPService.class.getName());
		intent.putExtra(UpdateAPPService.INTENT_DOWNLOAD_URL, downloadUrl);
		UpdateAPPService.setUpdateAppListener(SettingActivity.this);
		SettingActivity.this.startService(intent);
	}

	private void setEditState(boolean isEdit) {
		nameValue.setFocusable(isEdit);
		nameValue.setFocusableInTouchMode(isEdit);
		if (isEdit) {
			nameValue.requestFocus();
		}
		genderView.setEnabled(isEdit);
		emailValue.setFocusable(isEdit);
		emailValue.setFocusableInTouchMode(isEdit);
		ageValue.setFocusable(isEdit);
		ageValue.setFocusableInTouchMode(isEdit);
		heightValue.setFocusable(isEdit);
		heightValue.setFocusableInTouchMode(isEdit);
		weightValue.setFocusable(isEdit);
		weightValue.setFocusableInTouchMode(isEdit);
		mHeadView.setEnabled(isEdit);
	}

	private boolean completedEdit() {
		boolean isOk = true;
		String nickName = nameValue.getText().toString();
		String email = emailValue.getText().toString();
		String age = ageValue.getText().toString();
		String height = heightValue.getText().toString();
		String weight = weightValue.getText().toString();
		if (TextUtils.isEmpty(nickName)) {
			ToastUtils.toast(this, getString(R.string.nick_name) + "" + getString(R.string.can_not_be_null));
			return false;
		}
		if (!TextUtils.isEmpty(user.getEmail())) {
			if (TextUtils.isEmpty(email)) {
				ToastUtils.toast(this, getString(R.string.email) + "" + getString(R.string.can_not_be_null));
			}
		}
		if (!TextUtils.isEmpty(email)) {
			if (!CommonUtil.isEmail(email)) {
				ToastUtils.toast(this, getString(R.string.email) + "" + getString(R.string.email_incorrect));
				return false;
			}
		}

		user.setUsername(nickName);
		user.setEmail(email);
		user.setAge(age);
		user.setHeight(height);
		user.setWeight(weight);
		if (genderView.isSelected()) {
			user.setGender(User.GENDER_FEMALE);
		} else {
			user.setGender(User.GENDER_MALE);
		}
		SPUtils.setUser(this, user);
		return isOk;
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		hideLoading();
		switch (id) {
		case EBikeRequestService.ID_UPDATEUSERINFO:
			SPUtils.setUser(this, user);
			if (!TextUtils.isEmpty(photoPath)) {
				showLoading(true);
				mEBikeRequestService.updatePhoto(SPUtils.getToken(this), photoPath);
			} else {
				ToastUtils.toast(SettingActivity.this, getString(R.string.update_profile_success));
			}
			break;
		case EBikeRequestService.ID_USERINFO:
			RspUserInfo info = (RspUserInfo) obj;
			if (info != null && info.getData() != null) {
				user = CommonUtil.updateUserProfile(SettingActivity.this, info.getData());
			}
			updateUiUser(user);
			break;
		case EBikeRequestService.ID_PHOTO:
			RspUpdatePhoto rsp = (RspUpdatePhoto) obj;
			user.setPhoto(rsp.getData().getUrl());
			SPUtils.setUser(SettingActivity.this, user);
			ToastUtils.toast(SettingActivity.this, getString(R.string.update_profile_success));
			break;
		case EBikeRequestService.ID_VERSION:
			RspVersion version = (RspVersion) obj;
			this.version = version;
			chargeUpdate(version);

			break;
		default:
			break;
		}
	}

	/** 判断更新 */
	private void chargeUpdate(RspVersion version) {
		if (version != null) {
			String newest = version.getData().getNewest();
			final String url = version.getData().getUrl();
			// final String url =
			// "http://www.saner5.com/index.aspx?appId=1&appDownLoadCount=55&appDownloadUrl=upload/app/2014_07_17_17_44_48ear.apk";
			int m = Integer.parseInt(version.getData().getForce_update());
			boolean isForceUpdate = (m == 0 ? false : true);
			if (!TextUtils.isEmpty(newest)) {
				if (!isDownloading) {
					AlertUtil.getInstance().alertConfirm(this,getString(R.string.app_update_tip),
							getString(R.string.confirm), new AlertClick() {

								@Override
								public void onClick(AlertDialog dialog,View v) {
									dialog.dismiss();
									updateApk(url);
								}
							});

				} else {
					ToastUtils.toast(SettingActivity.this,
							 getString(R.string.app_is_update));
				}
			} else {
				ToastUtils.toast(SettingActivity.this, getString(R.string.app_now_is_newly));
			}
		} else {
			showLoading(true);
			mEBikeRequestService.version("Android", CommonUtil.getAppVersion(this));
		}
	}

	/** 请求出错都要提示这是最新的版本 */
	protected void requestError(int id) {
		if (id == EBikeRequestService.ID_REQUEST_ERROR) {
			ToastUtils.toast(this, getString(R.string.request_server_error));
		}else{
			ToastUtils.toast(SettingActivity.this, getString(R.string.app_now_is_newly));
		}
	}

	@Override
	public void updateAppCompleted(String message) {
		if (SettingActivity.this != null && versionValue != null) {
			versionValue.setSelected(false);
			versionValue.setText(CommonUtil.getAppVersion(this));
			ToastUtils.toast(this, message);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		CropHelper.handleResult(this, requestCode, resultCode, data);
		if (requestCode == 1) {
			LogUtils.e(tag, "onActivityResult");
		}
	}

	@Override
	public void onPhotoCropped(Uri uri) {
		LogUtils.i(tag, "onPhotoCropped Uri in path: " + uri.getPath());
		photoPath = uri.getPath();
		if (!mCropParams.compress)
			mHeadView.setImageBitmap(EBikeViewUtils.getRoundBitmap(BitmapUtil.decodeUriAsBitmap(this, uri)));
	}

	@Override
	public void onCompressed(Uri uri) {
		LogUtils.i(tag, "onCompressed in path: " + uri.getPath());
		photoPath = uri.getPath();
		mHeadView.setImageBitmap(EBikeViewUtils.getRoundBitmap(BitmapUtil.decodeUriAsBitmap(this, uri)));
	}

	@Override
	public void onCancel() {
		LogUtils.i(tag, "pic onCancel");
	}

	@Override
	public void onFailed(String message) {
		LogUtils.i(tag, "pic onFailed");
	}

	@Override
	public void handleIntent(Intent intent, int requestCode) {
		LogUtils.i(tag, "pic handleIntent");
		startActivityForResult(intent, requestCode);
	}

	@Override
	public CropParams getCropParams() {
		return mCropParams;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(snValue!=null){
			if (!TextUtils.isEmpty(SPUtils.getEBkieAddress(this))) {
				snValue.setText(SPUtils.getEBkieName(this));
			} else {
				snValue.setText(getString(R.string.ble_not_bind));
			}
		}
	}
	
	
}
