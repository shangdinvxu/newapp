package com.linkloving.rtring_new.logic.UI.device.handup;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.UI.device.alarm.PickerView;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.LocalUserSettingsToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.DeviceSetting;
import com.linkloving.rtring_new.utils.DeviceInfoHelper;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class HandUpActivity extends ToolBarActivity implements View.OnClickListener {
    private static final String TAG = HandUpActivity.class.getSimpleName();
    ImageView img_wurao;
    private TextView handup_start_time,handup_end_time,disturb_tv;
    private DeviceSetting deviceSetting;
    private Button BACK_btn, OK_btn;
    String handup_start_time_hour, handup_start_time_minute,handup_end_time_hour, handup_end_time_minute;;
    private LinearLayout wurao_layout,disturb_info,handup_time_ll,handup_start_time_linear, handup_end_time_linear;
    private final static int START_TIME = 222;
    private final static int END_TIME = 223;
    private PopupWindow popupWindow;
    private CheckBox handup_switch_checkbox;
    Toast toast;
    View view;
    //蓝牙观察者
    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;
    //蓝牙提供者
    private BLEProvider provider;
    UserEntity userEntity;
    //    同步提醒
    Button BLE_Button;
    private LinearLayout Synchronize_device;

    /**
     * 勿扰模式
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_up);
        userEntity = MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider();
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
        SetBarTitleText(getString(R.string.wurao_notify));

        handup_start_time_linear = (LinearLayout) findViewById(R.id.handup_start_time_linear);
        handup_end_time_linear = (LinearLayout) findViewById(R.id.handup_end_time_linear);
        img_wurao = (ImageView) findViewById(R.id.img_wurao);
        wurao_layout= (LinearLayout) findViewById(R.id.wurao_layout);
        disturb_info= (LinearLayout) findViewById(R.id.disturb_info);
        handup_time_ll= (LinearLayout) findViewById(R.id.handup_time_ll);

        handup_start_time = (TextView) findViewById(R.id.handup_start_time);
        handup_end_time = (TextView) findViewById(R.id.handup_end_time);
        disturb_tv= (TextView) findViewById(R.id.disturb_tv);

        handup_switch_checkbox= (CheckBox) findViewById(R.id.handup_switch_checkbox);

        BLE_Button= (Button) findViewById(R.id.handup_btn_Synchronize_device);
        Synchronize_device= (LinearLayout) findViewById(R.id.handup_Synchronize_device);

        view= LayoutInflater.from(this).inflate(R.layout.toast_synchronize, null);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = getResources().getDimensionPixelSize(resourceId);
        //得到Toast对象
        toast = new Toast(HandUpActivity.this);
        //设置Toast对象的位置，3个参数分别为位置，X轴偏移，Y轴偏
        toast.setGravity(Gravity.TOP, 0, result+50);
        //设置Toast对象的显示时间
        toast.setDuration(Toast.LENGTH_LONG);
        //设置Toast对象所要展示的视图
        toast.setView(view);
    }

    @Override
    protected void initListeners() {

        handup_start_time_linear.setOnClickListener(this);
        handup_end_time_linear.setOnClickListener(this);
        handup_switch_checkbox.setOnClickListener(this);
    }

    private void initData() {
        deviceSetting = LocalUserSettingsToolkits.getLocalSetting(HandUpActivity.this, userEntity.getUser_id()+"");
        String[] HandupData = deviceSetting.getDisturb_time().split("-");
        handup_start_time.setText(HandupData[0]);
        handup_end_time.setText(HandupData[1]);

        handup_start_time_hour = HandupData[0].split(":")[0];
        handup_start_time_minute = HandupData[0].split(":")[1];
        handup_end_time_hour = HandupData[1].split(":")[0];
        handup_end_time_minute = HandupData[1].split(":")[1];
        String Disturb_Valid=deviceSetting.getDisturb_vaild();
        if(Disturb_Valid.equals("0")){
            handup_switch_checkbox.setChecked(false);
            disturb_info.setVisibility(View.GONE);
            handup_time_ll.setVisibility(View.GONE);
            img_wurao.setImageResource(R.mipmap.s_disturb_off_144px);
            disturb_tv.setTextColor(getResources().getColor(R.color.text_gray));
        }else{
            handup_switch_checkbox.setChecked(true);
            disturb_info.setVisibility(View.VISIBLE);
            handup_time_ll.setVisibility(View.VISIBLE);
            img_wurao.setImageResource(R.mipmap.s_disturb_on_144px);
            disturb_tv.setTextColor(getResources().getColor(R.color.orange));
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.handup_start_time_linear:
                ShowPopupWindow(handup_start_time_hour, handup_start_time_minute, START_TIME);
                break;
            case R.id.handup_end_time_linear:
                ShowPopupWindow(handup_end_time_hour, handup_end_time_minute, END_TIME);
                break;
            case R.id.handup_switch_checkbox:
                if(handup_switch_checkbox.isChecked()){
                    disturb_info.setVisibility(View.VISIBLE);
                    handup_time_ll.setVisibility(View.VISIBLE);
                    img_wurao.setImageResource(R.mipmap.s_disturb_on_144px);
                    disturb_tv.setTextColor(Color.parseColor("#FF7700"));
                    Toast.makeText(HandUpActivity.this,getString(R.string.save_settings_successfully),Toast.LENGTH_LONG).show();
                    try {
                        setNotificition();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else{
                    disturb_info.setVisibility(View.GONE);
                    handup_time_ll.setVisibility(View.GONE);
                    img_wurao.setImageResource(R.mipmap.s_disturb_off_144px);
                    disturb_tv.setTextColor(Color.parseColor("#666666"));
                    try {
                        setNotificition();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(HandUpActivity.this,getString(R.string.save_settings_successfully),Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void ShowPopupWindow(String Hour, String Minute, int TagTime) {
        View layout = LayoutInflater.from(HandUpActivity.this).inflate(R.layout.dialog_layout, null);
        setTime(layout, Hour, Minute, TagTime);
        BACK_btn = (Button) layout.findViewById(R.id.BACK_btn);
        OK_btn = (Button) layout.findViewById(R.id.OK_btn);
        popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        this.OK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int startHour1=Integer.parseInt(handup_start_time_hour);
//                int startMinute1=Integer.parseInt(handup_start_time_minute);
//
//                int endHour1=Integer.parseInt(handup_end_time_hour);
//                int endMinute1=Integer.parseInt(handup_end_time_minute);
//                //时间段中起始时间小于结束时间
//                if ((startHour1 < endHour1 || (startHour1 == endHour1 && startMinute1 < endMinute1))){
                    handup_start_time.setText(handup_start_time_hour + ":" + handup_start_time_minute);
                    handup_end_time.setText(handup_end_time_hour + ":" + handup_end_time_minute);
                try {
                    setNotificition();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(HandUpActivity.this,getString(R.string.saved_to_local), Toast.LENGTH_LONG).show();
                    popupWindow.dismiss();
//                }else{
//                    //弹窗Dialog
//                    AlertDialog.Builder dialog=new AlertDialog.Builder(HandUpActivity.this);
//                    dialog.setTitle("生效时间无效");
//                    dialog.setMessage("结束时间必须晚于起始时间");
//                    dialog.setPositiveButton("确定",null);
//                    dialog.show();
//                }
            }
        });
        this.BACK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(HandUpActivity.this.findViewById(R.id.handup_layout), Gravity.BOTTOM, 0, 0);

    }

    private void setTime(View view, String HourTime, String MinuteTime, final int Tagtime) {

        PickerView hour_pv = (PickerView) view.findViewById(R.id.hour_pv);
        PickerView minute_pv = (PickerView) view.findViewById(R.id.minute_pv);
        List<String> hourData = new ArrayList<String>();
        List<String> minuteData = new ArrayList<String>();
        ContentResolver cv = this.getContentResolver();
//        String strTimeFromat = android.provider.Settings.System.getString(cv, Settings.System.TIME_12_24);//获取当前系统显示时间的格式，此方法放在此处不妥
//        if (strTimeFromat != null&&strTimeFromat.equals("24")) {
            for (int i = 0; i < 24; i++) {
                hourData.add(i < 10 ? "0" + i : "" + i);//设置显示小时数据
            }
//        } else {
//            for (int i = 1; i < 13; i++) {
//                hourData.add(i < 10 ? "0" + i : "" + i);//设置显示小时数据
//            }
//        }
        for (int i = 0; i < 60; i++) {
            minuteData.add(i < 10 ? "0" + i : "" + i);//设置显示分钟数据
        }
        hour_pv.setData(hourData, Integer.parseInt(HourTime));//给时间选择条设置数据源
        hour_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                if (Tagtime == START_TIME) {
                    handup_start_time_hour = text;
                } else {
                    handup_end_time_hour = text;
                }
            }
        });
        hour_pv.setSelected(Integer.parseInt(HourTime));
        minute_pv.setData(minuteData, Integer.parseInt(MinuteTime));
        minute_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                if (Tagtime == START_TIME) {
                    handup_start_time_minute = text;
                } else {
                    handup_end_time_minute = text;
                }
            }
        });
        minute_pv.setSelected(Integer.parseInt(MinuteTime));
    }

    private void setNotificition() throws ParseException {
        String HandUp_notif_ = handup_start_time_hour+":"+handup_start_time_minute+"-"+handup_end_time_hour+":"+handup_end_time_minute;
        deviceSetting.setDisturb_time(HandUp_notif_);
        deviceSetting.setDisturb_vaild(handup_switch_checkbox.isChecked() ? "1" : "0");
        LocalUserSettingsToolkits.updateLocalSetting(HandUpActivity.this, deviceSetting);
        MyLog.e(TAG, "存入的数据为："+HandUp_notif_);
        //判断蓝牙是否连接
        if (provider.isConnectedAndDiscovered()) {
            //同步到设备
            provider.SetHandUp(HandUpActivity.this, DeviceInfoHelper.fromUserEntity(HandUpActivity.this, userEntity));
            Synchronize_device.setVisibility(View.VISIBLE);
            BLE_Button.setText(getString(R.string.synchronized_to_device));
            toast.show();
        }else{
            android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(HandUpActivity.this)
                    .setTitle(ToolKits.getStringbyId(HandUpActivity.this, R.string.portal_main_unbound))
                    .setMessage(ToolKits.getStringbyId(HandUpActivity.this, R.string.portal_main_unbound_msg))
                    .setPositiveButton(ToolKits.getStringbyId(HandUpActivity.this, R.string.general_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
            dialog.show();
        }

    }
    /**
     * 蓝牙观察者实现类.
     */
    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return HandUpActivity.this;
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
