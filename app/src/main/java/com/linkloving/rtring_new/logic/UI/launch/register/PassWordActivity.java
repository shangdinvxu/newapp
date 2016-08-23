package com.linkloving.rtring_new.logic.UI.launch.register;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

/**
 * 手机注册第二步
 */
public class PassWordActivity extends Activity  {
    private static final String TAG = PassWordActivity.class.getSimpleName();
    private EditText passWord , confirpassWord;//手机号码 密码 确认密码 输入验证码
    RelativeLayout layout;
    private Button next;//获取验证码
    EditPopWindow popupWindow;
    private String phonenum;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_word);
        AppManager.getAppManager().addActivity(this);
        phonenum=getIntent().getStringExtra("phone");
        MyLog.e(TAG,"将要注册的号码="+phonenum);
        initView();
        initListener();
    }

    private void initView(){
        layout = (RelativeLayout) findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        passWord = (EditText) findViewById(R.id.password_phone_register);
        confirpassWord = (EditText) findViewById(R.id.password_phone_register1);
        next= (Button) findViewById(R.id.login_form_phone_register);
        popupWindow = EditPopWindow.createPopupWindow(this,R.layout.popup_reg_view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    private void initListener(){

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向服务器提交注册信息

                //判断网络是否连接
                if(MyApplication.getInstance(PassWordActivity.this).isLocalDeviceNetworkOk()){
                        if(checkfirstPassword() && checkSecondPassword()){
                          submittoserver();
                        }
                    }else {

                        MyToast.show(PassWordActivity.this, getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);

                    }
                 }
            });

        passWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    checkfirstPassword();
                }
            }
        });
        confirpassWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(!passWord.hasFocus())
                        checkSecondPassword();
                }
            }
        });

    }

    private boolean checkfirstPassword()
    {
        String firstPsw = String.valueOf(passWord.getText().toString().trim());
        // 新密码是否为空
        if (CommonUtils.isStringEmpty(firstPsw))
        {
            popupWindow.showAsDropDown(passWord);
            popupWindow.setText(popupWindow,getString(R.string.register_form_valid_psw));
            return false;
        }
        // 新密码长度是否大于6
        if (firstPsw.length() < 6)
        {
            popupWindow.showAsDropDown(passWord);
            popupWindow.setText(popupWindow,getString(R.string.register_form_valid_psw_notenouph));
            return false;
        }
        return true;
    }

    private boolean checkSecondPassword()
    {
        String secondPsw = String.valueOf(confirpassWord.getText().toString().trim());
        // 新密码是否为空
        if (CommonUtils.isStringEmpty(secondPsw))
        {
            popupWindow.showAsDropDown(confirpassWord);
            popupWindow.setText(popupWindow,getString(R.string.register_form_valid_psw));
            return false;
        }
        // 新密码长度是否大于6
        if (secondPsw.length() < 6)
        {
            popupWindow.showAsDropDown(confirpassWord);
            popupWindow.setText(popupWindow,getString(R.string.register_form_valid_psw_notenouph));
            return false;
        }
        //两次输入新密码是否一致
        String firstPsw = String.valueOf(passWord.getText().toString().trim());
        if (!firstPsw.equals(secondPsw))
        {
            popupWindow.showAsDropDown(confirpassWord);
            popupWindow.setText(popupWindow,getString(R.string.register_form_valid_psw_not_same));
            MyLog.e(TAG,"两次输入新密码bu一致");
            return false;
        }else{
            MyLog.e(TAG,"两次输入新密码一致");
        }
        return true;
    }

    private void submittoserver() {

         try {
            String password = String.valueOf(this.confirpassWord.getText());
            UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
            userRegisterDTO.setUser_mobile(phonenum.trim().replaceAll("\\s*", ""));
            userRegisterDTO.setUser_psw(password);
            userRegisterDTO.setRegister_os_type("Android");
            userRegisterDTO.setRegister_type(UserTypeConst.USERTYPE_MOBILE);
            CallServer.getRequestInstance().add(PassWordActivity.this,true, CommParams.HTTP_REGISTER_MOBILE, NoHttpRuquestFactory.createMobileRegisterRequest(userRegisterDTO),httpCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private HttpCallback<String> httpCallback=new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            MyLog.e(TAG, "注册返回结果：" + response.get());
            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
            switch (what){
                case  CommParams.HTTP_REGISTER_MOBILE:
                        if(dataFromServer.getErrorCode()== 1 ){
                        //不做处理
                        MyLog.e(TAG, "dataFromServer.getReturnValue():" + dataFromServer.getReturnValue().toString());
                        UserEntity userEntity = new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
                        MyApplication.getInstance(PassWordActivity.this).setLocalUserInfoProvider(userEntity, true);
                        if (userEntity.getUserBase().getUser_status() == 0){
                            IntentFactory.startAvatarActivityIntent(PassWordActivity.this,1); //这里是注册
                        }
                    }
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            MyLog.i(TAG, "onFailed"+message+"  responseCode"+responseCode+"");
        }

    };


}
