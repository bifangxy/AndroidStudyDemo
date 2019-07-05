package com.xy.bledemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private BleAdvertiserManager mBleAdvertiserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.ACCESS_COARSE_LOCATION};
            for (String str : permissions) {
                if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 111);
                    break;
                }
            }
        }

        mBleAdvertiserManager = BleAdvertiserManager.getInstance();


    }


    public void openAdvertise(View view){

        mBleAdvertiserManager.setContext(this);
//        mBleAdvertiserManager.setMajor(1);
//        mBleAdvertiserManager.setMinor(2);
//        mBleAdvertiserManager.setTxPower(10);
//        mBleAdvertiserManager.startAdvertising();

//        mBleAdvertiserManager.getConnectedDevice();
    }

    public void writeData(View view){
        mBleAdvertiserManager.write();
    }

    public void search(View view){
        mBleAdvertiserManager.search();
    }
}
