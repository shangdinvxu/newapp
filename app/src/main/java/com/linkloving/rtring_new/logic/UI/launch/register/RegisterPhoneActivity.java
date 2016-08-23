package com.linkloving.rtring_new.logic.UI.launch.register;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
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
import com.linkloving.rtring_new.logic.UI.launch.CountryActivity;
import com.linkloving.rtring_new.utils.LanguageHelper;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
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

public class RegisterPhoneActivity extends Activity implements View.OnClickListener {
    private static final String TAG = RegisterPhoneActivity.class.getSimpleName();

    private LinearLayout register_email;
    private EditText phoneNumber , edit_code;//手机号码 密码 确认密码 输入验证码
    private Button getCode , next;//获取验证码
    TextView supportCountry;//支持的国家
    RelativeLayout layout;

    private boolean isclickgetcode;

    /**
     * 选择组件：是否同意条款
     */
    private TextView cbAgreeLisence_1 = null;
    private TextView cbAgreeLisence_2 = null;
    private TextView cbAgreeLisence_3 = null;
    private TextView cbAgreeLisence_4= null;
    private static final String DEFAULT_COUNTRY_ID = "42";
    private static final int REQUEST_CODE_COUNTRY=2;
    public static String APPKEY = "112446b8d367e";
    //填写从短信SDK应用后台注册得到的APPSECRET
    public static String APPSECRET = "0c46483bb1c9085e1d429bf4b55a6b8b";
    private String currentId;
    private String currentCode;
    private EventHandler eventHandler;
    private OnSendMessageHandler osmHandler;
    private ProgressDialog pd;
    private EditPopWindow popupWindow;
    private boolean mailForUserChecked = false;//判断是否已经验证邮箱
    private boolean mailValid = false;//验证邮箱的结果
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
    @Override
    protected void onResume() {
        super.onResume();
        SMSSDK.registerEventHandler(eventHandler);
    }
    @Override
    public void finish() {
        super.finish();
        SMSSDK.unregisterEventHandler(eventHandler);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone);
        AppManager.getAppManager().addActivity(this);
        SMSSDK.initSDK(this, RegisterPhoneActivity.APPKEY, RegisterPhoneActivity.APPSECRET, false);
        initView();
        initListener();
        initSMSSDK();
        currentId = DEFAULT_COUNTRY_ID;
    }

    private void initSMSSDK() {
        eventHandler = new EventHandler() {
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
                    MyLog.i(TAG,"eventHandler获取验证码成功后的执行动作");
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
                    int resId = getStringRes(RegisterPhoneActivity.this, "smssdk_virificaition_code_sent");
                    if (resId > 0) {

                        Toast.makeText(RegisterPhoneActivity.this, resId, Toast.LENGTH_SHORT).show();
                    }

                    resId = getStringRes(RegisterPhoneActivity.this, "smssdk_receive_msg");

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
                            Toast.makeText(RegisterPhoneActivity.this, des, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        SMSLog.getInstance().w(e);
                    }
                    // / 如果木有找到资源，默认提示
                    int resId = 0;
                    if (status >= 400) {
                        resId = getStringRes(RegisterPhoneActivity.this,
                                "smssdk_error_desc_" + status);
                    } else {
                        resId = getStringRes(RegisterPhoneActivity.this,
                                "smssdk_network_error");
                    }
                    if (resId > 0) {
                        Toast.makeText(RegisterPhoneActivity.this, resId, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void countDown() {
        new CountDownTimer(60000,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                getCode.setEnabled(false);
                getCode.setText(millisUntilFinished / 1000 + getString(R.string.unit_second));
            }
            @Override
            public void onFinish() {
                getCode.setText(getString(R.string.smssdk_resend_identify_code));
                getCode.setEnabled(true);
            }
        }.start();
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
//                    Toast.makeText(RegisterPhoneActivity.this, "短信验证成功", Toast.LENGTH_SHORT).show();
                    //开启注册提交流程
                    submitRegister();
                } else {
                    ((Throwable) data).printStackTrace();
                    //验证码不正确
                    String message = ((Throwable) data).getMessage();
                    int resId = 0;
                    try {
                        JSONObject json = new JSONObject(message);
                        int status = json.getInt("status");
                        resId = getStringRes(RegisterPhoneActivity.this, "smssdk_error_detail_" + status);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (resId == 0) {
                        resId = getStringRes(RegisterPhoneActivity.this, "smssdk_virificaition_code_wrong");
                    }
                    if (resId > 0) {
                        Toast.makeText(RegisterPhoneActivity.this, resId, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    //开启注册提交流程

    /**
     * 提交注册信息
     */
    private void submitRegister(){
        //将手机号码什么的都传到下一个页面
        String phone ="+"+currentCode+phoneNumber.getText().toString().trim().replaceAll("\\s*", "");
        Intent intent=IntentFactory.create_Password_ActivityIntent(RegisterPhoneActivity.this);
        intent.putExtra("phone",phone);
        startActivity(intent);
    }
    private HttpCallback<String> httpCallback=new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            MyLog.e(TAG,"注册返回结果："+response.get());
            DataFromServer dataFromServer = JSON.parseObject(response.get(),DataFromServer.class);
            switch (what){
                case CommParams.HTTP_CHECK_IS_REGISTER:
                    mailForUserChecked = true;
                    if (dataFromServer.getErrorCode() == 1) {
                        // 可以注册
                        mailValid=true;
                        if(isclickgetcode){
                            isclickgetcode =false;
                            beginSendMessage();
                        }
                    }else {
                        // 不可以注册
                        mailValid=false;
                    }
                    if(!mailValid){
                        popupWindow.showAsDropDown(phoneNumber);
                        popupWindow.setText(popupWindow,getString(R.string.login_error10));
                    }
                    break;
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            MyLog.i(TAG, "onFailed"+message+"  responseCode"+responseCode+"");
        }

    };

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
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.layout:
                break;
            case R.id.register_by_email:
                //跳转到邮箱注册
                Intent intent=new Intent(RegisterPhoneActivity.this,RegisterActivity.class) ;
                startActivity(intent);
                break;
            //注册
            case R.id.login_form_phone_register:
                    //判断网络是否连接
                if(MyApplication.getInstance(this).isLocalDeviceNetworkOk()){
                    if(mailForUserChecked){
                        if(updatePswByVlidate()){
                            submitCode();
                        }
                    }else {
                        checkphonenum();
                        }
                    ToolKits.HideKeyboard(v);
                }else {
                    MyToast.show(RegisterPhoneActivity.this, getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
                }
                break;
            //获取验证码
            case R.id.send_message:
                //网络是否连接
                 if(MyApplication.getInstance(this).isLocalDeviceNetworkOk()){
                     isclickgetcode = true;
//                     if(mailForUserChecked){
                         if(checkphonenumInvild()){
                             ToolKits.HideKeyboard(v);
                             checkphonenum();
//                             beginSendMessage();
                         }
//                     }else{
//                         checkphonenum();
//                     }
                 }else {
                     MyToast.show(RegisterPhoneActivity.this, getString(R.string.main_more_sycn_fail), Toast.LENGTH_LONG);
                 }
                break;

            //获取国家列表:
            case R.id.support_country:
                Intent intent_country = new Intent(this, CountryActivity.class);
                intent_country.putExtra("currentId",currentId);
                startActivityForResult(intent_country,REQUEST_CODE_COUNTRY);
                break;
        }
    }

    private boolean updatePswByVlidate( )
    {
        if(!mailValid){
            popupWindow.showAsDropDown(phoneNumber);
            popupWindow.setText(popupWindow,getString(R.string.login_error10));
            return false;
        }
        return true;
    }

    private void submitCode() {
        // 提交验证码
        String verificationCode = edit_code.getText().toString().trim();
        if (!TextUtils.isEmpty(currentCode) && !TextUtils.isEmpty(verificationCode)) {
            if(pd!=null){
                pd.show();
            }
            //请求发送短信验证码
            String phone = phoneNumber.getText().toString().trim().replaceAll("\\s*", "");
            String code =currentCode;
            SMSSDK.submitVerificationCode(code, phone, verificationCode);
        }else{

            int resId = getStringRes(RegisterPhoneActivity.this, "smssdk_write_identify_code");
            if (resId > 0) {
                Toast.makeText(RegisterPhoneActivity.this, resId, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void beginSendMessage() {
        //请求发送短信验证码
        String phone = phoneNumber.getText().toString().trim().replaceAll("\\s*", "");
        String code =currentCode;
        if (phone != null)
            showDialog(phone, code);
     }
    /**
     * 是否请求发送验证码，对话框
     */
    public void showDialog(final String phone, final String code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.smssdk_contacts_phones)+"+"+code+" "+phone);
                builder.setTitle(getString(R.string.smssdk_make_sure_mobile_num));
        builder.setPositiveButton(getString(R.string.smssdk_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                currentCode = code;
                pd.show();
                //请求验证
                SMSSDK.getVerificationCode(code, phone.trim(), osmHandler);
            }
        });
        builder.setNegativeButton(getString(R.string.smssdk_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void initView() {
        pd=new ProgressDialog(this);
        layout = (RelativeLayout) findViewById(R.id.layout);
        layout.setOnClickListener(this);
        register_email= (LinearLayout) findViewById(R.id.register_by_email);
        phoneNumber = (EditText) findViewById(R.id.phone_number_register);
        edit_code = (EditText) findViewById(R.id.login_erification_code);
        getCode = (Button) findViewById(R.id.send_message);

        next = (Button) findViewById(R.id.login_form_phone_register);
        supportCountry= (TextView) findViewById(R.id.support_country);
        String[] country = getCurrentCountry();
        if (country != null) {
            currentCode = country[1];
            supportCountry.setText(country[0]);
            MyLog.e(TAG,"===currentCode===="+currentCode);
        }
        popupWindow = EditPopWindow.createPopupWindow(this,R.layout.popup_reg_view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, false);

        cbAgreeLisence_1 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_1);
        cbAgreeLisence_2 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_2);
        cbAgreeLisence_3 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_3);
        cbAgreeLisence_4 = (TextView) this.findViewById(R.id.register_form_agreeLisenseCb_4);
        cbAgreeLisence_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(IntentFactory.createCommonWebActivityIntent(RegisterPhoneActivity.this, LanguageHelper.isChinese_SimplifiedChinese() ? CommParams.REGISTER_AGREEMENT_CN_URL : CommParams.REGISTER_AGREEMENT_EN_URL));
            }
        });
        cbAgreeLisence_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(IntentFactory.createCommonWebActivityIntent(RegisterPhoneActivity.this, LanguageHelper.isChinese_SimplifiedChinese() ? CommParams.PRIVACY_CN_URL : CommParams.PRIVACY_EN_URL));
            }
        });
        cbAgreeLisence_1.setText(this.getString(R.string.register_agreelisenc_1));
        cbAgreeLisence_2.setText(Html.fromHtml("<u>"+this.getString(R.string.register_agreelisenc_2)+"</u>"));
        cbAgreeLisence_3.setText(this.getString(R.string.register_agreelisenc_3));
        cbAgreeLisence_4.setText(Html.fromHtml("<u>"+this.getString(R.string.register_agreelisenc_4)+"</u>"));
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
        private void initListener() {
        register_email.setOnClickListener(this);
        next.setOnClickListener(this);
        supportCountry.setOnClickListener(this);
        getCode.setOnClickListener(this);
            phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    if(checkphonenumInvild()){
                        //上面的验证已经通过,开始去验证手机号是否注册
                        checkphonenum();
                    }
                }else {
                    MyLog.i(TAG, "phoneNumber获取焦点了");
                    mailForUserChecked=false;
                }
                }
        });
    }
        private void checkphonenum() {
            CallServer.getRequestInstance().add(this, getString(R.string.login_yanzhen_phone), CommParams.HTTP_CHECK_IS_REGISTER, NoHttpRuquestFactory.Check_Is_Register(String.valueOf("+" + currentCode + phoneNumber.getText()).trim().toLowerCase()), httpCallback);
        }

    private boolean checkphonenumInvild() {
        if((String.valueOf(phoneNumber.getText())).trim().length()<=0){
            popupWindow.showAsDropDown(phoneNumber);
            popupWindow.setText(popupWindow,getString(R.string.login_error1));
            return false;
        }
//        if(LanguageHelper.isChinese_SimplifiedChinese()){
//            //号码位数不对的时候
//            if((String.valueOf(phoneNumber.getText())).trim().length()!=11){
//                popupWindow.showAsDropDown(phoneNumber);
//                popupWindow.setText(popupWindow,getString(R.string.login_error6));
//                return false;
//            }
//        }

        return true;
    }



}
