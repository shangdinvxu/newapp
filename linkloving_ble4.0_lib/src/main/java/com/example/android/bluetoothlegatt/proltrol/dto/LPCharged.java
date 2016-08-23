package com.example.android.bluetoothlegatt.proltrol.dto;

public class LPCharged {
	public  int  charge10;
	public  int  charge100;
	public  int  charge;
	
	public  LPCharged() {
		// 这两个默认值是为了兼容老板子
		charge10 = 220;
		charge100 = 243;
	}
	
	public double  getPercent() {
		return  charge >= charge100 ? 1.0 :
				charge >= charge10 ? ( ( charge - charge10 ) * 0.9 / ( charge100 - charge10 ) + 0.1 ):
				0.1 * charge / charge10;
	}
}
