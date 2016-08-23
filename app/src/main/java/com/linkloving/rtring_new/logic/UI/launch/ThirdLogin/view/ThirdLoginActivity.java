package com.linkloving.rtring_new.logic.UI.launch.ThirdLogin.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.linkloving.rtring_new.logic.UI.launch.ThirdLogin.Presenter.ThirdLoginPresenter;
import com.linkloving.rtring_new.utils.LanguageHelper;
import com.zhy.autolayout.AutoLayoutActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;

/**
 * MVP测试
 * Created by zkx on 2016/7/14.
 */
public class ThirdLoginActivity extends AutoLayoutActivity implements IThirdLoginView{
    @InjectView(R.id.login_btn)
    Button loginBtn;
    @InjectView(R.id.registered_btn)
    Button regisBtn;
    @InjectView(R.id.btn_login_qq)
    ImageView qqBtn;
    @InjectView(R.id.btn_login_weixin)
    ImageView wechatBtn;
    @InjectView(R.id.btn_login_facebook)
    ImageView facebookBtn;
    @InjectView(R.id.btn_login_twitter)
    ImageView twitterBtn;
    ProgressDialog dialog;

    ThirdLoginPresenter loginPresenter = new ThirdLoginPresenter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        ButterKnife.inject(this);
        AppManager.getAppManager().addActivity(this);
        ShareSDK.initSDK(this);
        //状态栏透明
        StatusBarUtil.setTranslucent(this, 0);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.general_loading));
        if (!LanguageHelper.isChinese_SimplifiedChinese()) {
            qqBtn.setVisibility(View.GONE);
            wechatBtn.setVisibility(View.GONE);
            facebookBtn.setVisibility(View.VISIBLE);
            twitterBtn.setVisibility(View.VISIBLE);
        } else {
            qqBtn.setVisibility(View.VISIBLE);
            wechatBtn.setVisibility(View.VISIBLE);
            facebookBtn.setVisibility(View.GONE);
            twitterBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @OnClick(R.id.login_btn)
    void login(View view){
        IntentFactory.start_LoginPage_Activity(ThirdLoginActivity.this);
    }

    @OnClick(R.id.registered_btn)
    void register(View view){
        IntentFactory.start_RegisterActivity_Activity(ThirdLoginActivity.this);
    }


    @OnClick(R.id.btn_login_qq)
    void qqLogin(View view){
        loginPresenter.LoginQq(ThirdLoginActivity.this);
    }

    @OnClick(R.id.btn_login_weixin)
    void wechatLogin(View view){
        loginPresenter.LoginWeChat(ThirdLoginActivity.this);
    }

    @OnClick(R.id.btn_login_facebook)
    void facebook(View view){
        loginPresenter.LoginFace(ThirdLoginActivity.this);
    }

    @OnClick(R.id.btn_login_twitter)
    void twitter(View view){
        loginPresenter.LoginTwitter(ThirdLoginActivity.this);
    }

    @Override
    public void showLoading() {
        dialog.show();
    }

    @Override
    public void dismissLoading() {
        dialog.dismiss();
    }

    @Override
    public void toAvatarActivity() {
        IntentFactory.startAvatarActivityIntent(ThirdLoginActivity.this,1);
    }

    @Override
    public void toPortaActivity() {
        startActivity(IntentFactory.createPortalActivityIntent(ThirdLoginActivity.this));
        //清除所有的activity
        AppManager.getAppManager().finishAllActivity();
        finish();
    }

    @Override
    public void showFailMessage() {
        Toast.makeText(ThirdLoginActivity.this,getString(R.string.login_form_error_message),Toast.LENGTH_SHORT).show();
    }
}
