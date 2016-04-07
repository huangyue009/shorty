package com.shorty.core.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.*;
import java.util.Vector;

/**
 * Logger 控制类
 *
 * @author yue.huang
 * @since 2015-04-27
 */
public class Logger {
    private static final String defaultTag = "Logger";
    public static final boolean DEBUG = false;

    public static class LogContext {
        String fileName;
        int lineNumber;

        public String getTag() {
            return fileName + " (" + lineNumber + ")";
        }
    }

    /**
     * 日志类别
     */
    public static enum LogType {
        // Log.v
        VERBOSE,
        // Log.d
        DEBUG,
        // Log.i
        INFO,
        // Log.w
        WARNING,
        // Log.e
        ERROR,
        // Log.f
        FILE;
    }

    /**
     * 记录一条Log.v
     *
     * @param msg
     */
    public static void v(String msg) {
        log(LogType.VERBOSE, null, msg);
    }

    /**
     * 记录一条Log.d
     *
     * @param msg
     */
    public static void d(String msg) {
        log(LogType.DEBUG, null, msg);
    }

    /**
     * 记录一条信息
     *
     * @param msg
     */
    public static void i(String msg) {
        log(LogType.INFO, null, msg);
    }

    /**
     * 记录一条警告日志
     *
     * @param msg
     */
    public static void w(String msg) {
        log(LogType.WARNING, null, msg);
    }

    /**
     * 记录一条警告异常
     *
     * @param throwable
     */
    public static void w(Throwable throwable) {
        log(LogType.WARNING, null, throwable);
    }

    /**
     * 记录一条警告异常
     *
     * @param throwable
     */
    public static void w(String msg, Throwable throwable) {
        log(LogType.WARNING, msg, throwable);
    }

    /**
     * 记录一条错误信息
     *
     * @param msg
     */
    public static void e(String msg) {
        log(LogType.ERROR, null, msg);
    }

    /**
     * 记录一条异常日志
     *
     * @param throwable
     */
    public static void e(Throwable throwable) {
        try {
            throwable.printStackTrace();
        } catch (Exception e) {

        }

    }

    /**
     * 记录一条异常日志
     *
     * @param throwable
     */
    public static void e(String msg, Throwable throwable) {
        log(LogType.ERROR, msg, throwable);
    }

    /**
     * 记录一条异常日志
     *
     * @param msg
     */
    public static void f(String msg) {
//        long ftime = System.currentTimeMillis();
//        log(LogType.FILE, null, msg + ":t=" + DateUtil.getHMS(ftime) + ":"
//                + ftime + "\n");
    }

    /**
     * 记录一条异常日志
     *
     * @param type
     * @param throwable
     */
    private static void log(LogType type, String msg, Throwable throwable) {
        if (!TextUtils.isEmpty(msg)) {
            LogContext context = getLogStatus();
            log(type, context, msg);
        }

        if (throwable != null) {
            if (true) {
                throwable.printStackTrace();
                return;
            }
        }
    }

    /**
     * 记录一条日志
     *
     * @param type    日志的类别
     * @param context 日志上下文
     * @param msg     日志内容
     */
    private synchronized static void log(LogType type, LogContext context, String msg) {
        if (msg == null) {
            msg = "null";
        }
        if (context == null) {
            context = getLogStatus();
        }
        switch (type) {
            case DEBUG: {
                if (DEBUG) {
                    Log.d(context.getTag(), msg);
                }
                break;
            }
            case ERROR: {
                Log.e(context.getTag(), msg);
                break;
            }
            case INFO: {
                Log.i(context.getTag(), msg);
                break;
            }
            case VERBOSE: {
                Log.v(context.getTag(), msg);
                break;
            }
            case WARNING: {
                Log.w(context.getTag(), msg);
                break;
            }
            default:
                break;
        }
    }

    private static LogContext getLogStatus() {
        LogContext logStatus = new LogContext();
        StackTraceElement[] stes = new Throwable().getStackTrace();
        if (stes != null && stes.length >= 4) {
            StackTraceElement caller = stes[3];
            logStatus.fileName = caller.getFileName();
            logStatus.lineNumber = caller.getLineNumber();
        } else {
            logStatus.fileName = defaultTag;
        }
        return logStatus;
    }

    public interface OnLogAppender {
        public void onLogAppend(String log);
    }



}