package com.example.android.bluetoothlegatt.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class ToastUtil 
{
	 static Toast toast;
	/**
	 * 自定义Toast提示框
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showMyToast(Context context, String msg) {
		if(toast == null)
		{
			toast =  Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		}
		else
		{
			toast.setText(msg);
		}
		toast.setDuration(1000);
		toast.show();
//		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

}
