package com.example.android.bluetoothlegatt;

import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class BLEListHandler extends Handler
{
	private static final String TAG = BLEHandler.class.getSimpleName();
	
	private Context mContext;
	private int index;
	private LPDeviceInfo deviceInfo;
	public static final int REQUEST_ENABLE_BT = 0x10;
	
	public BLEListHandler(Context context)
	{
		mContext = context;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public void handleMessage(Message msg)
	{
		super.handleMessage(msg);
		switch (msg.what)
		{
			case BLEListProvider.MSG_BLE_CONNECT_FAILED:
				handleConnectFailedMsg();
				break;
            case BLEListProvider.MSG_BLE_CONNECT_LOST:
            	handleConnectLostMsg();
				break;
			case BLEListProvider.MSG_BLE_CONNECT_SUCCESS:
				handleConnectSuccessMsg();
				break;
			case BLEListProvider.MSG_BLE_NOT_CONNECT:
				handleHaveNotConnectMsg();
				break;
			case BLEListProvider.MSG_BLE_NOT_ENABLED:
				handleNotEnableMsg();
				break;
//			case BLEProvider.MSG_BLE_NOT_SCAN:
//				handleNotScanMsg();
//				break;
			case BLEListProvider.MSG_BLE_NOTSUPPORT:
				handleNotSupportMsg();
				break;
			case BLEListProvider.MSG_BLE_SCAN_TIME_OUT:
				handleScanTimeOutMsg();
				break;
			case BLEListProvider.MSG_BLE_DATA:
				handleData((BluetoothDevice) msg.obj);
				break;
			case BLEListProvider.MSG_USER_ERROR:
				handleUserErrorMsg(((Integer)msg.obj).intValue());
				break;
			case BLEListProvider.MSG_BOUND_DEVICE_FAILED:
				handleBoundDeviceFailed();
				break;
//			case BLEProvider.MSG_BLE_DEVICE_INFO_DATA:
//				handleDeviceInfoDataMsg((LPDeviceInfo) msg.obj);
//				break;
			default:
				break;
		}
	}
	
	protected void handleConnectFailedMsg()
	{
//		Toast.makeText(mContext, "连接失败！！！！！", Toast.LENGTH_LONG).show();
		Log.e(TAG, "连接失败！！！！！！！！！！！！！！！！");
		//mProvider.connectNextDevice();
	}
	
	protected void handleConnectLostMsg()
	{
		Log.e(TAG, "连接断开！！！！！！！！！！！！！！！");
	}
	
	protected void handleConnectSuccessMsg()
	{
		Log.e(TAG, "连接成功！！！！！！！！！！！！！！！！");
	}
	
	protected void handleHaveNotConnectMsg()
	{
//		Toast.makeText(mContext, "未连接！！！！！", Toast.LENGTH_LONG).show();
		Log.e(TAG, "未连接！！！！！！！！！！！！！！");
//		mProvider.reConnect();11111
	}
	
//	protected void handleNotScanMsg()
//	{
//		Toast.makeText(mContext, "未扫描！！！！！", Toast.LENGTH_LONG).show();
//		Log.e(TAG, "未扫描！！！！！！！！！！！！！！");
//		mProvider.scan();
//	}
	
	protected void handleNotEnableMsg()
	{
		 Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		 if(mContext != null)
			 ((Activity)mContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}
	
	protected void handleNotSupportMsg()
	{
//		Toast.makeText(mContext, "设备不支持BLE！！！！！", Toast.LENGTH_LONG).show();
		Log.e(TAG, "设备不支持BLE！！！！！");
	}
	
	protected void handleScanTimeOutMsg()
	{
//		Toast.makeText(mContext, "扫描超时！！！！", Toast.LENGTH_LONG).show();
		Log.e(TAG, "扫描超时！！！！");
	}
	
	protected void handleUserErrorMsg(int id)
	{
		Log.e(TAG, "用户ID不同！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
		//mProvider.connectNextDevice();
	}
	
	protected void handleBoundDeviceFailed()
	{
//		Toast.makeText(mContext, "绑定设备失败！！！！！！！！！！！！！！！！！！！！！", Toast.LENGTH_LONG).show();
		Log.e(TAG, "绑定设备失败！！！！！！！！！！！！！！！！！！！！！！！");
	}
	
//	protected void handleDeviceInfoDataMsg(LPDeviceInfo deviceInfo)
//	{
//		
//	}
	
//	protected void  handleAddressError()
//	{
//		Toast.makeText(mContext, "没有设备地址！！！！！！！！！！！！！！！！！", Toast.LENGTH_LONG).show();
//	}
	
	protected abstract void handleData(BluetoothDevice device);
}
