//package com.linkloving.rtring_new.logic.UI.launch;
//
//import android.Manifest;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AlertDialog;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSON;
//import com.google.gson.Gson;
//import com.jaeger.library.StatusBarUtil;
//import com.linkloving.rtring_new.CommParams;
//import com.linkloving.rtring_new.IntentFactory;
//import com.linkloving.rtring_new.MyApplication;
//import com.linkloving.rtring_new.R;
//import com.linkloving.rtring_new.basic.AppManager;
//import com.linkloving.rtring_new.http.basic.CallServer;
//import com.linkloving.rtring_new.http.basic.HttpCallback;
//import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
//import com.linkloving.rtring_new.http.data.DataFromServer;
//import com.linkloving.rtring_new.logic.dto.UserBase;
//import com.linkloving.rtring_new.logic.dto.UserEntity;
//import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
//import com.linkloving.rtring_new.utils.CommonUtils;
//import com.linkloving.rtring_new.utils.LanguageHelper;
//import com.linkloving.rtring_new.utils.MyToast;
//import com.linkloving.rtring_new.utils.PermissionUtil;
//import com.linkloving.rtring_new.utils.ToolKits;
//import com.linkloving.rtring_new.utils.logUtils.MyLog;
//import com.linkloving.utils._Utils;
//import com.mob.tools.utils.UIHandler;
//import com.yolanda.nohttp.Response;
//import com.zhy.autolayout.AutoLayoutActivity;
//
//import java.util.HashMap;
//
//import cn.sharesdk.facebook.Facebook;
//import cn.sharesdk.framework.Platform;
//import cn.sharesdk.framework.PlatformActionListener;
//import cn.sharesdk.framework.ShareSDK;
//import cn.sharesdk.google.GooglePlus;
//import cn.sharesdk.tencent.qq.QQ;
//import cn.sharesdk.twitter.Twitter;
//import cn.sharesdk.wechat.friends.Wechat;
//
//public class LoginPageActivity extends AutoLayoutActivity implements View.OnClickListener, Handler.Callback {
//    public static final String TAG = LoginPageActivity.class.getSimpleName();
//    private static final int MSG_ACTION_CCALLBACK = 2;
//    private static final int WX_LOGIN = 3;
//    private static final int QQ_LOGIN = 1;
//    private static final int FB_LOGIN = 5;
//    private static final int TW_LOGIN = FB_LOGIN + 1;
//    private static final int GL_LOGIN = TW_LOGIN + 1;
//
//    private static final int WX_SUCCESS = 1;
//    private static final int WX_ERROR = WX_SUCCESS + 1;
//    private static final int WX_CALCEL = WX_ERROR + 1;
//
//    private static final int FB_SUCCESS = 4;
//    private static final int FB_ERROR = FB_SUCCESS + 1;
//    private static final int FB_CALCEL = FB_ERROR + 1;
//
//    private static final int TW_SUCCESS = 7;
//    private static final int TW_ERROR = TW_SUCCESS + 1;
//    private static final int TW_CALCEL = TW_ERROR + 1;
//
//    private static final int GL_SUCCESS = 10;
//    private static final int GL_ERROR = GL_SUCCESS + 1;
//    private static final int GL_CALCEL = GL_ERROR + 1;
//
//    private static final int QQ_SUCCESS = 13;
//    private static final int QQ_ERROR = QQ_SUCCESS + 1;
//    private static final int QQ_CALCEL = QQ_ERROR + 1;
//
//    Button Login_btn, Registered_btn;
//    ImageView Login_QQ_btn, login_WeChat, Login_Facebook_btn, login_Twitter, login_Google;
//    ProgressDialog dialog;
//    private String weixin_openid;
//    private String weixin_name;
//    private static String weixin_time = "";
//    private String weixinCode;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login_page);
//        AppManager.getAppManager().addActivity(this);
//        ShareSDK.initSDK(this);
//        //状态栏透明
//        StatusBarUtil.setTranslucent(this, 0);
//        initView();
//        if(!PermissionUtil.checkPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)){
//            requestPermissions();
//        }
//        if(!PermissionUtil.checkPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//            requestPermissions();
//        }
//
//    }
//    private void initView() {
//        Login_btn = (Button) this.findViewById(R.id.login_btn);
//        Registered_btn = (Button) this.findViewById(R.id.registered_btn);
//        Login_QQ_btn = (ImageView) findViewById(R.id.btn_login_qq);
//        login_WeChat = (ImageView) findViewById(R.id.btn_login_weixin);
//        Login_Facebook_btn = (ImageView) findViewById(R.id.btn_login_facebook);
//        login_Twitter = (ImageView) findViewById(R.id.btn_login_twitter);
//        login_Google = (ImageView) findViewById(R.id.btn_login_google);
//        if (!LanguageHelper.isChinese_SimplifiedChinese()) {
//            Login_QQ_btn.setVisibility(View.GONE);
//            login_WeChat.setVisibility(View.GONE);
//            Login_Facebook_btn.setVisibility(View.VISIBLE);
//            login_Twitter.setVisibility(View.VISIBLE);
//            login_Google.setVisibility(View.VISIBLE);
//        } else {
//            Login_QQ_btn.setVisibility(View.VISIBLE);
//            login_WeChat.setVisibility(View.VISIBLE);
//            Login_Facebook_btn.setVisibility(View.GONE);
//            login_Twitter.setVisibility(View.GONE);
//            login_Google.setVisibility(View.GONE);
//        }
//        Login_btn.setOnClickListener(this);
//        Registered_btn.setOnClickListener(this);
//        dialog = new ProgressDialog(this);
//        dialog.setMessage(getString(R.string.general_loading));
////      login_ViewPager= (ViewPager) findViewById(R.id.login_ViewPager);
//        Login_Facebook_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doLoginFacebook();
//            }
//        });
//        login_Twitter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doLoginTwitter();
//            }
//        });
//        login_Google.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doLoginGoogle();
//            }
//        });
//        Login_QQ_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doLoginFromQQ();
//            }
//        });
//        login_WeChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doLoginFromwechat();
//            }
//        });
//        login_Google.setVisibility(View.GONE);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.login_btn:
//                IntentFactory.start_LoginPage_Activity(LoginPageActivity.this);
//                break;
//            case R.id.registered_btn:
//                IntentFactory.start_RegisterActivity_Activity(LoginPageActivity.this);
//                break;
//        }
//    }
//
//
//    private void doLoginFromQQ() {
//        dialog.show();
//        MyLog.i(TAG, "点击扣扣登录");
//        final Platform qq = ShareSDK.getPlatform(QQ.NAME);
//        qq.setPlatformActionListener(new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = QQ_SUCCESS;
//                msg.arg2 = i;
//                msg.obj = platform;
//                if (platform.getName().equals(Facebook.NAME)) {
//                    MyLog.e(TAG, ">>>>>>>doLoginQQ>>>>>");
//                }
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//                Log.e(TAG, "onComplete" + hashMap);
//                Log.e(TAG, "UserName()：" + platform.getDb().getUserName());
//                Log.e(TAG, "getUserId()：" + platform.getDb().getUserId());
//                MyLog.e(TAG, ">>>>>>>doLoginQQ>>>>>" + platform.getDb().toString());
//                //获取资料
//                platform.getDb().getUserName();//获取用户名字
//                platform.getDb().getUserIcon(); //获取用户头像
//                MyLog.e(TAG, "QQ登陆成功");
//                weixin_openid = platform.getDb().getUserId();
//                weixin_name = platform.getDb().getUserName();
//                weixinCode = platform.getDb().getToken();
//                weixin_time = platform.getDb().getExpiresTime() + "";
//                qq.removeAccount(true);
//                MyLog.e(TAG, ">>>>>>>weixin_name>>>>>"+weixin_name+",weixin_openid"+weixin_openid+",weixinCode="+weixinCode);
//            }
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//                MyLog.e(TAG, "QQ登陆失败");
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = QQ_ERROR;
//                msg.arg2 = i;
//                msg.obj = throwable;
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//                // 分享失败的统计
//            }
//            @Override
//            public void onCancel(Platform platform, int i) {
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = QQ_CALCEL;
//                msg.arg2 = i;
//                msg.obj = platform;
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//            }
//        });
//        //设置false表示使用SSO授权方式 使用了SSO授权后，有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权。
//        qq.SSOSetting(false);
//        qq.showUser(null);
//        }
//    private void doLoginFacebook() {
//        dialog.show();
//        Platform facebookfd = ShareSDK.getPlatform(Facebook.NAME);
//        facebookfd.setPlatformActionListener(new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = FB_SUCCESS;
//                msg.arg2 = i;
//                msg.obj = platform;
//                if (platform.getName().equals(Facebook.NAME)) {
//                    MyLog.e(TAG, ">>>>>>>doLoginFacebook>>>>>");
//                }
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//                Log.e(TAG, "onComplete" + hashMap);
//                Log.e(TAG, "UserName()：" + platform.getDb().getUserName());
//                Log.e(TAG, "getUserId()：" + platform.getDb().getUserId());
//                MyLog.e(TAG, ">>>>>>>doLoginFacebook>>>>>");
//                //获取资料
//                platform.getDb().getUserName();//获取用户名字
//                platform.getDb().getUserIcon(); //获取用户头像
//                MyLog.e(TAG, "Facebook登陆成功");
//                weixin_openid = platform.getDb().getUserId();
//                weixin_name = platform.getDb().getUserName();
//                weixinCode = platform.getDb().getToken();
//                weixin_time = platform.getDb().getExpiresTime() + "";
//                platform.removeAccount(true);//移除账号
//                MyLog.e(TAG, ">>>>>>>weixin_name>>>>>" + weixin_name + ",weixin_openid" + weixin_openid + ",weixinCode=" + weixinCode);
//            }
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//                MyLog.e(TAG, "Facebook登陆失败" + throwable.toString());
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = FB_ERROR;
//                msg.arg2 = i;
//                msg.obj = throwable;
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//                // 分享失败的统计
//            }
//            @Override
//            public void onCancel(Platform platform, int i) {
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = FB_CALCEL;
//                msg.arg2 = i;
//                msg.obj = platform;
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//            }
//        });
//        //设置false表示使用SSO授权方式 使用了SSO授权后，有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权。
//        facebookfd.SSOSetting(false);
//        facebookfd.showUser(null);
//    }
//
//    private void doLoginTwitter() {
//        dialog.show();
//        Platform twitter = ShareSDK.getPlatform(Twitter.NAME);
//        twitter.setPlatformActionListener(new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = TW_SUCCESS;
//                msg.arg2 = i;
//                msg.obj = platform;
//                if (platform.getName().equals(Twitter.NAME)) {
//                    MyLog.e(TAG, ">>>>>>>doLoginTwitter>>>>>");
//                }
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//                Log.e(TAG, "doLoginTwitteronComplete" + hashMap);
//                Log.e(TAG, "doLoginTwitterUserName()：" + platform.getDb().getUserName());
//                Log.e(TAG, "doLoginTwittergetUserId()：" + platform.getDb().getUserId());
////               MyToast.show(LoginPageActivity.this, "id=" + platform.getDb().getUserId(), Toast.LENGTH_LONG);
//                //获取资料
//                platform.getDb().getUserName();//获取用户名字
//                platform.getDb().getUserIcon(); //获取用户头像
//                weixin_openid = platform.getDb().getUserId();
//                weixin_name = platform.getDb().getUserName();
//                weixinCode = platform.getDb().getToken();
//                weixin_time = platform.getDb().getExpiresTime() + "";
//                platform.removeAccount(true);//移除账号
//            }
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = TW_ERROR;
//                msg.arg2 = i;
//                msg.obj = throwable;
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//                // 分享失败的统计
//            }
//
//            @Override
//            public void onCancel(Platform platform, int i) {
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = TW_CALCEL;
//                msg.arg2 = i;
//                msg.obj = platform;
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//            }
//        });
//        //设置false表示使用SSO授权方式 使用了SSO授权后，有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权。
//        twitter.SSOSetting(false);
//        twitter.showUser(null);
//    }
//
//    private void doLoginGoogle() {
//        dialog.show();
////        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
////        startActivityForResult(signInIntent, RC_SIGN_IN);
//        MyLog.e(TAG, "点击Google登录");
//        Platform google = ShareSDK.getPlatform(GooglePlus.NAME);
//        google.setPlatformActionListener(new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                System.out.print(">>>>>>>>>>>>>>>>>>>>doLoginGoogle>>>>>>>>>>>>>>>>");
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = GL_SUCCESS;
//                msg.arg2 = i;
//                msg.obj = platform;
//                if (platform.getName().equals(GooglePlus.NAME)) {
//                    MyLog.i(TAG, ">>>>>>doLoginGoogle>>>>>>");
//                }
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//                Log.e(TAG, "onComplete" + hashMap);
//                Log.e(TAG, "UserName()：" + platform.getDb().getUserName());
//                Log.e(TAG, "getUserId()：" + platform.getDb().getUserId());
//                //获取资料
//                platform.getDb().getUserName();//获取用户名字
//                platform.getDb().getUserIcon(); //获取用户头像
//                MyLog.e(TAG, "Google登陆成功");
//                weixin_openid = platform.getDb().getUserId();
//                weixin_name = platform.getDb().getUserName();
//                weixinCode = platform.getDb().getToken();
//                weixin_time = platform.getDb().getExpiresTime() + "";
//                // new WX_LoginAsyncTask().execute(weixin_openid, weixin_name);
//                MyLog.e(TAG, "点击Google登录,weixin_openid=" + weixin_openid + "weixin_name=" + weixin_name + "weixinCode=" + weixinCode);
//                if (MyApplication.getInstance(LoginPageActivity.this).isLocalDeviceNetworkOk()) {
//                    //                 CallServer.getRequestInstance().add(LoginPageActivity.this, getString(R.string.login_form_logining), CommParams.HTTP_LOGIN_GOOGLE, NoHttpRuquestFactory.create_Login_Other_Request(), httpCallback);
//                } else
//                    MyToast.show(LoginPageActivity.this, getString(R.string.general_network_faild), Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//                System.out.print(">>>>>>>>>>>>>>>>>doLoginGoogle>>>>>>>>>>>>>>>>>>>");
//                MyLog.i(TAG, "Google失败" + throwable.toString());
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = GL_ERROR;
//                msg.arg2 = i;
//                msg.obj = throwable;
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//                // 分享失败的统计
////                ShareSDK.logDemoEvent(4, platform);
//            }
//
//            @Override
//            public void onCancel(Platform platform, int i) {
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = GL_CALCEL;
//                msg.arg2 = i;
//                msg.obj = platform;
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//            }
//        });
//        //设置false表示使用SSO授权方式 使用了SSO授权后，有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权。
//        google.SSOSetting(true);
//        google.showUser(null);
//    }
//    private void doLoginFromwechat() {
//        dialog.show();
//        MyLog.e(TAG, "点击微信登录");
//       // login_WeChat.setEnabled(false);
//        final Platform weixinfd = ShareSDK.getPlatform(Wechat.NAME);
//        weixinfd.setPlatformActionListener(new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                Log.e(TAG, "onComplete" + hashMap);
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = WX_SUCCESS;
//                msg.arg2 = i;
//                msg.obj = platform;
//                if (platform.getName().equals(Wechat.NAME)) {
//                    MyLog.i(TAG, ">>>>>>>>>>>>");
//                }
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//                Log.e(TAG, "onComplete" + hashMap);
//                Log.e(TAG, "UserName()：" + platform.getDb().getUserName());
//                Log.e(TAG, "getUserId()：" + platform.getDb().getUserId());
//                //获取资料
//                platform.getDb().getUserName();//获取用户名字
//                platform.getDb().getUserIcon(); //获取用户头像
//                MyLog.e(TAG, "微信登陆成功");
//                weixin_openid = platform.getDb().getUserId();
//                weixin_name = platform.getDb().getUserName();
//                weixinCode = platform.getDb().getToken();
//                weixin_time = platform.getDb().getExpiresTime() + "";
//                weixinfd.removeAccount(true);
//                // new WX_LoginAsyncTask().execute(weixin_openid, weixin_name);
//            }
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//                Log.e(TAG, "onComplete" + throwable.toString());
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = WX_ERROR;
//                msg.arg2 = i;
//                msg.obj = throwable;
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//                // 分享失败的统计
//                ShareSDK.logDemoEvent(4, platform);
//            }
//            @Override
//            public void onCancel(Platform platform, int i) {
//                Log.e(TAG, "点击微信登录onCancel");
//                if (dialog != null) {
//                    dialog.dismiss();
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//                Message msg = new Message();
//                msg.what = MSG_ACTION_CCALLBACK;
//                msg.arg1 = WX_CALCEL;
//                msg.arg2 = i;
//                msg.obj = platform;
//                UIHandler.sendMessage(msg, LoginPageActivity.this);
//            }
//        });
//        weixinfd.showUser(null);
//    }
//    @Override
//    public boolean handleMessage(Message msg) {
//        if (dialog != null) {
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
//        }
//        switch (msg.arg1) {
//            case WX_SUCCESS: {
//                MyLog.i(TAG, "成功");
//                login_WeChat.setEnabled(true);
////                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_success), Toast.LENGTH_SHORT).show();
//                getDataFromServletOther(WX_LOGIN);
//                break;
//            }
//            case WX_ERROR: {
//                MyLog.i(TAG, "失败");
//                login_WeChat.setEnabled(true);
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//                // 失败
//                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_fail), Toast.LENGTH_SHORT).show();
//                String expName = msg.obj.getClass().getSimpleName();
//                MyLog.i(TAG, ">>>>>微信>>>>>" + expName);
//                if ("WechatClientNotExistException".equals(expName)
//                        || "WechatTimelineNotSupportedException".equals(expName)
//                        || "WechatFavoriteNotSupportedException".equals(expName)) {
//                    Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat), Toast.LENGTH_SHORT).show();
//                }
//                break;
//            }
//            case WX_CALCEL: {
//                login_WeChat.setEnabled(true);
//                // 取消
//                MyLog.i(TAG, "取消····");
//                System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_cancle), Toast.LENGTH_SHORT)
//                        .show();
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//                break;
//            }
//            case FB_SUCCESS: {
//                MyLog.e(TAG, "成功");
//                // 成功
//                Login_Facebook_btn.setEnabled(true);
////                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_success), Toast.LENGTH_SHORT).show();
//                getDataFromServletOther(FB_LOGIN);
//                break;
//            }
//            case FB_ERROR: {
//                MyLog.e(TAG, "FB_ERROR失败");
//                Login_Facebook_btn.setEnabled(true);
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//                // 失败
//                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_fail), Toast.LENGTH_SHORT).show();
//                String expName = msg.obj.getClass().getSimpleName();
//                MyLog.i(TAG, ">>>>>>Facebook>>>>" + expName);
//                break;
//            }
//            case FB_CALCEL: {
//                Login_Facebook_btn.setEnabled(true);
//                // 取消
//                MyLog.i(TAG, "取消····");
//                System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_cancle), Toast.LENGTH_SHORT)
//                        .show();
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//                break;
//            }
//            case TW_SUCCESS: {
//                MyLog.i(TAG, "TW_SUCCESS成功");
////                MyToast.show("");
//                // 成功
//                login_Twitter.setEnabled(true);
////                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_success), Toast.LENGTH_SHORT).show();
//                getDataFromServletOther(TW_LOGIN);
//                if (MyApplication.getInstance(LoginPageActivity.this).isLocalDeviceNetworkOk()) {
//                    //Twitter登录
//                } else
//                    MyToast.show(LoginPageActivity.this, getString(R.string.general_network_faild), Toast.LENGTH_LONG);
//                break;
//            }
//            case TW_ERROR: {
//                MyLog.i(TAG, "===失败====");
//                login_Twitter.setEnabled(true);
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//                // 失败
//                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_fail), Toast.LENGTH_SHORT).show();
//                String expName = msg.obj.getClass().getSimpleName();
//                MyLog.i(TAG, ">>>>>Twitter>>>>>" + expName);
//                break;
//            }
//            case TW_CALCEL: {
//                login_Twitter.setEnabled(true);
//                // 取消
//                MyLog.i(TAG, "取消····");
//                System.out.print(">>>>>>>>>>>>>>>>>>>>>Twitter>>>>>>>>>>>>>>>");
//                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_cancle), Toast.LENGTH_SHORT)
//                        .show();
//
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//
//                }
//
//                break;
//            }
//            case GL_SUCCESS: {
//
//                MyLog.i(TAG, "成功");
//                // 成功
//                login_Google.setEnabled(true);
//
////                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_success), Toast.LENGTH_SHORT).show();
//                //谷歌登录
//                getDataFromServletOther(GL_LOGIN);
//
//                break;
//            }
//
//            case GL_ERROR: {
//                MyLog.i(TAG, "失败");
//                login_Google.setEnabled(true);
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//                // 失败
//                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_fail), Toast.LENGTH_SHORT).show();
//
//                String expName = msg.obj.getClass().getSimpleName();
//
//                MyLog.i(TAG, ">>>>Google>>>>>>" + expName);
//                break;
//            }
//
//            case GL_CALCEL: {
//                login_Google.setEnabled(true);
//                // 取消
//                MyLog.i(TAG, "取消····");
//                System.out.print(">>>>>>>>>>>>>>>>>>Google>>>>>>>>>>>>>>>>>>");
//                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_cancle), Toast.LENGTH_SHORT)
//                        .show();
//
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//
//                }
//
//                break;
//            }
//            case QQ_SUCCESS: {
//
//
//                MyLog.i(TAG, "成功");
//                // 成功
//                login_Google.setEnabled(true);
//
////                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_success), Toast.LENGTH_SHORT).show();
//                //谷歌登录
//                getDataFromServletOther(QQ_LOGIN);
//                break;
//            }
//            case QQ_ERROR: {
//                MyLog.i(TAG, "失败");
//                login_Google.setEnabled(true);
//
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//
//                }
//                // 失败
//                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_fail), Toast.LENGTH_SHORT).show();
//
//                String expName = msg.obj.getClass().getSimpleName();
//
//                MyLog.i(TAG, ">>>>Google>>>>>>" + expName);
//                break;
//            }
//            case QQ_CALCEL: {
//                login_Google.setEnabled(true);
//                // 取消
//                MyLog.i(TAG, "取消····");
//                System.out.print(">>>>>>>>>>>>>>>>>>Google>>>>>>>>>>>>>>>>>>");
//                Toast.makeText(LoginPageActivity.this, getString(R.string.login_activity_wechat_cancle), Toast.LENGTH_SHORT)
//                        .show();
//
//                if (dialog != null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//
//                }
//
//                break;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        AppManager.getAppManager().removeActivity(this);
//    }
//
//   /* @Override
//    protected void onResume() {
//        super.onResume();
//        if(dialog!=null&&dialog.isShowing()){
//            dialog.dismiss();
//        }
//    }*/
//    private void getDataFromServletOther(int User_type) {
//   if (MyApplication.getInstance(LoginPageActivity.this).isLocalDeviceNetworkOk()) {
//            UserBase userBase = new UserBase("1991-01-01");
//            userBase.setUser_mail(weixin_openid);
//            userBase.setUser_psw(weixin_openid);
//            userBase.setNickname(weixin_name);
//            if(weixinCode!=null){
//                userBase.setThirdparty_access_token(weixinCode);
//            }else{
//                userBase.setThirdparty_access_token("NOTTOKEN");
//            }
//            userBase.setThirdparty_expire_time(weixin_time);
//            userBase.setUser_type(User_type);
//            userBase.setRegister_os_type("Android");
//            userBase.setUser_height(170);
//            userBase.setUser_sex(1);
//            userBase.setUser_weight(120);
//            userBase.setPlay_calory(2500);
//            userBase.setUser_status(0);
//            userBase.setBirthdate(_Utils.getBirthdateByAge(Integer.parseInt("22")));
//            CallServer.getRequestInstance().add(LoginPageActivity.this, getString(R.string.login_form_logining), CommParams.HTTP_LOGIN_WECHAT, NoHttpRuquestFactory.create_Login_Other_Request(userBase), httpCallback);
//       } else
//      MyToast.show(LoginPageActivity.this, getString(R.string.general_network_faild), Toast.LENGTH_LONG);
//    }
//    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
//        @Override
//        public void onSucceed(int what, Response<String> response) {
//            dialog.dismiss();
//            MyLog.e(TAG, "response=" + JSON.toJSONString(response.get()));
//            if(response==null||ToolKits.isJSONNullObj(JSON.toJSONString(response.get()))||response.get()==null)
//            {
//                MyLog.e(TAG,"返回值为空：return");
//                return;
//            }
//            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
//            String value = dataFromServer.getReturnValue().toString();
////            MyToast.show(LoginPageActivity.this, "执行了登录返回结果：" + value, Toast.LENGTH_SHORT);
//            MyLog.e(TAG, "执行了登录返回结果:");
//            switch (what) {
//                case CommParams.HTTP_LOGIN_QQ:
//                    if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
////                        MyToast.show(LoginPageActivity.this, "执行了QQ登录返回结果：HTTP_LOGIN_WECHAT" + value, Toast.LENGTH_SHORT);
//                        UserEntity userAuthedInfo = new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
////                        MyToast.show(LoginPageActivity.this, "id=" + userAuthedInfo.getUser_id(), Toast.LENGTH_LONG);
//                        MyApplication.getInstance(LoginPageActivity.this).setLocalUserInfoProvider(userAuthedInfo);
//                        PreferencesToolkits.setAppStartFitst(LoginPageActivity.this);//登陆成功就设置为false
//                        if (userAuthedInfo.getUserBase().getUser_status() == 0) {
//                            IntentFactory.startAvatarActivityIntent(LoginPageActivity.this,1);
//                        } else {
//                            startActivity(IntentFactory.createPortalActivityIntent(LoginPageActivity.this));
//                            //清除所有的activity
//                            AppManager.getAppManager().finishAllActivity();
//                            finish();
//                        }
//                    } else {
//                        MyToast.showShort(LoginPageActivity.this, getString(R.string.login_error4));
//                    }
//                    break;
//                case CommParams.HTTP_LOGIN_WECHAT:
//                    if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
////                        MyToast.show(LoginPageActivity.this, "执行了微信登录返回结果：HTTP_LOGIN_WECHAT" + value, Toast.LENGTH_LONG);
//                        UserEntity userAuthedInfo = new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
////                        MyToast.show(LoginPageActivity.this,"id="+userAuthedInfo.getUser_id()+"========"+userAuthedInfo.getUserBase().getUser_id(),Toast.LENGTH_LONG);
//                        MyApplication.getInstance(LoginPageActivity.this).setLocalUserInfoProvider(userAuthedInfo);
//                        PreferencesToolkits.setAppStartFitst(LoginPageActivity.this);//登陆成功就设置为false
//                        if (userAuthedInfo.getUserBase().getUser_status() == 0) {
//                            IntentFactory.startAvatarActivityIntent(LoginPageActivity.this,1);
//                        } else {
//                            startActivity(IntentFactory.createPortalActivityIntent(LoginPageActivity.this));
//                            //清除所有的activity
//                            AppManager.getAppManager().finishAllActivity();
//                            finish();
//                        }
//                    } else {
//                        MyToast.showShort(LoginPageActivity.this, getString(R.string.login_error4));
//                    }
//                    break;
//                case CommParams.HTTP_LOGIN_FACEBOOK:
//                    if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
////                        MyToast.show(LoginPageActivity.this, "执行了FACEBOOK登录返回结果：HTTP_LOGIN_FACEBOOK" + value, Toast.LENGTH_LONG);
//                        UserEntity userAuthedInfo = new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
////                        MyToast.show(LoginPageActivity.this,"id="+userAuthedInfo.getUser_id()+"========"+userAuthedInfo.getUserBase().getUser_id(),Toast.LENGTH_LONG);
//                        MyApplication.getInstance(LoginPageActivity.this).setLocalUserInfoProvider(userAuthedInfo);
//                        PreferencesToolkits.setAppStartFitst(LoginPageActivity.this);//登陆成功就设置为false
//                        if (userAuthedInfo.getUserBase().getUser_status() == 0) {
//                            IntentFactory.startAvatarActivityIntent(LoginPageActivity.this,1);
//                        } else {
//                            startActivity(IntentFactory.createPortalActivityIntent(LoginPageActivity.this));
//                            //清除所有的activity
//                            AppManager.getAppManager().finishAllActivity();
//                            finish();
//                        }
//                    } else {
//                        MyToast.showShort(LoginPageActivity.this, getString(R.string.login_error4));
//                    }
//                    break;
//                case CommParams.HTTP_LOGIN_TWITTER:
//                    if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
////                        MyToast.show(LoginPageActivity.this, "执行了TWITTER登录返回结果：HTTP_LOGIN_TWITTER" + value, Toast.LENGTH_LONG);
//                        UserEntity userAuthedInfo = new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
////                       MyToast.show(LoginPageActivity.this,"id="+userAuthedInfo.getUser_id()+"========"+userAuthedInfo.getUserBase().getUser_id(),Toast.LENGTH_LONG);
//                        MyApplication.getInstance(LoginPageActivity.this).setLocalUserInfoProvider(userAuthedInfo);
//                        PreferencesToolkits.setAppStartFitst(LoginPageActivity.this);//登陆成功就设置为false
//                        if (userAuthedInfo.getUserBase().getUser_status() == 0) {
//                            IntentFactory.startAvatarActivityIntent(LoginPageActivity.this,1);
//                        } else {
//                            startActivity(IntentFactory.createPortalActivityIntent(LoginPageActivity.this));
//                            //清除所有的activity
//                            AppManager.getAppManager().finishAllActivity();
//                            finish();
//                        }
//                    } else {
//                        MyToast.showShort(LoginPageActivity.this, getString(R.string.login_error4));
//                    }
//                    break;
//                case CommParams.HTTP_LOGIN_GOOGLE:
//                    if (!CommonUtils.isStringEmptyPrefer(value) && value instanceof String && !ToolKits.isJSONNullObj(value)) {
////                        MyToast.show(LoginPageActivity.this, "执行了GOOGLE登录返回结果：HTTP_LOGIN_GOOGLE" + value, Toast.LENGTH_LONG);
//                        UserEntity userAuthedInfo = new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
////                        MyToast.show(LoginPageActivity.this,"id="+userAuthedInfo.getUser_id()+"========"+userAuthedInfo.getUserBase().getUser_id(),Toast.LENGTH_LONG);
//                        MyApplication.getInstance(LoginPageActivity.this).setLocalUserInfoProvider(userAuthedInfo);
//                        PreferencesToolkits.setAppStartFitst(LoginPageActivity.this);//登陆成功就设置为false
//                        if (userAuthedInfo.getUserBase().getUser_status() == 0) {
//                            IntentFactory.startAvatarActivityIntent(LoginPageActivity.this,1);
//                        } else {
//                            startActivity(IntentFactory.createPortalActivityIntent(LoginPageActivity.this));
//                            //清除所有的activity
//                            AppManager.getAppManager().finishAllActivity();
//                            finish();
//                        }
//                    } else {
//                        MyToast.showShort(LoginPageActivity.this, getString(R.string.login_error4));
//                    }
//                    break;
//
//            }
//        }
//
//        @Override
//        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
//            dialog.dismiss();
//            MyLog.e(TAG, "========网络请求失败========");
////            MyToast.showShort(LoginFromPhoneActivity.this, getString(R.string.login_error4));
////            MyToast.show(LoginPageActivity.this, "执行了登录失败返回结果：HTTP_LOGIN_WECHAT", Toast.LENGTH_SHORT);
//            MyLog.e(TAG, "onFailed");
//        }
//    };
//
//
//    private void requestPermissions() {
//        // Provide an additional rationale to the user. This would happen if the user denied the
//            AlertDialog dialog = new AlertDialog.Builder(this)
//                    .setTitle(ToolKits.getStringbyId(this, R.string.general_tip))
//                    .setMessage(ToolKits.getStringbyId(this, R.string.permission_ble))
//                    .setPositiveButton(ToolKits.getStringbyId(this, R.string.general_ok),
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    ActivityCompat.requestPermissions(LoginPageActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PermissionUtil.REQUEST_PERMISSIONS_REQUEST_CODE);
//                                    ActivityCompat.requestPermissions(LoginPageActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PermissionUtil.REQUEST_PERMISSIONS_REQUEST_CODE);
//                                }
//                            })
//                    .create();
//            dialog.show();
//
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PermissionUtil.REQUEST_PERMISSIONS_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    MyLog.e("checkBLEPermission", "权限已经打开：");
//                } else {
//                    AppManager.getAppManager().finishAllActivity();
//                    System.exit(0);
//                }
//                break;
//
//        }
//    }
//}
