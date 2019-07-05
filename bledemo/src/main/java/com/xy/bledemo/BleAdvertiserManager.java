package com.xy.bledemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by xieying on 2019/6/19.
 * Description：
 */
public class BleAdvertiserManager {

    private static final String TAG = BleAdvertiserManager.class.getSimpleName();

    private final static UUID UUID_SERVICE = UUID.fromString("10000000-0000-0000-0000-000000000000");
    public static final UUID UUID_CHAR_WRITE = UUID.fromString("12000000-0000-0000-0000-000000000000");
    private final static UUID UUID_CHAR_READ = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    private final static UUID UUID_DESC_READ = UUID.fromString("0000fff3-0000-1000-8000-00805f9b34fb");


    private BluetoothManager mBluetoothManager;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private BluetoothGattServer mBluetoothGattServer;

    private Context mContext;

    private short mMajor = 0;

    private short mMinor = 0;

    private int mTxPower = 0;

    private BluetoothGatt mBluetoothGatt;

    private static class SingleHolder {
        static final BleAdvertiserManager instance = new BleAdvertiserManager();
    }

    public synchronized static BleAdvertiserManager getInstance() {
        return SingleHolder.instance;
    }

    public void setContext(Context context) {
        mContext = context;

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        if (mBluetoothManager != null && mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }

        List<BluetoothDevice> deviceList = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);

        List<BluetoothDevice> deviceList1 = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);

        BluetoothDevice bluetoothDevice = deviceList.get(0);

        BluetoothGattService service = new BluetoothGattService(UUID_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic characteristicWrite = new BluetoothGattCharacteristic(UUID_CHAR_WRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);


        Log.d("---", "----deviceList----  " + deviceList + "  ------deviceList1-----  " + deviceList);


//        mBluetoothGatt = bluetoothDevice.connectGatt(mContext, false, mBluetoothGattCallback);



//
//        if (mBluetoothManager != null)
//            mBluetoothGattServer = mBluetoothManager.openGattServer(mContext, mBluetoothGattServerCallback);
//
//        setBluetoothGattServer();

    }

    public void init(Activity activity, int requestCode) {
        setContext(activity);
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, "不支持", Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        if (mBluetoothAdapter == null) {
            Toast.makeText(activity, "不支持", Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Toast.makeText(activity, "不支持", Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableIntent, requestCode);
        }

    }

    public void startAdvertising() {
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        }

        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true) //包含蓝牙名称
                .setIncludeTxPowerLevel(true) //包含发射功率级别
