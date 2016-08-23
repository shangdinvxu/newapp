package com.linkloving.rtring_new.logic.UI.launch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
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
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.linkloving.rtring_new.basic.EditPopWindow;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.launch.register.ForgetPassWordMainActivity;
import com.linkloving.rtring_new.utils.LanguageHelper;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

import static com.mob.tools.utils.R.getStringRes;

public class Change_Passw_Activity extends Activity implements View.OnClickListener, View.OnTouchListener {

    public final static String TAG = Change_Passw_Activity.class.getSimpleName();

    private static final String DEFAULT_COUNTRY_ID = "42";

    private static final int REQUEST_CODE_COUNTRY=2;

    public static String APPKEY = "112446b8d367e";

    //填写从短信SDK应用后台注册得到的APPSECRET

    public static String APPSECRET = "0c46483bb1c9085e1d429bf4b55a6b8b";

    RelativeLayout layout;
    TextView supportCountry;//支持的国家

    EditText phoneNum,login_erification_code;

    Button Login,sendMessage;
    private String currentId;
    private String currentCode;
    private EventHandler handler2;
    private OnSendMessageHandler osmHandler;
    private ProgressDialog pd;

    private boolean mailForUserChecked = false;//判断是否已经验证邮箱
    private boolean mailValid = false;//验证邮箱的结果

