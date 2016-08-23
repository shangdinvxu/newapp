package com.example.android.bluetoothlegatt.exception;

public class BLEScanTimeOutException extends BLException 
{
	public BLEScanTimeOutException()
	{
		super(BLErrCode.BLE_SCAN_TIME_OUT);
	}

	private static final long serialVersionUID = 1L;
}
