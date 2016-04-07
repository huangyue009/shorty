/**
 * Created with IntelliJ IDEA.
 * User: yue.huang
 * Date: 2015/7/10
 * Time: 19:19
 *
 */
package com.shorty.core.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.util.List;

/**
 * 杂项工具类
 * Created by yue.huang on 2015/7/10.
 */
public class AppUtils {
    private static Toast mToast;
    /**
     * 任意线程都可显示toast
     */
    public static void showToastInfo(final Context cxt, final String msg) {
        new Handler(cxt.getMainLooper()).post(new Runnable() {
            public void run() {
                if(mToast == null){
                    mToast = Toast.makeText(cxt, "", Toast.LENGTH_SHORT);
                }
                mToast.setText(msg);
                mToast.show();
            }
        });
    }


    /**
     * 应用是否在前台
     * @param context
     * @return
     */
    public static boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
//                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
//                    return false;
//                }else{
//                    KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//                    boolean isLock = mKeyguardManager.inKeyguardRestrictedInputMode();
//                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//                    return pm.isScreenOn() && !isLock;
//                }
            }
        }
        return false;
    }

    /**
     * 用来判断服务是否运行.
     * @param context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context context,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
//            Logger.i(serviceList.get(i).service.getClassName() + " = " + i);
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 获取当前进程名称
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
