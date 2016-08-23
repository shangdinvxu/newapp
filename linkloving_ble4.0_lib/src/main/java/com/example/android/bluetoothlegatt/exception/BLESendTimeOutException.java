package com.example.android.bluetoothlegatt.exception;

public class BLESendTimeOutException extends BLException
{
	public BLESendTimeOutException()
	{
		super(BLErrCode.BLE_SEND_TIME_OUT);
	}

	private static final long serialVersionUID = 1L;
}
