package com.linkloving.rtring_new.logic.UI.launch.ThirdLogin.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.dto.UserBase;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.utils._Utils;
import com.yolanda.nohttp.Response;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by zkx on 2016/7/14.
 * 逻辑处理层
 */
public class ThirdUserBinder implements IThirdBinder{
    public static final String TAG = ThirdUserBinder.class.getSimpleName();

    private static final int MSG_ACTION_CCALLBACK = 2;
    private static final int WX_LOGIN = 3;
    private static final int QQ_LOGIN = 1;
    private static final int FB_LOGIN = 5;
    private static final int TW_LOGIN = FB_LOGIN + 1;
    private static final int GL_LOGIN = TW_LOGIN + 1;

    private static final int WX_SUCCESS = 1;
    private static final int WX_ERROR = WX_SUCCESS + 1;
    private static final int WX_CALCEL = WX_ERROR + 1;

    private static final int FB_SUCCESS = 4;
    private static final int FB_ERROR = FB_SUCCESS + 1;
    private static final int FB_CALCEL = FB_ERROR + 1;

    private static final int TW_SUCCESS = 7;
    private static final int TW_ERROR = TW_SUCCESS + 1;
    private static final int TW_CALCEL = TW_ERROR + 1;

    private static final int GL_SUCCESS = 10;
    private static final int GL_ERROR = GL_SUCCESS + 1;
    private static final int GL_CALCEL = GL_ERROR + 1;

    private static final int QQ_SUCCESS = 13;
    private static final int QQ_ERROR = QQ_SUCCESS + 1;
    private static final int QQ_CALCEL = QQ_ERROR + 1;

    private String openId;
    private String name;
    private String code;
    private String time;


    @Override
    public void loginQQ(final Context context, final ThirdLoginLintener loginLintener) {

        final Platform qqPlaform = ShareSDK.getPlatform(QQ.NAME);
        qqPlaform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                //获取资料
                platform.getDb().getUserName();//获取用户名字
                platform.getDb().getUserIcon(); //获取用户头像
                MyLog.e(TAG, "QQ登陆成功");
                openId = platform.getDb().getUserId();
                name = platform.getDb().getUserName();
                code= platform.getDb().getToken();
                time = platform.getDb().getExpiresTime() + "";
                qqPlaform.removeAccount(true);
                getDataFromServletOther(context,QQ_LOGIN,loginLintener);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                loginLintener.loginFail();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                loginLintener.loginFail();
            }
        });
        //设置false表示使用SSO授权方式 使用了SSO授权后，有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权。
        qqPlaform.SSOSetting(false);
        qqPlaform.showUser(null);
    }

    @Override
    public void loginWechat(final Context context, final ThirdLoginLintener loginLintener) {

        final Platform weixinfd = ShareSDK.getPlatform(Wechat.NAME);
        weixinfd.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                platform.getDb().getUserName();//获取用户名字
                platform.getDb().getUserIcon(); //获取用户头像
                openId = platform.getDb().getUserId();
                name = platform.getDb().getUserName();
                code= platform.getDb().getToken();
                time = platform.getDb().getExpiresTime() + "";
                weixinfd.removeAccount(true);
                getDataFromServletOther(context,WX_LOGIN,loginLintener);

            }
            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                loginLintener.loginFail();
            }
            @Override
            public void onCancel(Platform platform, int i) {
                loginLintener.loginFail();
            }
        });
        weixinfd.SSOSetting(false);
        weixinfd.showUser(null);
    }

    @Override
    public void loginFacebook(final Context context, final ThirdLoginLintener loginLintener) {
        final Platform platform = ShareSDK.getPlatform(Facebook.NAME);
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                platform.getDb().getUserName();//获取用户名字
                platform.getDb().getUserIcon(); //获取用户头像
                openId = platform.getDb().getUserId();
                name = platform.getDb().getUserName();
                code= platform.getDb().getToken();
                time = platform.getDb().getExpiresTime() + "";
                platform.removeAccount(true);
                getDataFromServletOther(context,FB_LOGIN,loginLintener);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                loginLintener.loginFail();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                loginLintener.loginFail();
            }

        });
        //设置false表示使用SSO授权方式 使用了SSO授权后，有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权。
        platform.SSOSetting(false);
        platform.showUser(null);
    }

    @Override
    public void loginTwitter(final Context context, final ThirdLoginLintener loginLintener) {
        final Platform platform = ShareSDK.getPlatform(Twitter.NAME);
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                platform.getDb().getUserName();//获取用户名字
                platform.getDb().getUserIcon(); //获取用户头像
                openId = platform.getDb().getUserId();
                name = platform.getDb().getUserName();
                code= platform.getDb().getToken();
                time = platform.getDb().getExpiresTime() + "";
                platform.removeAccount(true);
                getDataFromServletOther(context,TW_LOGIN,loginLintener);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                loginLintener.loginFail();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                loginLintener.loginFail();
            }

        });
        //设置false表示使用SSO授权方式 使用了SSO授权后，有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权。
        platform.SSOSetting(false);
        platform.showUser(null);
    }

    private void getDataFromServletOther(final Context context, int userType, final ThirdLoginLintener loginLintener){
        UserBase userBase = new UserBase("1991-01-01");
        userBase.setUser_mail(openId);
        userBase.setUser_psw(openId);
        userBase.setNickname(name);
        if(code!=null){
            userBase.setThirdparty_access_token(code);
        }else{
            userBase.setThirdparty_access_token("NOTTOKEN");
        }
        userBase.setThirdparty_expire_time(time);
        userBase.setUser_type(userType);
        userBase.setRegister_os_type("Android");
        userBase.setUser_height(170);
        userBase.setUser_sex(1);
        userBase.setUser_weight(120);
        userBase.setPlay_calory(2500);
        userBase.setUser_status(0);
        userBase.setBirthdate(_Utils.getBirthdateByAge(Integer.parseInt("22")));
        CallServer.getRequestInstance().add(context, context.getString(R.string.login_form_logining), CommParams.HTTP_LOGIN_WECHAT, NoHttpRuquestFactory.create_Login_Other_Request(userBase), new HttpCallback<String>() {
            @Override
            public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
                loginLintener.loginFail();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                MyLog.e(TAG, "response=" + JSON.toJSONString(response.get()));
                if(response==null|| ToolKits.isJSONNullObj(JSON.toJSONString(response.get()))||response.get()==null)
                {
                    return;
                }
                DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
                String value = dataFromServer.getReturnValue().toString();
                MyLog.e(TAG, "执行了登录返回结果:");
                if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
                    UserEntity userAuthedInfo = new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
                    MyApplication.getInstance(context).setLocalUserInfoProvider(userAuthedInfo);
                    PreferencesToolkits.setAppStartFitst(context);//登陆成功就设置为false
                    if (userAuthedInfo.getUserBase().getUser_status() == 0) {
                        loginLintener.loginSuccess(true);
                    } else {
                        loginLintener.loginSuccess(false);
                    }
                } else {
                    loginLintener.loginFail();
                }
            }
        });
    }


}
