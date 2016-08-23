package com.example.android.bluetoothlegatt.utils;

import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class ServiceUtils {
	private static final int INDEX_CLOSE = 0Xff;
	private static final int INDEX_OPEN = 0X00;
	
	private static final String INDEX_FLAG = "PAY_APP_MSG";
	private static final String SERVICE_NAME = "com.linkloving.watch.BLE_SERVICE";
	private final static String MAIN_SERVICE = "com.linkloving.rtring_new.BleService";
	/**
	 * 关闭蓝牙内部服务：
	 * 启动服务：服务为：com.linkloving.watch.BLESERVICE
	 * 此时的场景是：打开支付APP开始扫描手表 连爱APP后台会静默断开蓝牙，禁止重连，取消定时扫描器
	 * 
	 */
   public static void CLOSE_LINK_BLE(Context context){
	   if(isServiceRunning(context,MAIN_SERVICE)){
		   Intent serviceintent = new Intent();
			serviceintent.setAction(SERVICE_NAME);
			serviceintent.putExtra(INDEX_FLAG, INDEX_CLOSE);
			Intent eintent = new Intent(getExplicitIntent(context,serviceintent));
			context.startService(eintent); //启动服务程序。
	   }
	   
   }
   /**
	 * 启动蓝牙内部服务：
	 * 启动服务：服务为：com.linkloving.watch.BLESERVICE
	 * 此时的场景是：使用完毕支付APP，并且退出了支付APP 连爱APP后台会自动连接手表并且开启定时扫描器
	 */
   public static void OPEN_LINK_BLE(Context context){
	   if(isServiceRunning(context,MAIN_SERVICE)){
		   Intent serviceintent = new Intent();
			serviceintent.setAction(SERVICE_NAME);
			serviceintent.putExtra(INDEX_FLAG, INDEX_OPEN);
			Intent eintent = new Intent(getExplicitIntent(context,serviceintent));
			context.startService(eintent); //启动服务程序。
	   }
   }
   
   public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
       // Retrieve all services that can match the given intent
       PackageManager pm = context.getPackageManager();
       List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
       // Make sure only one match was found
       if (resolveInfo == null || resolveInfo.size() != 1) {
           return null;
       }
       // Get component info and create ComponentName
       ResolveInfo serviceInfo = resolveInfo.get(0);
       String packageName = serviceInfo.serviceInfo.packageName;
       String className = serviceInfo.serviceInfo.name;
       ComponentName component = new ComponentName(packageName, className);
       // Create a new intent. Use the old one for extras and such reuse
       Intent explicitIntent = new Intent(implicitIntent);
       // Set the component to be explicit
       explicitIntent.setComponent(component);
       return explicitIntent;
   }
   
	/**
    * 用来判断某服务是否运行.
    * @param context
    * @param className 判断的服务名字
    * @return true 在运行 false 不在运行
    */
   public static  boolean isServiceRunning(Context mContext,String className) {
       boolean isRunning = false;
       /**获得Activity管理器并返回所有的服务列表*/
       ActivityManager activityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE); 
       List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(200);
       if (!(serviceList.size()>0)) {
           return false;
       }
       for (int i=0; i<serviceList.size(); i++) {
           if (serviceList.get(i).service.getClassName().toString().equals(className) == true)
           {
               isRunning = true;
               break;
           }
       }
       return isRunning;
   }
}
