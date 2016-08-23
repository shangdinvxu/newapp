package com.linkloving.rtring_new.logic.UI.device.power;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.LocalUserSettingsToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.DeviceSetting;
import com.linkloving.rtring_new.utils.DeviceInfoHelper;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

public class PowerActivity extends ToolBarActivity implements View.OnClickListener {
    private static final String TAG = PowerActivity.class.getSimpleName();

    TextView pm1content_1,pm1content_2,pm2content_1,pm2content_2,pm2content_3,pm3content_1,pm3content_2,pm3content_3;

    ImageView imageView_pm1,imageView_pm2;

    ImageView pm1_pic,pm2_pic;
    private int CURRENT_STATUS;
    DeviceSetting localSeting;
    //蓝牙观察者
    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;
    //蓝牙提供者
    private BLEProvider provider;
    UserEntity userEntity;
    Button BLE_Button;
    private LinearLayout Synchronize_device;

    View view;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_power);
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        Change();
    }
    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((R.string.power_setting)));
        view= LayoutInflater.from(this).inflate(R.layout.toast_synchronize, null);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = getResources().getDimensionPixelSize(resourceId);
        //得到Toast对象
        toast = new Toast(PowerActivity.this);
        //设置Toast对象的位置，3个参数分别为位置，X轴偏移，Y轴偏
        toast.setGravity(Gravity.TOP, 0, result+50);
        //设置Toast对象的显示时间
        toast.setDuration(Toast.LENGTH_LONG);
        //设置Toast对象所要展示的视图
        toast.setView(view);


        pm1content_1= (TextView) findViewById(R.id.text_pm1_content_1);
        pm1content_2= (TextView) findViewById(R.id.text_pm1_content_2);

        pm2content_1= (TextView) findViewById(R.id.text_pm2_content_1);
        pm2content_2= (TextView) findViewById(R.id.text_pm2_content_2);
        pm2content_3= (TextView) findViewById(R.id.text_pm2_content_3);

        pm3content_1= (TextView) findViewById(R.id.text_pm3_content_1);
        pm3content_2= (TextView) findViewById(R.id.text_pm3_content_2);
        pm3content_3= (TextView) findViewById(R.id.text_pm3_content_3);

        imageView_pm1= (ImageView) findViewById(R.id.imageView_pm1);
        imageView_pm2= (ImageView) findViewById(R.id.imageView_pm2);

        pm1_pic= (ImageView) findViewById(R.id.pm1_pic);
        pm2_pic= (ImageView) findViewById(R.id.pm2_pic);

        BLE_Button= (Button) findViewById(R.id.power_btn_Synchronize_device);
        Synchronize_device= (LinearLayout) findViewById(R.id.power_Synchronize_device);


    }

    @Override
    protected void initListeners() {
        imageView_pm1.setOnClickListener(this);
        imageView_pm2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_pm1:
                if(CURRENT_STATUS==0||CURRENT_STATUS==2){
                    //设置状态为1
                    localSeting.setElectricity_value(1);
                    LocalUserSettingsToolkits.updateLocalSetting(PowerActivity.this, localSeting);

                    //判断蓝牙是否连接
                    if (provider.isConnectedAndDiscovered()) {
                        //同步到设备
                        provider.SetPower(PowerActivity.this, DeviceInfoHelper.fromUserEntity(PowerActivity.this, userEntity));
                        Synchronize_device.setVisibility(View.VISIBLE);
                        BLE_Button.setText(getString(R.string.synchronized_to_device));
                        toast.show();
                    }else{

                    }

                }
                else if(CURRENT_STATUS==1){
                    // 设置状态为0
                    localSeting.setElectricity_value(0);
                    LocalUserSettingsToolkits.updateLocalSetting(PowerActivity.this,localSeting);
                    //判断蓝牙是否连接
                    if (provider.isConnectedAndDiscovered()) {
                        //同步到设备
                        provider.SetPower(PowerActivity.this, DeviceInfoHelper.fromUserEntity(PowerActivity.this, userEntity));
                        Synchronize_device.setVisibility(View.VISIBLE);
                        BLE_Button.setText(getString(R.string.synchronized_to_device));
                        toast.show();
                    }else{
               /* android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(PowerActivity.this)
                        .setTitle(ToolKits.getStringbyId(PowerActivity.this, R.string.portal_main_unbound))
                        .setMessage(ToolKits.getStringbyId(PowerActivity.this, R.string.portal_main_unbound_msg))
                        .setPositiveButton(ToolKits.getStringbyId(PowerActivity.this, R.string.general_ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).create();
                dialog.show();*/
                        MyToast.showLong(PowerActivity.this, getString(R.string.pay_no_connect));
                    }
                }



                Change();

                break;
            case R.id.imageView_pm2:

                if(CURRENT_STATUS==0||CURRENT_STATUS==1) {
                    //设置状态为2
                    localSeting.setElectricity_value(2);

                    LocalUserSettingsToolkits.updateLocalSetting(PowerActivity.this,localSeting);
                    //判断蓝牙是否连接
                    if (provider.isConnectedAndDiscovered()) {
                        //同步到设备
                        provider.SetPower(PowerActivity.this, DeviceInfoHelper.fromUserEntity(PowerActivity.this, userEntity));
                        Synchronize_device.setVisibility(View.VISIBLE);
                        BLE_Button.setText(getString(R.string.synchronized_to_device));
                        toast.show();
                    }else{
                       /* android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(PowerActivity.this)
                                .setTitle(ToolKits.getStringbyId(PowerActivity.this, R.string.portal_main_unbound))
                                .setMessage(ToolKits.getStringbyId(PowerActivity.this, R.string.portal_main_unbound_msg))
                                .setPositiveButton(ToolKits.getStringbyId(PowerActivity.this, R.string.general_ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).create();
                        dialog.show();*/
                        MyToast.showLong(PowerActivity.this, getString(R.string.pay_no_connect));

                    }
                }
                else if(CURRENT_STATUS==2){
                    // 设置状态为0
                    localSeting.setElectricity_value(0);
                    LocalUserSettingsToolkits.updateLocalSetting(PowerActivity.this,localSeting);
//判断蓝牙是否连接
                    if (provider.isConnectedAndDiscovered()) {
                        //同步到设备
                        provider.SetPower(PowerActivity.this, DeviceInfoHelper.fromUserEntity(PowerActivity.this,userEntity));
                        Synchronize_device.setVisibility(View.VISIBLE);
                        BLE_Button.setText(getString(R.string.synchronized_to_device));
                        toast.show();
                    }else{
                      /*  android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(PowerActivity.this)
                                .setTitle(ToolKits.getStringbyId(PowerActivity.this, R.string.portal_main_unbound))
                                .setMessage(ToolKits.getStringbyId(PowerActivity.this, R.string.portal_main_unbound_msg))
                                .setPositiveButton(ToolKits.getStringbyId(PowerActivity.this, R.string.general_ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).create();
                        dialog.show();*/
                        MyToast.showLong(PowerActivity.this, getString(R.string.pay_no_connect));

                    }
                }

                Change();

                break;
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (provider.getBleProviderObserver() == null) {
            provider.setBleProviderObserver(bleProviderObserver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        provider.setBleProviderObserver(null);
    }

    private void Change(){

        //每次获取本地的设置信息
        localSeting=LocalUserSettingsToolkits.getLocalSetting(PowerActivity.this,userEntity.getUser_id()+"");

        CURRENT_STATUS=localSeting.getElectricity_value();

        MyLog.i(TAG,"CURRENT_STATUS"+CURRENT_STATUS);

        switch (localSeting.getElectricity_value()){
            //pm1
            case 1:
                imageView_pm1.setImageResource(R.mipmap.btn_on);
                pm1_pic.setImageResource(R.mipmap.battery_pm1_on);
                pm1content_1.setTextColor(getResources().getColor(R.color.orange));
                pm1content_2.setTextColor(getResources().getColor(R.color.orange));

                imageView_pm2.setImageResource(R.mipmap.btn_off);
                pm2_pic.setImageResource(R.mipmap.battery_pm2_off);
                pm2content_1.setTextColor(getResources().getColor(R.color.text_gray));
                pm2content_2.setTextColor(getResources().getColor(R.color.text_gray));
                pm2content_3.setTextColor(getResources().getColor(R.color.text_gray));
                break;
            //pm2
            case 2:
                imageView_pm2.setImageResource(R.mipmap.btn_on);
                pm2_pic.setImageResource(R.mipmap.battery_pm2_on);
                pm1content_1.setTextColor(getResources().getColor(R.color.text_gray));
                pm1content_2.setTextColor(getResources().getColor(R.color.text_gray));

                pm2content_1.setTextColor(getResources().getColor(R.color.orange));
                pm2content_2.setTextColor(getResources().getColor(R.color.orange));
                pm2content_3.setTextColor(getResources().getColor(R.color.orange));


                imageView_pm1.setImageResource(R.mipmap.btn_off);
                pm1_pic.setImageResource(R.mipmap.battery_pm1_off);
                break;
            //没选
            case 0:
                imageView_pm1.setImageResource(R.mipmap.btn_off);
                pm1_pic.setImageResource(R.mipmap.battery_pm1_off);
                imageView_pm2.setImageResource(R.mipmap.btn_off);
                pm2_pic.setImageResource(R.mipmap.battery_pm2_off);

                pm1content_1.setTextColor(getResources().getColor(R.color.text_gray));
                pm1content_2.setTextColor(getResources().getColor(R.color.text_gray));

                pm2content_1.setTextColor(getResources().getColor(R.color.text_gray));
                pm2content_2.setTextColor(getResources().getColor(R.color.text_gray));
                pm2content_3.setTextColor(getResources().getColor(R.color.text_gray));

                break;
        }





    }
    /**
     * 蓝牙观察者实现类.
     */
    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return PowerActivity.this;
        }

        public BLEProviderObserverAdapterImpl() {
            super();
        }

        @Override
        public void updateFor_notifyForSetClockSucess() {
            super.updateFor_notifyForSetClockSucess();

        }

        @Override
        public void updateFor_notifyForSetClockFail() {
            super.updateFor_notifyForSetClockFail();
        }

    }


}
