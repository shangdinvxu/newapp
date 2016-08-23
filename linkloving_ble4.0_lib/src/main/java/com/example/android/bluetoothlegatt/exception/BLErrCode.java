package com.example.android.bluetoothlegatt.exception;

import java.util.HashMap;
import java.util.Map;

public class BLErrCode 
{
	public final static int BLE_OK = 0x00;
	public final static int BLE_NOT_SUPPORT = -1;
	public final static int BLE_INIT_ERR = -2;
	public final static int BLE_NOT_ENABLEDE = -3;
	public final static int BLE_CONNECT_ERR = -4;
	public final static int BLE_SEND_BUSY = -5;
	public final static int BLE_SEND_ERR = -6;
	public final static int BLE_SEND_TIME_OUT = -7;
	public final static int BLE_SCAN_TIME_OUT = -8;
	//新添加   没有被绑定设备的对象
	public final static int BLE_NOT_BOUNDED = -9 ;
	
	private static Map<Integer, String> messageMap = new HashMap< Integer, String >();
	static {
		messageMap.put(BLE_NOT_SUPPORT, "BLE_NOT_SUPPORT");
		messageMap.put(BLE_INIT_ERR, "BLE_INIT_ERR");
		messageMap.put(BLE_NOT_ENABLEDE, "BLE_NOT_ENABLEDE");
		messageMap.put(BLE_CONNECT_ERR, "BLE_CONNECT_ERR");
		messageMap.put(BLE_SEND_BUSY, "BLE_SEND_BUSY");
		messageMap.put(BLE_SEND_ERR, "BLE_SEND_ERR");
		messageMap.put(BLE_SEND_TIME_OUT, "BLE_SEND_TIME_OUT");
		messageMap.put(BLE_SCAN_TIME_OUT, "BLE_SCAN_TIME_OUT");
		messageMap.put(BLE_NOT_BOUNDED, "BLE_NOT_BOUNDED");
	}
	
	
	public static String getMessage(int key){
		String  ans = messageMap.get(key);
		return ans == null ? ("Unknown Error " + key) : ans;
	}
}