//                .addServiceUuid(new ParcelUuid(UUID_TEST))
//                .addManufacturerData(1, new byte[]{23, 33}) //设备厂商数据，自定义
                .build();
        //扫描响应数据(可选，当客户端扫描时才发送)
        AdvertiseData scanResponse = new AdvertiseData.Builder()
                .addManufacturerData(2, new byte[]{66, 66}) //设备厂商数据，自定义
                .addServiceUuid(new ParcelUuid(UUID_SERVICE)) //服务UUID
                .addServiceData(new ParcelUuid(UUID_SERVICE), new byte[]{2}) //服务数据，自定义
                .build();
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.startAdvertising(createAdvSettings(true, 0),
                    BleUtil.createScanAdvertiseData(mMajor, mMinor, (byte) 0X16), mAdvertiseCallback);
        }
    }

    private AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        builder.setConnectable(connectable);
        builder.setTimeout(timeoutMillis);
        return builder.build();
    }


    private void setBluetoothGattServer() {

        BluetoothGattService bluetoothGattService = new BluetoothGattService(UUID_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic characterWrite = new BluetoothGattCharacteristic(UUID_CHAR_WRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        BluetoothGattCharacteristic characterRead = new BluetoothGattCharacteristic(UUID_CHAR_READ,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
        characterRead.addDescriptor(new BluetoothGattDescriptor(UUID_DESC_READ, BluetoothGattDescriptor.PERMISSION_WRITE | BluetoothGattDescriptor.PERMISSION_READ));


        bluetoothGattService.addCharacteristic(characterRead);
        bluetoothGattService.addCharacteristic(characterWrite);

        if (mBluetoothGattServer != null)
            mBluetoothGattServer.addService(bluetoothGattService);

    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.d("---", "----BLE广播开启成功---");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.d("---", "----BLE广播开启失败---" + errorCode);

        }
    };

    private BluetoothGattServerCallback mBluetoothGattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.d("---", "----newState---" + newState);
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
            Log.d("---", "----BLE服务添加成功---" + service.getUuid().toString());

            List<BluetoothGattService> serviceList = mBluetoothGattServer.getServices();
            for (BluetoothGattService bluetoothGattService1 : serviceList) {
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService1.getCharacteristics()) {
                    Log.d("--", "BluetoothGattCharacteristic UUID  =" + bluetoothGattCharacteristic.getUuid().toString());
                    for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristic.getDescriptors()) {
                        Log.d("--", "BluetoothGattDescriptor UUID  =" + bluetoothGattDescriptor.getUuid().toString());

                    }
                }
            }

        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
        }
    };


    public void setMajor(int major) {
        mMajor = (short) major;
    }

    public void setMinor(int minor) {
        mMinor = (short) minor;
    }

    public void setTxPower(int txPower) {
        mTxPower = txPower;
    }

    public void stopAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            mBluetoothLeAdvertiser = null;
        }

        if (mBluetoothAdapter != null)
            mBluetoothAdapter = null;
    }

    public void getConnectedDevice() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {//得到连接状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(mBluetoothAdapter, (Object[]) null);

            Log.d("----", "-----state----" + state);

            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Log.i("BLUETOOTH", "BluetoothAdapter.STATE_CONNECTED");
                Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
                Log.i("BLUETOOTH", "devices:" + devices.size());

                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        Log.i("BLUETOOTH", "connected:" + device.getName());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BluetoothDevice dev = gatt.getDevice();
            Log.i(TAG, String.format("onConnectionStateChange:%s,%s,%s,%s", dev.getName(), dev.getAddress(), status, newState));
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices(); //启动服务发现
            } else {
            }
            Log.i(TAG, String.format(status == 0 ? (newState == 2 ? "与[%s]连接成功" : "与[%s]连接断开") : ("与[%s]连接出错,错误码:" + status), dev));
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, String.format("onServicesDiscovered:%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), status));
            if (status == BluetoothGatt.GATT_SUCCESS) { //BLE服务发现成功
                // 遍历获取BLE服务Services/Characteristics/Descriptors的全部UUID
                for (BluetoothGattService service : gatt.getServices()) {
                    StringBuilder allUUIDs = new StringBuilder("UUIDs={\nS=" + service.getUuid().toString());
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        allUUIDs.append(",\nC=").append(characteristic.getUuid());
                        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors())
                            allUUIDs.append(",\nD=").append(descriptor.getUuid());
                    }
                    allUUIDs.append("}");
                    Log.i(TAG, "onServicesDiscovered:" + allUUIDs.toString());
                    Log.i(TAG, "发现服务" + allUUIDs);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            UUID uuid = characteristic.getUuid();
            String valueStr = new String(characteristic.getValue());
            Log.i(TAG, String.format("onCharacteristicRead:%s,%s,%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), uuid, valueStr, status));
            Log.i(TAG, "读取Characteristic[" + uuid + "]:\n" + valueStr);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            UUID uuid = characteristic.getUuid();
            String valueStr = new String(characteristic.getValue());
            Log.i(TAG, String.format("onCharacteristicWrite:%s,%s,%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), uuid, valueStr, status));
            Log.i(TAG, "写入Characteristic[" + uuid + "]:\n" + valueStr);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            UUID uuid = characteristic.getUuid();
            String valueStr = new String(characteristic.getValue());
            Log.i(TAG, String.format("onCharacteristicChanged:%s,%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), uuid, valueStr));
            Log.i(TAG, "通知Characteristic[" + uuid + "]:\n" + valueStr);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            UUID uuid = descriptor.getUuid();
            String valueStr = Arrays.toString(descriptor.getValue());
            Log.i(TAG, String.format("onDescriptorRead:%s,%s,%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), uuid, valueStr, status));
            Log.i(TAG, "读取Descriptor[" + uuid + "]:\n" + valueStr);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            UUID uuid = descriptor.getUuid();
            String valueStr = Arrays.toString(descriptor.getValue());
            Log.i(TAG, String.format("onDescriptorWrite:%s,%s,%s,%s,%s", gatt.getDevice().getName(), gatt.getDevice().getAddress(), uuid, valueStr, status));
            Log.i(TAG, "写入Descriptor[" + uuid + "]:\n" + valueStr);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.i(TAG, "onMtuChanged mtu=" + mtu + ",status=" + status);

        }
    };


    public void write() {
        BluetoothGattService service = getGattService(UUID_SERVICE);
        if (service != null) {
            String text = "测试";
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID_CHAR_WRITE);//通过UUID获取可写的Characteristic
            characteristic.setValue(text.getBytes()); //单次最多20个字节
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    private BluetoothGattService getGattService(UUID uuid) {
        BluetoothGattService service = mBluetoothGatt.getService(uuid);
        return service;
    }

    public void search() {
        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        bluetoothLeScanner.startScan(mScanCallback);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.stopScan(mScanCallback);
            }
        },4000);
    }

    private final ScanCallback mScanCallback = new ScanCallback() {// 扫描Callback
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d("----", "------address----" + result.getDevice().getAddress());
            Log.d("----", "------name----" + result.getDevice().getName());

        }
    };
}
