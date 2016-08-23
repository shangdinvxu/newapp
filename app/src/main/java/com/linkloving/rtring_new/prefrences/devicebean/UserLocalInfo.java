package com.linkloving.rtring_new.prefrences.devicebean;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Linkloving on 2016/3/9.
 */
public class UserLocalInfo {
    private static final String TAG = UserLocalInfo.class.getSimpleName();

    public static void setLocalSettingAlarmInfo(Context context, DeviceSetting localSetting) {
        MyLog.e(TAG, "执行了setLocalSettingAlarmInfo方法"+localSetting.getAlarm_one());
        List<DeviceSetting> settings = getLocalSettingInfoList(context);
        MyLog.e(TAG,settings.size()+"settings.size()");
        int index = -1;
        DeviceSetting needUpdate = null;
        for (int i = 0; i < settings.size(); i++) {
            DeviceSetting setting = settings.get(i);
//            if (setting.getAlarm_one().equals(localSetting.getAlarm_one())) {
                index = i;
                needUpdate = setting;
                needUpdate.setAlarm_one(localSetting.getAlarm_one());
                needUpdate.setAlarm_two(localSetting.getAlarm_two());
                needUpdate.setAlarm_three(localSetting.getAlarm_three());
                break;
//            }
        }

        if (index == -1)
            settings.add(localSetting);
        else
        {
            settings.remove(index);
            settings.add(needUpdate);
        }
        MyLog.e(TAG, "执行了setLocalSettingAlarmInfo方法1"+settings);
        commitPreferencesSetting(context, settings);
    }

    /**
     * 获得本地设置信息列表：从SharedPreferences中获得。
     *
     * @param context
     * @return
     */
    private static List<DeviceSetting> getLocalSettingInfoList(Context context) {
        SharedPreferences sharedPreferences;
        try {
            sharedPreferences = PreferencesToolkits.getAppDefaultSharedPreferences(context, false);
            String jString = sharedPreferences.getString(PreferencesToolkits.KEY_USER_SETTING_INFO, "");
            System.out.println(jString);
            return CommonUtils.isStringEmpty(jString) ? new ArrayList<DeviceSetting>() : JSON.parseArray(jString, DeviceSetting.class);
        } catch (Exception e) {
//            Log.e();
            return new ArrayList<DeviceSetting>();
        }
    }

    private static void commitPreferencesSetting(Context context, List<DeviceSetting> settings) {

        String jString = JSON.toJSONString(settings);
        SharedPreferences sharedPreferences;
        try {
            sharedPreferences = PreferencesToolkits.getAppDefaultSharedPreferences(context, true);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(PreferencesToolkits.KEY_USER_SETTING_INFO, jString);
            edit.commit();
            MyLog.e(TAG, "执行了commitPreferencesSetting方法"+jString);
        } catch (Exception e) {
//            Log.e(e);
        }

    }

    public static DeviceSetting getLocalSettingInfo(Context context) {
        List<DeviceSetting> settings = getLocalSettingInfoList(context);
        for (int i = 0; i < settings.size(); i++) {
//            if (localSetting.getAlarm_one().equals(user_mail.getAlarm_one()))
//                return localSetting;
//        }
            MyLog.e(TAG,"获取本地存储对象"+settings.get(i).getAlarm_one());
            return settings.get(i);
        }
        return null;
    }
}

