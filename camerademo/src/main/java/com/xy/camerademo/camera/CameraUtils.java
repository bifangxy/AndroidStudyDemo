package com.xy.camerademo.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.xy.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xieying on 2019/4/17.
 * Description：
 */
public class CameraUtils {

    private ImageReader mImageReader;

    private int maxImages = 1;

    private int format = ImageFormat.JPEG;


    private CameraCaptureSession mCameraCaptureSession;

    private CameraDevice mCameraDevice;

    private CameraManager mCameraManager;

    private String mCameraId;

    private Surface mSurface;

    private Handler mCameraHandler;

    private Size mPreviewSize;

    private Size mCaptureSize;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setupCamera(int width, int height) {
        try {
            mCameraManager = (CameraManager) MyApplication.getContext().getSystemService(
                    Context.CAMERA_SERVICE);
            for (String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = mCameraManager.
                        getCameraCharacteristics(cameraId);
                Integer facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get
                            (CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    assert streamConfigurationMap != null;
                    mPreviewSize = getOptimalSize(streamConfigurationMap.
                            getOutputSizes(SurfaceTexture.class), width, height);

                    mCaptureSize = Collections.max(Arrays.asList(streamConfigurationMap.
                                    getOutputSizes(TextureView.class)),
                            ((size1, size2) -> Long.signum(size1.getHeight() * size1.getWidth()
                                    - size2.getHeight() * size2.getWidth())));
                    mCameraId = cameraId;
                    setupImageReader();

                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void openCamera(){

    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupImageReader() {
        LogUtils.d("mCaptureSize width = " + mCaptureSize.getWidth() + "  height = " +
                mCaptureSize.getHeight());
        mImageReader = ImageReader.newInstance(mCaptureSize.getWidth(), mCaptureSize.getHeight(),
                format, maxImages);
        mImageReader.setOnImageAvailableListener(imageReader -> {
            Image image = imageReader.acquireLatestImage();
            //TODO 对图片进行处理,记得image.close();

        }, mCameraHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startPreview() {
        try {
            CaptureRequest.Builder previewBuilder = mCameraDevice.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW);
            previewBuilder.addTarget(mSurface);
            mCameraDevice.createCaptureSession(Arrays.asList(mSurface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            if (null == mCameraDevice)
                                return;
                            mCameraCaptureSession = session;
                            previewBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                            previewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                            try {
                                mCameraCaptureSession.setRepeatingRequest(previewBuilder.build(),
                                        null, mCameraHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    }, mCameraHandler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void takePhote() {
        try {
            CaptureRequest.Builder takePhotoBuilder = mCameraDevice.createCaptureRequest
                    (CameraDevice.TEMPLATE_STILL_CAPTURE);
            takePhotoBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            takePhotoBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            CaptureRequest captureRequest = takePhotoBuilder.build();

            mCameraCaptureSession.capture(captureRequest, null, null);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startCameraThread() {
        HandlerThread handlerThread = new HandlerThread("cameraThread");
        handlerThread.start();
        mCameraHandler = new Handler(handlerThread.getLooper());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Size getOptimalSize(Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size size : sizeMap) {
            if (width > height) {
                if (size.getWidth() > width && size.getHeight() > height)
                    sizeList.add(size);
            } else {
                if (size.getHeight() > width && size.getWidth() > height)
                    sizeList.add(size);
            }
        }
        if (sizeList.size() > 0) {
            return Collections.min(sizeList, (size1, size2) -> Long.signum(
                    size1.getHeight() * size1.getHeight() -
                            size2.getHeight() * size2.getWidth()));
        }
        return sizeMap[0];
    }


    public void setMaxImages(int maxImages) {
        this.maxImages = maxImages;
    }


    public void setFormat(int format) {
        this.format = format;
    }
}
