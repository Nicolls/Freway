/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freway.ebike.bluetooth;


/**
 * Defines several constants used between {@link BluetoothConnection} and the UI.
 */
public interface BlueToothConstants {
	
	public static final int RESULT_SUCCESS=0;
	public static final int RESULT_FAIL=-1;
	public static final int RESULT_COMPLETED=1;
    /**
     * @Fields BLUETOOTH_ACTION_HANDLE_SERVER 控制服务
     */
    public static final String BLUETOOTH_ACTION_HANDLE_SERVER="BLUETOOTH_ACTION_HANDLE_SERVER";
    
    /**
     * @Fields BLUETOOTH_ACTION_HANDLE_EXTRA 操作类型
     */
    public static final String EXTRA_HANDLE_TYPE="EXTRA_HANDLE_TYPE";
    /**
     * @Fields BLUETOOTH_ACTION_HANDLE_EXTRA 操作传入的参数
     */
    public static final String EXTRA_DATA="EXTRA_DATA";
    /**
     * @Fields BLUETOOTH_ACTION_HANDLE_EXTRA 操作传入的参数
     */
    public static final String EXTRA_STATUS="EXTRA_STATUS";
    /**
     * @Fields HANDLE_SERVER_SCAN 扫描
     */
    public static final int HANDLE_SERVER_SCAN=1;
    
    /**
     * @Fields HANDLE_SERVER_CONNECT 链接
     */
    public static final int HANDLE_SERVER_CONNECT=2;
    /**
     * @Fields HANDLE_SERVER_CONNECT 同步
     */
    public static final int HANDLE_SERVER_SYNC=3;
    /**
     * @Fields HANDLE_SERVER_SEND_DATA 发送数据
     */
    public static final int HANDLE_SERVER_SEND_DATA=4;
    /**
     * @Fields HANDLE_SERVER_SEND_DATA 断开蓝牙链接
     */
    public static final int HANDLE_SERVER_DISCONNECT=5;
    /**
     * @Fields BLUETOOTH_ACTION_HANDLE_SERVER 控制服务扫描结果返回
     */
    public static final String BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SCAN_DEVICE="BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SCAN_DEVICE";
    /**
     * @Fields BLUETOOTH_ACTION_HANDLE_SERVER 控制服务发送数据结果返回
     */
    public static final String BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SEND_DATA="BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SEND_DATA";
    /**
     * @Fields BLUETOOTH_ACTION_HANDLE_SERVER 控制服务同步数据结果返回
     */
    public static final String BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SYNC_DATA="BLUETOOTH_ACTION_HANDLE_SERVER_RESULT_SYNC_DATA";
    
    public static final int SYNC_START = 0;//同步开始
    public static final int SYNC_END = 1;//同步结束
    public static final int SYNC_ERROR = -1;//同步出问题
    /**读取下一条历史数据action*/
    public static final String BLUETOOTH_ACTION_HISTORY_DATA_NEXT="BLUETOOTH_ACTION_HISTORY_DATA_NEXT";
    
    /**蓝牙状态改变*/
    public static final String BLE_SERVER_STATE_CHANAGE="BLUETOOTH_SERVER_STATE_CHANAGE";
	public static final String EXTRA_STATE = "EXTRA_STATE";
    public static final int BLE_STATE_NONE = 0;
	public static final int BLE_STATE_CONNECTTING = 1;
	public static final int BLE_STATE_CONNECTED = 2;
	public static final int BLE_STATE_DISCONNECTED = 3;
	
	
}