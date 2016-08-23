package com.linkloving.rtring_new.logic.UI.launch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

public class AppStartActivity extends AppCompatActivity {

    private final static String TAG = AppStartActivity.class.getSimpleName();
    public static String SHAREDPREFERENCES_NAME = "first_pref";
    private RelativeLayout startLL = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // FIX: 以下代码是为了解决Android自level 1以来的[安装完成点击“Open”后导致的应用被重复启动]的Bug
        // @see https://code.google.com/p/android/issues/detail?id=52247
        // @see https://code.google.com/p/android/issues/detail?id=2373
        // @see https://code.google.com/p/android/issues/detail?id=26658
        // @see https://github.com/cleverua/android_startup_activity
        // @see http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
        // @see http://stackoverflow.com/questions/12111943/duplicate-activities-on-the-back-stack-after-initial-installation-of-apk
        // 加了以下代码还得确保Manifast里加上权限申请：“android.permission.GET_TASKS”
        if (!isTaskRoot())
        {// FIX START
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
                    intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
            }
        }// FIX END
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final View view = View.inflate(this, R.layout.activity_app_start, null);
        setContentView(view);

        startLL = (RelativeLayout) findViewById(R.id.start_LL);
        /**设置APP状态为打开*/
        BleService.getInstance(this).setEXIT_APP(false);

        UserEntity user = PreferencesToolkits.getLocalUserInfo(this);

        AppManager.getAppManager().addActivity(this);




      /*  if(user != null && !CommonUtils.isStringEmpty(user.getEsplash_screen_file_name()))
        {
            //MyApplication.getInstance(this)._const.DIR_ENT_IMAGE_RELATIVE_DIR + "/" + user.getEsplash_screen_file_name()
            File file = new File(EntHelper.getEntFileSavedDir(this)+"/"+ user.getEsplash_screen_file_name());
            if(file.exists())
            {
                try
                {
                    // Drawable com.eva.android
                    startLL.setBackground(BitmapHelper.loadDrawble(file.getAbsolutePath()));
                }
                catch (Exception e)
                {
                    Log.w(TAG, e.getMessage(), e);
                }
            }
        }*/
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0)
            {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }

            @Override
            public void onAnimationStart(Animation animation)
            {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }


    /**
     * 跳转到...
     */
    private void redirectTo()
    {
        SharedPreferences preferences = this.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean isFrist = (!preferences.contains("isFirstIn")) || (preferences.getBoolean("isFirstIn", true));
        if (isFrist)
        {
            //如果是第一次登陆的话,本来是出现引导页的,这里直接简化,让它跳到登陆页面
            MyLog.i(TAG,"第一次登陆>>>>>>>>>>>>>>>>>>");
            //跳到登陆界面
            startActivity(IntentFactory.createLoginPageActivity(AppStartActivity.this));
//            MyApplication.getInstance(this).setFirstIn(true);
            finish();

            //startActivity(IntentFactory.createHelpActivityIntent(AppStart.this, HelpActivity.FININSH_VIEWPAGE_GO_TAB_HOST, false));
//            finish();
        }
        else
        {
//            MyApplication.getInstance(this).setFirstIn(false);
            UserEntity userAuthedInfo = PreferencesToolkits.getLocalUserInfoForLaunch(this);
           // MyLog.i(TAG, "免登陆>>>>>>>>>>>>>>>>>>" + userAuthedInfo.getDeviceEntity().getLast_sync_device_id());
            // 2014-06-24新版本更新前，此值肯定是null，此条件就是强制要求老版本用户登陆一次（之后免登陆自然就ok了）
            // user_type为1表示为第三方登录，是不需要记录用户名的
            //原来的app还对最近用户判断了是不是空的
            // 免登陆
            if( userAuthedInfo != null)
            {
                // && ((!CommonUtils.isStringEmpty(userAuthedInfo.getUser_type()) && !userAuthedInfo.getUser_type().equals("0")))
                //** 【注意】当免登陆时，以下代码一定要与正常登陆时成功后的代码保持一致！！！！！！！！！
                //** 【注意】当免登陆时，以下代码一定要与正常登陆时成功后的代码保持一致！！！！！！！！！
                //** 【注意】当免登陆时，以下代码一定要与正常登陆时成功后的代码保持一致！！！！！！！！！
                // 把本地用户信息保存到全局变量备用哦
                if (userAuthedInfo.getUserBase().getUser_status() == 0) {
                    startActivity(IntentFactory.createLoginPageActivity(this));//没有设置身高体重,让他重新登录
                    AppStartActivity.this.finish();
                } else{
                    MyApplication.getInstance(this).setLocalUserInfoProvider(userAuthedInfo);
                    startActivity(IntentFactory.createPortalActivityIntent(AppStartActivity.this));
                    AppStartActivity.this.finish();
                }
            }
            else
            {
                MyLog.i(TAG,">>>>>>>>>>>>>>>>>>UserEntity是null");
                //跳到登陆界面
                startActivity(IntentFactory.createLoginPageActivity(this));
                finish();
            }
        }
    }


}
