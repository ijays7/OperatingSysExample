package com.ijays.operatonsysexample.utils;

import android.app.ActivityManager;
import android.content.Context;


/**
 * Created by ijaysdev on 16/5/13.
 */
public class Utils {
    /**
     * 获取当前进程的进程名
     * @param context
     * @return
     */
    public static String getProcessName(Context context,int processId) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appInfo : activityManager.getRunningAppProcesses()) {
            if (appInfo.pid == processId) {
                return appInfo.processName;
            } else {
                return null;
            }
        }
        return null;
    }
}
