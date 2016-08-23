package com.linkloving.rtring_new.logic.UI.main.datachatactivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.db.weight.UserWeight;
import com.linkloving.rtring_new.db.weight.WeightTable;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.CountCalUtil;
import com.linkloving.rtring_new.utils.SwitchUnit;
import com.linkloving.rtring_new.utils.ToolKits;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChangeWeightActivity extends ToolBarActivity implements View.OnClickListener {
    TextView unit_kg,date_view,weight;
    LinearLayout date_linear,weight_linear;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_weight);
    }
    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString(R.string.change_weight_activity));
        unit_kg= (TextView) findViewById(R.id.unit_kg);
        if (SwitchUnit.getLocalUnit(ChangeWeightActivity.this) == ToolKits.UNIT_GONG) {
            unit_kg.setText(getString(R.string.unit_kilogramme));
        }else {
            unit_kg.setText(getResources().getString(R.string.unit_pound));
        }
        date_view= (TextView) findViewById(R.id.date);
        date_view.setText(new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date()));//默认显示是今天的日期
        weight= (TextView) findViewById(R.id.user_weight);
        float weight_goal =  Float.parseFloat(PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_WEIGHT));
        weight.setText(weight_goal+"");
        date_linear= (LinearLayout) findViewById(R.id.date_linear);
        weight_linear= (LinearLayout) findViewById(R.id.weight_linear);
        save= (Button) findViewById(R.id.save);
    }

    @Override
    protected void initListeners() {
        date_linear.setOnClickListener(this);
        weight_linear.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
           case R.id.date_linear:
                       /*final Calendar calendar;// 用来装日期的
                       calendar = Calendar.getInstance();
                       DatePickerDialog dialog1;
                       dialog1 = new DatePickerDialog(ChangeWeightActivity.this, new DatePickerDialog.OnDateSetListener() {
                           @Override
                           public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                               calendar.set(Calendar.YEAR, year);
                               calendar.set(Calendar.MONTH, monthOfYear);
                               calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                               Date date = new Date(calendar.getTimeInMillis());
                               String endDateString = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date);
                               if (ToolKits.compareDate(date, new Date())) {
                                   Toast.makeText(ChangeWeightActivity.this, getString(R.string.date_picker_out_time), Toast.LENGTH_LONG).show();
                               } else {
                                   date_view.setText(endDateString);
                               }
                           }
                       }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                       dialog1.show();*/
            break;
            case R.id.weight_linear:
                /*//弹出对话框,去更改目标
                final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final Dialog alertDialog7=new Dialog(ChangeWeightActivity.this);
                alertDialog7.setTitle(ToolKits.getStringbyId(ChangeWeightActivity.this, R.string.body_info_weight));
                View weightlayout = inflater.inflate(R.layout.nicknamedialog,null);
                final EditText weightEdittext;
                Button weight_ok,weight_no;
                weightEdittext= (EditText) weightlayout.findViewById(R.id.nickname);
                weight_ok= (Button) weightlayout.findViewById(R.id.ok);
                weight_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isStringEmpty(weightEdittext.getText().toString().trim())) {
                            weightEdittext.setError(getString(R.string.error_field_required));
                            ToolKits.HideKeyboard(v);
                            return;
                        }else {
                            weight.setText(weightEdittext.getText());
                            alertDialog7.dismiss();
                        }
                    }
                });
                weight_no= (Button) weightlayout.findViewById(R.id.no);
                weight_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog7.dismiss();
                        ToolKits.HideKeyboard(v);
                    }
                });
                alertDialog7.setCanceledOnTouchOutside(false);
                alertDialog7.addContentView(weightlayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                alertDialog7.show();*/
                View weightlayout = LayoutInflater.from(ChangeWeightActivity.this).inflate(
                        R.layout.dialog_layout_sport, null);
                final EditText weightEdittext;
                weightEdittext = (EditText) weightlayout.findViewById(R.id.show_msg_sport_goal);
                android.app.AlertDialog dialog6 = new android.app.AlertDialog.Builder(ChangeWeightActivity.this)
                        .setTitle(ToolKits.getStringbyId(ChangeWeightActivity.this, R.string.body_info_weight))
                        .setView(weightlayout)
                        .setPositiveButton(ToolKits.getStringbyId(ChangeWeightActivity.this, R.string.general_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (CommonUtils.isStringEmpty(weightEdittext.getText().toString().trim())) {
                                            weightEdittext.setError(getString(R.string.error_field_required));
                                            ToolKits.HideKeyboard(weightEdittext);
                                            CountCalUtil.allowCloseDialog(dialog, false);//对话框消失
                                            return;
                                        }else {
                                            weight.setText(weightEdittext.getText());
                                            CountCalUtil.allowCloseDialog(dialog, true);
                                        }
                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(ChangeWeightActivity.this, R.string.general_no), new DatePickerDialog.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CountCalUtil.allowCloseDialog(dialog,true);
                            }
                        })
                        .create();
                dialog6.show();
            break;
            case R.id.save:
                //插入数据库
                List<UserWeight> weightlist = new ArrayList<>();
                weightlist.add(new UserWeight(date_view.getText().toString(), MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id() + "", weight.getText().toString()));
                WeightTable.saveToSqliteAsync(this, weightlist, MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id() + "");
                //插入成功后,返回体重界面
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
