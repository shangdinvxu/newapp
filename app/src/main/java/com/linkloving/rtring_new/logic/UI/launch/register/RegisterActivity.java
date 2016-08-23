package com.linkloving.rtring_new.logic.UI.launch.register;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.linkloving.rtring_new.logic.UI.launch.dto.UserRegisterDTO;
import com.linkloving.rtring_new.logic.UI.launch.dto.UserTypeConst;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.LanguageHelper;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

public class RegisterActivity extends Activity implements View.OnClickListener {
    public static final String TAG = RegisterActivity.class.getSimpleName();
    public static final int REGISTER_BACK_FROM_BODY = 99;

    RelativeLayout layout;
    /**
     * 文本编辑组件：email
     */
    private EditText txtEmail = null;
    /**
     * 文本编辑组件：输入密码
     */
    private EditText txtPassword = null;
    /**
     * 文本编辑组件：确认密码
     */
    private EditText txtConformPassword = null;
    /**
     * 选择组件：是否同意条款
     */
    private TextView cbAgreeLisence_1 = null;
    private TextView cbAgreeLisence_2 = null;
    private TextView cbAgreeLisence_3 = null;
    private TextView cbAgreeLisence_4= null;

    EditPopWindow popupWindow;


    /**
     * 按钮：下一页
     */
    private Button btnNext = null;


    private boolean mailForUserChecked = false;//判断是否已经验证邮箱

    private boolean mailValid = false;//验证邮箱的结果

