package com.linkloving.rtring_new.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

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

public class SMSBroadcastReceiver extends BroadcastReceiver {
	 
	 public static final String TAG = "SMSBroadcastReceiver";
	 public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	 public static final char SMS='1';
	private UserEntity userEntity;
	 private BLEProvider provider;
	 private static int seq=0;
	 private Context context;
	 private char[] array_phone = { 0, 0, 0, 0, 0 };
	 
	 
	 private void connectble0x02(Context context) {
			/************************测试(开机启动)***************************/
			Log.e(TAG, "收到短信了");
			Intent serviceintent = new Intent();  
			serviceintent.setAction("com.linkloving.watch.BLE_SERVICE");
			serviceintent.setPackage(context.getPackageName());
			serviceintent.putExtra("PAY_APP_MSG", 0x02);
			context.startService(serviceintent); //启动服务程序。
			/************************测试(开机启动)***************************/
		}
	 
	 
	@Override
	public void onReceive(Context context, Intent intent) {
		 this.context = context;
		if(MyApplication.getInstance(context)==null){
			//APP还未启动的时候  获取userEntity会null
			return;
		}
		userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
		//手表才有的功能
		if(userEntity==null)
			return;
		if(userEntity.getUserBase()==null)
			return;
		ModelInfo modelInfo = PreferencesToolkits.getInfoBymodelName(context,userEntity.getDeviceEntity().getModel_name());
		if(modelInfo==null) return;

		if(modelInfo.getAncs()<=0){
			return;
		}
		 provider = BleService.getInstance(context).getCurrentHandlerProvider();
		MyLog.e(TAG,"========SMSBroadcastReceiver========");
		 DeviceSetting deviceSetting = LocalUserSettingsToolkits.getLocalSetting(context, MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id()+"");
		 String Ansc_str = Integer.toBinaryString(deviceSetting.getANCS_value());
		 char[] charr = Ansc_str.toCharArray(); // 将字符串转换为字符数组
		 System.arraycopy(charr, 0, array_phone, 5 - charr.length, charr.length);
		 if(SMS_RECEIVED_ACTION.equals(intent.getAction()) || !CommonUtils.isStringEmpty(MyApplication.getInstance(context).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id()) ){
			 connectble0x02(context);
			if(provider.isConnectedAndDiscovered() && array_phone[2] == SMS && !BleService.getInstance(context).isCANCLE_ANCS()){
				 SmsMessage msg = null;
		           Bundle bundle = intent.getExtras();
		           if (bundle != null) {
		               Object[] pdusObj = (Object[]) bundle.get("pdus");
		               for (Object p : pdusObj) {
		                   msg= SmsMessage.createFromPdu((byte[]) p);
		                   String msgTxt =msg.getMessageBody();
		                   String senderNumber = msg.getOriginatingAddress();
		                   Log.e(TAG, "发送人："+senderNumber+"  短信内容："+msgTxt);
		                   Log.e(TAG, "seq的值："+seq);
    		     		   try {
							provider.setNotification_MSG(context, (byte)seq, CutString.stringtobyte(PhoneReceiver.getContactNameByPhoneNumber(context, senderNumber), 24), CutString.stringtobyte(msgTxt, 84));
							seq++;
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}	
		                   return;
		           }
		           return;
		       }
			}
			
	           
		}
	}
	
	
}
