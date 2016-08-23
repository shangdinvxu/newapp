package com.linkloving.rtring_new.basic.device;

import android.app.Activity;
import android.content.Context;

import com.example.android.bluetoothlegatt.BLEHandler.BLEProviderObserverAdapter;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.logic.UI.device.incomingtel.IncomingTelActivity;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.LocalUserSettingsToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.DeviceSetting;
import com.linkloving.rtring_new.utils.DeviceInfoHelper;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

/**
 * Created by zkx on 2016/3/21.
 */
public class BleSynchronImpl implements BleSynchron {
    private static final String TAG = BleSynchronImpl.class.getSimpleName();

//    /**当前要走的流程值*/
//    public static int LAST_STATE = 0;
//    /**0x13命令*/
//    public static final int COMMAND_13 = 1;
//    /**身体信息设置命令*/
//    public static final int COMMAND_BODY = COMMAND_13 + 1;
//    /**消息提醒命令*/
//    public static final int COMMAND_NOTIFY = COMMAND_13 + 1;
//    /**闹钟命令*/
//    public static final int COMMAND_CLOCK = COMMAND_NOTIFY + 1;
//    /**久坐提醒命令*/
//    public static final int COMMAND_LONGSIT = COMMAND_CLOCK + 1;
//    /**勿扰模式设置命令*/
//    public static final int COMMAND_HANDUP = COMMAND_BODY + 1;
//    /**运动数据命令*/
//    public static final int COMMAND_SPORT = COMMAND_HANDUP + 1;
//    /**设置时间命令*/
//    public static final int COMMAND_TIME = COMMAND_SPORT + 1;
//    /**获取卡号命令*/
//    public static final int COMMAND_CARD = COMMAND_TIME + 1;

    private Context context;
    private BLEProvider provider;
    private UserEntity userEntity;

    /******蓝牙回调******/
    private BLEProviderObserverAdapter bleProviderObserver = null;

    public BleSynchronImpl(Context context,BLEProvider provider_) {
        this.context = context;
        if(this.provider==null)
            this.provider = provider_;
        if(provider.getBleProviderObserver()==null){
            MyLog.e(TAG,"provider.getBleProviderObserver()==null");
            bleProviderObserver = new BLEProviderObserverAdapterImpl();
            this.provider.setBleProviderObserver(bleProviderObserver);
        }
    }

    public static BleSynchronImpl instance ;

    public static BleSynchronImpl getInstance(Context context_,BLEProvider provider)
    {
        if(instance==null)
            instance = new BleSynchronImpl(context_,provider);
        return instance;
    }

    public void setBleProviderObserver(BLEProviderObserverAdapter bleObserver){
        if(provider.getBleProviderObserver()==null)
            provider.setBleProviderObserver(bleObserver);
    }


    @Override
    public void synchronAll() {
        if(provider.isConnectedAndDiscovered())
        {
            provider.getAllDeviceInfoNew(context); //获得设备信息
        }
    }

    private class BLEProviderObserverAdapterImpl extends BLEProviderObserverAdapter{

        @Override
        protected Activity getActivity() {
            return (Activity) context;
        }

        /**0x13--->去设置身体信息*/
        @Override
        public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
            super.updateFor_notifyFor0x13ExecSucess_D(latestDeviceInfo);
            MyLog.d(TAG, "updateFor_notifyFor0x13ExecSucess_D");
            userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
            //命令停止
                    provider.regiesterNew(context, DeviceInfoHelper.fromUserEntity(context,userEntity));
        }

        /**设置身体信息成功失败*/
        @Override
        public void updateFor_notifyForSetBodyFail() {
            super.updateFor_notifyForSetBodySucess();
            provider.regiesterNew(context, DeviceInfoHelper.fromUserEntity(context,userEntity));

        }

        /**设置身体信息成功成功--->去设置消息提醒*/
        @Override
        public void updateFor_notifyForSetBodySucess() {
            super.updateFor_notifyForSetBodySucess();
            MyLog.d(TAG, "updateFor_notifyForSetBodySucess");
            /***********************设置消息提醒**********************/
            DeviceSetting deviceSetting = LocalUserSettingsToolkits.getLocalSetting(context, userEntity.getUserBase().getUser_id()+"");
            int ancs = deviceSetting.getANCS_value();
            byte[] send_data = IncomingTelActivity.intto2byte(ancs);
            provider.setNotification(context, send_data);
            /***********************设置消息提醒**********************/
        }


