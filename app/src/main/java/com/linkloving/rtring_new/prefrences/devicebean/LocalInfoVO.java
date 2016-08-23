package com.linkloving.rtring_new.prefrences.devicebean;

import android.content.Context;

import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.band.dto.SleepData;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.utils._Utils;

/**
 * Created by zkx on 2016/3/17.
 */
public class LocalInfoVO {
    public String userId = "-1";

    public String version;
    public byte[] version_byte=new byte[2];

    public  long syncTime;
    public  int timestamp;

    public  int battery;
    public  String deviceId;

    public  int steps;
    public  int calory;
    public  int distance;
    public  int totalsteps;
    public String modelname;

    public  float sleepDeepTime;
    public  float sleepTime;
    /**
     *  余额
     */
    public  String money="0";
    /**
     * 激活状态
     */
    public int recoderStatus = -1;
    /**
     * 设备状态（省电模式）
     */
    public int deviceStatus = 0;


    public void updateByDeviceInfo(Context context, LPDeviceInfo deviceInfo)
    {
        version = deviceInfo.version;
        version_byte = deviceInfo.version_byte;
        recoderStatus = deviceInfo.recoderStatus;
        int level = deviceInfo.charge;
        timestamp = deviceInfo.timeStamp;
        battery = level > 10 ? 100: ( level< 0 ? 0 : level*10 );
        steps = deviceInfo.dayWalkSteps + deviceInfo.dayRunSteps;
        totalsteps = deviceInfo.stepDayTotals;
        int userWeight = CommonUtils.getIntValue(MyApplication.getInstance(context) .getLocalUserInfoProvider().getUserBase().getUser_weight());
        calory = _Utils.calculateCalories(deviceInfo.dayRunDistance/(deviceInfo.dayRunTime*30.0d), deviceInfo.dayRunTime*30, userWeight)
                + _Utils.calculateCalories(deviceInfo.dayWalkDistance/(deviceInfo.dayWalkTime*30.0d), deviceInfo.dayWalkTime*30, userWeight);
        distance = deviceInfo.dayRunDistance + deviceInfo.dayWalkDistance;
        deviceStatus = deviceInfo.deviceStatus;
        modelname = deviceInfo.modelName;
    }

    public void updateBySleepInfo(SleepData sleepInfo)
    {
        sleepDeepTime = (float)sleepInfo.getDeepSleep();
        sleepTime = (float)sleepInfo.getSleep();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public byte[] getVersion_byte() {
        return version_byte;
    }

    public void setVersion_byte(byte[] version_byte) {
        this.version_byte = version_byte;
    }

    public long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getCalory() {
        return calory;
    }

    public void setCalory(int calory) {
        this.calory = calory;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTotalsteps() {
        return totalsteps;
    }

    public void setTotalsteps(int totalsteps) {
        this.totalsteps = totalsteps;
    }

    public float getSleepDeepTime() {
        return sleepDeepTime;
    }

    public void setSleepDeepTime(float sleepDeepTime) {
        this.sleepDeepTime = sleepDeepTime;
    }

    public float getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(float sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getRecoderStatus() {
        return recoderStatus;
    }

    public void setRecoderStatus(int recoderStatus) {
        this.recoderStatus = recoderStatus;
    }

    public int getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getModelname() {
        return modelname;
    }

    public void setModelname(String modelname) {
        this.modelname = modelname;
    }
}
