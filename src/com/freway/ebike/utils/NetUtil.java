package com.freway.ebike.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.freway.ebike.R;
import com.freway.ebike.db.DBHelper;
import com.freway.ebike.db.Travel;
import com.freway.ebike.db.TravelLocation;
import com.freway.ebike.db.TravelSpeed;
import com.freway.ebike.map.TravelConstant;
import com.freway.ebike.model.EBErrorResponse;
import com.freway.ebike.model.EBResponse;
import com.freway.ebike.net.DataUpdateListener;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.net.EBikeRequestServiceFactory;
import com.google.gson.Gson;

import android.content.Context;
import android.text.TextUtils;

public class NetUtil implements DataUpdateListener{

	private static final String TAG=NetUtil.class.getSimpleName();
	private Context context;
	private List<Travel> travelList;
	private int index=0;
	private EBikeRequestService mEBikeRequestService;
	private static Gson gson=new Gson();
	public NetUtil(Context context){
		this.context=context;
		mEBikeRequestService = EBikeRequestServiceFactory.getInstance(context,
				EBikeRequestServiceFactory.REQUEST_VOLLEY);
		mEBikeRequestService.setUptateListener(this);
	}
	/**上传本地行程记录*/
	public void uploadLocalRecord(){
		index=0;
		travelList=DBHelper.getInstance(context).listTravelUnSync();
		upLoadTravel();
	}
	//上传
	private void upLoadTravel(){
		if(index<travelList.size()){
			Travel travel=travelList.get(index);
			List<TravelLocation> locations=DBHelper.getInstance(context).listTravelLocation(travel.getId());
			List<TravelSpeed> speeds=DBHelper.getInstance(context).listTravelSpeed(travel.getId());
			List<String[]> loList=new ArrayList<String[]>();
			for(TravelLocation location:locations){
				//[["x1","y1"],["x2","y2"],["x3","y3"]]
				String[] temp=new String[2];
				temp[0]=location.getLocation().getLatitude()+"";
				temp[1]=location.getLocation().getLongitude()+"";
				loList.add(temp);
			}
			String locationList=gson.toJson(loList);
			float[]spList=new float[speeds.size()];
			for(int i=0;i<speeds.size();i++){
				spList[i]=speeds.get(i).getSpeed();
			}
			String speedList=gson.toJson(spList);
			LogUtils.i(TAG, "要上传的行程是："+travel.toString());
			mEBikeRequestService.upLoadTravel(SPUtils.getToken(context), travel.getType()+"",travel.formatTime(new Date(travel.getStartTime()))+"", travel.formatTime(new Date(travel.getEndTime()))+"", travel.getDistance()+"", travel.getSpendTime()+"", travel.getCadence()+"", travel.getCalorie()+"", speedList, locationList, travel.getMaxSpeed()+"",travel.getAvgSpeed()+"");
		}
	}
	
	@Override
	public void update(int id, Object obj) {
		if (id == EBikeRequestService.ID_REQUEST_ERROR) {
			ToastUtils.toast(context, context.getString(R.string.http_request_error));
			return;
		} else if (obj instanceof EBErrorResponse) {// 登录或者请求出问题
			EBErrorResponse errorRes = (EBErrorResponse) obj;
			return;
		} else if(!TextUtils.equals(((EBResponse) obj).getCode(),EBResponse.SUCCESS_CODE)) {// 说明有错误
			ToastUtils.toast(context, ((EBResponse) obj).getMsg());
			return;
		}
		
		switch(id){
		case EBikeRequestService.ID_UPLOADTRAVEL:
			Travel travel=travelList.get(index);
			travel.setSync(TravelConstant.TRAVEL_SYNC_TRUE);
			DBHelper.getInstance(context).updateTravel(travel);
			index++;
			upLoadTravel();
			break;
			default:
				break;
		}
	}
}
