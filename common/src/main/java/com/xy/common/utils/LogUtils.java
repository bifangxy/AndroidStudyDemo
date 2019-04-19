package com.xy.common.utils;

import android.util.Log;

/**
 * Created by xieying on 2019/4/4.
 * Description：Log日志工具类
 * 需要在Application中调用debugMode()，获取当前为Debug还是Release
 */
public class LogUtils {

    private static final int LEVEL = Log.DEBUG;

    private static Boolean isDebug;

    private static boolean isDebug() {
        return isDebug == null ? true : isDebug;
    }

    public static void debugMode() {
//        if (isDebug == null) {
//            isDebug = MyApplication.getContext().getApplicationInfo() != null &&
//                    (MyApplication.getContext().getApplicationInfo().flags &
//                            ApplicationInfo.FLAG_DEBUGGABLE) != 0;
//        }
    }

    private static boolean isDebug(int level) {
        return level >= LEVEL && isDebug();
    }

    private static void println(String tag, int level, String msg) {
        if (isDebug(level)) {
            Log.println(level, tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        println(tag, Log.DEBUG, msg);
    }

    public static void e(String tag, String msg) {
        println(tag, Log.ERROR, msg);
    }

    public static void w(String tag, String msg) {
        println(tag, Log.WARN, msg);
    }

    public static void i(String tag, String msg) {
        println(tag, Log.INFO, msg);
    }


    public static void d(String msg) {
        println(getTag(), Log.DEBUG, msg);
    }

    public static void e(String msg) {
        println(getTag(), Log.ERROR, msg);
    }

    public static void w(String msg) {
        println(getTag(), Log.WARN, msg);
    }

    public static void i(String msg) {
        println(getTag(), Log.INFO, msg);
    }

    private static String getTag() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = caller.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName
                .lastIndexOf(".") + 1);
        String methodName = caller.getMethodName();
        int lineNumber = caller.getLineNumber();
        return callerClazzName + "#" + methodName + "(line:" + lineNumber + ")";
    }


}
