package com.linkloving.rtring_new.logic.UI.launch.register;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.linkloving.rtring_new.basic.EditPopWindow;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.db.weight.UserWeight;
import com.linkloving.rtring_new.db.weight.WeightTable;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.dto.UserBase;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.utils.CommonUtils;
import com.linkloving.utils._Utils;
import com.yolanda.nohttp.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BodyActivity extends ToolBarActivity {
    public static final String TAG = BodyActivity.class.getSimpleName();
    public final static int REGISTER_ACTIVITY = 1;
    public final static int USER_ACTIVITY = 2;
    public final static int LOGIN_ACTIVITY = 4;
    public final static int REGISTER_BACK_FROM_TARGET = 50;
    LinearLayout reduceHeight,plusHeight;
    LinearLayout reduceWeight,plusWeight;
    LinearLayout reduceAge,plusAge;
    EditText et_userAge, et_userHeight, et_userWeight;
    Button mButton;
    RadioButton womanCb, manCb;
    EditPopWindow popupWindow;

    //    private UserRegisterDTO u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        //设置标题
        SetBarTitleText(getString(R.string.body_activity));
//        et_target_step = (EditText) findViewById(R.id.et_target_step);
        reduceHeight = (LinearLayout) findViewById(R.id.body_info_reduceHieght);
        plusHeight = (LinearLayout) findViewById(R.id.body_info_plusHeight);
        reduceWeight = (LinearLayout) findViewById(R.id.body_info_reduceWieght);
        plusWeight = (LinearLayout) findViewById(R.id.body_info_plusWeight);
        reduceAge = (LinearLayout) findViewById(R.id.body_info_reduceAge);
        plusAge = (LinearLayout) findViewById(R.id.body_info_plusAge);

        et_userAge = (EditText) findViewById(R.id.et_userAge);
        et_userHeight = (EditText) findViewById(R.id.et_userHeight);
        et_userWeight = (EditText) findViewById(R.id.et_userWeight);
        et_userHeight.setText("170");
        et_userWeight.setText("60");
        et_userAge.setText("35");
        womanCb = (RadioButton) findViewById(R.id.body_info_womanCb);
        manCb = (RadioButton) findViewById(R.id.body_info_manCb);
        mButton = (Button) findViewById(R.id.body_info_save_btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolKits.HideKeyboard(v);
                if (checkBodyInfo()) {
                    //返回数据给注册界面
                    if (Integer.parseInt(et_userAge.getText().toString()) < 12 || Integer.parseInt(et_userAge.getText().toString()) > 114) {
                        Toast.makeText(getApplicationContext(), getString(R.string.body_info_age_recommend), Toast.LENGTH_SHORT).show();
                    } else {
                        String sex = null;
                        if (manCb.isChecked()) {
                            sex = "1";
                        } else
                            sex = "0";
//
                        UserBase userbase = MyApplication.getInstance(BodyActivity.this).getLocalUserInfoProvider().getUserBase();
                        userbase.setUser_height((int) Float.parseFloat(et_userHeight.getText().toString()));
                        userbase.setUser_weight(Integer.parseInt(et_userWeight.getText().toString()));
                        List<UserWeight> weightlist = new ArrayList<>();
                        String dateString = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date());
                        weightlist.add(new UserWeight(dateString,MyApplication.getInstance(BodyActivity.this).getLocalUserInfoProvider().getUser_id()+"",et_userWeight.getText().toString()));
                        WeightTable.saveToSqliteAsync(BodyActivity.this,weightlist,MyApplication.getInstance(BodyActivity.this).getLocalUserInfoProvider().getUser_id()+"");
                        userbase.setPlay_calory(2500);
                        userbase.setBirthdate(_Utils.getBirthdateByAge(Integer.parseInt(et_userAge.getText().toString())));
                        userbase.setUser_sex(Integer.parseInt(sex));
                        if (MyApplication.getInstance(BodyActivity.this).isLocalDeviceNetworkOk()) {
                            //网络交互：提交目标数据
                            CallServer.getRequestInstance().add(BodyActivity.this, getString(R.string.general_submitting), CommParams.HTTP_SUBMIT_BODYDATA, NoHttpRuquestFactory.submit_RegisterationToServer_Modify(userbase), httpCallback);
                        } else
                            ToolKits.showCommonTosat(BodyActivity.this, false, getString(R.string.main_more_sycn_fail), Toast.LENGTH_SHORT);
                    }
                }
            }
        });

        popupWindow = EditPopWindow.createPopupWindow(this, R.layout.popup_reg_view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }

    @Override
    protected void initListeners() {
        reduceHeight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int height = CommonUtils.getIntValue(et_userHeight.getText().toString());
                et_userHeight.setText(height <= 0 ? "0" : (height - 1) + "");
                checkBodyInfo();
            }
        });

        plusHeight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int height = CommonUtils.getIntValue(et_userHeight.getText().toString());
                et_userHeight.setText((height + 1) + "");
                checkBodyInfo();
            }
        });

        et_userHeight.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                checkBodyInfo();
            }
        });


        reduceWeight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                int weight = CommonUtils.getIntValue(et_userWeight.getText().toString());
                et_userWeight.setText(weight <= 0 ? "0" : (weight - 1) + "");
                checkBodyInfo();
            }
        });

        plusWeight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                int weight = CommonUtils.getIntValue(et_userWeight.getText().toString());
                et_userWeight.setText((weight + 1) + "");
                checkBodyInfo();
            }
        });

        et_userWeight.addTextChangedListener(new TextWatcher(){

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                checkBodyInfo();
            }
        });

        reduceAge.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int age = CommonUtils.getIntValue(et_userAge.getText().toString());
                et_userAge.setText(age <= 0 ? "0" : (age - 1) + "");
                checkBodyInfo();
            }
        });

        plusAge.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int age = CommonUtils.getIntValue(et_userAge.getText().toString());
                et_userAge.setText((age + 1) + "");
                checkBodyInfo();
            }
        });

        et_userAge.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                checkBodyInfo();
            }
        });

    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case CommParams.HTTP_SUBMIT_BODYDATA:

                    DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
                    MyLog.e(TAG, "=====onSucceed====Body返回结果：" + dataFromServer.getReturnValue().toString() + "===getErrorCode====" + dataFromServer.getErrorCode() + "====getErrMsg====" + dataFromServer.getErrMsg());
                    if (dataFromServer.getErrorCode() == 1) {

                        PreferencesToolkits.setAppStartFitst(BodyActivity.this);
                        //跳转到主页面
                        startActivity(IntentFactory.createPortalActivityIntent(BodyActivity.this));
                        //销毁前面的avtivity
                        AppManager.getAppManager().finishAllActivity();
                        finish();
                        break;
                    }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            MyLog.e(TAG, "Body返回结果：what=" +what+",message="+ message+",url"+url+",tag="+tag+",responseCode="+responseCode);
            ToolKits.showCommonTosat(BodyActivity.this, false, getString(R.string.login_form_error), Toast.LENGTH_SHORT);
        }
    };

    /**
     * 在点击保存时验证输入信息是否符合规则
     *
     * @return
     */
    private boolean checkBodyInfo() {
        /*验证目标不能为空
        /**
         * 验证年龄
         */
        if (CommonUtils.isStringEmpty(String.valueOf(et_userAge.getText()), true)) {
            et_userAge.setError(getString(R.string.body_info_age_is_necessary));
            return false;
        } else {
            int age = CommonUtils.getIntValue(et_userAge.getText().toString());
            if (age < 12 || age > 114)
            {
                et_userAge.setError(getString(R.string.body_info_age_recommend));
                return false;
            }
        }
        /**
         * 验证身高
         */
        if (CommonUtils.isStringEmpty(String.valueOf(et_userHeight.getText()), true)) {
//            popupWindow.showAsDropDown(et_userHeight);
//            popupWindow.setText(popupWindow, getString(R.string.body_info_height_is_necessary));
            et_userHeight.setError(getString(R.string.body_info_height_is_necessary));
            return false;
        } else {
            int height = CommonUtils.getIntValue(et_userHeight.getText().toString());
            if (height < 60 || height > 272) {
//                popupWindow.showAsDropDown(et_userHeight);
//                popupWindow.setText(popupWindow, getString(R.string.body_info_height_recommend));
                et_userHeight.setError(getString(R.string.body_info_height_recommend));
                return false;
            }
        }
        /**
         * 验证体重
         */
        if (CommonUtils.isStringEmpty(String.valueOf(et_userWeight.getText()), true)) {
//            popupWindow.showAsDropDown(et_userWeight);
//            popupWindow.setText(popupWindow, getString(R.string.body_info_weight_is_necessary));
            et_userWeight.setError(getString(R.string.body_info_weight_is_necessary));
            return false;
        } else {
            int weight = CommonUtils.getIntValue(et_userWeight.getText().toString());
            MyLog.i(TAG,"现在的体重:"+weight);
            if (weight < 20 || weight > 453) {
//                popupWindow.showAsDropDown(et_userWeight);
//                popupWindow.setText(popupWindow, getString(R.string.body_info_weight_recommend));
                 et_userWeight.setError(getString(R.string.body_info_weight_recommend));
                return false;
            }
        }
        return true;
    }
}
