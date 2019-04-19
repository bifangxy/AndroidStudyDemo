package com.xy.camerademo.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
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
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import com.xy.common.utils.DipPixelUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by xieying on 2019/4/17.
 * Description：
 */
public class CameraUtils {

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private static CameraUtils mCameraUtils;

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

    private Bitmap mBitmap;

    public static CameraUtils getInstance() {
        if (mCameraUtils == null) {
            synchronized (CameraUtils.class) {
                if (mCameraUtils == null) {
                    mCameraUtils = new CameraUtils();
                }
            }
        }
        return mCameraUtils;
    }


    //初始化相机
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setupCamera(int width, int height, SurfaceTexture surfaceTexture) {
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
                                    getOutputSizes(ImageFormat.JPEG)),
                            ((size1, size2) -> Long.signum(size1.getHeight() * size1.getWidth()
                                    - size2.getHeight() * size2.getWidth())));
                    surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getWidth());
                    mSurface = new Surface(surfaceTexture);
                    mCameraId = cameraId;
                    setupImageReader();
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //打开相机
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    public void openCamera() {
        if (mCameraManager != null) {
            try {
                mCameraManager.openCamera(mCameraId, mStateCallback, mCameraHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }


    //设置ImageReader
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupImageReader() {
        mImageReader = ImageReader.newInstance(mCaptureSize.getWidth(), mCaptureSize.getHeight(),
                format, maxImages);
        mImageReader.setOnImageAvailableListener(imageReader -> {
            Image image = imageReader.acquireLatestImage();
            //TODO 对图片进行处理,记得image.close();

            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[byteBuffer.remaining()];
            byteBuffer.get(data);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);

            mBitmap = drawTextToLeftTop(bitmap, "xieying", 25, Color.rgb(255, 0, 0), 10, 10);
            image.close();

        }, mCameraHandler);
    }

    //相机开启结果回调
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

    //开始预览
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startPreview() {
        try {
            if (null == mCameraDevice)
                return;
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

    //照相
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void takePhoto(int rotation) {
        try {
            CaptureRequest.Builder takePhotoBuilder = mCameraDevice.createCaptureRequest
                    (CameraDevice.TEMPLATE_STILL_CAPTURE);
            takePhotoBuilder.addTarget(mImageReader.getSurface());
            takePhotoBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            takePhotoBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            takePhotoBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            CaptureRequest captureRequest = takePhotoBuilder.build();

            mCameraCaptureSession.capture(captureRequest, null, null);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //开启子线程Handler
    public void startCameraThread() {
        HandlerThread handlerThread = new HandlerThread("cameraThread");
        handlerThread.start();
        mCameraHandler = new Handler(handlerThread.getLooper());
    }

    //获取预览图片缓存大小
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void clear() {
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    public static class ImageSaver implements Runnable {

        private Image mImage;

        public ImageSaver(Image image) {
            mImage = image;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            ByteBuffer byteBuffer = mImage.getPlanes()[0].getBuffer();
            byte[] data = new byte[byteBuffer.remaining()];
            byteBuffer.get(data);
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);

        }
    }

    public static Bitmap drawTextToLeftTop(Bitmap bitmap, String text,
                                           int size, int color, int paddingLeft, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(DipPixelUtil.dip2px(size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(bitmap, text, paint, bounds,
                DipPixelUtil.dip2px(paddingLeft),
                DipPixelUtil.dip2px(paddingTop) + bounds.height());
    }

    //图片上绘制文字
    private static Bitmap drawTextToBitmap(Bitmap bitmap, String text,
                                           Paint paint, Rect bounds, int paddingLeft, int paddingTop) {
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取更清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
