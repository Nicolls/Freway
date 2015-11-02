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

import com.freway.ebike.protocol.ProtocolByteHandler;

/**
 * Defines several constants used between {@link BluetoothConnection} and the UI.
 */
public interface BlueToothConstants {
	

    /**
     * @Fields BLUETOOTH_ACTION_HANDLE_SERVER 控制服务
     */
    public static final String BLUETOOTH_ACTION_HANDLE_SERVER="BLUETOOTH_ACTION_HANDLE_SERVER";
    
    /**
     * @Fields BLUETOOTH_ACTION_HANDLE_EXTRA 操作值
     */
    public static final String BLUETOOTH_ACTION_HANDLE_EXTRA_FLAG="BLUETOOTH_ACTION_HANDLE_EXTRA_FLAG";
    /**
     * @Fields BLUETOOTH_ACTION_HANDLE_EXTRA 操作传入的参数
     */
    public static final String BLUETOOTH_ACTION_HANDLE_EXTRA_DATA="BLUETOOTH_ACTION_HANDLE_EXTRA_DATA";
   
    /**
     * @Fields HANDLE_SERVER_SCAN 扫描
     */
    public static final int HANDLE_SERVER_SCAN=1;
    
    /**
     * @Fields HANDLE_SERVER_CONNECT 链接
     */
    public static final int HANDLE_SERVER_CONNECT=2;
    
    /**
     * @Fields BLUETOOTH_ACTION_SERVER_SEND_DATA 发送数据
     */
    public static final String BLUETOOTH_ACTION_SERVER_SEND_DATA="BLUETOOTH_ACTION_SERVER_SEND_DATA";
    /**
     * @Fields BLUETOOTH_ACTION_SERVER_DATA 服务返回扫描设备得到的数据广播
     */
    public static final String BLUETOOTH_ACTION_SERVER_SCAN_RESULT="BLUETOOTH_ACTION_SERVER_SCAN_RESULT";
    /**
     * @Fields BLUETOOTH_SERVER_EXTRA_ADDRESS 服务扫描到的设备
     */
    public static final String BLUETOOTH_SERVER_EXTRA_DEVICE="BLUETOOTH_SERVERee_EXTRA_DEVICE";
    /**
     * @Fields BLUETOOTH_ACTION_SERVER_SEND_RESULT 服务发送数据，后返回的数据
     */
    public static final String BLUETOOTH_ACTION_SERVER_SEND_RESULT="BLUETOOTH_ACTION_SERVER_SEND_RESULT";
    
    /**
     * @Fields BLUETOOTH_SERVER_EXTRA_DATA 打包发送的数据，是一个hashmap，其中有，ProtocolByteHandler.EXTRA_CMD,ProtocolByteHandler.EXTRA_DATA的值
     */
    public static final String BLUETOOTH_SERVER_EXTRA_DATA="BLUETOOTH_SERVER_EXTRA_DATA";
    
    
    public static final String BLE_SERVER_STATE_CHANAGE="BLUETOOTH_SERVER_STATE_CHANAGE";
    public static final int BLE_STATE_NONE = 0;
	public static final int BLE_STATE_CONNECTTING = 1;
	public static final int BLE_STATE_CONNECTED = 2;
	public static final int BLE_STATE_DISCONNECTED = 3;
	public static final String EXTRA_STATE = "EXTRA_STATE";
}
