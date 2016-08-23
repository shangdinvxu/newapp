package com.example.android.bluetoothlegatt.exception;

public class BLENotSupportException extends BLException {

	public BLENotSupportException()
	{
		super(BLErrCode.BLE_NOT_SUPPORT);
	}

	private static final long serialVersionUID = 1L;

}
