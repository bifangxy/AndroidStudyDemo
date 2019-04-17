package com.xy.common.utils.permission;

/**
 * Created by xieying on 2019/4/17.
 * Description：
 */
public interface PermissionCallback {

    void onPermissionGrated();

    void onPermissionDenied();
}
