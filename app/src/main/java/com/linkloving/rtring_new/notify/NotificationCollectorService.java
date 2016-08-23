package com.linkloving.rtring_new.notify;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.LocalUserSettingsToolkits;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.DeviceSetting;
import com.linkloving.rtring_new.prefrences.devicebean.ModelInfo;
import com.linkloving.rtring_new.utils.CutString;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.utils.CommonUtils;

import java.io.UnsupportedEncodingException;

public class NotificationCollectorService  extends NotificationListenerService{
	public static final String TAG = NotificationCollectorService.class.getSimpleName();

    public static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    public static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String msgAction="android.provider.Telephony.SMS_RECEIVED";//这个之后会作为是不是短信的过滤条件
    
    private static String sms;
    private static final int MSG_OUTBOXCONTENT =998;
    
    long lastTimeofCall = 0L;
    long lastTimeofUpdate = 0L;
    long threshold_time = 10000;
    
	/**来电*/
    public static int phoneincall_count=0;   
	/**接听*/
    public static int phoneinhook_count=0; 
	/**挂断*/
    public static int phoneinidle_count=1;
    
    
    private BLEProvider provider;
	private UserEntity userEntity;
    private byte seq=1;
    public static byte incallphone;
    public static byte[] incallphone_title;
    public static byte[] incallphone_text;
    
    char[] array = { 0, 0, 0, 0, 0 };
    char[] charr;
    private String Ansc;
    private String MSG_TEXT;
    
