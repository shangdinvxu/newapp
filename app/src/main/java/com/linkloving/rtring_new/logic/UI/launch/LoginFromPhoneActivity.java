package com.linkloving.rtring_new.logic.UI.launch;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.linkloving.rtring_new.basic.EditPopWindow;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.launch.register.MyAutoCompleteTextView;
import com.linkloving.rtring_new.logic.UI.launch.register.MyAutoCompleteTextViewHelperListener;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

import java.util.ArrayList;

import cn.smssdk.SMSSDK;

/**
 * Created by leo.wang on 2016/3/15.
 */

public class LoginFromPhoneActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    public final static String TAG = LoginFromPhoneActivity.class.getSimpleName();

    private static final String DEFAULT_COUNTRY_ID = "42";//默认是中国

    private static final int REQUEST_CODE_COUNTRY = 2;

    public static String APPKEY = "112446b8d367e";
    //填写从短信SDK应用后台注册得到的APPSECRET
    public static String APPSECRET = "0c46483bb1c9085e1d429bf4b55a6b8b";

    private String currentId;

    private String currentCode;

    RelativeLayout layout;

    TextView supportCountry, forgetPwd;//支持的国家

    EditText  password;

    MyAutoCompleteTextView phoneNum;

    Button Login;

    ImageView checkPwd;

    boolean isHidden = true;

    TextView login_email,login_message;

    ProgressDialog dialog;

    private boolean mailForUserChecked = false;//判断是否已经验证邮箱

    private boolean mailValid = false;//验证邮箱的结果

    private EditPopWindow popupWindow;//提示错误信息


    String[] usernames;//从本地取出来的
    ArrayList<String> username=new ArrayList<>();//保存和输入内容有关的

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_COUNTRY) {

            if (data != null) {
                currentId = (String) data.getStringExtra("id");
                String[] country = SMSSDK.getCountry(currentId);
                if (country != null) {
                    currentCode = country[1];
                    supportCountry.setText(country[0]);
                }
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MyLog.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        finish();
        return super.onTouchEvent(event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_login_layout);
        AppManager.getAppManager().addActivity(this);
        SMSSDK.initSDK(LoginFromPhoneActivity.this, LoginFromPhoneActivity.APPKEY, LoginFromPhoneActivity.APPSECRET, false);
        initView();
        initListeners();
        usernames = PreferencesToolkits.getLoginNames(LoginFromPhoneActivity.this, true);
    }

    protected void initView() {
        supportCountry = (TextView) findViewById(R.id.support_country);
        String[] country = getCurrentCountry();
        if (country != null) {
            currentCode = country[1];
            supportCountry.setText(country[0]);
        }
        layout = (RelativeLayout) findViewById(R.id.layout);
        phoneNum = (MyAutoCompleteTextView) findViewById(R.id.phone_number);
        password = (EditText) findViewById(R.id.password);
        Login = (Button) findViewById(R.id.login_form_phone);
        checkPwd = (ImageView) findViewById(R.id.check_pwd);
        login_email= (TextView) findViewById(R.id.login_email);
        login_message= (TextView) findViewById(R.id.login_message);
        dialog = new ProgressDialog(this);
        popupWindow = EditPopWindow.createPopupWindow(this,R.layout.popup_reg_view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }
    protected void initListeners() {
        supportCountry.setOnClickListener(this);
        Login.setOnClickListener(this);
        checkPwd.setOnClickListener(this);
        login_message.setOnClickListener(this);
        login_email.setOnClickListener(this);
        phoneNum.setOnTouchListener(this);
        password.setOnTouchListener(this);
        phoneNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //失去焦点时
                if (!hasFocus) {
                    if (checkphonenumInvild())
                        //上面的条件都满足时,就要检验该账号是否存在
                        checkPhoneNumber();
                } else {
                    mailForUserChecked = false;
                }
            }
        });

        phoneNum.setOnItemSelectedListener(new InputOnItemSelectedListener());
        phoneNum.setAutoCompleteTextViewHelpListener(new InputMyAutoCompleteTextViewHelperListener());

    }

    private int num = -1;       //整数标记变量，标记是第几个item被选中

    private class InputOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            num = position;
            password.setText(PreferencesToolkits.getLoginPswByLoginName(LoginFromPhoneActivity.this, username.get(position),true));
            MyLog.i(TAG,">>>>>>>>>>>>>>>>>>>>>>position="+position);
        }

        public void onNothingSelected(AdapterView<?> parent) {  }

    }

    private class InputMyAutoCompleteTextViewHelperListener implements MyAutoCompleteTextViewHelperListener {
        @Override
        public ArrayList<String> refreshPostData(String key) {
            return getLastUsername(key);
        }

        @Override
        public void resetMarkNum() {
            num = -1;
        }
    }


    //看出入的字符,本地用户名有没有
    private ArrayList<String> getLastUsername(String s){
        ArrayList<String> names = new ArrayList<>();
        if(usernames!=null&&usernames.length>0){
            for (int i = 0; i < usernames.length; i++) {
                if (usernames[i].contains(s.trim())) {
                    names.add(usernames[i]);
                }
            }
        }
        username=names;
        return names;
    }


        private boolean checkphonenumInvild() {
        if((String.valueOf(phoneNum.getText())).trim().length()<=0){
            popupWindow.showAsDropDown(phoneNum);
            popupWindow.setText(popupWindow,getString(R.string.login_error1));
            return false;
        }
        //号码位数不对的时候
//            if(LanguageHelper.isChinese_SimplifiedChinese()){
//            if(currentCode.equals("86")){
//                if((String.valueOf(phoneNum.getText())).trim().length()!=11){
//                    popupWindow.showAsDropDown(phoneNum);
//                    popupWindow.setText(popupWindow,getString(R.string.login_error6));
//                    return false;
//                }
//            }
//            }

        return true;
        }



    //向服务器请求.验证号码是否已经存在
    private void checkPhoneNumber() {
        CallServer.getRequestInstance().add(this, false, CommParams.HTTP_CHECK_IS_REGISTER, NoHttpRuquestFactory.Check_Is_Register(String.valueOf("+"+currentCode+phoneNum.getText()).trim().toLowerCase()), httpCallback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_form_phone:
                //点击了登录按钮
                if(mailForUserChecked){
                    if (check()) {
                        ToolKits.HideKeyboard(v);
                        doLodin();
                    }
                }else {
                    //没有检验号码是否存在
                    checkPhoneNumber();
                }
                break;
            case R.id.check_pwd:
                //点击查看了密码
                if (isHidden) {
                    //设置密码可见
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //imageView_pm1.setImageResource(R.mipmap.btn_on);
                    checkPwd.setImageResource(R.mipmap.eye_open);
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    checkPwd.setImageResource(R.mipmap.eye_closed);
                }
                isHidden = !isHidden;
                password.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = password.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
                break;
            case R.id.login_email:
                //邮箱登录
                Intent intent1=new Intent(LoginFromPhoneActivity.this,Login_Email_Activity.class);
                startActivity(intent1);
                break;
            case R.id.login_message:
                //修改密码
                Intent intent2=new Intent(LoginFromPhoneActivity.this,Change_Passw_Activity.class);
                startActivity(intent2);
                break;
            case R.id.support_country:
                //获取国家列表:
                Intent intent = new Intent(this, CountryActivity.class);
                intent.putExtra("currentId", currentId);
                startActivityForResult(intent, REQUEST_CODE_COUNTRY);
                break;
        }
    }
    //开始登录
    private void doLodin() {
        dialog.show();
        if (MyApplication.getInstance(LoginFromPhoneActivity.this).isLocalDeviceNetworkOk()) {

            MyLog.i(TAG, "输入符合要求，直接登陆");
            CallServer.getRequestInstance().add(LoginFromPhoneActivity.this, getString(R.string.login_form_logining), CommParams.HTTP_LOGIN_PHONE, NoHttpRuquestFactory.create_Login_From_Phone_Request(getUserLoginInfuo().getUseName(), getUserLoginInfuo().getPwd()), httpCallback);
        } else
            MyToast.show(LoginFromPhoneActivity.this, getString(R.string.general_network_faild), Toast.LENGTH_LONG);
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            dialog.dismiss();
            MyLog.e(TAG, "response=" + JSON.toJSONString(response.get()));
            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();

            switch (what){
                case CommParams.HTTP_LOGIN_PHONE :
                    if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
                        MyLog.e(TAG, "onSucceed");
                        MyLog.e(TAG, "returnValue" + value);
                        UserEntity userAuthedInfo = new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
                        MyApplication.getInstance(LoginFromPhoneActivity.this).setLocalUserInfoProvider(userAuthedInfo);
                        //保存用户名
                        String name = String.valueOf(phoneNum.getText()).trim().toLowerCase();
                        String pwd = String.valueOf(password.getText()).trim();
                        LoginInfo userInfo = new LoginInfo(name, pwd);
                        PreferencesToolkits.addLoginInfo(LoginFromPhoneActivity.this, userInfo, true);
                        PreferencesToolkits.setAppStartFitst(LoginFromPhoneActivity.this);//登陆成功就设置为false
                        if (userAuthedInfo.getUserBase().getUser_status() == 0) {
                            IntentFactory.startAvatarActivityIntent(LoginFromPhoneActivity.this,1); //这里是注册
                        } else{
                            startActivity(IntentFactory.createPortalActivityIntent(LoginFromPhoneActivity.this));
                            //清除所有的activity
                            AppManager.getAppManager().finishAllActivity();
                            finish();
                        }
                    } else {
                        MyToast.showShort(LoginFromPhoneActivity.this, getString(R.string.login_error4));
                    }
                    break;
                case CommParams.HTTP_CHECK_IS_REGISTER :
                    mailForUserChecked = true;
                    if (dataFromServer.getErrorCode() == 1) {
                        //可以注册//就是没有该邮箱账号
                        mailValid=false;
                    }else {
                        //账号存在,验证成功
                        mailValid=true;
                    }
                    if(!mailValid){
                        popupWindow.showAsDropDown(phoneNum);
                        popupWindow.setText(popupWindow, getString(R.string.login_error8));
                    }
                break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            dialog.dismiss();
            MyLog.e(TAG, "========失败========");
//            MyToast.showShort(LoginFromPhoneActivity.this, getString(R.string.login_error4));
            MyLog.e(TAG, "onFailed");
        }
    };
    //获得用户登录时候的信息，并对其中有些字段进行处理
    public LoginInfo getUserLoginInfuo() {
        String name = String.valueOf("+" + currentCode + phoneNum.getText()).trim().toLowerCase();
        String pwd = String.valueOf(password.getText()).trim();
        LoginInfo userInfo = new LoginInfo(name, pwd);
        return userInfo;
    }
    //判断是否可以登录
    /**
     * 1,输入框不能为空
     * 2,输入正确的手机号码
     */
    private boolean check() {
        if (CommonUtils.isStringEmpty(phoneNum.getText().toString().trim())) {
           // phoneNum.setError(getString(R.string.login_error1));
            popupWindow.showAsDropDown(phoneNum);
            popupWindow.setText(popupWindow, getString(R.string.login_error1));
            return false;
        } else if (phoneNum.getText().toString().trim().length() != 11) {
            //phoneNum.setError(getString(R.string.login_error6));
            popupWindow.showAsDropDown(phoneNum);
            popupWindow.setText(popupWindow, getString(R.string.login_error6));

            return false;
        } else if (CommonUtils.isStringEmpty(password.getText().toString().trim())) {
            //password.setError(getString(R.string.login_error3));
            popupWindow.showAsDropDown(password);
            popupWindow.setText(popupWindow, getString(R.string.login_error3));
            return false;
        }

        else if(password.getText().toString().trim().length()<6){
            popupWindow.showAsDropDown(password);
            popupWindow.setText(popupWindow,getString(R.string.register_form_valid_psw_notenouph));
            return false;
        }

        if(!mailValid){
           // phoneNum.setError(getString(R.string.login_error8));
            popupWindow.showAsDropDown(phoneNum);
            popupWindow.setText(popupWindow, getString(R.string.login_error8));
            return false;
        }
        return true;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {


            case R.id.phone_number:

                supportCountry.setBackground(getBaseContext().getResources().getDrawable(R.drawable.textview_orange));
                checkPwd.setBackground(getBaseContext().getResources().getDrawable(R.drawable.textview_black));
                break;
            case R.id.password:
                supportCountry.setBackground(ContextCompat.getDrawable(LoginFromPhoneActivity.this, R.drawable.textview_black));
                checkPwd.setBackground(ContextCompat.getDrawable(LoginFromPhoneActivity.this, R.drawable.textview_orange));

                break;
        }

        return false;
    }

    private String[] getCurrentCountry() {

        String mcc = getMCC();
        String[] country = null;
        if (!TextUtils.isEmpty(mcc)) {

            country = SMSSDK.getCountryByMCC(mcc);
        }

        if (country == null) {

            MyLog.i(TAG, "country是空的");

            country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);

        }
        return country;
    }

    private String getMCC() {

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 返回当前手机注册的网络运营商所在国家的MCC+MNC. 如果没注册到网络就为空.
        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator;
        }

        // 返回SIM卡运营商所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
        return tm.getSimOperator();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }


}
