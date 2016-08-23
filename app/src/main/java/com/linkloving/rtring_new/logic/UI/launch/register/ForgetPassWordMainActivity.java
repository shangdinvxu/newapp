package com.linkloving.rtring_new.logic.UI.launch.register;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.linkloving.rtring_new.basic.EditPopWindow;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;

public class ForgetPassWordMainActivity extends AppCompatActivity {
    public final static String TAG = ForgetPassWordMainActivity.class.getSimpleName();
    private EditText password,password_con;
    private Button ok;
    String phonenum;
    EditPopWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_word_main);
        phonenum=getIntent().getStringExtra("phone");
        password= (EditText) findViewById(R.id.password);
        password_con=(EditText) findViewById(R.id.password_con);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                    if (String.valueOf(password.getText()).trim().length() < 6) {
                        popupWindow.showAsDropDown(password);
                        popupWindow.setText(popupWindow, getString(R.string.register_form_valid_psw_notenouph));
                        //password.setError(getString(R.string.register_form_valid_psw_not_same));
                    }
                }
            }
        });


        ok= (Button) findViewById(R.id.next);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkpassword()){
                 changepassword();
                }
            }
            });
        popupWindow = EditPopWindow.createPopupWindow(this,R.layout.popup_reg_view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }

    private void changepassword() {
        //向服务端请求,请求成功后,直接跳到结束界面
        CallServer.getRequestInstance().add(this, false, CommParams.HTTP_CHECK_IS_REGISTER, NoHttpRuquestFactory.Change_Password(phonenum,password.getText().toString().trim()), httpCallback);
    }

    private boolean checkpassword(){
        if (String.valueOf(password.getText()).trim().length() < 6) {
            popupWindow.showAsDropDown(password);
            popupWindow.setText(popupWindow, getString(R.string.register_form_valid_psw_notenouph));
            //password.setError(getString(R.string.register_form_valid_psw_not_same));
            return false;
        }

        if(String.valueOf(password_con.getText()).trim().length() < 6){
            popupWindow.showAsDropDown(password_con);
            popupWindow.setText(popupWindow, getString(R.string.register_form_valid_psw_notenouph));
            //password.setError(getString(R.string.register_form_valid_psw_not_same));
            return false;
        }

        if(!String.valueOf(password.getText()).trim().equals(String.valueOf(password_con.getText()).trim())){
            popupWindow.showAsDropDown(password_con);
            popupWindow.setText(popupWindow, getString(R.string.register_form_valid_psw_not_same));
            //password.setError(getString(R.string.register_form_valid_psw_not_same));
            return false;
        }
        return true;
    }

    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            MyLog.e(TAG, "response=" + JSON.toJSONString(response.get()));
            DataFromServer dataFromServer = new Gson().fromJson(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            switch (what){
                case CommParams.HTTP_CHECK_IS_REGISTER :
                   if(dataFromServer.getErrorCode()==1){
                       //请求成功,跳到登陆界面
                       //弹出一个对话框,告诉修改密码成功了
                       new android.support.v7.app.AlertDialog.Builder(ForgetPassWordMainActivity.this)
                               .setTitle(getString(R.string.password_modification_success))
                               .setPositiveButton(getString(R.string.general_ok),
                                       new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               startActivity(IntentFactory.createLoginPageActivity(ForgetPassWordMainActivity.this));
                                               AppManager.getAppManager().finishAllActivity();
                                               finish();
                                           }
                                       })
                               .create().show();
                   }
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

            MyLog.e(TAG, "onFailed");
        }
    };

}

