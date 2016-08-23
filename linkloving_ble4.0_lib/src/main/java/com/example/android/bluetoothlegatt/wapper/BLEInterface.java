package com.example.android.bluetoothlegatt.wapper;

import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.content.Context;
import android.os.Handler;

import com.example.android.bluetoothlegatt.exception.BLENotBounded;
import com.example.android.bluetoothlegatt.exception.BLENotEnabledException;
import com.example.android.bluetoothlegatt.exception.BLENotSupportException;
import com.example.android.bluetoothlegatt.exception.BLESendTimeOutException;
import com.example.android.bluetoothlegatt.exception.BLException;

public interface BLEInterface 
{
    /**初始化*/
	public abstract boolean init(Context context) throws BLENotSupportException, BLENotEnabledException,BLENotBounded;
	
	/**扫描蓝牙设备*/
	public abstract void scan(Handler handler, final LeScanCallback mLeScanCallback) throws BLException;
	
	/**停止扫描*/
	public abstract void stopScan()throws BLException ;

//	/**扫描蓝牙设备5.0以上*/
//	public abstract void scanAPI21(Handler handler, final ScanCallback scanCallback) throws BLException;
//
//	/**停止扫描5.0以上*/
//	public abstract void stopScannAPI21()throws BLException ;
	
	/** 连接蓝牙设备*/
	public abstract boolean connect(String address) throws BLException;
	
	/** 发送数据*/
	public abstract byte[] send(byte[] data,int status) throws BLException,BLESendTimeOutException;
	
	/** 断开连接*/
    public abstract void disconnect();
    
    /** 释放*/
	public abstract boolean release();

}