	@Override
	public void onCreate() {
		super.onCreate();
		MyLog.e( TAG ,  "消息通知服务--Notification开始onCreate()了" );
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		MyLog.i( TAG ,  "NotificationCollectorService--onStartCommand了" );
		connectble0x02(this);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void connectble0x02(Context context) {
		/************************测试(开机启动)***************************/
		Intent serviceintent = new Intent();
		serviceintent.setAction("com.linkloving.watch.BLE_SERVICE");
		serviceintent.setPackage(context.getPackageName());
		serviceintent.putExtra("PAY_APP_MSG", 0x02);
		context.startService(serviceintent); //启动服务程序。
		/************************测试(开机启动)***************************/
	}

	@SuppressLint("NewApi")
	@Override
	public void onNotificationPosted(StatusBarNotification sbn) {
		 if(MyApplication.getInstance(NotificationCollectorService.this)==null){
			 //APP还未启动的时候  获取userEntity会null
			 return;
		 }
		 userEntity = MyApplication.getInstance(NotificationCollectorService.this).getLocalUserInfoProvider();
		 provider = BleService.getInstance(NotificationCollectorService.this).getCurrentHandlerProvider();  //获取蓝牙实例
		 Notification mNotification = sbn.getNotification();
	     if(mNotification!=null && provider!=null ){
			 Bundle extras = mNotification.extras;
			 if(extras==null) return;
			 if(CommonUtils.isStringEmpty(extras.getString(Notification.EXTRA_TEXT))) return;
			 ModelInfo modelInfo = PreferencesToolkits.getInfoBymodelName(NotificationCollectorService.this,userEntity.getDeviceEntity().getModel_name());
			 if(modelInfo==null) return;
			 //OAD的时候取消一切其他蓝牙命令
	    	 if(!CommonUtils.isStringEmpty(userEntity.getDeviceEntity().getLast_sync_device_id()) && !BleService.isCANCLE_ANCS() && modelInfo.getAncs()>0){
				 if(!provider.isConnectedAndDiscovered()) return; //蓝牙未连接
				 MyLog.e(TAG,"===qq/wechat的内容："+extras.getString(Notification.EXTRA_TEXT));
				 DeviceSetting deviceSetting = LocalUserSettingsToolkits.getLocalSetting(NotificationCollectorService.this,userEntity.getUser_id()+""); //获取用户设置 判断是否要发送指令
				 String Ansc_str = Integer.toBinaryString(deviceSetting.getANCS_value());
				 charr = Ansc_str.toCharArray(); // 将字符串转换为字符数组
				 System.arraycopy(charr, 0, array, 5 - charr.length, charr.length);
				 
				 if("com.tencent.mobileqq".equals(sbn.getPackageName()) && array[0] == '1'){

					 connectble0x02(NotificationCollectorService.this); //检查蓝牙连接

					 try {
						 String text="";
						 if(extras.getString(Notification.EXTRA_TEXT)!=null)
						 text = extras.getString(Notification.EXTRA_TEXT);

						 MyLog.e(TAG,"接收到的消息："+text);
						 
						 int note_result1 =text.indexOf("触摸即可了解详情或停止应用");    //需要过滤的字段
						 int note_result2 =text.indexOf("点击了解详情或停止应用");		  //需要过滤的字段
						 
						  if(note_result1<0 && note_result2<0 ){
							  int result=text.indexOf(":");
								if(result>=0){
									provider.setNotification_qq(NotificationCollectorService.this, seq++, CutString.stringtobyte(text, 24), CutString.stringtobyte(extras.getString(Notification.EXTRA_TEXT), 84));
								}
								else{
									provider.setNotification_qq(NotificationCollectorService.this, seq++, CutString.stringtobyte(text, 24), CutString.stringtobyte(extras.getString(Notification.EXTRA_TITLE)+":"+extras.getString(Notification.EXTRA_TEXT), 84));
								}	
						 }
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} 
				 }
				 else if("com.tencent.qqlite".equals(sbn.getPackageName()) && array[0] == '1'){

					 connectble0x02(NotificationCollectorService.this); //检查蓝牙连接
					 
					 try {
						 String text="";
						 if(extras.getString(Notification.EXTRA_TEXT)!=null)
						 text = extras.getString(Notification.EXTRA_TEXT);
						 
						 int note_result1 =text.indexOf("触摸即可了解详情或停止应用");    //需要过滤的字段
						 int note_result2 =text.indexOf("点击了解详情或停止应用");		 //需要过滤的字段
						 
						  if(note_result1 < 0 && note_result2 < 0 ){
							  int result=text.indexOf(":");
								if(result>=0){
									provider.setNotification_qq(NotificationCollectorService.this, seq++, CutString.stringtobyte(text, 24), CutString.stringtobyte(extras.getString(Notification.EXTRA_TEXT), 84));
								}
								else{
									provider.setNotification_qq(NotificationCollectorService.this, seq++, CutString.stringtobyte(text, 24), CutString.stringtobyte(extras.getString(Notification.EXTRA_TITLE)+":"+extras.getString(Notification.EXTRA_TEXT), 84));
								}	
						 }
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} 
				 }
				 else if("com.tencent.mm".equals(sbn.getPackageName()) && array[1] == '1'){   // 微信
					 
					 connectble0x02(NotificationCollectorService.this); //检查蓝牙连接
					 
					 try {
						 String text="";
						 if(extras.getString(Notification.EXTRA_TEXT)!=null)
						  		text = extras.getString(Notification.EXTRA_TEXT);
						 int note_result1 =text.indexOf("触摸即可了解详情或停止应用");    	//需要过滤的字段
						 int note_result2 =text.indexOf("点击了解详情或停止应用");		 	//需要过滤的字段
						 int note_result3 =text.indexOf("WeChat is running");    //需要过滤的字段
						 if(note_result1<0 && note_result2<0 && note_result3<0){
						 int result=text.indexOf(":");
							if(result>=0){
								provider.setNotification_WX(NotificationCollectorService.this, seq++, CutString.stringtobyte(text, 24), CutString.stringtobyte(extras.getString(Notification.EXTRA_TEXT), 84));
							}else{
								provider.setNotification_WX(NotificationCollectorService.this, seq++, CutString.stringtobyte(text, 24), CutString.stringtobyte(extras.getString(Notification.EXTRA_TITLE)+":"+extras.getString(Notification.EXTRA_TEXT), 84));
							}
						 }
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
				 }else if("com.linkloving.rtring_c_watch".equals(sbn.getPackageName()) && array[4] == '1'){ // 连爱
					 try {
							provider.setNotification_LINK(NotificationCollectorService.this, seq++, CutString.stringtobyte(extras.getString(Notification.EXTRA_TITLE), 24), CutString.stringtobyte(extras.getString(Notification.EXTRA_TEXT), 84));
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
				 }
	    	 }
	    	 
		 }
	}
	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) {
		 MyLog.d(TAG, "shut"+"-----"+sbn.toString());
		 Notification mNotification = sbn.getNotification();
		 if(mNotification!=null){
			 
		 }
	}

}
