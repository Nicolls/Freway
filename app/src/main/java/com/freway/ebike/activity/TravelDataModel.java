package com.freway.ebike.activity;

import android.util.Log;

import com.freway.ebike.common.EBConstant;
import com.freway.ebike.utils.LogUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;

public class TravelDataModel implements Serializable {
    /**
     * @Fields serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private static final String TAG = "EBikeTravelData";

    public interface TravelChangedCallBack {
        void onChanged();
    }

    public enum TravelState {
        /**
         * 待骑行
         */
        NONE,
        /**
         * 正在骑行
         */
        RUN,
        /**
         * 骑行暂停
         */
        PAUSE
    }

    private static final TravelDataModel instance = new TravelDataModel();

    public static TravelDataModel getInstance(){
        return instance;
    }

    /**
     * 骑行状态
     */
    public TravelState travelState = TravelState.NONE;

    /**
     * UI模式
     */
    public int uiModel = EBConstant.MODEL_DAY;

    /**
     * 蓝牙连接状态
     */
    public boolean bluetoothConnect = false;

    /**
     * @Fields startTime 开始时间 单位毫秒
     */
    public long startTime;
    /**
     * @Fields endTime 结束时间 单位毫秒
     */
    public long endTime;
    /**
     * @Fields avgSpeed 平均速度 单位km/h
     */
    public float avgSpeed;
    /**
     * @Fields insSpeed 瞬时速度 单位km/h
     */
    public float insSpeed;
    /**
     * @Fields maxSpeed 最大速度 单位km/h
     */
    public float maxSpeed;
    /**
     * @Fields spendTime 行程时间单位秒
     */
    public long spendTime;
    /**
     * @Fields distance 行程距离 单位 km
     */
    public float distance;
    /**
     * @Fields calorie 卡路里 单位cal
     */
    public float calorie;

    /**
     * @Fields cadence 踏频量（圈）单位 圈/每分钟
     */
    public float cadence;
    /**
     * @Fields altitude 海拔 单位km
     */
    public double altitude;
    /**
     * @fields backLed 后灯状态
     */
    public int backLed;
    /**
     * @fields frontLed 前灯状态
     */
    public int frontLed;
    /**
     * @fields biking_state_change 骑行状态改变标志，也就是档位0运动，1电动2助力1.3助力2.4助力3
     */
    public int gear;

    /**
     * @fields batteryResidueCapacity 剩余容量 单位%
     */
    public int batteryResidueCapacity;

    /**
     * @fields miCapacity 剩余里程km
     */
    public float remaindTravelCapacity;
    /**
     * 计时线程
     */
    private TravelThread travelThread = null;
    /**
     * 通知骑行数据变化，让UI更新
     */
    private TravelChangedCallBack travelChangedCallBack;

    /**
     * 构造
     */
    private TravelDataModel() {
    }

    public void setTravelChangedCallBack(TravelChangedCallBack travelChangedCallBack){
        this.travelChangedCallBack = travelChangedCallBack;
    }

    /**
     * 初始化
     */
    public void init() {
        resetData();
        travelChangedCallBack.onChanged();
    }

    private void resetData() {
        spendTime = 0;
        insSpeed = 0;
        avgSpeed = 0;
        maxSpeed = 0;
        distance = 0;
        calorie = 0;
        cadence = 0;
        altitude = 0;
        startTime = Calendar.getInstance().getTimeInMillis();
        endTime = startTime;
    }

    /**
     * 开始骑行
     */
    public void start() {
        resetData();
        travelState = TravelState.RUN;
        if (travelThread == null) {
            travelThread = new TravelThread();
            travelThread.start();
        }
    }

    /**
     * 暂停骑行
     */
    public void pause() {
        insSpeed = 0;
        travelState = TravelState.PAUSE;
        if (travelThread != null) {
            travelThread.pause();
        }
        travelChangedCallBack.onChanged();
    }

    /**
     * 恢复骑行
     */
    public void resume() {
        travelState = TravelState.RUN;
        if (travelThread != null) {
            travelThread.reStart();
        }
    }

    /**
     * 完成骑行
     */
    public void completed(boolean saveData) {
        if (travelThread != null) {
            travelThread.cancel();
            travelThread = null;
        }
        resetData();
        travelState = TravelState.NONE;
        travelChangedCallBack.onChanged();
    }

    public void setBlueToothConnect(boolean connect) {
        this.bluetoothConnect = connect;
        if (!connect) {
            clearBlueToothData();
        }
        travelChangedCallBack.onChanged();
    }

    public void setUiMode(int mode) {
        this.uiModel = mode;
        travelChangedCallBack.onChanged();
    }

    /**
     * Activity退出
     */
    public void exit() {
        completed(false);
    }

    private void clearBlueToothData() {
        if (bluetoothConnect) {
            return;
        }
        cadence = 0;
        frontLed = 0;
        gear = 0;
        batteryResidueCapacity = 0;
        remaindTravelCapacity = 0;
    }

    /**
     * 时间线程
     */
    private class TravelThread extends Thread {
        private boolean cancel = false;
        private boolean pause = false;

        public void run() {
            LogUtils.i(TAG, "thread start");
            setName("TravelThread");
            cancel = false;
            pause = false;
            while (!cancel) { // 用cancel来做行程结束标识
                Log.d(TAG, "travel go on pause:" + pause + " spendTime:" + spendTime);
                if (!pause) { // 用pause来做行程暂停标识
                    spendTime += 1;
                    // 生产数据
                    simulateTravel(spendTime);
                    travelChangedCallBack.onChanged();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            cancel = true;
        }

        public void pause() {
            pause = true;
        }

        public void reStart() {
            pause = false;
        }
    }

    private void simulateTravel(long spendTime) {
        Random random = new Random();
        avgSpeed = random.nextInt(30) + 5;
        insSpeed = random.nextInt(6) + avgSpeed;
        distance = 0.5f + spendTime * 0.01f;
        calorie = 60;
        cadence = 30;
        altitude = 1.2;
        frontLed = 1;
        gear = 1;
        batteryResidueCapacity = 60;
        remaindTravelCapacity = 35;
        if (!bluetoothConnect) {
            clearBlueToothData();
        }
    }

}
