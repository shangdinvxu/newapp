package com.linkloving.rtring_new.logic.UI.launch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Selection;
import android.text.Spannable;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

public class Login_Email_Activity extends Activity implements View.OnClickListener {
    public final static String TAG = Login_Email_Activity.class.getSimpleName();
    EditText password;
    MyAutoCompleteTextView userEmail;
    Button Login;
    ImageView checkPwd;
    RelativeLayout layout;
    boolean isHidden = true;
    ProgressDialog dialog;
    EditPopWindow popupWindow;
    private boolean mailForUserChecked = false;//判断是否已经验证邮箱
    private boolean mailValid = false;//验证邮箱的结果


    /**
     * 最近登录的用户名
     */
    //在进入该页面我们就去找本地是否有用户名

    String[] usernames;

    ArrayList<String> username = new ArrayList<>();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_from_email);
        AppManager.getAppManager().addActivity(this);
        initView();
        initListener();
        usernames = PreferencesToolkits.getLoginNames(Login_Email_Activity.this, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }

    private void initView() {
        layout = (RelativeLayout) findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        userEmail = (MyAutoCompleteTextView) findViewById(R.id.user_email);
        userEmail.requestFocus();
        ToolKits.ShowKeyboard(userEmail);
        password = (EditText) findViewById(R.id.password);
        Login = (Button) findViewById(R.id.login_form_email);
        checkPwd = (ImageView) findViewById(R.id.check_pwd);
        dialog = new ProgressDialog(this);
        popupWindow = EditPopWindow.createPopupWindow(this, R.layout.popup_reg_view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);

    }

    private void initListener() {
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkPwd.setBackground(ContextCompat.getDrawable(Login_Email_Activity.this, R.drawable.textview_orange));
                return false;
            }
        });

        Login.setOnClickListener(this);
        checkPwd.setOnClickListener(this);

        userEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mailForUserChecked = false;
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //邮箱为空的时候
                    if ((String.valueOf(userEmail.getText())).trim().length() <= 0) {

                        popupWindow.showAsDropDown(userEmail);
                        popupWindow.setText(popupWindow, getString(R.string.login_error7));
                        //userEmail.setError(getString(R.string.login_error7));
                        userEmail.requestFocus();
                        password.clearFocus();
                        return;
                    }
                    //输入邮箱格式不正确
                    if (!isEmailValid(userEmail.getText().toString().trim())) {
                        // userEmail.setError(getString(R.string.login_error2));
                        popupWindow.showAsDropDown(userEmail);
                        popupWindow.setText(popupWindow, getString(R.string.login_error2));
                        userEmail.requestFocus();
                        password.clearFocus();
                        return;
                    }
                    //上面的检查都过了,可以去请求是否可以验证
                    checkemail();
                }
            }
        });

        userEmail.setOnItemSelectedListener(new InputOnItemSelectedListener());

        userEmail.setAutoCompleteTextViewHelpListener(new InputMyAutoCompleteTextViewHelperListener());

    }

    private int num = -1;       //整数标记变量，标记是第几个item被选中

    private class InputOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            num = position;
            password.setText(PreferencesToolkits.getLoginPswByLoginName(Login_Email_Activity.this, username.get(position), false));
            MyLog.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>position=" + position);
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

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

    //向服务器请求.验证邮箱是否已经存在
    private void checkemail() {
        CallServer.getRequestInstance().add(this, false, CommParams.HTTP_CHECK_IS_REGISTER, NoHttpRuquestFactory.Check_Is_Register(userEmail.getText().toString().trim()), httpCallback);
    }


    //看出入的字符,本地用户名有没有
    private ArrayList<String> getLastUsername(String s) {
        ArrayList<String> names = new ArrayList<>();
        if (usernames != null && usernames.length > 0) {
            for (int i = 0; i < usernames.length; i++) {
                if (usernames[i].contains(s.trim())) {
                    names.add(usernames[i]);
                }
            }
        }
        username = names;
        return names;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.login_form_email:
                //点击登录
                if (mailForUserChecked) {
                    if (check()) {
                        ToolKits.HideKeyboard(v);
                        doLogin();
                    }
                } else {
                    //还没有验证
                    checkemail();
                }
                break;
            case R.id.check_pwd:
                //点击了查看密码
                //点击查看了密码
                if (isHidden) {
                    //设置密码可见
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //  imageView_pm1.setImageResource(R.mipmap.btn_on);
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
        }

    }

    private JSONObject getLoginInfo() {

        String password = null;

        try {    //对密码进行加密	 com.rtring.buiness.util.Des3
            password = String.valueOf(this.password.getText()).trim();
        } catch (Exception e) {
            MyLog.e("[LZ]=====================", e.getMessage());
            Toast.makeText(this, "密码加密失败. 原文密码：" + String.valueOf(this.password.getText()).trim(), Toast.LENGTH_SHORT).show();
        }

        JSONObject obj = new JSONObject();
        obj.put("login_name", String.valueOf(this.userEmail.getText()).trim().toLowerCase());
        obj.put("login_pwd", password);
        return obj;
    }

    private void doLogin() {
        dialog.show();
        if (MyApplication.getInstance(this).isLocalDeviceNetworkOk()) {
            //向服务器请求数据
            CallServer.getRequestInstance().add(Login_Email_Activity.this, false, CommParams.HTTP_LOGIN_EMAIL, NoHttpRuquestFactory.create_Login_From_Email_Request(getLoginInfo().getString("login_name"), getLoginInfo().getString("login_pwd")), httpCallback);
        } else {
            dialog.dismiss();
            MyToast.show(Login_Email_Activity.this, getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
        }
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            dialog.dismiss();
            MyLog.e(TAG, "response=" + JSON.toJSONString(response.get()));
            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            switch (what) {
                case CommParams.HTTP_LOGIN_EMAIL:
                    if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
                        MyLog.i(TAG, "onSucceed");
                        MyLog.i(TAG, "returnValue=" + value);
                        MyLog.e(TAG, "returnValue" + value);
                        UserEntity userAuthedInfo = new Gson().fromJson(value, UserEntity.class);
                        MyApplication.getInstance(Login_Email_Activity.this).setLocalUserInfoProvider(userAuthedInfo);
                        //保存用户名
                        LoginInfo user = new LoginInfo();
                        user.setUseName(userEmail.getText().toString().trim());
                        user.setPwd(password.getText().toString().trim());

                        PreferencesToolkits.addLoginInfo(Login_Email_Activity.this, user, false);
                        PreferencesToolkits.setAppStartFitst(Login_Email_Activity.this);//登陆成功就设置为false
                        if (userAuthedInfo.getUserBase().getUser_status() == 0) {
                            IntentFactory.startAvatarActivityIntent(Login_Email_Activity.this,1); //这里是注册
                        } else {
                            startActivity(IntentFactory.createPortalActivityIntent(Login_Email_Activity.this));
                            //清除所有的activity
                            AppManager.getAppManager().finishAllActivity();
                            finish();
                        }
                    } else {
                        MyToast.showShort(Login_Email_Activity.this, getString(R.string.login_error4));
                    }

                    break;
                case CommParams.HTTP_CHECK_IS_REGISTER:
                    mailForUserChecked = true;
                    if (dataFromServer.getErrorCode() == 1) {
                        //可以注册//就是没有该邮箱账号
                        mailValid = false;
                    } else {
                        //不可以注册
                        mailValid = true;
                    }
                    if (!mailValid) {
                        //
                        popupWindow.showAsDropDown(userEmail);
                        popupWindow.setText(popupWindow, getString(R.string.login_error8));
                        //userEmail.setError(getString(R.string.login_error8));
                    }
                    break;
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            dialog.dismiss();
            MyLog.i(TAG, "========失败========");
            MyToast.showShort(Login_Email_Activity.this, getString(R.string.login_error4));
            MyLog.i(TAG, "onFailed" + message + "        " + url);

        }
    };

    private boolean check() {

        userEmail.setError(null);

        password.setError(null);

        if (CommonUtils.isStringEmpty(userEmail.getText().toString().trim())) {

            popupWindow.showAsDropDown(userEmail);
            popupWindow.setText(popupWindow, getString(R.string.login_error1));

            //userEmail.setError(getString(R.string.login_error1));
            return false;
        } else if (!isEmailValid(userEmail.getText().toString().trim())) {

            popupWindow.showAsDropDown(userEmail);
            popupWindow.setText(popupWindow, getString(R.string.login_error2));
            // userEmail.setError(getString(R.string.login_error2));
            return false;
        } else if (CommonUtils.isStringEmpty(password.getText().toString().trim())) {
            popupWindow.showAsDropDown(password);
            popupWindow.setText(popupWindow, getString(R.string.login_error3));
            //password.setError(getString(R.string.login_error3));
            return false;
        } else if (password.getText().toString().trim().length() < 6) {
            popupWindow.showAsDropDown(password);
            popupWindow.setText(popupWindow, getString(R.string.register_form_valid_psw_notenouph));
            return false;
        } else if (!mailValid) {
            popupWindow.showAsDropDown(userEmail);
            popupWindow.setText(popupWindow, getString(R.string.login_error8));

            //userEmail.setError(getString(R.string.login_error8));
            return false;
        }

        return true;

    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


}
