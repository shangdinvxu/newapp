package com.example.android.bluetoothlegatt.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/7/5.
 */
public class BLETookit {

    private boolean refreshGattStructure(BluetoothGatt gatt) {
        BluetoothGatt localGatt = gatt;
        try {
            Method localMethod = localGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean result = ((Boolean) localMethod.invoke(localGatt, new Object[0])).booleanValue();
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**与设备解除配对*/
    static public boolean removeBond(Class btClass,BluetoothDevice btDevice) throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    /**与设备配对*/
    public static  boolean createBond(Class btClass,BluetoothDevice btDevice) throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }
}
