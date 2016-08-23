package com.linkloving.rtring_new.prefrences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.linkloving.rtring_new.prefrences.devicebean.DeviceSetting;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 *  Created by zkx on 2016/3/12.
 *  设备信息UI层保存到本地的逻辑中枢
 */
public class LocalUserSettingsToolkits {
    private static final String TAG = LocalUserSettingsToolkits.class.getSimpleName();
    /**
     * 更新或者添加本地设置信息列表
     * @param context
     * @param deviceSetting
     */
    public static void updateLocalSetting(Context context, DeviceSetting deviceSetting){
        MyLog.i(TAG,"updateLocalSetting");
        List<DeviceSetting> settings = getLocalSettingInfoList(context);
        int index = -1;
        for (int i = 0; i < settings.size(); i++)
        {
            DeviceSetting setting = settings.get(i);
            if (setting.getUser_id().equals(deviceSetting.getUser_id()))
            {
                index = i;
                break;
            }
        }
        if (index != -1)
        {
            settings.remove(index);
        }
        settings.add(deviceSetting);
        commitPreferencesSetting(context, settings);
    }

    /**
     * 通过userId获取DeviceSetting
     * @param context
     * @param userId
     */
    public static DeviceSetting getLocalSetting(Context context,String userId){
        List<DeviceSetting> settings = getLocalSettingInfoList(context);
        for (int i = 0; i < settings.size(); i++)
        {
            DeviceSetting setting = settings.get(i);
            if (setting.getUser_id().equals(userId))
            {
                return setting;
            }
        }
        DeviceSetting deviceSetting = new DeviceSetting();
        deviceSetting.setUser_id(userId);
        return deviceSetting;
    }
    /**
     * 获得本地设置信息列表：从SharedPreferences中获得。
     * @param context
     * @return
     */
    private static List<DeviceSetting> getLocalSettingInfoList(Context context)
    {
        SharedPreferences sharedPreferences;
        try
        {
            sharedPreferences = PreferencesToolkits.getAppDefaultSharedPreferences(context, false);
            String jString = sharedPreferences.getString(PreferencesToolkits.KEY_DEVICE_INFO, "");
            return CommonUtils.isStringEmpty(jString) ? new ArrayList<DeviceSetting>() : JSON.parseArray(jString, DeviceSetting.class);
        }
        catch (Exception e)
        {
            Log.e(TAG,"getLocalSettingInfoList出错");
            return new ArrayList<DeviceSetting>();
        }
    }
    private static List<DeviceSetting> getLocalSettingInfoList_sportgoal(Context context)
    {
        SharedPreferences sharedPreferences;
        try
        {
            sharedPreferences = PreferencesToolkits.getAppDefaultSharedPreferences(context, false);
            String jString = sharedPreferences.getString(PreferencesToolkits.KEY_USER_SETTING_INFO, "");
            return CommonUtils.isStringEmpty(jString) ? new ArrayList<DeviceSetting>() : JSON.parseArray(jString, DeviceSetting.class);
        }
        catch (Exception e)
        {
            Log.e(TAG,"getLocalSettingInfoList出错");
            return new ArrayList<DeviceSetting>();
        }
    }
    private static void commitPreferencesSetting(Context context, List<DeviceSetting> settings)
    {
        String jString = JSON.toJSONString(settings);
        SharedPreferences sharedPreferences;
        try
        {
            sharedPreferences = PreferencesToolkits.getAppDefaultSharedPreferences(context, true);
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString(PreferencesToolkits.KEY_DEVICE_INFO, jString);
            edit.commit();
            MyLog.i(TAG,"getLocalSettingInfoList:commit--:"+jString);
        }
        catch (Exception e)
        {
            MyLog.e(TAG,"commitPreferencesSetting出错");
        }

    }
    /**
     * 设置本地目标信息
     * */
    public static void setLocalSettingGoalInfo(Context context, DeviceSetting localSetting)
    {
        List<DeviceSetting> settings = getLocalSettingInfoList_sportgoal(context);
        int index = -1;
        DeviceSetting needUpdate = null;
        for (int i = 0; i < settings.size(); i++)
        {
            DeviceSetting setting = settings.get(i);
            setting.getUser_mail();
            localSetting.getUser_mail();
            if (setting.getUser_mail().equals(localSetting.getUser_mail()))
            {
                index = i;
                needUpdate = setting;
                needUpdate.setGoal(localSetting.getGoal());
                needUpdate.setGoal_update(localSetting.getGoal_update());
                break;
            }
        }

        if (index == -1)
            settings.add(localSetting);
        else
        {
            settings.remove(index);
            settings.add(needUpdate);
        }

        commitPreferencesSetting(context, settings);

    }
    public static void removeLocalSettingGoalInfo(Context context, String user_mail)
    {
        List<DeviceSetting> settings = getLocalSettingInfoList(context);
        int index = -1;
        DeviceSetting needUpdate = null;
        for (int i = 0; i < settings.size(); i++)
        {
            DeviceSetting localSetting = settings.get(i);
            if (localSetting.getUser_mail().equals(user_mail))
            {
                index = i;
                needUpdate = localSetting;
                needUpdate.setGoal(null);
                break;
            }
        }

        if (index != -1)
        {
            settings.remove(index);
            settings.add(needUpdate);
        }
        commitPreferencesSetting(context, settings);
    }
}
