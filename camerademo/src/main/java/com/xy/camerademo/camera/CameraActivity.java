package com.xy.camerademo.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xy.camerademo.R;
import com.xy.common.base.BaseActivity;

import java.nio.ByteBuffer;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xieying on 2019/4/16.
 * Description：
 */
public class CameraActivity extends BaseActivity {
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @BindView(R.id.camera_surface_preview)
    SurfaceView mCameraSurfacePreview;
    @BindView(R.id.camera_bt_back)
    Button mCameraBtBack;
    @BindView(R.id.camera_bt_phone)
    Button mCameraBtPhone;
    @BindView(R.id.camera_iv_image)
    ImageView mCameraIvImage;

    private SurfaceHolder mSurfaceHolder;

    private CameraManager mCameraManager;

    private int mViewWidth;

    private int mViewHeight;

    private Handler mChildHandler;

    private Handler mMainHandler;

    private String mCameraId;

    private ImageReader mImageReader;

    private CameraCaptureSession mCameraCaptureSession;

    private CameraDevice mCameraDevice;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initData() {
        mSurfaceHolder = mCameraSurfacePreview.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
    }

    @Override
    protected void initEvent() {
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera2();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (null != mCameraDevice) {
                    mCameraDevice.close();
                    mCameraDevice = null;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initCamera2() {
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            for (String cameraId : mCameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = mCameraManager.
                        getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT)
                    continue;

                StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.
                        get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mCameraId = cameraId;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        mChildHandler = new Handler(handlerThread.getLooper());
        mMainHandler = new Handler(getMainLooper());
        mImageReader = ImageReader.newInstance(1080, 1920,
                ImageFormat.JPEG, 1);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                mCameraDevice.close();
                mCameraSurfacePreview.setVisibility(View.GONE);
                mCameraIvImage.setVisibility(View.VISIBLE);

                Image image = reader.acquireLatestImage();
                ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null)
                    mCameraIvImage.setImageBitmap(bitmap);
                image.close();
            }
        }, mMainHandler);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 1);
            return;
        }

        try {
            mCameraManager.openCamera(mCameraId, mStateCallback, mMainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mCameraDevice = camera;
            takePreview();
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
            Log.d("xieying", "摄像头开启失败" + error);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void takePreview() {
        try {
            final CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(
                    CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(),
                        mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(CameraCaptureSession session) {
                        if (null == mCameraDevice)
                            return;
                        mCameraCaptureSession = session;
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        CaptureRequest captureRequest = previewRequestBuilder.build();
                        try {
                            mCameraCaptureSession.setRepeatingRequest(captureRequest,
                                    null, mChildHandler);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onConfigureFailed(CameraCaptureSession session) {
                        Log.d("xieying", "配置失败");
                    }

                }, mChildHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @OnClick(R.id.camera_bt_back)
    public void back() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.camera_bt_phone)
    public void takePhone() {
        if (null == mCameraDevice)
            return;
        CaptureRequest.Builder captureRequestBuilder;
        try {
            captureRequestBuilder = mCameraDevice.createCaptureRequest
                    (CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            CaptureRequest captureRequest = captureRequestBuilder.build();
            mCameraCaptureSession.capture(captureRequest, null, mChildHandler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
