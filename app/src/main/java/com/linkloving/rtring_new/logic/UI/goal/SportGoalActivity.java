package com.linkloving.rtring_new.logic.UI.goal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.CountCalUtil;
import com.linkloving.rtring_new.utils.SwitchUnit;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

public class SportGoalActivity extends ToolBarActivity implements View.OnClickListener {
    public static final String TAG = SportGoalActivity.class.getSimpleName();
    /**
     * 显示性别的图片
     */
    private ImageView body_sex = null;
    /**
     * BMI指数
     */
    private TextView body_BMI = null;
    /**
     * BMI指数对应的身体状况
     */
    private TextView body_BMIDesc = null;
    /**
     * 手动目标运动步数
     */
    private TextView target_step = null;
    private TextView sport_stored_value = null;
    private TextView sport_mileage = null;
    private TextView sport_calories = null;
    private TextView sport_exercise = null;
    private TextView sport_sleeping = null;
    private TextView sport_weight = null;
    private TextView tv_meter, tv_kg;

    private LinearLayout target_step_linearlayout,sport_stored_value_linearlayout,sport_mileage_linearlayout,sport_calories_linearlayout,sport_exercise_linearlayout,sport_sleeping_linearlayout,sport_weight_linearlayout;

    private String target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spor_goal_setting);
    }

    @Override
    protected void getIntentforActivity() {
        Intent intent = getIntent();
        target = intent.getStringExtra("user_target");
    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((R.string.goal_title)));

        sport_stored_value_linearlayout= (LinearLayout) findViewById(R.id.layout_stored_value);
        target_step_linearlayout= (LinearLayout) findViewById(R.id.layout_target_step);
        sport_mileage_linearlayout= (LinearLayout) findViewById(R.id.layout_mileage);
        sport_calories_linearlayout= (LinearLayout) findViewById(R.id.layout_calories);
        sport_exercise_linearlayout= (LinearLayout) findViewById(R.id.layout_exercise);
        sport_exercise_linearlayout.setVisibility(View.GONE);
        sport_sleeping_linearlayout= (LinearLayout) findViewById(R.id.layout_sleeping);
        sport_weight_linearlayout= (LinearLayout) findViewById(R.id.layout_weight);
        target_step = (TextView) findViewById(R.id.sport_target_step);
        String step = PreferencesToolkits.getGoalInfo(this,PreferencesToolkits.KEY_GOAL_STEP)+"";
        MyLog.e(TAG,"取出步数目标："+step);
        target_step.setText(step);
        sport_stored_value = (TextView) findViewById(R.id.sport_stored_value);
        sport_stored_value.setText(PreferencesToolkits.getGoalInfo(this,PreferencesToolkits.KEY_GOAL_MONEY)+"");

        sport_mileage = (TextView) findViewById(R.id.sport_mileage);
        sport_mileage.setText(PreferencesToolkits.getGoalInfo(this,PreferencesToolkits.KEY_GOAL_DISTANCE)+"");

        sport_calories = (TextView) findViewById(R.id.sport_calories);
        sport_calories.setText(PreferencesToolkits.getGoalInfo(this,PreferencesToolkits.KEY_GOAL_CAL)+"");

        sport_exercise = (TextView) findViewById(R.id.sport_exercise);
        sport_exercise.setText(PreferencesToolkits.getGoalInfo(this,PreferencesToolkits.KEY_GOAL_RUN)+"");

        sport_sleeping = (TextView) findViewById(R.id.sport_sleeping);
        sport_sleeping.setText(PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_SLEEP) + "");

        sport_weight = (TextView) findViewById(R.id.sport_weight);
        sport_weight.setText(PreferencesToolkits.getGoalInfo(this, PreferencesToolkits.KEY_GOAL_WEIGHT) + "");

        tv_kg = (TextView) findViewById(R.id.tv_kg);
        tv_meter = (TextView) findViewById(R.id.tv_meter);
        if (SwitchUnit.getLocalUnit(SportGoalActivity.this) == ToolKits.UNIT_GONG) {
            tv_kg.setText(getString(R.string.unit_kilogramme));
            tv_meter.setText(getString(R.string.unit_m));
        } else {
//            if (!CommonUtils.isStringEmpty(sport_mileage.getText().toString()))
//                sport_mileage.setText(UnitTookits.MChangetoMIRate(Integer.parseInt(sport_mileage.getText().toString())) + "");
            tv_meter.setText(getString(R.string.unit_mile));
//            if (!CommonUtils.isStringEmpty(sport_weight.getText().toString()))
//                sport_weight.setText(UnitTookits.KGChange2LBRate(Integer.parseInt(sport_weight.getText().toString())) + "");
            tv_kg.setText(getString(R.string.unit_pound));

        }