    UserRegisterDTO registerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AppManager.getAppManager().addActivity(this);
        layout = (RelativeLayout) findViewById(R.id.layout);
        layout.setOnClickListener(this);
        txtEmail = (EditText) this.findViewById(R.id.register_form_emailEdit);
        txtPassword = (EditText) this.findViewById(R.id.register_form_passwordEdit);
        txtConformPassword = (EditText) this.findViewById(R.id.register_form_conformPswEdit);
        cbAgreeLisence_1 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_1);
        cbAgreeLisence_2 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_2);
        cbAgreeLisence_3 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_3);
        cbAgreeLisence_4 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_4);
        cbAgreeLisence_2.setOnClickListener(this);
        cbAgreeLisence_4.setOnClickListener(this);
            cbAgreeLisence_1.setText(this.getString(R.string.register_agreelisenc_1));
            cbAgreeLisence_2.setText(Html.fromHtml("<u>"+this.getString(R.string.register_agreelisenc_2)+"</u>"));
            cbAgreeLisence_3.setText(this.getString(R.string.register_agreelisenc_3));
            cbAgreeLisence_4.setText(Html.fromHtml("<u>"+this.getString(R.string.register_agreelisenc_4)+"</u>"));
        btnNext = (Button) this.findViewById(R.id.register_form_submitBtn);
        btnNext.setOnClickListener(this);
        popupWindow = EditPopWindow.createPopupWindow(this,R.layout.popup_reg_view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, false);
        //判断输入的邮箱号是否符合要求
        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(emailInvild()){
                        //邮箱合法
                        checkemail();
                    }
                }else{
                    mailForUserChecked=false;
                }
            }
        });

        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!txtEmail.hasFocus())
                        checkfirstPassword();
                }
            }
        });
        txtConformPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!txtPassword.hasFocus() && !txtEmail.hasFocus())
                        checkSecondPassword();
                }
            }
        });
    }

    private boolean emailInvild() {
        if (String.valueOf(txtEmail.getText()).trim().length() <= 0) {
            popupWindow.showAsDropDown(txtEmail);
            popupWindow.setText(popupWindow,getString(R.string.login_error7));
            return false;
        }
        if (!CommonUtils.isEmail(String.valueOf(txtEmail.getText()).trim())) {
            popupWindow.showAsDropDown(txtEmail);
            popupWindow.setText(popupWindow,getString(R.string.login_error2));
            return false;
        }
        return true;
    }


    private boolean checkfirstPassword()
    {
        String firstPsw = String.valueOf(txtPassword.getText().toString().trim());
        // 新密码是否为空
        if (CommonUtils.isStringEmpty(firstPsw))
        {
            popupWindow.showAsDropDown(txtPassword);
            popupWindow.setText(popupWindow,getString(R.string.register_form_valid_psw));
            return false;
        }
        // 新密码长度是否大于6
        if (firstPsw.length() < 6)
        {
            popupWindow.showAsDropDown(txtPassword);
            popupWindow.setText(popupWindow,getString(R.string.register_form_valid_psw_notenouph));
            return false;
        }
        return true;
    }

    private boolean checkSecondPassword()
    {
        String secondPsw = String.valueOf(txtConformPassword.getText().toString().trim());
        // 新密码是否为空
        if (CommonUtils.isStringEmpty(secondPsw))
        {
            popupWindow.showAsDropDown(txtConformPassword);
            popupWindow.setText(popupWindow,getString(R.string.register_form_valid_psw));
            return false;
        }
        // 新密码长度是否大于6
        if (secondPsw.length() < 6)
        {
            popupWindow.showAsDropDown(txtConformPassword);
            popupWindow.setText(popupWindow,getString(R.string.register_form_valid_psw_notenouph));
            return false;
        }
        //两次输入新密码是否一致
        String firstPsw = String.valueOf(txtPassword.getText().toString().trim());
        if (!firstPsw.equals(secondPsw))
        {
            popupWindow.showAsDropDown(txtConformPassword);
            popupWindow.setText(popupWindow,getString(R.string.register_form_valid_psw_not_same));
            MyLog.e(TAG,"两次输入新密码bu一致");
            return false;
        }else{
            MyLog.e(TAG,"两次输入新密码一致");
        }
        return true;
    }

    //向服务器请求.验证邮箱是否已经注册
    private void checkemail() {
        CallServer.getRequestInstance().add(this, false, CommParams.HTTP_CHECK_IS_REGISTER, NoHttpRuquestFactory.Check_Is_Register(txtEmail.getText().toString().trim()), httpCallbackSubmit);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        MyLog.e(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        finish();
        return super.onTouchEvent(event);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout:
                break;
            case R.id.register_form_agreeLisenseCb_2:
//              跳转到服务条款
                startActivity(IntentFactory.createCommonWebActivityIntent(RegisterActivity.this, LanguageHelper.isChinese_SimplifiedChinese() ? CommParams.REGISTER_AGREEMENT_CN_URL : CommParams.REGISTER_AGREEMENT_EN_URL));
                break;
            case R.id.register_form_agreeLisenseCb_4:
//              跳转到服务条款
                startActivity(IntentFactory.createCommonWebActivityIntent(RegisterActivity.this, LanguageHelper.isChinese_SimplifiedChinese() ? CommParams.PRIVACY_CN_URL : CommParams.PRIVACY_EN_URL));
                break;
            case R.id.register_form_submitBtn:
                if(mailForUserChecked){
                    if (emailInvild() && checkfirstPassword() && checkSecondPassword())
                        Submit_RegisteredInfo();
                    }else {
                        checkemail();
                    }
                break;
        }
    }

    public UserRegisterDTO getFormData() {
        UserRegisterDTO u = new UserRegisterDTO();
        u.setUser_mail(String.valueOf(this.txtEmail.getText()).toLowerCase());
        u.setRegister_os_type("Android");
        u.setRegister_type(UserTypeConst.USERTYPE_NORMAL);
        try {
//            u.setUser_psw(Des3.encode(String.valueOf(this.txtPassword.getText())));
            u.setUser_psw(String.valueOf(this.txtPassword.getText()));
        } catch (Exception e) {
            MyLog.e("[LZ]=====================", "密码加密失败. 原文密码：" + String.valueOf(this.txtPassword.getText()));
            return null;
        }
        return u;
    }


    /**
     * 跳转到信息设置界面时用来判断输入的信息是否符合规则
     * @return
     */
