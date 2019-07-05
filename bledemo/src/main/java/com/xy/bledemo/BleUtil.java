package com.xy.bledemo;

import android.bluetooth.le.AdvertiseData;
import android.os.ParcelUuid;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Created by xieying on 2019/6/19.
 * Description：
 */
public class BleUtil {

    public static final UUID BLUETOOTH_SERVICE_UUID = UUID.fromString("fda50693-a4e2-4fb1-afcf-c6eb07647825");

    public static AdvertiseData createScanAdvertiseData(short major, short minor, byte txPower) {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.setIncludeDeviceName(true);
        byte[] serverData = new byte[5];
        ByteBuffer bb = ByteBuffer.wrap(serverData);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putShort(major);
        bb.putShort(minor);
        bb.put(txPower);
//        builder.addServiceData(ParcelUuid.fromString(BLUETOOTH_SERVICE_UUID.toString()), serverData);
        builder.addServiceUuid(new ParcelUuid(BLUETOOTH_SERVICE_UUID));
        return builder.build();
    }

    /*public static AdvertiseData createIBeaconAdvertiseData(UUID proximityUuid,short major,short minor,byte txPower){
        if(proximityUuid == null){
            throw new IllegalArgumentException("proximityUuid can not null");
        }

        String[] uuidstr = proximityUuid.toString().replaceAll("-","").toLowerCase().split("");
        byte[] uuidBytes = new byte[16];
        for (int i= 1,x=0;i<uuidstr.length;x++){
            uuidBytes[x] = (byte) ((Integer.parseInt(uuidstr[i++],16)<<4)|Integer.parseInt(uuidstr[i++],16));

        }
    }*/
}
