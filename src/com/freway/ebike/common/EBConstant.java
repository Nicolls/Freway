package com.freway.ebike.common;

/** 常量类 */
public class EBConstant {

	/**存储文件夹*/
	public static final String DIR_FREWAY="/freway";
	/**第三方登录分享*/
	public static final int STATE_UN_LOGIN = 0;
	public static final int STATE_LOGIN_SUCCESS = 1;
	public static final int STATE_LOGIN_FAIL = 2;
	public static final int STATE_LOGIN_ED = 3;
	public static final int STATE_SHARE_SUCCESS = 4;
	public static final int STATE_SHARE_FAIL = 5;
	/**UI*/
	public static final int MODEL_DAY = 0;
	public static final int MODEL_NIGHT = 1;

	public static final int DISTANCE_UNIT_MPH = 0;
	public static final int DISTANCE_UNIT_KM = 1;
	
	public static final int GEAR0 = 0;
	public static final int GEAR1 = 1;
	public static final int GEAR2 = 2;
	public static final int GEAR3 = 3;
	
	public static final int ON=1;
	public static final int OFF=0;
	
	public static final int WHEEL_VALUE = 2180;
	//登录类型
	public static final int SIGN_NORMAL=0;
	public static final int SIGN_FACE_BOOK=1;
	public static final int SIGN_TWITTER=2;

	
	/**工作模式*/
	public static final int WORK_BLUETOOTH=0;//工作模式为蓝牙控制器，默认
	public static final int WORK_MAP=1;//工作模式为地图，由地图来提供数据
	/** 客户端类型 */
	public static final String CLIENT_TYPE = "EB Android";
	
	/**登录类型*/
	public static final int LOGIN_TYPE_NORMAL=0;
	public static final int LOGIN_TYPE_FACEBOOK=1;
	public static final int LOGIN_TYPE_TWITTER=2;
	
	/** 概览页面资源利用 率html */
	public static final String APP_DOWNLOAD_APK_NAME = "eBike.apk";
	public static final String DEFAULT_HOST="app.ifreway.com/index.php";
	public static final int DEFAULT_PORT = 0;
	public static final String DEFAULT_KEY="tESTfORaPP2015";//私钥
	/** 统一时间格式化 */
	public static final String TIME_FORMAT_STYLE = "yyyy-MM-dd HH:mm:ss";

	/**统一通过startActivityForResult返回成功为result_code_ok=0*/
	public static final int ACTIVITY_START_FOR_RESULT_CODE_OK = 0;
	/**统一通过startActivityForResult返回失败为result_code_fail*/
	public static final int ACTIVITY_START_FOR_RESULT_CODE_FAIL = -1;
	/** 打开蓝牙 */
	public static final int ACTIVITY_START_FOR_RESULT_ENABLE_BLUETOOTH = 1001;

	/** Activity之间传递的Intent 名称 */
	public class IntentExtra {
		// 验证服务器节点
		public static final int SEVERCONFIG_VERIFY_STATE_SUCCESS = 1;
		public static final int SEVERCONFIG_VERIFY_STATE_FAILURE = 0;
		public static final String EXTRA_SEVERCONFIG_VERIFY_STATE = "SEVERCONFIG_VERIFY_STATE";

		/** ListView传弟Item */
		public static final String EXTRA_LIST_VIEW_ITEM_DATA = "EXTRA_LIST_VIEW_ITEM_DATA";
		/** ListView传弟Item data type */
		public static final String EXTRA_LIST_VIEW_ITEM_DATA_TYPE = "EXTRA_LIST_VIEW_ITEM_DATA_TYPE";
		/** parentID */
		public static final String EXTRA_PARENT_ID = "EXTRA_PARENT_ID";
	}

	/**启动Activity,service,broadcast等的action
	 * 如果修改了这里也要修改manifext.xm文件
	 * */
	public class ContextAction{
		/**更新app服务*/
		public static final String ACTION_SERVICE_UPDATEAPP_SERVICE="com.dawning.gridview.service.UpdateAPPService";
		/**蓝牙服务*/
		public static final String ACTION_SERVICE_BLUETOOTH="com.freway.bluetooth.BlueToothService";
		/**蓝牙状态*/
		public static final String ACTION_BROADCAST_BLUETOOTH_STATE="com.freway.bluetooth.BlueToothStatusReceiver";
		/**蓝牙数据更新*/
		public static final String ACTION_BROADCAST_BLUETOOTH_DATE="com.freway.bluetooth.BlueToothDeviceDataReceiver";

	}
	
	//个人页面的html5的URL连接
	public static final String HTML5_URL_RECORDS="http://"+DEFAULT_HOST+"/history";
	public static final String HTML5_URL_GRADES="http://"+DEFAULT_HOST+"/grades";
	public static final String HTML5_URL_NEWS="http://"+DEFAULT_HOST+"/news";
	public static final String HTML5_URL_TUTORIAL="http://"+DEFAULT_HOST+"/tutorial";
	
}