        /**设置消息提醒失败*/
        @Override
        public void notifyForSetNOTIFYFail() {
            super.notifyForSetNOTIFYFail();
            /***********************设置消息提醒**********************/
            DeviceSetting deviceSetting = LocalUserSettingsToolkits.getLocalSetting(context, userEntity.getUserBase().getUser_id()+"");
            int ancs = deviceSetting.getANCS_value();
            byte[] send_data = IncomingTelActivity.intto2byte(ancs);
            if(provider.isConnectedAndDiscovered())
                provider.setNotification(context, send_data);
            /***********************设置消息提醒**********************/


        }

        /**设置消息提醒成功--->去设置闹钟*/
        @Override
        public void updateFor_notify() {
            super.updateFor_notify();
                provider.SetClock(context, DeviceInfoHelper.fromUserEntity(context,userEntity));

        }


        /**设置闹钟提醒失败*/
        @Override
        public void updateFor_notifyForSetClockFail() {
            super.updateFor_notifyForSetClockFail();
                if(provider.isConnectedAndDiscovered())
                    provider.SetClock(context, DeviceInfoHelper.fromUserEntity(context,userEntity));
        }

        /**设置闹钟提醒成功--->去设置久坐*/
        @Override
        public void updateFor_notifyForSetClockSucess() {
            super.updateFor_notifyForSetClockSucess();
                provider.SetLongSit(context, DeviceInfoHelper.fromUserEntity(context,userEntity));
        }

        /**设置久坐提醒失败*/
        @Override
        public void updateFor_notifyForLongSitFail() {
            super.updateFor_notifyForLongSitFail();
                if(provider.isConnectedAndDiscovered())
                    provider.SetLongSit(context, DeviceInfoHelper.fromUserEntity(context,userEntity));
        }

        /**设置久坐提醒成功 --->去设置勿扰（抬手）*/
        @Override
        public void updateFor_notifyForLongSitSucess() {
            super.updateFor_notifyForLongSitSucess();
                provider.SetHandUp(context, DeviceInfoHelper.fromUserEntity(context,userEntity));
        }

        /**设置抬手（勿扰）提醒失败*/
        @Override
        public void updateFor_notifyForSetHandUpFail() {
            super.updateFor_notifyForSetHandUpFail();
                if(provider.isConnectedAndDiscovered())
                    provider.SetHandUp(context, DeviceInfoHelper.fromUserEntity(context,userEntity));
        }

        /**设置抬手（勿扰）提醒成功 --->获取运动数据*/
        @Override
        public void updateFor_notifyForSetHandUpSucess() {
            super.updateFor_notifyForSetHandUpSucess();
            provider.getSportDataNew(context);
        }

        /**运动数据获取完毕--->设置时间*/
        @Override
        public void updateFor_handleDataEnd() {
            super.updateFor_handleDataEnd();
            provider.SetDeviceTime(context);
        }

        /**时间设置失败*/
        @Override
        public void updateFor_handleSetTimeFail() {
            super.updateFor_handleSetTimeFail();
            if(provider.isConnectedAndDiscovered())
                provider.SetDeviceTime(context);
        }

        /**时间设置成功*/
        @Override
        public void updateFor_handleSetTime() {
            super.updateFor_handleSetTime();
                provider.SetPower(context, DeviceInfoHelper.fromUserEntity(context,userEntity));
                provider.get_cardnum(context);
        }

        /**设置省电成功*/
        @Override
        public void updateFor_notifyForSetPowerSucess() {
            super.updateFor_notifyForSetPowerSucess();
        }

        /**设置省电失败*/
        @Override
        public void updateFor_notifyForSetPowerFail() {
            super.updateFor_notifyForSetPowerFail();
        }

        /**获取卡号失败*/
        @Override
        public void updateFor_CardNumber_fail() {
            super.updateFor_CardNumber_fail();
            if(provider.isConnectedAndDiscovered())
                 provider.getdeviceId(context);
        }

        /**获取卡号成功*/
        @Override
        public void updateFor_CardNumber(String card_number) {
            super.updateFor_CardNumber(card_number);
            provider.getdeviceId(context);

        }


        /**获取getDeviceId*/
        @Override
        public void updateFor_getDeviceId(String obj) {

        }

        /**设置步数成功*/
        @Override
        public void updateFor_notifyForSetStepSucess() {
            super.updateFor_notifyForSetStepSucess();
        }

        /**设置步数失败*/
        @Override
        public void updateFor_notifyForSetStepFail() {
            super.updateFor_notifyForSetStepFail();

        }

    }


}
