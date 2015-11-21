package com.freway.ebike.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.freway.ebike.R;
import com.freway.ebike.bluetooth.BLEScanConnectActivity;
import com.freway.ebike.bluetooth.BlueToothUtil;
import com.freway.ebike.common.BaseActivity;
import com.freway.ebike.common.EBConstant;
import com.freway.ebike.crop.BitmapUtil;
import com.freway.ebike.crop.CropHandler;
import com.freway.ebike.crop.CropHelper;
import com.freway.ebike.crop.CropParams;
import com.freway.ebike.model.RspUserInfo;
import com.freway.ebike.model.User;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.service.UpdateAPPService;
import com.freway.ebike.service.UpdateAPPService.UpdateAppListener;
import com.freway.ebike.utils.AlertUtil;
import com.freway.ebike.utils.CommonUtil;
import com.freway.ebike.utils.EBkieViewUtils;
import com.freway.ebike.utils.FontUtil;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.SPUtils;
import com.freway.ebike.utils.ToastUtils;
import com.freway.ebike.view.HeadPicView;

public class SettingActivity extends BaseActivity implements OnClickListener, UpdateAppListener, CropHandler {

	private static final String GENDER_MAN = "man";
	private static final String GENDER_WOMEN = "women";

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
	private TextView aboutTitle;
	private TextView aoubtValue;
	private TextView exitTitle;

	private View snLL;
	private View languageLL;
	private View unitDistanceLL;
	private View aboutLL;
	private View exitLL;

