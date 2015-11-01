package com.freway.ebike.map;

import com.google.android.gms.maps.LocationSource;

import android.content.Context;
import android.location.Location;

/**
 * 用于自定义定位
 */
public class CustomerLocationSource implements LocationSource {
	private static final String TAG="";
	private OnLocationChangedListener mListener;
	private Context context;
	/**
	 * Flag to keep track of the activity's lifecycle. This is not strictly
	 * necessary in this case because onMapLongPress events don't occur
	 * while the activity containing the map is paused but is included to
	 * demonstrate best practices (e.g., if a background service were to be
	 * used).
	 */
	private boolean mPaused;

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
	}

	@Override
	public void deactivate() {
		mListener = null;
	}

	public void changeLocation(Location location) {
		if (mListener != null && !mPaused) {
			mListener.onLocationChanged(location);
		}
	}

	public void onPause() {
		mPaused = true;
	}

	public void onResume() {
		mPaused = false;
	}
}