package com.linkloving.rtring_new;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.logic.UI.launch.LoginInfo;
import com.linkloving.rtring_new.logic.dto.EntEntity;
import com.linkloving.rtring_new.logic.dto.SportDeviceEntity;
import com.linkloving.rtring_new.logic.dto.UserBase;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.linkloving.rtring_new.utils.manager.OsUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Response;
import com.youzan.sdk.YouzanSDK;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by zkx on 2016/3/2.
 */
public class MyApplication extends Application{
    private final static String TAG = MyApplication.class.getSimpleName();

    public final static String SERVICE_WATCH = "com.linkloving.rtring_new";
    public final static int DEVICE_BAND_VERSION3 = 4;
    public final static int DEVICE_WATCH = 3;
    public final static int DEVICE_BAND = 2;

    private static MyApplication self = null;

    private LoginInfo userInfo;

    /**目前用于记录SNS中的未读数的全局变量 */
    private int currentTotalCommentNum;

    /** QQ开放平台APP_ID*/
    public static final String QQ_OPEN_APP_ID = "1105312963";  //正式APPID：

    /**
     * <p>
     * 一个获得本application对象的方便方法.<br>
     * 本方法就相当于在你的activity中调用：(MyApplication)this.getApplicationContext()
     * ，本方法只是为了简化操作而已.
     * </p>
     *
     * @param context
     * @return
     */
    public static MyApplication getInstance(Context context)
    {
        return self;
    }

    /**
     * 用户登陆成功后，服务端返回的该用户的最新信息封装对象.
     */
    private UserEntity localUserInfoProvider = null;

    /**
     * true表示是首次进入app，否则不是。用于首页这样的界面里判断是否显示指示性帮助提示。
     */
    private boolean firstIn = false;
    /**
     * true表示已准备好在网络连接上事件发生时自动启运离线数据自动同步机制，
     * 否则表示还没有准备好。加此标识的目的是：当程序启动时（比如刚打开闪屏界面时），
     * 系统就会触发一个网络已连接上的事件（实际上此时程序还没有登陆好、没有准备好呢），此标识
     * 的目的就是要跳过此启动时就发出的事件通知。
     */
    private boolean preparedForOfflineSyncToServer = false;
    /**
     * 网络是否可用, true表示可用，否则表示不可用.
     * <p>
     * 本字段在将在网络事件通知处理中被设置.
     * <p>
     * 注意：本类中的网络状态变更事件，尤其在网络由断变好之后，事件收到延迟在1~2秒
     * ，不知道有没有更好的方法能实时获得网络的变更情况，以后再说吧！
     */
    private boolean localDeviceNetworkOk = true;

    /**OAD完成后回填步数step */
    private int old_step = 0;
    /**OAD完成后回填步数step */
    public int getOld_step()
    {
        return old_step;
    }
    /**OAD完成后回填步数step */
    public void setOld_step(int old_step)
    {
        this.old_step = old_step;
    }


    /**app用到的图片信息常量*/
    public final Const pic_const = new Const();

    // uncaught exception handler variable
    private Thread.UncaughtExceptionHandler defaultUEH;

