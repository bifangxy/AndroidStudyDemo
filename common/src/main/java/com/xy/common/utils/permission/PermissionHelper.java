package com.xy.common.utils.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.xy.common.utils.permission.PermissionHelperPermissionsDispatcher.showCameraGroupWithPermissionCheck;
import static com.xy.common.utils.permission.PermissionHelperPermissionsDispatcher.showCameraWithPermissionCheck;
import static com.xy.common.utils.permission.PermissionHelperPermissionsDispatcher.showLocationWithPermissionCheck;
import static com.xy.common.utils.permission.PermissionHelperPermissionsDispatcher.showReadPhoneStateWithPermissionCheck;
import static com.xy.common.utils.permission.PermissionHelperPermissionsDispatcher.showRecordWithPermissionCheck;
import static com.xy.common.utils.permission.PermissionHelperPermissionsDispatcher.showStorageWithPermissionCheck;

/**
 * Created by xieying on 2019/4/18.
 * Description：
 */
@RuntimePermissions
public class PermissionHelper extends Fragment {
    private static final String TAG = PermissionHelper.class.getSimpleName();

    private static final String CAMERA = Manifest.permission.CAMERA;
    private static final String RECORD = Manifest.permission.RECORD_AUDIO;
    private static final String R_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String W_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String R_PHONE = Manifest.permission.READ_PHONE_STATE;
    private static final String LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private PermissionCallback mPermissionCallback;
    private Permission mPermission;
    private String mMessage;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelperPermissionsDispatcher.onRequestPermissionsResult(
                this,requestCode,grantResults);
    }

    private void callbackPermissionGranted(){
        if(mPermissionCallback!=null)
            mPermissionCallback.onPermissionGranted();
    }

    private void callBackPermissionDenied(){
        if(mPermissionCallback!=null)
            mPermissionCallback.onPermissionDenied();
    }

    private void getPermission() {
        if (mPermission == null)
            return;
        switch (mPermission) {
            case CAMERA:
                getCameraPermission();
                break;
            case CAMERA_GROUP:
                getCameraGroupPermission();
                break;
            case PHONE:
                getReadPhoneStatePermission();
                break;
            case STORAGE:
                getStoragePermission();
                break;
            case LOCATION:
                getLocationPermission();
                break;
            case RECORD:
                getRecordPermission();
                break;
            default:
                break;

        }

    }

    void getCameraPermission() {
        showCameraWithPermissionCheck(this);
    }

    void getCameraGroupPermission() {
        showCameraGroupWithPermissionCheck(this);
    }

    void getReadPhoneStatePermission() {
        showReadPhoneStateWithPermissionCheck(this);
    }

    void getStoragePermission() {
        showStorageWithPermissionCheck(this);
    }

    void getLocationPermission() {
        showLocationWithPermissionCheck(this);
    }

    void getRecordPermission() {
        showRecordWithPermissionCheck(this);
    }

    //相机
    @NeedsPermission(CAMERA)
    void showCamera() {
        callbackPermissionGranted();

    }

    @OnShowRationale(CAMERA)
    void showRationaleForCamera(PermissionRequest request) {

    }

    @OnPermissionDenied(CAMERA)
    void showDeniedForCamera() {

    }

    @OnNeverAskAgain(CAMERA)
    void showNeverAskForCamera() {

    }


    //拍照,录像权限组
    @NeedsPermission({CAMERA, R_STORAGE, W_STORAGE, RECORD})
    void showCameraGroup() {
        callbackPermissionGranted();
    }

    @OnShowRationale({CAMERA, R_STORAGE, W_STORAGE, RECORD})
    void showRationaleForCameraGroup(PermissionRequest reques) {

    }

    @OnPermissionDenied({CAMERA, R_STORAGE, W_STORAGE, RECORD})
    void showDeniedForCameraGroup() {

    }

    @OnNeverAskAgain({CAMERA, R_STORAGE, W_STORAGE, RECORD})
    void showNeverAskForCameraGroup() {

    }

    //读取手机状态
    @NeedsPermission(R_PHONE)
    void showReadPhoneState() {
        callbackPermissionGranted();

    }

    @OnShowRationale(R_PHONE)
    void showRationaleForReadPhoneState(PermissionRequest reques) {

    }

    @OnPermissionDenied(R_PHONE)
    void showDeniedForReadPhoneState() {

    }

    @OnNeverAskAgain(R_PHONE)
    void showNeverAskForReadPhoneState() {

    }

    //读写存储权限

    @NeedsPermission({R_STORAGE, W_STORAGE})
    void showStorage() {
        callbackPermissionGranted();

    }

    @OnShowRationale({R_STORAGE, W_STORAGE})
    void showRationaleForStorage(PermissionRequest reques) {

    }

    @OnPermissionDenied({R_STORAGE, W_STORAGE})
    void showDeniedForStorage() {

    }

    @OnNeverAskAgain({R_STORAGE, W_STORAGE})
    void showNeverAskForStorage() {

    }

    //定位权限
    @NeedsPermission(LOCATION)
    void showLocation() {
        callbackPermissionGranted();

    }

    @OnShowRationale(LOCATION)
    void showRationaleForLocation(PermissionRequest reques) {

    }

    @OnPermissionDenied(LOCATION)
    void showDeniedForLocation() {

    }

    @OnNeverAskAgain(LOCATION)
    void showNeverAskForLocation() {

    }

    //录音权限
    @NeedsPermission(RECORD)
    void showRecord() {
        callbackPermissionGranted();
    }

    @OnShowRationale(RECORD)
    void showRationaleForRecord(PermissionRequest reques) {

    }

    @OnPermissionDenied(RECORD)
    void showDeniedForRecord() {

    }

    @OnNeverAskAgain(RECORD)
    void showNeverAskForRecord() {

    }


    public static class Builder {
        private PermissionCallback mPermissionCallback;

        private Permission mPermission;

        private String mMessage;

        public Builder(Permission permission) {
            this.mPermission = permission;
        }

        public Builder setMessage(String message) {
            this.mMessage = message;
            return this;
        }

        public Builder setCallback(PermissionCallback callback) {
            this.mPermissionCallback = callback;
            return this;
        }

        @MainThread
        public void show(FragmentManager fm) {
            if (fm.isDestroyed())
                return;
            PermissionHelper permissionHelper;
            Fragment fragment = fm.findFragmentByTag(TAG);
            if (null != fragment) {
                permissionHelper = (PermissionHelper) fragment;
                permissionHelper.mMessage = mMessage;
                permissionHelper.mPermission = mPermission;
                permissionHelper.mPermissionCallback = mPermissionCallback;
                permissionHelper.getPermission();
            } else {
                permissionHelper = new PermissionHelper();
                permissionHelper.mMessage = mMessage;
                permissionHelper.mPermission = mPermission;
                permissionHelper.mPermissionCallback = mPermissionCallback;
                fm.beginTransaction().add(permissionHelper, TAG).commitAllowingStateLoss();
            }

        }

    }


}
