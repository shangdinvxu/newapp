package com.example.android.bluetoothlegatt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.example.android.bluetoothlegatt.exception.BLENotBounded;
import com.example.android.bluetoothlegatt.exception.BLENotEnabledException;
import com.example.android.bluetoothlegatt.exception.BLENotSupportException;
import com.example.android.bluetoothlegatt.exception.BLException;
import com.example.android.bluetoothlegatt.wapper.BLEWapper;

public class BLEListProvider
{
	public static final int MSG_BLE_NOTSUPPORT = 0x10;
	public static final int MSG_BLE_NOT_ENABLED = MSG_BLE_NOTSUPPORT+1;
//	public static final int MSG_BLE_NOT_SCAN = MSG_BLE_NOT_ENABLED + 1;
	public static final int MSG_BLE_SCAN_TIME_OUT = MSG_BLE_NOT_ENABLED + 1;//MSG_BLE_NOT_SCAN + 1;
	public static final int MSG_BLE_NOT_CONNECT = MSG_BLE_SCAN_TIME_OUT + 1;
	public static final int MSG_BLE_CONNECT_FAILED = MSG_BLE_NOT_CONNECT + 1;
	public static final int MSG_BLE_CONNECT_LOST = MSG_BLE_CONNECT_FAILED + 1;
	public static final int MSG_BLE_CONNECT_SUCCESS = MSG_BLE_CONNECT_LOST + 1;
	public static final int MSG_BLE_DATA = MSG_BLE_CONNECT_SUCCESS + 1;
	public static final int MSG_BLE_SPORT_DATA_PROESS = MSG_BLE_DATA + 1; 
	public static final int MSG_DATA_ERROR = MSG_BLE_SPORT_DATA_PROESS + 1;
	public static final int MSG_USER_ERROR = MSG_DATA_ERROR + 1;
	public static final int MSG_BOUND_DEVICE_FAILED = MSG_USER_ERROR + 1;
	public static final int MSG_BLE_ENERGY = MSG_BOUND_DEVICE_FAILED + 1;
	
	private Context mContext;
	private BLEWapper mBlEWapper;
	private final String TAG = BLEListProvider.class.getSimpleName();
	
	private static final long SCAN_PERIOD = 30000;
	
	private boolean isScaning  =  false ;
	
	private BLEListHandler mHandler;
	
	public BLEListProvider(Context context,BLEListHandler handler)
	{
		 mContext = context;
		 mBlEWapper =BLEWapper.getInstence();
		 this.mHandler = handler;
	}
	
	public boolean init(Context context)
	{
		try
		{
			mBlEWapper.init(context);
		}
		catch (BLENotSupportException e)
		{
			mHandler.sendEmptyMessage(MSG_BLE_NOTSUPPORT);
			return false;
		}
		catch (BLENotEnabledException e)
		{
			mHandler.sendEmptyMessage(MSG_BLE_NOT_ENABLED);
			return false;
		} catch (BLENotBounded e) {
			e.printStackTrace();
		}
		return true;
	}
	
    
    
    public void scanDeviceList() 
	{
		if(isScaning)
		{
			return;
		}
		isScaning = true;
		
		if(!init(mContext))
		{
			Log.e(TAG, "init failed!!!!!!!!!!!!!!!!!!");
			isScaning = false;
			return;
		}else{
			Log.e(TAG, "init 成功!!!!!!!!!!!!!!!!!!");
		}
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				stopScan();
				isScaning = false;
			}
		}, SCAN_PERIOD);
		
		try {
			//扫描API判断
//			if (Build.VERSION.SDK_INT < 21){
				mBlEWapper.scan(mHandler, mLeScanCallback);
//			}else{
//				mBlEWapper.scanAPI21(mHandler, mScanCallback);
//			}

		} catch (BLException e) {
			Log.e(TAG, "scan error!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			e.printStackTrace();
		}
	}
    
    public void stopScan() 
   	{
    	if(!isScaning)
		{
			return;
		}
    	try {
			//断开扫描API判断
//			if (Build.VERSION.SDK_INT < 21){
				mBlEWapper.stopScan();
//			}else{
//				mBlEWapper.stopScannAPI21();
//			}
			isScaning = false;
		} catch (BLException e) {
			Log.e(TAG, "scan error!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			e.printStackTrace();
		}
 
   	}
    
    private BluetoothAdapter.LeScanCallback mLeScanCallback =new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) 
        {
        	/**判断是否是手表设备*/
 		   if(( ((scanRecord[5] == (byte)0xE1) && (scanRecord[6] == (byte)0xFE)) || ((scanRecord[23] == (byte)0xE1) && (scanRecord[24] == (byte)0xFE)) ))//&& device.getAddress().equals(getCurrentDeviceMac())
		   {
     			   /**判断是否是手环设备*/
//     			   Log.i(TAG, " device address.......................... " + device.getAddress());
     			   Message msg = mHandler.obtainMessage();
     			   msg.what = MSG_BLE_DATA;
     			   msg.obj = device;
     			   msg.sendToTarget();
		   }
		}
    };

//	@SuppressLint("NewApi")
//	private ScanCallback mScanCallback = new ScanCallback() {
//		@Override
//		public void onScanResult(int callbackType, ScanResult result) {
//			Log.i("callbackType", String.valueOf(callbackType));
//			Log.i("result", result.toString());
//			BluetoothDevice btDevice = result.getDevice();
//			Message msg = mHandler.obtainMessage();
//			msg.what = MSG_BLE_DATA;
//			msg.obj = btDevice;
//			msg.sendToTarget();
//		}
//
//		@Override
//		public void onBatchScanResults(List<ScanResult> results) {
//			for (ScanResult sr : results) {
//				Log.i("ScanResult - Results", sr.toString());
//			}
//		}
//
//		@Override
//		public void onScanFailed(int errorCode) {
//			Log.e("Scan Failed", "Error Code: " + errorCode);
//		}
//	};

}
