package com.linkloving.rtring_new.logic.UI.device.alarm;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.LocalUserSettingsToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.DeviceSetting;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.DeviceInfoHelper;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.text.ParseException;

public class AlarmActivity extends ToolBarActivity implements View.OnClickListener {
    private static final String TAG = AlarmActivity.class.getSimpleName();

    public static final int ALARM_ONE = 123;
    public static final int ALARM_TWO = 124;
    public static final int ALARM_THREE = 125;
    private DeviceSetting deviceSetting;
    private ImageView mImageView;
    private ImageView mImageView1;
    private ImageView mImageView2;
    private LinearLayout mLinearLayout;
    private LinearLayout mLinearLayout1;
    private LinearLayout mLinearLayout2;
    private TextView tv_showtime, tv_showtime1, tv_showtime2;
    private boolean tv_color = false;
    private boolean tv_color1 = false;
    private boolean tv_color2 = false;
    String strTime;
    String strTime1;
    String strTime2;
    String strRepeat;
    String strRepeat1;
    String strRepeat2;
    View view;
    Toast toast;
    //蓝牙观察者
    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;
    //蓝牙提供者
    private BLEProvider provider;
    UserEntity userEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        userEntity = MyApplication.getInstance(AlarmActivity.this).getLocalUserInfoProvider();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        initData();
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((R.string.alarm_notify)));
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        mLinearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        mLinearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);

        mImageView = (ImageView) findViewById(R.id.img_switch);
        tv_showtime = (TextView) findViewById(R.id.tv_showtime);

        mImageView1 = (ImageView) findViewById(R.id.img_switch1);
        tv_showtime1 = (TextView) findViewById(R.id.tv_showtime1);

        mImageView2 = (ImageView) findViewById(R.id.img_switch2);
        tv_showtime2 = (TextView) findViewById(R.id.tv_showtime2);


        view= LayoutInflater.from(this).inflate(R.layout.toast_synchronize, null);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = getResources().getDimensionPixelSize(resourceId);
        //得到Toast对象
        toast = new Toast(AlarmActivity.this);
        //设置Toast对象的位置，3个参数分别为位置，X轴偏移，Y轴偏
        toast.setGravity(Gravity.TOP, 0, result+50);
        //设置Toast对象的显示时间
        toast.setDuration(Toast.LENGTH_LONG);
        //设置Toast对象所要展示的视图
        toast.setView(view);
    }

    private void initData() {
        deviceSetting = LocalUserSettingsToolkits.getLocalSetting(AlarmActivity.this, userEntity.getUser_id()+"");
        String Alarm_one = deviceSetting.getAlarm_one();
        MyLog.e(TAG, "闹钟字符串：" + Alarm_one);
        String[] strAlarm_one = Alarm_one.split("-");
        strTime = strAlarm_one[0];
        strRepeat = strAlarm_one[1];
        initCheckBoxStatus(Integer.parseInt(strRepeat), ALARM_ONE);
        MyLog.e(TAG, "字符串数组的长度：" + strAlarm_one.length + "重复周期为：" + Integer.parseInt(strRepeat));
        if (Integer.parseInt(strAlarm_one[2]) == 0) {
            mImageView.setImageResource(R.mipmap.btn_off);
            tv_showtime.setTextColor(getResources().getColor(R.color.text_gray));
            tv_showtime.setText(strTime);
            tv_color = false;
        } else {
            mImageView.setImageResource(R.mipmap.btn_on);
            tv_showtime.setTextColor(getResources().getColor(R.color.yellow_title));
            tv_showtime.setText(strTime);
            tv_color = true;
        }
        String Alarm_two = deviceSetting.getAlarm_two();
        String[] strAlarm_two = Alarm_two.split("-");
        strTime1 = strAlarm_two[0];
        strRepeat1 = strAlarm_two[1];
        initCheckBoxStatus(Integer.parseInt(strRepeat1), ALARM_TWO);
        MyLog.e(TAG, "字符串数组1的长度：" + strAlarm_two.length);
        if (Integer.parseInt(strAlarm_two[2]) == 0) {
            mImageView1.setImageResource(R.mipmap.btn_off);
            tv_showtime1.setTextColor(getResources().getColor(R.color.text_gray));
            tv_showtime1.setText(strAlarm_two[0]);
            tv_color1 = false;
        } else {
            mImageView1.setImageResource(R.mipmap.btn_on);
            tv_showtime1.setTextColor(getResources().getColor(R.color.yellow_title));
            tv_showtime1.setText(strAlarm_two[0]);
            tv_color1 = true;
        }
        String Alarm_three = deviceSetting.getAlarm_three();
        String[] strAlarm_three = Alarm_three.split("-");
        strTime2 = strAlarm_three[0];
        strRepeat2 = strAlarm_three[1];
        initCheckBoxStatus(Integer.parseInt(strRepeat2), ALARM_THREE);
        MyLog.e(TAG, "字符串数组2的长度：" + strAlarm_three.length + "时间数据：" + strAlarm_three[0]);
        if (Integer.parseInt(strAlarm_three[2]) == 0) {
            mImageView2.setImageResource(R.mipmap.btn_off);
            tv_showtime2.setTextColor(getResources().getColor(R.color.text_gray));
            tv_showtime2.setText(strAlarm_three[0]);
            tv_color2 = false;
        } else {
            mImageView2.setImageResource(R.mipmap.btn_on);
            tv_showtime2.setTextColor(getResources().getColor(R.color.yellow_title));
            tv_showtime2.setText(strAlarm_three[0]);
            tv_color2 = true;
        }
    }

    @Override
    protected void initListeners() {
        mLinearLayout.setOnClickListener(this);
        mLinearLayout1.setOnClickListener(this);
        mLinearLayout2.setOnClickListener(this);

        mImageView.setOnClickListener(this);

        mImageView1.setOnClickListener(this);

        mImageView2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_switch:
                if (tv_color == false) {
                    if(strRepeat.equals("0")){
                        IntentFactory.start_SetAlarmActivity(AlarmActivity.this, ALARM_ONE, tv_showtime.getText().toString(),strRepeat);
                    }else {
                        mImageView.setImageResource(R.mipmap.btn_on);
                        tv_showtime.setTextColor(getResources().getColor(R.color.yellow_title));
                        tv_color = true;
                        try {
                            setNotificition(ALARM_ONE);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    mImageView.setImageResource(R.mipmap.btn_off);
                    tv_showtime.setTextColor(getResources().getColor(R.color.text_gray));
                    tv_color = false;
                    try {
                        setNotificition(ALARM_ONE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.linearLayout:
                IntentFactory.start_SetAlarmActivity(AlarmActivity.this, ALARM_ONE, tv_showtime.getText().toString(),strRepeat);
                break;


            case R.id.img_switch1:
                if (tv_color1 == false) {
                    if(strRepeat1.equals("0")){
                        IntentFactory.start_SetAlarmActivity(AlarmActivity.this, ALARM_TWO, tv_showtime.getText().toString(),strRepeat1);
                    }else {
                        mImageView1.setImageResource(R.mipmap.btn_on);
                        tv_showtime1.setTextColor(getResources().getColor(R.color.yellow_title));
                        tv_color1 = true;
                        try {
                            setNotificition(ALARM_TWO);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    mImageView1.setImageResource(R.mipmap.btn_off);
                    tv_showtime1.setTextColor(getResources().getColor(R.color.text_gray));
                    tv_color1 = false;
                    try {
                        setNotificition(ALARM_TWO);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.linearLayout1:
                IntentFactory.start_SetAlarmActivity(AlarmActivity.this, ALARM_TWO, tv_showtime1.getText().toString(),strRepeat1);
                break;

            case R.id.img_switch2:
                if (tv_color2 == false) {
                    if(strRepeat2.equals("0")){
                        IntentFactory.start_SetAlarmActivity(AlarmActivity.this, ALARM_THREE, tv_showtime.getText().toString(),strRepeat2);
                    }else {
                        mImageView2.setImageResource(R.mipmap.btn_on);
                        tv_showtime2.setTextColor(getResources().getColor(R.color.yellow_title));
                        tv_color2 = true;
                        try {
                            setNotificition(ALARM_THREE);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    mImageView2.setImageResource(R.mipmap.btn_off);
                    tv_showtime2.setTextColor(getResources().getColor(R.color.text_gray));
                    tv_color2 = false;
                    try {
                        setNotificition(ALARM_THREE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.linearLayout2:
                IntentFactory.start_SetAlarmActivity(AlarmActivity.this, ALARM_THREE, tv_showtime2.getText().toString(),strRepeat2);
                break;
        }
    }
    private void setNotificition(int i) throws ParseException {
        if (i == ALARM_ONE) {
            String Alarm_one_notif_ = strTime + "-" + strRepeat + "-" + (tv_color ? 1 : 0);
            deviceSetting.setAlarm_one(Alarm_one_notif_);
            LocalUserSettingsToolkits.updateLocalSetting(AlarmActivity.this, deviceSetting);
            Synchronize_device();
        } else if (i == ALARM_TWO) {
            String Alarm_two_notif_ = strTime1 + "-" + strRepeat1 + "-" + (tv_color1 ? 1 : 0);
            deviceSetting.setAlarm_two(Alarm_two_notif_);
            LocalUserSettingsToolkits.updateLocalSetting(AlarmActivity.this, deviceSetting);
            Synchronize_device();
        } else if (i == ALARM_THREE) {
            String Alarm_three_notif_ = strTime2 + "-" + strRepeat2 + "-" + (tv_color2 ? 1 : 0);
            deviceSetting.setAlarm_three(Alarm_three_notif_);
            LocalUserSettingsToolkits.updateLocalSetting(AlarmActivity.this, deviceSetting);
            Synchronize_device();
        }
    }
    //同步到设备
    private void Synchronize_device() throws ParseException {
        //           判断设备是否连接 连接提示同步成功  没连接提示同步失败
        String s = userEntity.getDeviceEntity().getLast_sync_device_id();
        MyLog.e(TAG,"userEntity.getLast_sync_device_id()========="+userEntity.getDeviceEntity().getLast_sync_device_id());
        if (CommonUtils.isStringEmpty(s)) {
            AlertDialog dialog = new AlertDialog.Builder(AlarmActivity.this)
                    .setTitle(ToolKits.getStringbyId(AlarmActivity.this, R.string.portal_main_unbound))
                    .setMessage(ToolKits.getStringbyId(AlarmActivity.this, R.string.portal_main_unbound_msg))
                    .setPositiveButton(ToolKits.getStringbyId(AlarmActivity.this, R.string.general_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
            dialog.show();
        } else {
                //同步到设备
                provider.SetClock(AlarmActivity.this, DeviceInfoHelper.fromUserEntity(AlarmActivity.this, userEntity));
            //显示Toast
            toast.show();

        }
    }

    /**
     * 初始化闹钟状态
     */
    private void initCheckBoxStatus(int repeat, int index) {
        if (index == ALARM_ONE) {
            for (int i = 0; i < 7; i++) {
                CheckBox check = (CheckBox) findViewById(getCheckBoxIdByIndex(i, index));
                check.setChecked((repeat & (int) Math.pow(2, i)) == (int) Math.pow(2, i));
                if (check.isChecked()) {
                    check.setTextColor(getResources().getColor(R.color.yellow_title));
                } else {
                    check.setTextColor(getResources().getColor(R.color.text_gray));
                }
            }
        } else if (index == ALARM_TWO) {
            for (int i = 0; i < 7; i++) {
                CheckBox check = (CheckBox) findViewById(getCheckBoxIdByIndex(i, index));
                check.setChecked((repeat & (int) Math.pow(2, i)) == (int) Math.pow(2, i));
                if (check.isChecked()) {
                    check.setTextColor(getResources().getColor(R.color.yellow_title));
                } else {
                    check.setTextColor(getResources().getColor(R.color.text_gray));
                }
            }
        } else if (index == ALARM_THREE) {
            for (int i = 0; i < 7; i++) {
                CheckBox check = (CheckBox) findViewById(getCheckBoxIdByIndex(i, index));
                check.setChecked((repeat & (int) Math.pow(2, i)) == (int) Math.pow(2, i));
                if (check.isChecked()) {
                    check.setTextColor(getResources().getColor(R.color.yellow_title));
                } else {
                    check.setTextColor(getResources().getColor(R.color.text_gray));
                }
            }
        }

    }

    private int getCheckBoxIdByIndex(int i, int week_index) {
        if (week_index == ALARM_ONE) {
            switch (i) {
                case 0:
                    return R.id.alarm_change_alarm_dialog_sunday;
                case 1:
                    return R.id.alarm_change_alarm_dialog_monday;
                case 2:
                    return R.id.alarm_change_alarm_dialog_tuesday;
                case 3:
                    return R.id.alarm_change_alarm_dialog_wednessday;
                case 4:
                    return R.id.alarm_change_alarm_dialog_thursday;
                case 5:
                    return R.id.alarm_change_alarm_dialog_friday;
                case 6:
                    return R.id.alarm_change_alarm_dialog_saturday;
            }
        } else if (week_index == ALARM_TWO) {
            switch (i) {
                case 0:
                    return R.id.alarm_change_alarm_dialog_sunday1;
                case 1:
                    return R.id.alarm_change_alarm_dialog_monday1;
                case 2:
                    return R.id.alarm_change_alarm_dialog_tuesday1;
                case 3:
                    return R.id.alarm_change_alarm_dialog_wednessday1;
                case 4:
                    return R.id.alarm_change_alarm_dialog_thursday1;
                case 5:
                    return R.id.alarm_change_alarm_dialog_friday1;
                case 6:
                    return R.id.alarm_change_alarm_dialog_saturday1;
            }
        } else if (week_index == ALARM_THREE) {
            switch (i) {
                case 0:
                    return R.id.alarm_change_alarm_dialog_sunday2;
                case 1:
                    return R.id.alarm_change_alarm_dialog_monday2;
                case 2:
                    return R.id.alarm_change_alarm_dialog_tuesday2;
                case 3:
                    return R.id.alarm_change_alarm_dialog_wednessday2;
                case 4:
                    return R.id.alarm_change_alarm_dialog_thursday2;
                case 5:
                    return R.id.alarm_change_alarm_dialog_friday2;
                case 6:
                    return R.id.alarm_change_alarm_dialog_saturday2;
            }
        }

        return 0;
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
        Log.i(TAG, "onDestroy");
        provider.setBleProviderObserver(null);
    }

    @Override
    public void finish() {
        super.finish();
        Log.i(TAG, "finish");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case ALARM_ONE:
                MyLog.e(TAG, "设置闹钟界面返回值0为：" + data.getExtras().getString("result"));
                //处理返回值  保存到本地（注：返回值中没有闹钟的开关状态 ）
                String[] Back_value = data.getExtras().getString("result").split("-");
                strTime = Back_value[0];
                tv_showtime.setText(strTime);
                mImageView.setImageResource(R.mipmap.btn_on);
                tv_showtime.setTextColor(getResources().getColor(R.color.yellow_title));
                tv_color = true;
                strRepeat = Back_value[1];
                initCheckBoxStatus(Integer.parseInt(strRepeat), ALARM_ONE);
                try {
                    setNotificition(requestCode);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case ALARM_TWO:
                MyLog.e(TAG, "设置闹钟界面返回值1为：" + data.getExtras().getString("result"));
                //处理返回值  保存到本地（注：返回值中没有闹钟的开关状态 ）
                String[] Back_value1 = data.getExtras().getString("result").split("-");
                strTime1 = Back_value1[0];
                tv_showtime1.setText(strTime1);
                mImageView1.setImageResource(R.mipmap.btn_on);
                tv_showtime1.setTextColor(getResources().getColor(R.color.yellow_title));
                tv_color1 = true;
                strRepeat1 = Back_value1[1];
                initCheckBoxStatus(Integer.parseInt(strRepeat1), ALARM_TWO);
                try {
                    setNotificition(requestCode);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case ALARM_THREE:
                MyLog.e(TAG, "设置闹钟界面返回值2为：" + data.getExtras().getString("result"));
                //处理返回值  保存到本地（注：返回值中没有闹钟的开关状态 ）
                String[] Back_value2 = data.getExtras().getString("result").split("-");
                strTime2 = Back_value2[0];
                tv_showtime2.setText(strTime2);
                mImageView2.setImageResource(R.mipmap.btn_on);
                tv_showtime2.setTextColor(getResources().getColor(R.color.yellow_title));
                tv_color2 = true;
                strRepeat2 = Back_value2[1];
                initCheckBoxStatus(Integer.parseInt(strRepeat2), ALARM_THREE);
                try {
                    setNotificition(requestCode);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case 999:
                break;

        }
    }
    /**
     * 蓝牙观察者实现类.
     */
    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return AlarmActivity.this;
        }

        public BLEProviderObserverAdapterImpl() {
            super();
        }

        @Override
        public void updateFor_notifyForSetClockSucess() {
            super.updateFor_notifyForSetClockSucess();
//            闹钟设置成功 显示在按钮上

        }

        @Override
        public void updateFor_notifyForSetClockFail() {
            super.updateFor_notifyForSetClockFail();
        }
    }
}
