package com.shorty.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.common.io.Files;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Vector;

/**
 *
 * Logger 控制类
 * @author yue.huang
 * @since 2016-05-23
 */
public class Logger {
    private static final String defaultTag = "Logger";
    public static final boolean DEBUG = true;
    public static final boolean PRINT_TO_FILE = true;
    private static String PACKAGE_NAME;
    private static String APP_VERSION;
    private static final String DEFAULT_PACKAGE_NAME = "/shorty";

    /**
     * 日志类别
     */
    public static enum LogType {
        // Log.d
        DEBUG,
        // Log.i
        INFO,
        // Log.w
        WARNING,
        // Log.e
        ERROR,
    }

    public static void init(Context context){
        PACKAGE_NAME = context.getPackageName();

        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(PACKAGE_NAME, 0);
            APP_VERSION = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void d(String msg){
        log(msg, LogType.DEBUG);
    }

    public static void i(String msg){
        log(msg, LogType.INFO);
    }

    public static void w(String msg){
        log(msg, LogType.WARNING);
    }

    public static void e(String msg){
        log(msg, LogType.ERROR);
    }

    public static void e(Throwable e){
        Throwable cause = e;
        StringBuffer sb = new StringBuffer();
        while (cause != null) {
            sb.append(cause.getMessage());
            cause = cause.getCause();
        }
        log(sb.toString(), LogType.ERROR);
        if(DEBUG) {
            e.printStackTrace();
        }
    }

    private static void log(String msg, LogType type) {
        switch (type) {
            case DEBUG: {
                if (DEBUG) {
                    Log.d(defaultTag, msg);
                }
                return;
            }
            case ERROR: {
                Log.e(defaultTag, msg);
                recordToFile(msg, true);
                break;
            }
            case INFO: {
                Log.i(defaultTag, msg);
                recordToFile(msg, false);
                break;
            }
            case WARNING: {
                Log.w(defaultTag, msg);
                recordToFile(msg, false);
                break;
            }
            default:
                return;
        }
    }

    private static void recordToFile(String msg, boolean isCrash) {
        if(!PRINT_TO_FILE){
            return;
        }

//        try {
//            msg = DateFormat.format("yyyy-MM-dd kk:mm:ss", System.currentTimeMillis()).toString()
//                    + ": "
//                    + getLogStatus()
//                    + ":" + msg + "\r\n";
//            String dir = Environment.getExternalStorageDirectory()
//                    + "/log" + (PACKAGE_NAME == null ? DEFAULT_PACKAGE_NAME : PACKAGE_NAME);
//            String fileName = android.os.Build.MODEL
//                    + "_"
//                    + APP_VERSION
//                    + "_"
//                    + android.os.Build.VERSION.RELEASE
//                    + "_"
//                    + DateFormat.format("MMddkk", System.currentTimeMillis()).toString() + ".log";
//
//            Files.append(msg, new File(dir, fileName), Charset.defaultCharset());
//
//            if(isCrash){
//                Files.append(msg, new File(dir, "crash_" + fileName), Charset.defaultCharset());
//            }
//        }catch (Exception e){
////            e.printStackTrace();
//        }
    }

    private static String getLogStatus() {
        StackTraceElement[] stes = new Throwable().getStackTrace();
        if (stes != null && stes.length >= 4) {
            StackTraceElement caller = stes[3];
            return  caller.getFileName() + "(" + caller.getLineNumber() + ")";
        } else {
            return defaultTag;
        }
    }
}