package com.freway.ebike.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * author: mengjiankang created on  2021/3/20
 */
public class MainThreadUtil {
    private final static Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void runOnMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public static void runOnMainThread(Runnable runnable, long delaySecondMills) {
        mainHandler.postDelayed(runnable, delaySecondMills);
    }
}