//        target_info_save_btn = (Button) findViewById(R.id.target_info_save_btn);
    }


    @Override
    protected void initListeners() {
        // 设置性别图片
//        if (Integer.parseInt(sex) == SEX_MAN)
//        {
//            body_sex.setBackgroundResource(R.drawable.widget_toast_icon_ok);
//        }
//        else
//            body_sex.setBackgroundResource(R.drawable.widget_toast_icon_error);
//        body_BMI.setText(BMI);
//        body_BMIDesc.setText(BMIDesc);
//        target_step.setText(target);
       /* sport_stored_value.setOnClickListener(this);
        target_step.setOnClickListener(this);
        sport_mileage.setOnClickListener(this);
        sport_calories.setOnClickListener(this);
        sport_exercise.setOnClickListener(this);
        sport_sleeping.setOnClickListener(this);
        sport_weight.setOnClickListener(this);
*/

        sport_stored_value_linearlayout.setOnClickListener(this);
        target_step_linearlayout.setOnClickListener(this);
        sport_mileage_linearlayout.setOnClickListener(this);
        sport_calories_linearlayout.setOnClickListener(this);
        sport_exercise_linearlayout.setOnClickListener(this);
        sport_sleeping_linearlayout.setOnClickListener(this);
        sport_weight_linearlayout.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        switch (v.getId()) {
            case R.id.layout_stored_value:

               /*这是准备自己写布局文件的,后期可以改动*/
               /* final Dialog alertDialog1=new Dialog(SportGoalActivity.this);
                alertDialog1.setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.stored_value));
                View storelayout = inflater.inflate(R.layout.nicknamedialog,null);
                final EditText storeEdittext;
                Button store_ok,store_no;
                storeEdittext= (EditText) storelayout.findViewById(R.id.nickname);
                storeEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                store_ok= (Button) storelayout.findViewById(R.id.ok);
                store_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isStringEmpty(storeEdittext.getText().toString().trim())) {
                            storeEdittext.setError(getString(R.string.error_field_required));
                            ToolKits.HideKeyboard(v);
                            return;
                        }else {
                            sport_stored_value.setText(storeEdittext.getText().toString());
                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_MONEY, storeEdittext.getText().toString());
                            alertDialog1.dismiss();
                        }
                    }
                });
                store_no= (Button) storelayout.findViewById(R.id.no);
                store_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.dismiss();
                        ToolKits.HideKeyboard(v);
                    }
                });
                alertDialog1.setCanceledOnTouchOutside(false);
                alertDialog1.addContentView(storelayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                alertDialog1.show();*/

                View storelayout = LayoutInflater.from(SportGoalActivity.this).inflate(R.layout.dialog_layout_sport, null);
                final EditText storeEdittext;
                storeEdittext = (EditText) storelayout.findViewById(R.id.show_msg_sport_goal);
                storeEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog dialog1 = new AlertDialog.Builder(SportGoalActivity.this)
                        .setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.stored_value))
                        .setView(storelayout)
                        .setPositiveButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (CommonUtils.isStringEmpty(storeEdittext.getText().toString().trim())) {
                                            storeEdittext.setError(getString(R.string.error_field_required));
                                            ToolKits.HideKeyboard(storeEdittext);
                                            CountCalUtil.allowCloseDialog(dialog, false);//对话框不消失
                                            return;
                                        }else {
                                            sport_stored_value.setText(storeEdittext.getText().toString());
                                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_MONEY, storeEdittext.getText().toString());
                                            ToolKits.HideKeyboard(storeEdittext);
                                            CountCalUtil.allowCloseDialog(dialog, true);//对话框消失
                                            }
                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_no), new DatePickerDialog.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CountCalUtil.allowCloseDialog(dialog,true);
                            }
                        })
                        .create();
                dialog1.show();

                break;

            case R.id.layout_target_step:
                //弹窗
               /* final Dialog alertDialog2=new Dialog(SportGoalActivity.this);
                alertDialog2.setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.report_steps));
                View steplayout = inflater.inflate(R.layout.nicknamedialog,null);
                final EditText stepEdittext;
                Button step_ok,step_no;
                stepEdittext= (EditText) steplayout.findViewById(R.id.nickname);
                stepEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                step_ok= (Button) steplayout.findViewById(R.id.ok);
                step_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isStringEmpty(stepEdittext.getText().toString().trim())) {
                            stepEdittext.setError(getString(R.string.error_field_required));
                            ToolKits.HideKeyboard(v);
                            return;
                        }else {
                            target_step.setText(stepEdittext.getText().toString());
                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_STEP, stepEdittext.getText().toString());
                            alertDialog2.dismiss();
                        }
                    }
                });
                step_no= (Button) steplayout.findViewById(R.id.no);
                step_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog2.dismiss();
                        ToolKits.HideKeyboard(v);
                    }
                });
                alertDialog2.setCanceledOnTouchOutside(false);
                alertDialog2.addContentView(steplayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                alertDialog2.show();*/

                View steplayout =LayoutInflater.from(SportGoalActivity.this).inflate(R.layout.dialog_layout_sport, null);
                final EditText stepEdittext;
                stepEdittext = (EditText) steplayout.findViewById(R.id.show_msg_sport_goal);
                stepEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog dialog = new AlertDialog.Builder(SportGoalActivity.this)
                        .setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.report_steps))
                        .setView(steplayout)
                        .setPositiveButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (CommonUtils.isStringEmpty(stepEdittext.getText().toString().trim())) {
                                            stepEdittext.setError(getString(R.string.error_field_required));
                                            ToolKits.HideKeyboard(stepEdittext);
                                            CountCalUtil.allowCloseDialog(dialog, false);//对话框不消失
                                            return;
                                        } else {
                                            target_step.setText(stepEdittext.getText().toString());
                                            String step_goal = stepEdittext.getText().toString();
                                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_STEP, step_goal);
                                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_DISTANCE, (Integer.parseInt(step_goal) * 0.7)+"");
                                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_CAL, (Integer.parseInt(step_goal) * 0.025)+"");

                                            int step_ = Integer.parseInt(PreferencesToolkits.getGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_STEP));
                                            int distace_goal = (int)(Float.parseFloat(PreferencesToolkits.getGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_DISTANCE)));
                                            int cal_goal = (int)(Float.parseFloat(PreferencesToolkits.getGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_CAL)));
                                            Log.e(TAG,"步数目标是:"+step_);
                                            Log.e(TAG,"距离目标是:"+distace_goal);
                                            Log.e(TAG,"卡路里目标是:"+cal_goal);
                                            CountCalUtil.allowCloseDialog(dialog, true);//对话框消失
                                        }
                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_no), new DatePickerDialog.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CountCalUtil.allowCloseDialog(dialog,true);
                            }
                        })
                        .create();
                dialog.show();

                break;
            case R.id.layout_mileage:
               /* final Dialog alertDialog3=new Dialog(SportGoalActivity.this);
                alertDialog3.setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.unit_mileage));
                View mileagelayout = inflater.inflate(R.layout.nicknamedialog,null);
                final EditText mileageEdittext;
                Button mileage_ok,mileage_no;
                mileageEdittext= (EditText) mileagelayout.findViewById(R.id.nickname);
                mileageEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                mileage_ok= (Button) mileagelayout.findViewById(R.id.ok);
                mileage_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isStringEmpty(mileageEdittext.getText().toString().trim())) {
                            mileageEdittext.setError(getString(R.string.error_field_required));
                            ToolKits.HideKeyboard(v);
                            return;
                        }else {
                            sport_mileage.setText(mileageEdittext.getText().toString());
                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_DISTANCE, mileageEdittext.getText().toString());
                            alertDialog3.dismiss();
                        }
                    }
                });
                mileage_no= (Button) mileagelayout.findViewById(R.id.no);
                mileage_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog3.dismiss();
                        ToolKits.HideKeyboard(v);
                    }
                });
                alertDialog3.setCanceledOnTouchOutside(false);
                alertDialog3.addContentView(mileagelayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                alertDialog3.show();*/


                View mileagelayout = LayoutInflater.from(SportGoalActivity.this).inflate(
                        R.layout.dialog_layout_sport, null);
                final EditText mileageEdittext;
                mileageEdittext = (EditText) mileagelayout.findViewById(R.id.show_msg_sport_goal);
                mileageEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog dialog2 = new AlertDialog.Builder(SportGoalActivity.this)
                        .setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.unit_mileage))
                        .setView(mileagelayout)
                        .setPositiveButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (CommonUtils.isStringEmpty(mileageEdittext.getText().toString().trim())) {
                                            mileageEdittext.setError(getString(R.string.error_field_required));
                                            ToolKits.HideKeyboard(mileageEdittext);
                                            CountCalUtil.allowCloseDialog(dialog, false);//对话框不消失
                                            return;
                                        }else {
                                            sport_mileage.setText(mileageEdittext.getText().toString());
                                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_DISTANCE, mileageEdittext.getText().toString());
                                            CountCalUtil.allowCloseDialog(dialog, true);//对话框消失
                                        }
                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_no), new DatePickerDialog.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CountCalUtil.allowCloseDialog(dialog,true);
                            }
                        })
                        .create();
                dialog2.show();
                break;
            case R.id.layout_calories:
               /* final Dialog alertDialog4=new Dialog(SportGoalActivity.this);
                alertDialog4.setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.report_calories));
                View calorieslayout = inflater.inflate(R.layout.nicknamedialog,null);
                final EditText caloriesEdittext;
                Button calories_ok,calories_no;
                caloriesEdittext= (EditText) calorieslayout.findViewById(R.id.nickname);
                caloriesEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                calories_ok= (Button) calorieslayout.findViewById(R.id.ok);
                calories_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isStringEmpty(caloriesEdittext.getText().toString().trim())) {
                            caloriesEdittext.setError(getString(R.string.error_field_required));
                            ToolKits.HideKeyboard(v);
                            return;
                        }else {
                            sport_calories.setText(caloriesEdittext.getText().toString());
                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_CAL, caloriesEdittext.getText().toString());
                            alertDialog4.dismiss();
                        }
                    }
                });
                calories_no= (Button) calorieslayout.findViewById(R.id.no);
                calories_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog4.dismiss();
                        ToolKits.HideKeyboard(v);
                    }
                });
                alertDialog4.setCanceledOnTouchOutside(false);
                alertDialog4.addContentView(calorieslayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                alertDialog4.show();*/

                View calorieslayout = LayoutInflater.from(SportGoalActivity.this).inflate(
                        R.layout.dialog_layout_sport, null);
                final EditText caloriesEdittext;
                caloriesEdittext = (EditText) calorieslayout.findViewById(R.id.show_msg_sport_goal);
                caloriesEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog dialog3 = new AlertDialog.Builder(SportGoalActivity.this)
                        .setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.report_calories))
                        .setView(calorieslayout)
                        .setPositiveButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (CommonUtils.isStringEmpty(caloriesEdittext.getText().toString().trim())) {
                                            caloriesEdittext.setError(getString(R.string.error_field_required));
                                            ToolKits.HideKeyboard(caloriesEdittext);
                                            CountCalUtil.allowCloseDialog(dialog, true);//对话框不消失
                                            return;
                                        }else {
                                            sport_calories.setText(caloriesEdittext.getText().toString());
                                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_CAL, caloriesEdittext.getText().toString());
                                            CountCalUtil.allowCloseDialog(dialog, true);//对话框消失
                                        }
                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_no), new DatePickerDialog.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CountCalUtil.allowCloseDialog(dialog,true);
                            }
                        })
                        .create();
                dialog3.show();
                break;
            case R.id.layout_exercise:

             /*   final Dialog alertDialog5=new Dialog(SportGoalActivity.this);
                alertDialog5.setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.sport_exercise));
                View exerciselayout = inflater.inflate(R.layout.nicknamedialog,null);
                final EditText exerciseEdittext;
                Button exercise_ok,exercise_no;
                exerciseEdittext= (EditText) exerciselayout.findViewById(R.id.nickname);
                exerciseEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                exercise_ok= (Button) exerciselayout.findViewById(R.id.ok);
                exercise_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isStringEmpty(exerciseEdittext.getText().toString().trim())) {
                            exerciseEdittext.setError(getString(R.string.error_field_required));
                            ToolKits.HideKeyboard(v);
                            return;
                        }else {
                            sport_exercise.setText(exerciseEdittext.getText().toString());
                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_RUN, exerciseEdittext.getText().toString());
                            alertDialog5.dismiss();
                        }
                    }
                });
                exercise_no= (Button) exerciselayout.findViewById(R.id.no);
                exercise_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog5.dismiss();
                        ToolKits.HideKeyboard(v);
                    }
                });
                alertDialog5.setCanceledOnTouchOutside(false);
                alertDialog5.addContentView(exerciselayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                alertDialog5.show();*/

                View exerciselayout = LayoutInflater.from(SportGoalActivity.this).inflate(
                        R.layout.dialog_layout_sport, null);
                final EditText exerciseEdittext;
                exerciseEdittext = (EditText) exerciselayout.findViewById(R.id.show_msg_sport_goal);
                exerciseEdittext.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog dialog4 = new AlertDialog.Builder(SportGoalActivity.this)
                        .setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.sport_exercise))
                        .setView(exerciselayout)
                        .setPositiveButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (CommonUtils.isStringEmpty(exerciseEdittext.getText().toString().trim())) {
                                            exerciseEdittext.setError(getString(R.string.error_field_required));
                                            ToolKits.HideKeyboard(exerciseEdittext);
                                            CountCalUtil.allowCloseDialog(dialog, false);//对话框消失
                                            return;
                                        }else {
                                            sport_exercise.setText(exerciseEdittext.getText().toString());
                                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_RUN, exerciseEdittext.getText().toString());
                                            CountCalUtil.allowCloseDialog(dialog, true);//对话框消失
                                        }
                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_no), new DatePickerDialog.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CountCalUtil.allowCloseDialog(dialog,true);
                            }
                        })
                        .create();
                dialog4.show();
                break;
            case R.id.layout_sleeping:

              /*
                final Dialog alertDialog6=new Dialog(SportGoalActivity.this);
                alertDialog6.setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.report_sleep));
                View sleepinglayout = inflater.inflate(R.layout.nicknamedialog,null);
                final EditText sleepingEdittext;
                Button sleeping_ok,sleeping_no;
                sleepingEdittext= (EditText) sleepinglayout.findViewById(R.id.nickname);
                sleeping_ok= (Button) sleepinglayout.findViewById(R.id.ok);
                sleeping_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isStringEmpty(sleepingEdittext.getText().toString().trim())) {
                            sleepingEdittext.setError(getString(R.string.error_field_required));
                            ToolKits.HideKeyboard(v);
                            return;
                        }else {
                            sport_sleeping.setText(sleepingEdittext.getText().toString());
                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_SLEEP, sleepingEdittext.getText().toString());
                            alertDialog6.dismiss();
                        }
                    }
                });

                sleeping_no= (Button) sleepinglayout.findViewById(R.id.no);
                sleeping_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog6.dismiss();
                        ToolKits.HideKeyboard(v);
                    }
                });
                alertDialog6.setCanceledOnTouchOutside(false);
                alertDialog6.addContentView(sleepinglayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                alertDialog6.show();*/
               View sleepinglayout = LayoutInflater.from(SportGoalActivity.this).inflate(
                        R.layout.dialog_layout_sport, null);
                final EditText sleepingEdittext;
                sleepingEdittext = (EditText) sleepinglayout.findViewById(R.id.show_msg_sport_goal);
                AlertDialog dialog5 = new AlertDialog.Builder(SportGoalActivity.this)
                        .setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.report_sleep))
                        .setView(sleepinglayout)
                        .setPositiveButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (CommonUtils.isStringEmpty(sleepingEdittext.getText().toString().trim())) {
                                            sleepingEdittext.setError(getString(R.string.error_field_required));
                                            ToolKits.HideKeyboard(sleepingEdittext);
                                            CountCalUtil.allowCloseDialog(dialog, false);//对话框消失
                                            return;
                                        }else {
                                            sport_sleeping.setText(sleepingEdittext.getText().toString());
                                            PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_SLEEP, sleepingEdittext.getText().toString());
                                            CountCalUtil.allowCloseDialog(dialog, true);//对话框消失
                                        }
                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_no), new DatePickerDialog.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CountCalUtil.allowCloseDialog(dialog,true);
                            }
                        })
                        .create();
                dialog5.show();
                break;
            case R.id.layout_weight:
