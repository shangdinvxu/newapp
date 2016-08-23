package com.linkloving.rtring_new.logic.UI.launch.register;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.dto.UserBase;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

public class RegisteredSuccessActivity extends Activity {
    public static final String TAG = RegisteredSuccessActivity.class.getCanonicalName();
    public final static int REQUEST_CODE_FOR_REGISTER = 3;
    Button button;
    TextView mTextView;
    private UserBase u;
    private String pwd;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_success);
        AppManager.getAppManager().addActivity(this);
        u=MyApplication.getInstance(RegisteredSuccessActivity.this).getLocalUserInfoProvider().getUserBase();
        pwd=getIntent().getStringExtra("__UserRegisterDTO__");
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }
    protected void initView() {

        button = (Button) findViewById(R.id.button);
        mTextView = (TextView) findViewById(R.id.textView8);
        if (u != null) {
            mTextView.setText(u.getUser_mail());
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getInstance(RegisteredSuccessActivity.this).isLocalDeviceNetworkOk()) {
                    //执行登录流程
                    CallServer.getRequestInstance().add(RegisteredSuccessActivity.this, false, CommParams.HTTP_LOGIN_EMAIL, NoHttpRuquestFactory.create_Login_From_Email_Request(u.getUser_mail(), pwd), httpCallback);
                }else
                    MyToast.show(RegisteredSuccessActivity.this,getString(R.string.general_network_faild), Toast.LENGTH_LONG);
            }
        });
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {

            MyLog.e(TAG, "response=" + JSON.toJSONString(response.get()));

            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            if (value != null && value instanceof String && !ToolKits.isJSONNullObj(response.get())) {

                MyLog.e(TAG, "=======returnValue====" + value);

                UserEntity userAuthedInfo = new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
                MyApplication.getInstance(RegisteredSuccessActivity.this).setLocalUserInfoProvider(userAuthedInfo);
                //判断状态
                if (userAuthedInfo.getUserBase().getUser_status() == 0) {
//跳转到目标设置界面
                    startActivity(IntentFactory.start_BodyActivityIntent(RegisteredSuccessActivity.this));
                } else {
                    startActivity(IntentFactory.createPortalActivityIntent(RegisteredSuccessActivity.this));
                }
                finish();
            } else {
                MyToast.showShort(RegisteredSuccessActivity.this, getString(R.string.login_error4));
            }

        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

            MyToast.showShort(RegisteredSuccessActivity.this, getString(R.string.login_error4));

            MyLog.i(TAG, "登录失败");

        }
    };
}
