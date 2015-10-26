/**
 * 
 */
package com.freway.ebike.listener;

import android.content.Intent;

/**
 * 
 * 打开activity带返回值监听类
 * @author mengjk
 *
 * 2015年9月30日
 */
public interface OpenActivityResultListener {
	void onOpenActivityResult(int requestCode, int resultCode, Intent data);
}
