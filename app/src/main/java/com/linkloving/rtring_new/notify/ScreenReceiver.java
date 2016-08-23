package com.linkloving.rtring_new.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver {
	
	private static final String TAG = ScreenReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(Intent.ACTION_USER_PRESENT))
		{  
			 Intent serviceintent = new Intent();
			 serviceintent.setAction("com.linkloving.watch.BLE_SERVICE");
			 serviceintent.setPackage(context.getPackageName());
			 serviceintent.putExtra("PAY_APP_MSG", 0x02);
			 context.startService(serviceintent); //启动服务程序。
		}
	}

}
