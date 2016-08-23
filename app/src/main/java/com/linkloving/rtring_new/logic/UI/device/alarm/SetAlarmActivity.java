package com.linkloving.rtring_new.logic.UI.device.alarm;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

import java.util.ArrayList;
import java.util.List;

public class SetAlarmActivity extends ToolBarActivity implements View.OnClickListener{
    private static final String TAG=SetAlarmActivity.class.getSimpleName();

    public Toolbar toolbar ;

    private PickerView hour_pv;
    private PickerView minute_pv;
    private Button btn_savetime;
    private String HOUR;
    private String MINUTE;
    int repeat = 0;
    String Intentrepeat;
    int Result_code;
    String getIntentTime;
//    int  Hour;
//    int  Minute;
    /******
     * 返回上一级activity的数据
     *****/
    String backStr;
    private CheckBox monday, tuesday, wednessday, thursday, friday, saturday, sunday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        initData();

    }
    @Override
    protected void getIntentforActivity() {
        Result_code=getIntent().getIntExtra("str", -1);
        getIntentTime=getIntent().getStringExtra("str_tv");
        Intentrepeat=getIntent().getStringExtra("strRepeat");
        String [] strTime=getIntentTime.split(":");
        HOUR=strTime[0];
        MINUTE=strTime[1];
    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((R.string.alarm_notify)));

        btn_savetime = (Button) findViewById(R.id.btn_savetime);
        hour_pv = (PickerView) findViewById(R.id.hour_pv);
        minute_pv = (PickerView) findViewById(R.id.minute_pv);

        monday = (CheckBox) findViewById(R.id.set_alarm_change_alarm_dialog_monday);
        tuesday = (CheckBox) findViewById(R.id.set_alarm_change_alarm_dialog_tuesday);
        wednessday = (CheckBox) findViewById(R.id.set_alarm_change_alarm_dialog_wednessday);
        thursday = (CheckBox) findViewById(R.id.set_alarm_change_alarm_dialog_thursday);
        friday = (CheckBox) findViewById(R.id.set_alarm_change_alarm_dialog_friday);
        saturday = (CheckBox) findViewById(R.id.set_alarm_change_alarm_dialog_saturday);
        sunday = (CheckBox) findViewById(R.id.set_alarm_change_alarm_dialog_sunday);

    }

    private void initData() {

        List<String> hourData = new ArrayList<String>();
        List<String> minuteData = new ArrayList<String>();
        ContentResolver cv = this.getContentResolver();
//        String strTimeFromat = android.provider.Settings.System.getString(cv, Settings.System.TIME_12_24);//获取当前系统显示时间的格式
//        if (strTimeFromat != null && strTimeFromat.equals("24")) {
            for (int i = 0; i < 24; i++) {
                hourData.add(i < 10 ? "0" + i : "" + i);//设置显示小时数据
//                Log.e(TAG, "当前系统时间格式是：" + strTimeFromat + "小时制");
            }
//        } else {
//            for (int i = 1; i < 13; i++) {
//                hourData.add(i < 10 ? "0" + i : "" + i);//设置显示小时数据
////                Log.e(TAG, "当前系统时间格式是：" + strTimeFromat + "小时制");
//            }
//        }
        for (int i = 0; i < 60; i++) {
            minuteData.add(i < 10 ? "0" + i : "" + i);//设置显示分钟数据
        }
        hour_pv.setData(hourData, Integer.parseInt(HOUR));//给时间选择条设置数据源
        hour_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                HOUR = text;
            }
        });
        hour_pv.setSelected(Integer.parseInt(HOUR));
        minute_pv.setData(minuteData, Integer.parseInt(MINUTE));
        minute_pv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                MINUTE = text;
            }
        });
        minute_pv.setSelected(Integer.parseInt(MINUTE));
        initCheckBoxStatus(Integer.parseInt(Intentrepeat));
    }

    @Override
    protected void initListeners() {
        btn_savetime.setOnClickListener(this);
        monday.setOnClickListener(this);
        tuesday.setOnClickListener(this);
        wednessday.setOnClickListener(this);
        thursday.setOnClickListener(this);
        friday.setOnClickListener(this);
        saturday.setOnClickListener(this);
        sunday.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_savetime:
                int repeat=setRepeat();
                if(repeat==0){
                    MyToast.show(this,getString(R.string.choose_date), Toast.LENGTH_LONG);
                }else {
                    /***保存闹钟时间和重复周期***/
                    backStr = HOUR + ":" + MINUTE + "-" + repeat;
                    MyLog.e(TAG, "设置的数据为：" + backStr);
                    Intent intent = new Intent();
                    intent.putExtra("result", backStr);
                    //根据返回码判断设置的是哪个闹钟的数据
                    SetAlarmActivity.this.setResult(Result_code, intent);
                    finish();
                }
                break;
        }
    }

    private int setRepeat(){
        repeat += sunday.isChecked() ? 1 : 0;
        repeat += monday.isChecked() ? 2 : 0;
        repeat += tuesday.isChecked() ? 4 : 0;
        repeat += wednessday.isChecked() ? 8 : 0;
        repeat += thursday.isChecked() ? 16 : 0;
        repeat += friday.isChecked() ? 32 : 0;
        repeat += saturday.isChecked() ? 64 : 0;
        return repeat;
    }
    private void initCheckBoxStatus(int repeat) {
        for (int i = 0; i < 7; i++) {
            CheckBox check = (CheckBox) findViewById(getCheckBoxIdByIndex(i));
            check.setChecked((repeat & (int) Math.pow(2, i)) == (int) Math.pow(2, i));
            if (check.isChecked()) {
                check.setVisibility(View.VISIBLE);
            }
        }
    }
    private int getCheckBoxIdByIndex(int i) {
        switch (i) {
            case 0:
                return R.id.set_alarm_change_alarm_dialog_sunday;
            case 1:
                return R.id.set_alarm_change_alarm_dialog_monday;
            case 2:
                return R.id.set_alarm_change_alarm_dialog_tuesday;
            case 3:
                return R.id.set_alarm_change_alarm_dialog_wednessday;
            case 4:
                return R.id.set_alarm_change_alarm_dialog_thursday;
            case 5:
                return R.id.set_alarm_change_alarm_dialog_friday;
            case 6:
                return R.id.set_alarm_change_alarm_dialog_saturday;
        }
        return 0;
    }
    @Override
    public void onBackPressed() {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("result", backStr);
        //设置返回数据
        this.setResult(999, intent);
        //关闭Activity
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
