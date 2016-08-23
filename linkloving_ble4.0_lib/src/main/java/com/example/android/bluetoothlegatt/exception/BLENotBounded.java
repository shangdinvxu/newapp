package com.example.android.bluetoothlegatt.exception;

public class BLENotBounded extends BLException{

	public BLENotBounded() {
		super(BLErrCode.BLE_SCAN_TIME_OUT);
	}
	private static final long serialVersionUID = 1L;
}
