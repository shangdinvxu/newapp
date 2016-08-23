package com.linkloving.rtring_new.logic.UI.device.longsit;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
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

public class LongSitActivity extends ToolBarActivity implements View.OnClickListener {
    private static final String TAG = LongSitActivity.class.getSimpleName();
    private LinearLayout mLinearLayout;
    ImageView img_longsit;
    private LinearLayout long_sit_start_time_one_linear, long_sit_end_time_one_linear,
            long_sit_start_time_two_linear, long_sit_end_time_two_linear;
    private Button mButton;
    private CheckBox mCheckBox;
    private TextView mTextView;
    private TextView long_sit_start_time_one, long_sit_end_time_one,
            long_sit_start_time_two, long_sit_end_time_two;
    String start_time_one_hour;
    String start_time_one_minute;
    String end_time_one_hour;
    String end_time_one_minute;
    String start_time_two_hour;
    String start_time_two_minute;
    String end_time_two_hour;
    String end_time_two_minute;
    private DeviceSetting deviceSetting;
    private Button OK_btn;
    private Button BACK_btn;
    private PopupWindow popupWindow;
    public final static int START_TIME = 222;
    public final static int END_TIME = 223;
    public final static int START_TIME1 = 333;
    public final static int END_TIME1 = 334;
    private EditText long_sit_step_edit;
    View view;
    Toast toast;
    //蓝牙观察者
    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;
    //蓝牙提供者
    private BLEProvider provider;
    UserEntity userEntity;
    //    同步提醒
    Button BLE_Button;
    private LinearLayout Synchronize_device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_sit);
        userEntity = MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        initData();
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
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((R.string.long_sit_text)));
        mLinearLayout = (LinearLayout) findViewById(R.id.longsit_time_ll);
        mCheckBox = (CheckBox) findViewById(R.id.longsit_switch_checkbox);
        mTextView = (TextView) findViewById(R.id.tv_longsit);
        img_longsit = (ImageView) findViewById(R.id.img_longsit);
        BLE_Button= (Button) findViewById(R.id.longsit_btn_Synchronize_device);
        Synchronize_device= (LinearLayout) findViewById(R.id.longsit_Synchronize_device);

        long_sit_start_time_one_linear = (LinearLayout) findViewById(R.id.long_sit_start_time_one_linear);
        long_sit_end_time_one_linear = (LinearLayout) findViewById(R.id.long_sit_end_time_one_linear);
        long_sit_start_time_two_linear = (LinearLayout) findViewById(R.id.long_sit_start_time_two_linear);
        long_sit_end_time_two_linear = (LinearLayout) findViewById(R.id.long_sit_end_time_two_linear);

        long_sit_start_time_one = (TextView) findViewById(R.id.long_sit_start_time_one);
        long_sit_end_time_one = (TextView) findViewById(R.id.long_sit_end_time_one);
        long_sit_start_time_two = (TextView) findViewById(R.id.long_sit_start_time_two);
        long_sit_end_time_two = (TextView) findViewById(R.id.long_sit_end_time_two);
        long_sit_step_edit = (EditText) findViewById(R.id.long_sit_step_edit);

        view= LayoutInflater.from(this).inflate(R.layout.toast_synchronize, null);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = getResources().getDimensionPixelSize(resourceId);
        //得到Toast对象
        toast = new Toast(LongSitActivity.this);
        //设置Toast对象的位置，3个参数分别为位置，X轴偏移，Y轴偏
        toast.setGravity(Gravity.TOP, 0, result+50);
        //设置Toast对象的显示时间
        toast.setDuration(Toast.LENGTH_LONG);
        //设置Toast对象所要展示的视图
        toast.setView(view);

        if (mCheckBox.isChecked()) {
            mLinearLayout.setVisibility(View.VISIBLE);
            mTextView.setTextColor(Color.parseColor("#FF7700"));
        } else {
            mLinearLayout.setVisibility(View.GONE);
            mTextView.setTextColor(Color.parseColor("#666666"));
        }
    }

    private void initData() {
        deviceSetting = LocalUserSettingsToolkits.getLocalSetting(LongSitActivity.this, userEntity.getUser_id()+"");
        String[] LongsitData = deviceSetting.getLongsit_time().split("-");
        long_sit_start_time_one.setText(LongsitData[0]);
        long_sit_end_time_one.setText(LongsitData[1]);
        long_sit_start_time_two.setText(LongsitData[2]);
        long_sit_end_time_two.setText(LongsitData[3]);
        String Longsit_vaild = deviceSetting.getLongsit_vaild();
        if (Longsit_vaild.equals("0")) {
            mCheckBox.setChecked(false);
            mLinearLayout.setVisibility(View.GONE);
            mTextView.setTextColor(Color.parseColor("#666666"));
        } else {
            mCheckBox.setChecked(true);
            mLinearLayout.setVisibility(View.VISIBLE);
            mTextView.setTextColor(Color.parseColor("#FF7700"));
        }
        String Longsit_step = deviceSetting.getLongsit_step();
        long_sit_step_edit.setText(Longsit_step);
        start_time_one_hour = LongsitData[0].split(":")[0];
        start_time_one_minute = LongsitData[0].split(":")[1];
        end_time_one_hour = LongsitData[1].split(":")[0];
        end_time_one_minute = LongsitData[1].split(":")[1];

        start_time_two_hour = LongsitData[2].split(":")[0];
        start_time_two_minute = LongsitData[2].split(":")[1];
        end_time_two_hour = LongsitData[3].split(":")[0];
        end_time_two_minute = LongsitData[3].split(":")[1];
        MyLog.e(TAG, "取出本地数据为：" + start_time_one_hour + ":" + start_time_one_minute + "-"
                + end_time_one_hour + ":" + end_time_one_minute + "-" + start_time_two_hour + ":"
                + start_time_two_minute + "-" + end_time_two_hour + ":" + end_time_two_minute);

    }

    @Override
    protected void initListeners() {

        long_sit_start_time_one_linear.setOnClickListener(this);
        long_sit_end_time_one_linear.setOnClickListener(this);
        long_sit_start_time_two_linear.setOnClickListener(this);
        long_sit_end_time_two_linear.setOnClickListener(this);

        mCheckBox.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.long_sit_start_time_one_linear:
                ShowPopupWindow(start_time_one_hour, start_time_one_minute, START_TIME);
                break;
            case R.id.long_sit_end_time_one_linear:
                ShowPopupWindow(end_time_one_hour, end_time_one_minute, END_TIME);
                break;
            case R.id.long_sit_start_time_two_linear:
                ShowPopupWindow(start_time_two_hour, start_time_two_minute, START_TIME1);
                break;
            case R.id.long_sit_end_time_two_linear:
                ShowPopupWindow(end_time_two_hour, end_time_two_minute, END_TIME1);
                break;
            case R.id.longsit_switch_checkbox:
                if (mCheckBox.isChecked()) {
                    img_longsit.setImageResource(R.mipmap.s_inactive_on_144px);
                    mLinearLayout.setVisibility(View.VISIBLE);
                    mTextView.setTextColor(Color.parseColor("#FF7700"));
                    try {
                        setNotificition();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    img_longsit.setImageResource(R.mipmap.s_inactive_off_144px);
                    mLinearLayout.setVisibility(View.GONE);
                    mTextView.setTextColor(Color.parseColor("#666666"));
                    try {
                        setNotificition();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void ShowPopupWindow(String Hour, String Minute, int TagTime) {
        View layout = LayoutInflater.from(LongSitActivity.this).inflate(R.layout.dialog_layout, null);
        BACK_btn = (Button) layout.findViewById(R.id.BACK_btn);
        OK_btn = (Button) layout.findViewById(R.id.OK_btn);
        setTime(layout, Hour, Minute, TagTime);
        popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        this.OK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//显示时间
//                判断结束时间是否小于第一个开始时间或第二个结束时间

                int startHour1 = Integer.parseInt(start_time_one_hour);
                int startMinute1 = Integer.parseInt(start_time_one_minute);

                int endHour1 = Integer.parseInt(end_time_one_hour);
                int endMinute1 = Integer.parseInt(end_time_one_minute);

                int startHour2 = Integer.parseInt(start_time_two_hour);
                int startMinute2 = Integer.parseInt(start_time_two_minute);

                int endHour2 = Integer.parseInt(end_time_two_hour);
                int endMinute2 = Integer.parseInt(end_time_two_minute);

                if (
                    //时间段1中起始时间小于结束时间
                        (startHour1 < endHour1 || (startHour1 == endHour1 && startMinute1 < endMinute1))
                                &&
                                //时间段2中起始时间小于结束时间
                                (startHour2 < endHour2 || (startHour2 == endHour2 && startMinute2 < endMinute2))
                                &&
                                //时间段2中的起始时间小于时间段1中的结束时间
                                (endHour1 < startHour2 || (endHour1 == startHour2 && startMinute1 < startMinute2))
                        ) {
                    long_sit_start_time_one.setText(start_time_one_hour + ":" + start_time_one_minute);
                    long_sit_end_time_one.setText(end_time_one_hour + ":" + end_time_one_minute);
                    long_sit_start_time_two.setText(start_time_two_hour + ":" + start_time_two_minute);
                    long_sit_end_time_two.setText(end_time_two_hour + ":" + end_time_two_minute);
                    try {
                        setNotificition();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(LongSitActivity.this, getString(R.string.saved_to_local), Toast.LENGTH_LONG).show();
                    popupWindow.dismiss();
                } else {
                    //弹窗Dialog
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LongSitActivity.this);
                    dialog.setTitle(getString(R.string.effective_time_is_invalid));
                    dialog.setMessage(getString(R.string.long_sit_time_error_message));
                    dialog.setPositiveButton(getString(R.string.queren), null);
                    dialog.show();
                }

            }
        });
        this.BACK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_time_one_hour = deviceSetting.getLongsit_time().split("-")[0].split(":")[0];
                start_time_one_minute = deviceSetting.getLongsit_time().split("-")[0].split(":")[1];
                end_time_one_hour = deviceSetting.getLongsit_time().split("-")[1].split(":")[0];
                end_time_one_minute = deviceSetting.getLongsit_time().split("-")[1].split(":")[1];

                start_time_two_hour = deviceSetting.getLongsit_time().split("-")[2].split(":")[0];
                start_time_two_minute = deviceSetting.getLongsit_time().split("-")[2].split(":")[1];
                end_time_two_hour = deviceSetting.getLongsit_time().split("-")[3].split(":")[0];
                end_time_two_minute = deviceSetting.getLongsit_time().split("-")[3].split(":")[1];
                popupWindow.dismiss();
                MyLog.e(TAG, "执行了点击事件");
            }
        });
        popupWindow.showAtLocation(LongSitActivity.this.findViewById(R.id.longsit_layout), Gravity.BOTTOM, 0, 0);
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
                //获取选中的小时时间
                if (Tagtime == START_TIME) {
                    start_time_one_hour = text;
                } else if (Tagtime == END_TIME) {
                    end_time_one_hour = text;
                } else if (Tagtime == START_TIME1) {
                    start_time_two_hour = text;
                } else {//if (Tagtime==END_TIME1)
                    end_time_two_hour = text;
                }

            }
        });
        hour_pv.setSelected(Integer.parseInt(HourTime));
        minute_pv.setData(minuteData, Integer.parseInt(MinuteTime));
        minute_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                //获取选中的分钟时间
                if (Tagtime == START_TIME) {
                    start_time_one_minute = text;
                } else if (Tagtime == END_TIME) {
                    end_time_one_minute = text;
                } else if (Tagtime == START_TIME1) {
                    start_time_two_minute = text;
                } else {//if (Tagtime==END_TIME1)
                    end_time_two_minute = text;
                }
            }
        });
        minute_pv.setSelected(Integer.parseInt(MinuteTime));
    }

    /*****
     * 根据传入的参数 保存更新数据
     ****/
    private void setNotificition() throws ParseException {

        String LongSit_notif_ = start_time_one_hour + ":" + start_time_one_minute + "-"
                + end_time_one_hour + ":" + end_time_one_minute + "-" + start_time_two_hour + ":"
                + start_time_two_minute + "-" + end_time_two_hour + ":" + end_time_two_minute;
        deviceSetting.setLongsit_time(LongSit_notif_);
        deviceSetting.setLongsit_step(long_sit_step_edit.getText().toString());
        deviceSetting.setLongsit_vaild((mCheckBox.isChecked() ? "1" : "0"));
        LocalUserSettingsToolkits.updateLocalSetting(LongSitActivity.this, deviceSetting);
        MyLog.e(TAG, "存入的本地数据为：" + start_time_one_hour + ":" + start_time_one_minute + "-"
                + end_time_one_hour + ":" + end_time_one_minute + "-" + start_time_two_hour + ":"
                + start_time_two_minute + "-" + end_time_two_hour + ":" + end_time_two_minute + deviceSetting.getLongsit_vaild());
       //判断蓝牙是否连接
        if (provider.isConnectedAndDiscovered()) {
            //同步到设备
            provider.SetLongSit(LongSitActivity.this, DeviceInfoHelper.fromUserEntity(LongSitActivity.this, userEntity));
            Synchronize_device.setVisibility(View.VISIBLE);
            BLE_Button.setText(getString(R.string.saved_to_local));
            toast.show();
        }else{
            android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(LongSitActivity.this)
                    .setTitle(ToolKits.getStringbyId(LongSitActivity.this, R.string.portal_main_unbound))
                    .setMessage(ToolKits.getStringbyId(LongSitActivity.this, R.string.portal_main_unbound_msg))
                    .setPositiveButton(ToolKits.getStringbyId(LongSitActivity.this, R.string.general_ok),
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
            return LongSitActivity.this;
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
