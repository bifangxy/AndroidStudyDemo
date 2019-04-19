package com.xy.camerademo.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xy.camerademo.R;
import com.xy.common.base.BaseActivity;
import com.xy.common.utils.permission.Permission;
import com.xy.common.utils.permission.PermissionCallback;
import com.xy.common.utils.permission.PermissionHelper;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by xieying on 2019/4/16.
 * Description：
 */
public class CameraActivity extends BaseActivity {

    @BindView(R.id.camera_textureView_preview)
    TextureView mCameraTextureViewPreview;
    @BindView(R.id.camera_bt_back)
    Button mCameraBtBack;
    @BindView(R.id.camera_bt_phone)
    Button mCameraBtPhone;
    @BindView(R.id.camera_iv_image)
    ImageView mCameraIvImage;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void initData() {


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        CameraUtils.getInstance().startCameraThread();
        if (!mCameraTextureViewPreview.isAvailable()) {
            mCameraTextureViewPreview.setSurfaceTextureListener(mTextureListener);
        } else {
            CameraUtils.getInstance().startPreview();
        }
    }

    private TextureView.SurfaceTextureListener mTextureListener = new TextureView.
            SurfaceTextureListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            CameraUtils.getInstance().setupCamera(width, height, mCameraTextureViewPreview.getSurfaceTexture());
            new PermissionHelper.Builder(Permission.CAMERA)
                    .setCallback(new PermissionCallback() {
                        @Override
                        public void onPermissionGranted() {
                            CameraUtils.getInstance().openCamera();
                        }

                        @Override
                        public void onPermissionDenied() {

                        }
                    }).show(getSupportFragmentManager());
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    @Override
    protected void initEvent() {
    }


    @OnClick(R.id.camera_bt_back)
    public void back() {
//        if (mCameraIvImage.getVisibility() == View.GONE) {
        Bitmap bitmap = scaleImage(CameraUtils.getInstance().getBitmap(), 1080, 1920);
        mCameraIvImage.setImageBitmap(bitmap);
        mCameraIvImage.setVisibility(View.VISIBLE);
        mCameraTextureViewPreview.setVisibility(View.GONE);
//        } else {
//            mCameraIvImage.setVisibility(View.GONE);
//            mCameraTextureViewPreview.setVisibility(View.VISIBLE);
//        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.camera_bt_phone)
    public void takePhone() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        CameraUtils.getInstance().takePhoto(rotation);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStop() {
        super.onStop();
        CameraUtils.getInstance().clear();
    }

    public static Bitmap scaleImage(Bitmap bitmap, int newWidth, int newHeight) {
        if (bitmap == null) {
            return null;
        }
        float scaleWidth = (float) newWidth / bitmap.getWidth();
        float scaleHeight = (float) newHeight / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
