package com.xy.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by xieying on 2019/4/4.
 * Description：
 */
public class AppInfoUtils {

    /**
     * 获取客户端版本名
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        context.getApplicationInfo();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取客户端版本号
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = -1;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获得packageName
     */
    public static String getPackageName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String packageName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            packageName = packageInfo.packageName + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }

    /**
     * 获取应用名称
     */
    public static String getAppName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String appName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            appName = packageInfo.applicationInfo.loadLabel(packageManager).toString() + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

}