    // handler listener
    private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            if(ex != null)
                MyLog.e("_unCaughtExceptionHandler", ex.getMessage());
            defaultUEH.uncaughtException(thread, ex);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        //获取进程名称
        String processName = OsUtils.getProcessName(this,android.os.Process.myPid());
        if (processName != null){
            //判断是否是主进程
            boolean defaultProcess = processName.equals(MyApplication.SERVICE_WATCH);
            if (defaultProcess) {
                MyLog.e(TAG, "MyApplication 开始 onCreate。。。");
                //必要的初始化资源操作
                self = this;
                /** 网络连接事件注册*/
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                this.registerReceiver(networkConnectionStatusBroadcastReceiver, intentFilter);
                //开始服务
                startBleService();
                /** 崩溃处理handler */
                defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
                // setup handler for uncaught exception
                Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
                /**不必为每一次HTTP请求都创建一个RequestQueue对象，推荐在application中初始化*/
				NoHttp.init(MyApplication.this);
                ImageLoader.getInstance().init(CommParams.getImgConfig(self, pic_const));
                SDKInitializer.initialize(this);

                /**
                 * 初始化有赞SDK
                 * @param Context application Context
                 * @param userAgent 用户代理 UA, 调试可以使用"demo"这个UA
                 */
                YouzanSDK.init(this, "32876aeee6e816263f1465378270032");
            }
        }
    }
    /**
     * 本地网络状态变更消息接收对象.
     * <p>
     * 接收本地网络状态变更的目的在于解决当正常的连接因本地网络改变（比如：网络断开了后，又连上了）
     * 而无法再次正常发送数据的问题（即使网络恢复后），解决的方法是：当检测到本地网络断开后就立即关停
     * 本地UDP Socket，这样当下次重新登陆或尝试发送数据时就会重新建立Socket从而达到重置Socket的目的，
     * Socket重置后也就解决了这个问题。
     */
    private final BroadcastReceiver networkConnectionStatusBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(final Context context, Intent intent)
        {
            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!(mobNetInfo != null && mobNetInfo.isConnected()) && !(wifiNetInfo != null && wifiNetInfo.isConnected()))
            {
                MyLog.e(TAG, "【本地网络通知】检测本地网络连接断开了!");
                localDeviceNetworkOk = false;
            }
            else
            {
                // connect network
                MyLog.e(TAG, "【本地网络通知】检测本地网络已连接上了!");
                //
                localDeviceNetworkOk = true;

                if(preparedForOfflineSyncToServer)
                {
                }
            }
        }
    };


    private HttpCallback<String> httpCallback=new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {

        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

        }

    };

    /**当前SNS记录的未读数据*/
    public int getCommentNum()
    {
        return currentTotalCommentNum;
    }

    public void setCommentNum(int commentNum)
    {
        currentTotalCommentNum = commentNum;
    }


    /**返回用户信息：获得用户信息提供者：*/
    public UserEntity getLocalUserInfoProvider()
    {
        // 用户信息对象是null的情况，可能是被Android内存回收机制错误地进行垃圾回收了，
        // 因此对象使用范围很广，为了在发生错误内存回收后，不会导致调用方的NullPointException，
        // 此处就强制从本地读取一份（好在当app启动后，每个属性的改变都会及时保存一次到本地，所以
        // 此时读不会导致不一致的发生，是安全的！）
        if(localUserInfoProvider == null)
            localUserInfoProvider = PreferencesToolkits.getLocalUserInfo(this);
        return localUserInfoProvider;
    }

    /**
     * 本方法只在2种情形下被调用.
     * 1)正常登陆成功时，会把返回的用户信息返回保存起来（也就调用本方法）；<br>
     * 2)当免登陆时，会把本地存储的用户信息读取并调用本方法保存到内存中.
     * @param _localUserInfoProvider
     */
    public void setLocalUserInfoProvider(final UserEntity _localUserInfoProvider)
    {
        setLocalUserInfoProvider(_localUserInfoProvider, true);
    }
    /**
     * 本方法只在2种情形下被调用.
     * <p>
     * 1)正常登陆成功时，会把返回的用户信息返回保存起来（也就调用本方法）；<br>
     * 2)当免登陆时，会把本地存储的用户信息读取并调用本方法保存到内存中.
     *
     * @param _localUserInfoProvider
     * @param savePreferences true表示同时会被写到本地preference，否则不保存
     */
    public void setLocalUserInfoProvider(final UserEntity _localUserInfoProvider, boolean savePreferences)
    {
        this.localUserInfoProvider = _localUserInfoProvider;
        if(localUserInfoProvider.getDeviceEntity()==null){
            localUserInfoProvider.setDeviceEntity(new SportDeviceEntity());
        }
        if(localUserInfoProvider.getUserBase()==null){
            localUserInfoProvider.setUserBase(new UserBase("1990-01-01"));
        }
        if(localUserInfoProvider.getEntEntity()==null){
            localUserInfoProvider.setEntEntity(new EntEntity());
        }
        if(savePreferences)
            // 2014-06-13日把完整的UserEntity对象保存起来备用（用于接下来的免登陆功能）
            PreferencesToolkits.setLocalUserInfo(this, localUserInfoProvider);
        // 为UserEntity对象添加属性改变监听事件处理
        if(localUserInfoProvider != null)
        {
            localUserInfoProvider.setPropertyChangedObserver(new Observer()
            {
                // 当每个属性改变时，都即时更新到本地存储，以保证内存中与Prefrence中的内容完全一致
                @Override
                public void update(Observable observable, Object data)
                {
                    MyLog.e(TAG,"【UserEntity属性改变了】属性名:"+data);
                    // 每次属性被改变时（比如修改昵称、修改性能等）都将重新把此用户对象存储一次（以备下次免登陆时使用）
                    PreferencesToolkits.setLocalUserInfo(MyApplication.this, MyApplication.this.localUserInfoProvider);
                }
            });

            localUserInfoProvider.getDeviceEntity().setPropertyChangedObserver(new Observer()
            {
                // 当每个属性改变时，都即时更新到本地存储，以保证内存中与Prefrence中的内容完全一致
                @Override
                public void update(Observable observable, Object data)
                {
                    MyLog.e(TAG,"【DeviceEntity(】属性名:"+data);
                    // 每次属性被改变时（比如修改昵称、修改性能等）都将重新把此用户对象存储一次（以备下次免登陆时使用）
                    PreferencesToolkits.setLocalUserInfo(MyApplication.this, MyApplication.this.localUserInfoProvider);
                }
            });

            localUserInfoProvider.getUserBase().setPropertyChangedObserver(new Observer()
            {
                // 当每个属性改变时，都即时更新到本地存储，以保证内存中与Prefrence中的内容完全一致
                @Override
                public void update(Observable observable, Object data)
                {
                    MyLog.e(TAG,"【UserBase】属性名:"+data);
                    // 每次属性被改变时（比如修改昵称、修改性能等）都将重新把此用户对象存储一次（以备下次免登陆时使用）
                    PreferencesToolkits.setLocalUserInfo(MyApplication.this, MyApplication.this.localUserInfoProvider);
                }
            });

            localUserInfoProvider.getEntEntity().setPropertyChangedObserver(new Observer()
            {
                // 当每个属性改变时，都即时更新到本地存储，以保证内存中与Prefrence中的内容完全一致
                @Override
                public void update(Observable observable, Object data)
                {
                    MyLog.e(TAG,"【EntEntity】属性名:"+data);
                    // 每次属性被改变时（比如修改昵称、修改性能等）都将重新把此用户对象存储一次（以备下次免登陆时使用）
                    PreferencesToolkits.setLocalUserInfo(MyApplication.this, MyApplication.this.localUserInfoProvider);
                }
            });

            // 根据用户的日志开启属性来决定蓝牙log是否要被抓到本地
//            if("1".equals(localUserInfoProvider.getEnable_sync_log()))
//                LogX.enabled = true;
//            else
//                LogX.enabled = false;
        }
    }

    public boolean isLocalDeviceNetworkOk()
    {
        return localDeviceNetworkOk;
    }

    public boolean isFirstIn()
    {
        return firstIn;
    }

    public void setFirstIn(boolean firstIn)
    {
        this.firstIn = firstIn;
    }
    // 系统到主界面中，应该已经完成准备好了，开启在网络连上事件时自动启动同步线程的开关吧
    public void setPreparedForOfflineSyncToServer(boolean preparedForOfflineSyncToServer)
    {
        this.preparedForOfflineSyncToServer = preparedForOfflineSyncToServer;
    }

    /**
     * 用来判断某服务是否运行.
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public  boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        /**获得Activity管理器并返回所有的服务列表*/
        ActivityManager activityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(100);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().toString().equals(className) == true)
            {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 启动蓝牙服务：
     * 启动服务：服务为：com.linkloving.watch.BLESERVICE
     *
     */
    public void startBleService()
    {
        MyLog.i(TAG, "registerServices");
        Intent serviceintent = new Intent();
        serviceintent.setAction("com.linkloving.watch.BLE_SERVICE");
        serviceintent.setPackage(getPackageName());
        startService(serviceintent); //启动服务程序。
    }

    /**
     * 停止蓝牙服务：
     * 停止指定的服务：com.linkloving.watch.SERVICE
     */
    public void stopBleService()
    {
        MyLog.i(TAG, "stopServices");
        Intent stopIntent =new Intent();
        stopIntent.setAction("com.linkloving.watch.BLE_SERVICE");
        stopIntent.setPackage(getPackageName());
        stopService(stopIntent);
    }

    /**内存低处理*/
    public void onLowMemory ()
    {
        super.onLowMemory();
        MyLog.e(TAG,"----------------------------onLowMemory-------------------------------");
    }

    /**.
     * 释放蓝牙相关的所有资源.
     * 释放蓝牙提供者所占的资源
     */
    public void releaseBLE()
    {
        Log.e(TAG, "releaseBLE");
        BleService.getInstance(MyApplication.this).releaseBLE();
    }

    /**
     * APP退出时需要做的释放操作.
     */
    public void releaseAll()
    {
        try
        {
            Log.e(TAG, "Myapplication  releaseAll");
            this.unregisterReceiver(networkConnectionStatusBroadcastReceiver);
            releaseBLE();
        }
        catch (Exception e)
        {
            Log.w(TAG, e.getMessage(), e);
        }
    }



    /**获取用户的信息*/
    public LoginInfo getUserInfo(){

        return null;
    }

    /**设置用户信息*/
    public void serLoginInfo(LoginInfo userInfo ,boolean savePreferences){

        this.userInfo=userInfo;
        if(savePreferences){
            //将用户数据保存到Preferences，用在免登录的情况

        }
        //为用户增加一个观察者
        }
    /**
     * app用到的图片信息常量：地址/大小
     * @author Linkloving
     */
    public class Const
    {
        /**根目录*/
        public final static String DIR_KCHAT_WORK_RELATIVE_ROOT = "/"+".Linkloving";
        /** 用户头像缓存目录 */
        public final static String DIR_KCHAT_AVATART_RELATIVE_DIR = DIR_KCHAT_WORK_RELATIVE_ROOT+"/"+"avatar";
        /** 用户OAD升级文件缓存目录 */
        public final static String DIR_KCHAT_OAD_RELATIVE_DIR = DIR_KCHAT_WORK_RELATIVE_ROOT+"/"+"OAD";
        /**企业用户头像图片缓存目录*/
        public final static String DIR_ENT_IMAGE_RELATIVE_DIR = DIR_KCHAT_WORK_RELATIVE_ROOT+"/"+"ent";
        /** 用户上传头像时，允许的最大用户头像文件大小 */
        public final static long LOCAL_AVATAR_FILE_DATA_MAX_LENGTH = 2 * 1024 * 1024;
    }


}
