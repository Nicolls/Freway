/**
 * 
 */
package com.freway.ebike.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freway.ebike.net.DataUpdateListener;
import com.freway.ebike.net.EBikeRequestService;
import com.freway.ebike.net.EBikeRequestServiceFactory;
import com.freway.ebike.utils.ErrorUtils;
import com.freway.ebike.utils.LogUtils;
import com.freway.ebike.utils.ErrorUtils.ErrorListener;
import com.freway.ebike.utils.ErrorUtils.SuccessListener;

/**
 * Fragment基类
 * 
 * @author mengjk
 *
 *         2015年5月19日
 */
public abstract class BaseFragment extends Fragment implements DataUpdateListener {
	protected EBikeRequestService mGVRequestService;
	private String name;

	public BaseFragment(String name) {
		this.name = name;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGVRequestService = EBikeRequestServiceFactory.getInstance(getActivity()
				.getApplicationContext(), EBikeRequestServiceFactory.REQUEST_VOLLEY);
		LogUtils.i("setUpdateListener", this.getClass().getName());
		mGVRequestService.setUptateListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void update(int id, Object obj) {
		ErrorUtils.handle(getActivity(), id, obj, new SuccessListener() {

			@Override
			public void successCompleted(int id, Object obj) {
				dateUpdate(id, obj);
			}
		},new ErrorListener() {
			
			@Override
			public void errorCompleted() {
				((BaseActivity)getActivity()).hideLoading();
			}
		});
	}

	/** 抽象方法，用来通知fragment数据已请求回来 */
	public abstract void dateUpdate(int id, Object obj);
}