/*

                final Dialog alertDialog7=new Dialog(SportGoalActivity.this);
                alertDialog7.setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.body_info_weight));
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
                            if (SwitchUnit.getLocalUnit(SportGoalActivity.this) == ToolKits.UNIT_GONG) {
                                sport_weight.setText(weightEdittext.getText().toString());
                                tv_kg.setText(getString(R.string.unit_kilogramme));
                                PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_WEIGHT, weightEdittext.getText().toString());
                            } else {
                                sport_weight.setText(weightEdittext.getText().toString());
//                                            sport_weight.setText(UnitTookits.KGChange2LBRate(Integer.parseInt(mEditText6.getText().toString())));
                                tv_kg.setText(getResources().getString(R.string.unit_pound));
                                PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_WEIGHT, weightEdittext.getText().toString());
                            }
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
                alertDialog7.show();
*/

                View weightlayout = LayoutInflater.from(SportGoalActivity.this).inflate(
                        R.layout.dialog_layout_sport, null);
                final EditText weightEdittext;
                weightEdittext = (EditText) weightlayout.findViewById(R.id.show_msg_sport_goal);
                AlertDialog dialog6 = new AlertDialog.Builder(SportGoalActivity.this)
                        .setTitle(ToolKits.getStringbyId(SportGoalActivity.this, R.string.body_info_weight))
                        .setView(weightlayout)
                        .setPositiveButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (CommonUtils.isStringEmpty(weightEdittext.getText().toString().trim())) {
                                            weightEdittext.setError(getString(R.string.error_field_required));
                                            ToolKits.HideKeyboard(weightEdittext);
                                            CountCalUtil.allowCloseDialog(dialog, false);//对话框消失
                                            return;
                                        }else {
                                            if (SwitchUnit.getLocalUnit(SportGoalActivity.this) == ToolKits.UNIT_GONG) {
                                                sport_weight.setText(weightEdittext.getText().toString());
                                                tv_kg.setText(getString(R.string.unit_kilogramme));
                                                PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_WEIGHT, weightEdittext.getText().toString());
                                            } else {
                                                sport_weight.setText(weightEdittext.getText().toString());
                                                tv_kg.setText(getResources().getString(R.string.unit_pound));
                                                PreferencesToolkits.setGoalInfo(SportGoalActivity.this, PreferencesToolkits.KEY_GOAL_WEIGHT, weightEdittext.getText().toString());
                                            }
                                            CountCalUtil.allowCloseDialog(dialog, true);//对话框消失
                                        }
                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(SportGoalActivity.this, R.string.general_no), new DatePickerDialog.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CountCalUtil.allowCloseDialog(dialog,true);
                            }
                        })
                        .create();
                dialog6.show();
                break;
        }
    }

    //提交步数
    private void SubmitGoal(String type, int goal) {
//        DeviceSetting localSetting = new DeviceSetting();
//        long update_time = ToolKits.getDayFromDate(new Date(), 0).getTime();
//        localSetting.setUser_mail(String.valueOf(MyApplication.getInstance(SportGoalActivity.this).getLocalUserInfoProvider().getUserBase().getUser_mail()).toLowerCase());
//        localSetting.setGoal(target_step.getText().toString());
//        localSetting.setGoal_update(update_time);
//        LocalUserSettingsToolkits.setLocalSettingGoalInfo(this, localSetting);
//
//
//        JSONObject dataObj = new JSONObject();
//        dataObj.put("goal", target_step.getText().toString());
//        dataObj.put("goal_update", update_time);
//        dataObj.put("user_id", MyApplication.getInstance(SportGoalActivity.this).getLocalUserInfoProvider().getUser_id());
//        if (ToolKits.isNetworkConnected(SportGoalActivity.this)) {
//            CallServer.getRequestInstance().add(this, false, CommParams.HTTP_REGISTERED_EMAIL, NoHttpRuquestFactory.submit_RegisterationToServer1(MyApplication.getInstance(SportGoalActivity.this).getLocalUserInfoProvider().getUser_id() + "", update_time, target_step.getText().toString()), httpCallbackSubmit_Target);
//        } else {
//            return;
//        }
    }


//    private HttpCallback<String> httpCallbackSubmit_Target = new HttpCallback<String>() {
//
//        @Override
//        public void onSucceed(int what, Response<String> response) {
////            处理服务器返回结果
//            JSONObject object = JSON.parseObject(response.get());
//            String value = object.getString("returnValue");
//            JSONObject obj = JSON.parseObject(value);
//            MyLog.e(TAG, "提交运动目标数据成功=======服务器返回结果======" + obj.getString("goal"));
//
//            MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider().getUserBase().setPlay_calory(Integer.parseInt(obj.getString("goal")));
//
//            if (ToolKits.isNetworkConnected(SportGoalActivity.this))
//                // 删除内存
//                LocalUserSettingsToolkits.removeLocalSettingGoalInfo(getApplicationContext(), MyApplication.getInstance(SportGoalActivity.this).getLocalUserInfoProvider().getUserBase().getUser_mail());
//            Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_LONG).show();
//            provider = BleService.getInstance(SportGoalActivity.this).getCurrentHandlerProvider();
//        }
//
//        @Override
//        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
//            MyLog.e(TAG, "提交运动目标数据失败");
//        }
//    };


}