//    private boolean fireSave() {
//        if (String.valueOf(txtEmail.getText()).trim().length() <= 0) {
//            txtEmail.setError(getString(R.string.register_form_valid_mail));
//            return false;
//        }
//        if (!CommonUtils.isEmail(String.valueOf(txtEmail.getText()).trim())) {
//            txtEmail.setError(getString(R.string.login_error2));
//            return false;
//        }
//
//        if(!mailValid){
//            txtEmail.setError(getString(R.string.register_form_mail_have_exist));
//        }
//
//        if (String.valueOf(txtPassword.getText()).trim().length() < 6) {
//            txtPassword.setError(getString(R.string.register_form_valid_psw_notenouph));
//            return false;
//        }
//        String password = String.valueOf(this.txtPassword.getText());
//        if (password != null && !password.equals(String.valueOf(this.txtConformPassword.getText()))) {
//            txtConformPassword.setError(getString(R.string.register_form_valid_psw_not_same));
//            return false;
//        }
//
//        return true;
//    }
    private void Submit_RegisteredInfo() {
        if (MyApplication.getInstance(RegisterActivity.this).isLocalDeviceNetworkOk()) {
            registerData = getFormData();
            CallServer.getRequestInstance().add(this, getString(R.string.general_submitting), CommParams.HTTP_REGISTERED_EMAIL, NoHttpRuquestFactory.submit_RegisterationToServer(registerData), httpCallbackSubmit);
        } else
            MyToast.show(RegisterActivity.this, "还未连接网络！", Toast.LENGTH_LONG);
        }

    private HttpCallback<String> httpCallbackSubmit = new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {

            DataFromServer dataFromServer =JSON.parseObject(response.get(), DataFromServer.class);
            MyLog.e(TAG, "========注册====="+"response.get()=="+response.get());
            MyLog.e(TAG, "返回ErrMsg为：" + dataFromServer.getErrMsg() + "=====ErrorCode====" + dataFromServer.getErrorCode());
            MyLog.e(TAG, "返回的是哪个请求："+what);
            switch (what) {
                case CommParams.HTTP_REGISTERED_EMAIL:
                    if (dataFromServer.getErrorCode() == 1) {
                        MyLog.e(TAG, "========注册返回======"+"dataFromServer.getReturnValue().toString()=="+dataFromServer.getReturnValue().toString());
                        UserEntity userAuthedInfo = new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
                        MyApplication.getInstance(RegisterActivity.this).setLocalUserInfoProvider(userAuthedInfo);
                        //判断状态
                        if (userAuthedInfo.getUserBase().getUser_status() == 0) {
                        //跳转到目标设置界面
                            IntentFactory.startAvatarActivityIntent(RegisterActivity.this,1); //这里是注册
                        } else {
                            startActivity(IntentFactory.createPortalActivityIntent(RegisterActivity.this));
                        }
                        finish();
                    } else {
                        switch (dataFromServer.getErrorCode()){
                            case 10003:
                                MyToast.show(RegisterActivity.this, getString(R.string.register_form_mail_have_exist), Toast.LENGTH_LONG);
                                break;
                        }
                    }
                    break;
                case CommParams.HTTP_CHECK_IS_REGISTER:
                    mailForUserChecked = true;
                    if (dataFromServer.getErrorCode() == 1) {
                        //可以注册
                        mailValid=true;
                    }else {
                        //不可以注册
                        mailValid=false;
                    }
                    if(!mailValid){
                        popupWindow.showAsDropDown(txtEmail);
                        popupWindow.setText(popupWindow,getString(R.string.register_form_mail_have_exist));
                    }
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            MyLog.e(TAG, "上传数据失败==url=" + url + "tag=" + tag + "what=" + what + "message=" + message + "responseCode=" + responseCode + "networkMillis" + networkMillis);
        }
    };


}