	private CropParams mCropParams;
	private boolean isEdit = false;
	private User user;
	private boolean isNeed2Update = false;
	private String photoPath="";

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
		iconButton.setOnClickListener(this);
		genderView.setOnClickListener(this);
		mHeadView.setOnClickListener(this);
		snLL.setOnClickListener(this);
		languageLL.setOnClickListener(this);
		unitDistanceLL.setOnClickListener(this);
		aboutLL.setOnClickListener(this);
		exitLL.setOnClickListener(this);
	}

	private void initData() {
		mCropParams = new CropParams(this);
		mHeadView.setEnabled(false);
		iconButton.setImageResource(R.drawable.settings_confirm_red);
		iconButton.setVisibility(View.GONE);
		titleTv.setText(getString(R.string.settings) + "");
		rightButton.setText(getString(R.string.edit) + "");
		user = SPUtils.getUserProfile(this);
		updateUiUser(user);
		mEBikeRequestService.userInfo(SPUtils.getToken(this));
	}

	private void updateUiUser(User user) {
		if (user != null) {
			nameValue.setText(user.getUsername());
			emailValue.setText(user.getEmail());
			if (TextUtils.equals(GENDER_MAN, user.getGender())) {// 男
				genderView.setSelected(true);
			} else {
				genderView.setSelected(false);
			}
			ageValue.setText(user.getAge());
			heightValue.setText(user.getHeight());
			weightValue.setText(user.getWeight());
			mHeadView = (HeadPicView) findViewById(R.id.profile_head_view);
			EBkieViewUtils.displayPhoto(this, mHeadView, user.getPhoto());
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
			aoubtValue.setText(CommonUtil.getAppVersion(this));
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
			if (v.isSelected()) {//
				user.setGender(GENDER_MAN);
				v.setSelected(false);
			} else {
				user.setGender(GENDER_WOMEN);
				v.setSelected(true);
			}
			break;
		case R.id.profile_head_view:
			mCropParams.refreshUri();
			AlertUtil.getInstance(this).alertChoice(getString(R.string.portrait_settings), getString(R.string.album),
					getString(R.string.camera), new OnClickListener() {

						@Override
						public void onClick(View v) {
							// album
							AlertUtil.getInstance(SettingActivity.this).dismiss();
							mCropParams.enable = true;
							mCropParams.compress = false;
							Intent intent = CropHelper.buildGalleryIntent(mCropParams);
							startActivityForResult(intent, CropHelper.REQUEST_CROP);
						}
					}, new OnClickListener() {

						@Override
						public void onClick(View v) {
							// camera
							AlertUtil.getInstance(SettingActivity.this).dismiss();
							mCropParams.enable = true;
							mCropParams.compress = false;
							Intent intent = CropHelper.buildCameraIntent(mCropParams);
							startActivityForResult(intent, CropHelper.REQUEST_CAMERA);

						}
					});
			break;
		case R.id.setting_ll_sn:
			BlueToothUtil.toBindBleActivity(this, BLEScanConnectActivity.HANDLE_SCAN);
			break;
		case R.id.setting_ll_language:

			break;
		case R.id.setting_ll_unit_distance:
			AlertUtil.getInstance(this).alertChoice(getString(R.string.unit_distance_settings),
					getString(R.string.distance_unit_mph), getString(R.string.distance_unit_mi), new OnClickListener() {

						@Override
						public void onClick(View v) {
							// mph
							AlertUtil.getInstance(SettingActivity.this).dismiss();
							SPUtils.setUnitOfDistance(getApplicationContext(), EBConstant.DISTANCE_UNIT_MPH);
							unitDistanceValue.setText(getString(R.string.distance_unit_mph));
						}
					}, new OnClickListener() {

						@Override
						public void onClick(View v) {
							// mi
							AlertUtil.getInstance(SettingActivity.this).dismiss();
							SPUtils.setUnitOfDistance(getApplicationContext(), EBConstant.DISTANCE_UNIT_MI);
							unitDistanceValue.setText(getString(R.string.distance_unit_mi));
						}
					});
			break;
		case R.id.setting_ll_about:
			// if (isNeed2Update) {
			AlertUtil.getInstance(this).alertConfirm(getString(R.string.app_update_tip), getString(R.string.confirm),
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							AlertUtil.getInstance(SettingActivity.this).dismiss();
							updateApk();
						}
					});

			// } else {
			// ToastUtils.toast(SettingActivity.this,
			// getString(R.string.app_now_is_newly));
			// }

			break;
		case R.id.setting_ll_exit:
			finish();
			break;
		default:
			break;
		}
	}

	/** 版本更新 */
	private void updateApk() {
		final String downloadUrl = "http://www.saner5.com/index.aspx?appId=1&appDownLoadCount=55&appDownloadUrl=upload/app/2014_07_17_17_44_48ear.apk";
		ToastUtils.toast(SettingActivity.this, getString(R.string.start_download));
		aoubtValue.setText(getString(R.string.app_downloading));
		Intent intent = new Intent(UpdateAPPService.class.getName());
		intent.putExtra(UpdateAPPService.INTENT_DOWNLOAD_URL, downloadUrl);
		UpdateAPPService.setUpdateAppListener(SettingActivity.this);
		SettingActivity.this.startService(intent);
	}

	private void setEditState(boolean isEdit) {
		nameValue.setEnabled(isEdit);
		nameValue.requestFocus();
		emailValue.setEnabled(isEdit);
		ageValue.setEnabled(isEdit);
		heightValue.setEnabled(isEdit);
		weightValue.setEnabled(isEdit);
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
		if (TextUtils.isEmpty(email)) {
			ToastUtils.toast(this, getString(R.string.email) + "" + getString(R.string.can_not_be_null));
			return false;
		}
		if (!CommonUtil.isEmail(email)) {
			ToastUtils.toast(this, getString(R.string.email) + "" + getString(R.string.email_incorrect));
			return false;
		}
		user.setUsername(nickName);
		user.setEmail(email);
		user.setAge(age);
		user.setHeight(height);
		user.setWeight(weight);
		if (genderView.isSelected()) {
			user.setGender(GENDER_WOMEN);
		} else {
			user.setGender(GENDER_MAN);
		}
		SPUtils.setUserProfile(this, user);
		return isOk;
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		switch (id) {
		case EBikeRequestService.ID_UPDATEUSERINFO:
			SPUtils.setUserProfile(this, user);
			if(!TextUtils.isEmpty(photoPath)){
				mEBikeRequestService.updatePhoto(SPUtils.getToken(this), photoPath);
			}
			break;
		case EBikeRequestService.ID_USERINFO:
			RspUserInfo info = (RspUserInfo) obj;
			User user = info.getData();
			if (user != null) {
				updateUiUser(user);
				SPUtils.setUserProfile(this, user);
			}
			break;
		case EBikeRequestService.ID_PHOTO:
			ToastUtils.toast(getApplicationContext(), getString(R.string.photo_upload_success));
			break;
		default:
			break;
		}
	}

	@Override
	public void updateAppCompleted(String message) {
		if (SettingActivity.this != null && aoubtValue != null) {
			aoubtValue.setSelected(false);
			aoubtValue.setText(CommonUtil.getAppVersion(this));
			ToastUtils.toast(this, message);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		user = SPUtils.getUserProfile(this);
		updateUiUser(user);
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
		photoPath=uri.getPath();
		if (!mCropParams.compress)
			mHeadView.setImageBitmap(mHeadView.toRoundBitmap(BitmapUtil.decodeUriAsBitmap(this, uri)));
	}

	@Override
	public void onCompressed(Uri uri) {
		LogUtils.i(tag, "onCompressed in path: " + uri.getPath());
		photoPath=uri.getPath();
		mHeadView.setImageBitmap(mHeadView.toRoundBitmap(BitmapUtil.decodeUriAsBitmap(this, uri)));
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

}
