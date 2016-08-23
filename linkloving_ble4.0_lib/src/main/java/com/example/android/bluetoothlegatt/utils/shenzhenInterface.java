package com.example.android.bluetoothlegatt.utils;


public interface shenzhenInterface {

	Object closeConnection();
	
	Object getConnectState();
	
	void transmit(byte[] arg0) ;
	
	void powerOn();
	
	void powerOff();
	
	void connection(String mac);
}