    private EditPopWindow popupWindow;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE_COUNTRY){

            if(data!=null){

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_from_message);
        SMSSDK.initSDK(this, APPKEY, APPSECRET, false);
        //SMSSDK.registerEventHandler(handler);
        currentId = DEFAULT_COUNTRY_ID;
        initView();
        initListener();
        initSMSSDK();
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }

    private void initView(){

        pd=new ProgressDialog(this);
        layout = (RelativeLayout) findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        supportCountry= (TextView) findViewById(R.id.support_country);
        // 国家


        String[] country = getCurrentCountry();

        if (country != null) {
            currentCode = country[1];

            supportCountry.setText(country[0]);

        }

        phoneNum= (EditText) findViewById(R.id.phone_number);
        login_erification_code= (EditText) findViewById(R.id.login_erification_code);
        sendMessage= (Button) findViewById(R.id.send_message);
        Login= (Button) findViewById(R.id.login_form_phone);
        popupWindow = EditPopWindow.createPopupWindow(this,R.layout.popup_reg_view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }

    private void initListener(){
        supportCountry.setOnClickListener(this);
        Login.setOnClickListener(this);
        sendMessage.setOnClickListener(this);
        phoneNum.setOnTouchListener(this);
        login_erification_code.setOnTouchListener(this);

        //失去焦点的时候判断
        phoneNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //失去焦点时
                if (!hasFocus) {

                    if(checkphonenumInvild()){
                        //上面的验证已经通过,开始去验证手机号是否注册
                        checkPhoneNumber();
                    }

                } else {
                    mailForUserChecked = false;
                }
            }
        });
    }
        
    
    
    private boolean checkphonenumInvild() {
        if((String.valueOf(phoneNum.getText())).trim().length()<=0){
            popupWindow.showAsDropDown(phoneNum);
            popupWindow.setText(popupWindow,getString(R.string.login_error1));
            return false;
        }
        //号码位数不对的时候
//        if(LanguageHelper.isChinese_SimplifiedChinese()){
//            if((String.valueOf(phoneNum.getText())).trim().length()!=11){
//                popupWindow.showAsDropDown(phoneNum);
//                popupWindow.setText(popupWindow,getString(R.string.login_error6));
//                return false;
//            }
//        }
        return true;
    }

    private void checkPhoneNumber() {
        CallServer.getRequestInstance().add(this, false, CommParams.HTTP_CHECK_IS_REGISTER, NoHttpRuquestFactory.Check_Is_Register(String.valueOf("+" + currentCode + phoneNum.getText()).trim().toLowerCase()), httpCallback);

    }
    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            MyLog.e(TAG, "response=" + JSON.toJSONString(response.get()));
            DataFromServer dataFromServer = new Gson().fromJson(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            switch (what){
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
                       // phoneNum.setError(getString(R.string.login_error8));
                    }
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

            MyLog.e(TAG, "onFailed");
        }
    };
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_message:
                if(MyApplication.getInstance(this).isLocalDeviceNetworkOk()){
                    if(mailForUserChecked){
                        if(checkPhoneNum())
                            beginSendMessage();
                    }else {
                        checkPhoneNumber();
                    }
                }else {
                    MyToast.show(Change_Passw_Activity.this, getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
                }
                break;
            case R.id.login_form_phone:
                //提交验证码
                if(MyApplication.getInstance(this).isLocalDeviceNetworkOk()){
                    if(checkCode())
                        submitCode();
                }else {
                    MyToast.show(Change_Passw_Activity.this, getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
                }
                break;
            case R.id.support_country:
                //获取国家列表:
                Intent intent = new Intent(this, CountryActivity.class);
                intent.putExtra("currentId",currentId);
                startActivityForResult(intent,REQUEST_CODE_COUNTRY);
                break;
        }
    }

    //验证号码
    private boolean checkPhoneNum(){
        //号码为空的时候
        if((String.valueOf(phoneNum.getText())).trim().length()<=0){

            popupWindow.showAsDropDown(phoneNum);
            popupWindow.setText(popupWindow, getString(R.string.login_error1));

            //phoneNum.setError(getString(R.string.login_error1));
            return false;
        }
        if(!mailValid){
            popupWindow.showAsDropDown(phoneNum);
            popupWindow.setText(popupWindow, getString(R.string.login_error8));
            //phoneNum.setError(getString(R.string.login_error8));
            return false;
        }
        if(!LanguageHelper.isChinese_SimplifiedChinese()){
            return true;
        }else{
            //号码位数不对的时候
            if((String.valueOf(phoneNum.getText())).trim().length()!=11){
                popupWindow.showAsDropDown(phoneNum);
                popupWindow.setText(popupWindow, getString(R.string.login_error6));

                //phoneNum.setError(getString(R.string.login_error6));
                return false;
            }
        }

        return true;
    }


    //校验 验证码
    private boolean checkCode(){
        if((String.valueOf(login_erification_code.getText())).trim().length()!=4){
            popupWindow.showAsDropDown(phoneNum);
            popupWindow.setText(popupWindow,getString(R.string.login_error9));

           // login_erification_code.setError(getString(R.string.login_error9));
            return false;
        }
        return true;
    }

    private void submitCode() {
        //提交代码

        // 提交验证码
        String verificationCode = login_erification_code.getText().toString().trim();

        if (!TextUtils.isEmpty(currentCode) && !TextUtils.isEmpty(verificationCode)) {
            if(pd!=null)

                pd.show();


            //请求发送短信验证码
            String phone = phoneNum.getText().toString().trim().replaceAll("\\s*", "");

            String code =currentCode;

            SMSSDK.submitVerificationCode(code, phone, verificationCode);

        }else{

            int resId = getStringRes(Change_Passw_Activity.this, "smssdk_write_identify_code");

            if (resId > 0) {

                Toast.makeText(Change_Passw_Activity.this, resId, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void beginSendMessage() {

        //请求发送短信验证码
        String phone = phoneNum.getText().toString().trim().replaceAll("\\s*", "");

        String code =currentCode;
        if (phone != null)
            showDialog(phone, code);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.phone_number:
        supportCountry.setBackground(getBaseContext().getResources().getDrawable(R.drawable.textview_orange));
        sendMessage.setBackground(getBaseContext().getResources().getDrawable(R.drawable.textview_black));
        break;
        case R.id.login_erification_code:

        supportCountry.setBackground(ContextCompat.getDrawable(Change_Passw_Activity.this, R.drawable.textview_black));
        sendMessage.setBackground(ContextCompat.getDrawable(Change_Passw_Activity.this, R.drawable.textview_orange));
        break;
     }
        return false;
    }

    /**
     * 是否请求发送验证码，对话框
     */
    public void showDialog(final String phone, final String code) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.send_confirm)+"+" + code+" " + phone);

        builder.setTitle(getString(R.string.smssdk_make_sure_mobile_num));

        builder.setPositiveButton(getString(R.string.queren), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                currentCode = code;

                pd.show();

                //请求验证
                SMSSDK.getVerificationCode(code, phone.trim(), osmHandler);

            }
        });

        builder.setNegativeButton(getString(R.string.general_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    private void initSMSSDK() {
        handler2 = new EventHandler() {

            public void afterEvent(int event, int result, Object data) {

                if(pd.isShowing()){
                    pd.dismiss();
                }
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                    /**提交验证码 校验验证码，返回校验的手机和国家代码 */

                    MyLog.i(TAG,"提交验证码 校验验证码，返回校验的手机和国家代码");

                    afterSubmit(result, data);

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    /** 获取验证码成功后的执行动作 */
                    MyLog.i(TAG,"handle2获取验证码成功后的执行动作");
                    afterGet(result, data);

                } else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
                    /** 获取语音版验证码成功后的执行动作 */
                    //afterGetVoice(result, data);
                }
            }
        };

    }

    /**
     * 获取验证码成功后,的执行动作
     *
     * @param result
     * @param data
     */

    private void afterGet(final int result, final Object data) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (pd != null) {
                    pd.dismiss();
                }

                if (result == SMSSDK.RESULT_COMPLETE) {
                    int resId = getStringRes(Change_Passw_Activity.this,
                            "smssdk_virificaition_code_sent");
                    if (resId > 0) {

                        Toast.makeText(Change_Passw_Activity.this, resId, Toast.LENGTH_SHORT).show();
                    }

                    resId = getStringRes(Change_Passw_Activity.this, "smssdk_receive_msg");

                    if (resId > 0) {
                        //String unReceive = getContext().getString(resId, time);
                        // tvUnreceiveIdentify.setText(Html.fromHtml(unReceive));

                    }
                    // btnSounds.setVisibility(View.GONE);
                    //time = RETRY_INTERVAL;
                    countDown();
                } else {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;
                    // 根据服务器返回的网络错误，给toast提示
                    int status = 0;
                    try {
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String des = object.optString("detail");
                        status = object.optInt("status");
                        if (!TextUtils.isEmpty(des)) {
                            Toast.makeText(Change_Passw_Activity.this, des, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        SMSLog.getInstance().w(e);
                    }
                    // / 如果木有找到资源，默认提示
                    int resId = 0;
                    if (status >= 400) {
                        resId = getStringRes(Change_Passw_Activity.this,
                                "smssdk_error_desc_" + status);
                    } else {
                        resId = getStringRes(Change_Passw_Activity.this,
                                "smssdk_network_error");
                    }
                    if (resId > 0) {
                        Toast.makeText(Change_Passw_Activity.this, resId, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });



    }

    private void countDown() {

        new CountDownTimer(60000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

                    sendMessage.setEnabled(false);

                sendMessage.setText(millisUntilFinished / 1000 + getString(R.string.unit_second));
            }

            @Override
            public void onFinish() {

                sendMessage.setText(getString(R.string.smssdk_resend_identify_code));

                sendMessage.setEnabled(true);

            }

        }.start();

    }


    @Override
    protected void onResume() {
        super.onResume();

        //SMSSDK.registerEventHandler(handler1);

        SMSSDK.registerEventHandler(handler2);

    }

    @Override
    public void finish() {
        super.finish();

        SMSSDK.unregisterEventHandler(handler2);
    }


    /**
     * 提交验证码成功后的执行事件
     *
     * @param result
     * @param data
     */
    private void afterSubmit(final int result, final Object data) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (pd != null) {
                    pd.dismiss();
                }
                if (result == SMSSDK.RESULT_COMPLETE) {
                    HashMap<String, Object> res = new HashMap<String, Object>();
                    //验证成功
                    //跳到输入验证码的界面
                    //finish();
                    String phone ="+"+currentCode+phoneNum.getText().toString().trim().replaceAll("\\s*", "");
                    Intent intent= new Intent(Change_Passw_Activity.this, ForgetPassWordMainActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);

                } else {
                    ((Throwable) data).printStackTrace();
                    //验证码不正确
                    String message = ((Throwable) data).getMessage();
                    int resId = 0;
                    try {
                        JSONObject json = new JSONObject(message);
                        int status = json.getInt("status");
                        resId = getStringRes(Change_Passw_Activity.this,
                                "smssdk_error_detail_" + status);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (resId == 0) {
                        resId = getStringRes(Change_Passw_Activity.this, "smssdk_virificaition_code_wrong");
                    }
                    if (resId > 0) {
                        Toast.makeText(Change_Passw_Activity.this, resId, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }





}
