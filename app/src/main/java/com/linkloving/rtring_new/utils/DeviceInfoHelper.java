package com.linkloving.rtring_new.utils;

import android.content.Context;
import android.util.Log;

import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.LocalUserSettingsToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.DeviceSetting;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zkx on 2016/3/17.
 */
public class DeviceInfoHelper {
    private static final String TAG = DeviceInfoHelper.class.getSimpleName();

    public static DaySynopic toDaySynopic(LPDeviceInfo deviceInfo)
    {
        DaySynopic daySynopi = null;
        if(deviceInfo != null)
        {
            daySynopi = new DaySynopic();
            daySynopi.setData_date(new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date()));
            daySynopi.setData_date2(new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS).format(new Date()));

            daySynopi.setRun_step(String.valueOf(deviceInfo.dayRunSteps));
            daySynopi.setRun_distance(String.valueOf(deviceInfo.dayRunDistance));
            daySynopi.setRun_duration(String.valueOf(deviceInfo.dayRunTime * 30));// *30才是秒，否则是时间片

            daySynopi.setWork_step(String.valueOf(deviceInfo.dayWalkSteps));
            daySynopi.setWork_distance(String.valueOf(deviceInfo.dayWalkDistance));
            daySynopi.setWork_duration(String.valueOf(deviceInfo.dayWalkTime * 30));// *30才是秒，否则是时间片
        }

        return daySynopi;
    }

    public static LPDeviceInfo fromDaySynopic(DaySynopic daySynopic)
    {
        LPDeviceInfo deviceInfo = new LPDeviceInfo();
        deviceInfo.dayRunSteps = Integer.parseInt(daySynopic.getRun_step());
        Log.i(TAG, "deviceInfo.dayRunSteps:"+daySynopic.getRun_step());
        deviceInfo.dayRunDistance = Integer.parseInt(daySynopic.getRun_distance());
        deviceInfo.dayRunTime = Integer.parseInt(daySynopic.getRun_duration()) / 30; // 需要转成时间片

        deviceInfo.dayWalkDistance = Integer.parseInt(daySynopic.getWork_distance());
        deviceInfo.dayWalkSteps = Integer.parseInt(daySynopic.getWork_step());
        deviceInfo.dayWalkTime = Integer.parseInt(daySynopic.getWork_duration()) / 30; // 需要转成时间片
        return deviceInfo;
    }

    public static final LPDeviceInfo fromUserEntity(Context context ,UserEntity userEntity)
    {
        DeviceSetting deviceSetting = LocalUserSettingsToolkits.getLocalSetting(context, userEntity.getUser_id()+"");
        LPDeviceInfo deviceInfo = new LPDeviceInfo();
        //默认设置进去激活状态
        deviceInfo.recoderStatus=1;
        //从出生日期算出用户年龄
        try {
            Date dt = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).parse(userEntity.getUserBase().getBirthdate());
            deviceInfo.userAge = (int)((System.currentTimeMillis() - dt.getTime())/(365L * 24 * 60 * 60 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //性别
        deviceInfo.userGender = userEntity.getUserBase().getUser_sex();
        deviceInfo.userHeight = userEntity.getUserBase().getUser_height();
        deviceInfo.userId = userEntity.getUser_id();
        deviceInfo.userNickname = userEntity.getUserBase().getNickname();
        deviceInfo.userWeight = userEntity.getUserBase().getUser_weight();
        //这个暂时没用到 不知道何作用
        deviceInfo.dayIndex = Integer.parseInt("10");
        deviceInfo.step = userEntity.getUserBase().getPlay_calory();// TODO

        // 久坐提醒间隔是负值的话，就意味着关闭，那么直接把start和end time置0就行了。
        String[] LongsitData = deviceSetting.getLongsit_time().split("-");
        String Longsit_vaild = deviceSetting.getLongsit_vaild();
        if(Longsit_vaild .equals("0"))  //关闭了久坐提醒
        {
            // 早上提醒的开始时间(小时)
            deviceInfo.startTime1_H = 0;
            // 早上提醒的开始时间(分钟)
            deviceInfo.startTime1_M = 0;
            // 早上结束的开始时间(小时)
            deviceInfo.endTime1_H = 0;
            // 早上结束的开始时间(分钟)
            deviceInfo.endTime1_M = 0;
            // 下午提醒的开始时间(小时)
            deviceInfo.startTime2_H = 0;
            // 下午提醒的开始时间(分钟)
            deviceInfo.startTime2_M = 0;
            // 下午提醒的结束时间(小时)
            deviceInfo.endTime2_H = 0;
            // 下午提醒的结束时间(分钟)
            deviceInfo.endTime2_M = 0;
        }
        else
        {
            //提醒的时间段内,运动步数少于此值,提醒
            deviceInfo.longsit_step =  Integer.parseInt(deviceSetting.getLongsit_step());
            // 早上提醒的开始时间(小时)
            deviceInfo.startTime1_H = Integer.parseInt(LongsitData[0].split(":")[0]);
            // 早上提醒的开始时间(分钟)
            deviceInfo.startTime1_M = Integer.parseInt(LongsitData[0].split(":")[1]);
            // 早上结束的开始时间(小时)
            deviceInfo.endTime1_H = Integer.parseInt(LongsitData[1].split(":")[0]);
            // 早上结束的开始时间(分钟)
            deviceInfo.endTime1_M = Integer.parseInt(LongsitData[1].split(":")[1]);
            // 下午提醒的开始时间(小时)
            deviceInfo.startTime2_H = Integer.parseInt(LongsitData[2].split(":")[0]);
            // 下午提醒的开始时间(分钟)
            deviceInfo.startTime2_M = Integer.parseInt(LongsitData[2].split(":")[1]);
            // 下午提醒的结束时间(小时)
            deviceInfo.endTime2_H = Integer.parseInt(LongsitData[3].split(":")[0]);
            // 下午提醒的结束时间(分钟)
            deviceInfo.endTime2_M = Integer.parseInt(LongsitData[3].split(":")[1]);
        }
        // 勿扰模式关闭，那么直接把start和end time置0就行了。
        String Disturb_Valid=deviceSetting.getDisturb_vaild();
        if(Integer.parseInt(Disturb_Valid) > 0)
        {
                    String[] HandupData = deviceSetting.getDisturb_time().split("-");
                    int start_time_hour = Integer.parseInt(HandupData[0].split(":")[0]);
                    int start_time_minute = Integer.parseInt(HandupData[0].split(":")[1]);
                    int end_time_hour =Integer.parseInt(HandupData[1].split(":")[0]);
                    int end_time_minute = Integer.parseInt(HandupData[1].split(":")[1]);

             if(start_time_hour < end_time_hour || (
                     start_time_hour == end_time_hour
                            && (start_time_minute)
                            < end_time_minute
            )){

                        deviceInfo.handup_startTime1_H = 0;
                        deviceInfo.handup_startTime1_M = 0;
                        deviceInfo.handup_endTime1_H   = start_time_hour ;
                        deviceInfo.handup_endTime1_M   = start_time_minute ;

                        deviceInfo.handup_startTime2_H = end_time_hour ;
                        deviceInfo.handup_startTime2_M = end_time_minute ;
                        deviceInfo.handup_endTime2_H   = 23;
                        deviceInfo.handup_endTime2_M   = 59;
                    }
                    else
                    {
                        deviceInfo.handup_startTime1_H = end_time_hour;
                        deviceInfo.handup_startTime1_M = end_time_minute;
                        deviceInfo.handup_endTime1_H   = start_time_hour;
                        deviceInfo.handup_endTime1_M   = start_time_minute ;
                    }
        }
        else
        {

            deviceInfo.handup_startTime1_H = 0;
            deviceInfo.handup_startTime1_M = 0 ;
            deviceInfo.handup_endTime1_H   = 23;
            deviceInfo.handup_endTime1_M   = 59 ;
        }

        //闹钟1
        String Alarm_one = deviceSetting.getAlarm_one();
        String[] strAlarm_one = Alarm_one.split("-");
        int Repeat_1 = Integer.parseInt(strAlarm_one[1]);
        int valid_1 = Integer.parseInt(strAlarm_one[2]);
        String [] strTime_1=strAlarm_one[0].split(":");

            if(valid_1 != 0)
            {
                deviceInfo.alarmTime1_H = Integer.parseInt(strTime_1[0]);
                deviceInfo.alarmTime1_M =Integer.parseInt(strTime_1[1]);
                deviceInfo.frequency1 = Repeat_1;

                MyLog.i(TAG,"deviceInfo.alarmTime1_H："+deviceInfo.alarmTime1_H);
                MyLog.i(TAG,"deviceInfo.alarmTime1_M："+deviceInfo.alarmTime1_M);
                MyLog.i(TAG,"deviceInfo.frequency1："+deviceInfo.frequency1);
            }

        //闹钟2
        String Alarm_two = deviceSetting.getAlarm_two();
        String[] strAlarm_two = Alarm_two.split("-");
        int Repeat_2 = Integer.parseInt(strAlarm_two[1]);
        int valid_2 = Integer.parseInt(strAlarm_two[2]);
        String [] strTime_2=strAlarm_two[0].split(":");

            if(valid_2 != 0)
            {
                deviceInfo.alarmTime2_H =Integer.parseInt(strTime_2[0]);
                deviceInfo.alarmTime2_M =Integer.parseInt(strTime_2[1]);
                deviceInfo.frequency2 = Repeat_2;
            }

        //闹钟3
        String Alarm_three = deviceSetting.getAlarm_three();
        String[] strAlarm_three = Alarm_three.split("-");
        int Repeat_3 = Integer.parseInt(strAlarm_three[1]);
        int valid_3 = Integer.parseInt(strAlarm_three[2]);
        String [] strTime_3=strAlarm_three[0].split(":");
            if(valid_3 != 0)
            {
                deviceInfo.alarmTime3_H = Integer.parseInt(strTime_3[0]);
                deviceInfo.alarmTime3_M =Integer.parseInt(strTime_3[1]);
                deviceInfo.frequency3 = Repeat_3;
            }

        deviceInfo.deviceStatus = deviceSetting.getElectricity_value();
        MyLog.i(TAG,deviceInfo.toString());
//		 deviceInfo.startTime = Integer.parseInt(userEntity.get);
        return deviceInfo;
    }

}
