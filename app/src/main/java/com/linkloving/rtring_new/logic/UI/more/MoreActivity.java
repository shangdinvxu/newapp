package com.linkloving.rtring_new.logic.UI.more;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.google.gson.Gson;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.AppManager;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.db.sport.UserDeviceRecord;
import com.linkloving.rtring_new.http.HttpHelper;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromClientNew;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.launch.ThirdLogin.view.ThirdLoginActivity;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.notify.NotificationCollectorService;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.LocalInfoVO;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.HttpUtils;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Response;
import com.youzan.sdk.YouzanSDK;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


public class MoreActivity extends ToolBarActivity implements View.OnClickListener {
    public static final String TAG = MoreActivity.class.getSimpleName();

    List<SportRecord> sportRecords=new ArrayList<>();
    /**
     * 客户端版本码：读取自程序apk文件的AndroidManifest.xml中的同名属性.
     * 本字段值将在 initViews()前由方法 initProgrammVersion()进行初始化.
     */
    private static int versionCode = -1;
    /**
     * 客户端版本名：读取自程序apk文件的AndroidManifest.xml中的同名属性.
     * 本字段值将在 initViews()前由方法 initProgrammVersion()进行初始化.
     */
    private static String versionName = "";
    private TextView tv_more_unit, tv_more_verson;
    //关于我们  关于群组　群组layout
    private LinearLayout aboutUs,about_group,group_layout;
    private LinearLayout activity_more_start, activity_more_Cloud_Sync,more_unit;
    private LinearLayout main_more_settings_exitent_layout,relogin,exitApp;
    private TextView main_more_settings_entexit,main_more_settings_ent_about_group;
    //蓝牙观察者
    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;
    //蓝牙提供者
    private BLEProvider provider;
    LocalInfoVO vo;
    BLEHandler.BLEProviderObserverAdapter observerAdapter = null;
    UserEntity userEntity;
    private ProgressDialog progressDialog;
    private int pageIndex = 1;
    private int progressindex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        observerAdapter = new BLEProviderObserverAdapterImpl();
        vo = PreferencesToolkits.getLocalDeviceInfo(MoreActivity.this);
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        provider.setBleProviderObserver(observerAdapter);
        initData();
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        SetBarTitleText(getString(R.string.general_more));
        more_unit = (LinearLayout) findViewById(R.id.activity_more_unit_settings);
        tv_more_unit = (TextView) findViewById(R.id.tv_more_unit);
        tv_more_verson = (TextView) findViewById(R.id.tv_more_verson);
        aboutUs = (LinearLayout) findViewById(R.id.activity_more_aboutUs);
        activity_more_start = (LinearLayout) findViewById(R.id.activity_more_start);
        activity_more_Cloud_Sync = (LinearLayout) findViewById(R.id.activity_more_Cloud_Sync);
        relogin = (LinearLayout) findViewById(R.id.activity_more_relogin);
        exitApp = (LinearLayout) findViewById(R.id.activity_more_exit);
        userEntity = MyApplication.getInstance(this).getLocalUserInfoProvider();
        MyLog.e(TAG,"=====userEntity.getEntEntity().getEid()==="+userEntity.getEntEntity().getEnt_id()+"=====userEntity.getEntEntity().getUser_id()==="+userEntity.getUserBase().getUser_id()+"===="+userEntity.getEntEntity().getEnt_name());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_more_start:
                //跳转到入门指南界面
                Intent intentGuide = new Intent(MoreActivity.this, GuideActivity.class);
                startActivity(intentGuide);
                break;
            case R.id.activity_more_aboutUs:
                //跳转到about界面
                Intent intent = new Intent(MoreActivity.this, AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_more_unit_settings:
                LayoutInflater inflater = getLayoutInflater();
                View layoutbund =inflater.inflate(R.layout.modify_sex_dialog, (ViewGroup)findViewById(R.id.linear_modify_sex));
                final RadioButton metric_system= (RadioButton) layoutbund.findViewById(R.id.rb_left);
                metric_system.setText(getString(R.string.metric_system));
                final RadioButton more_inch=(RadioButton) layoutbund.findViewById(R.id.rb_right);
                more_inch.setText(getString(R.string.more_inch));
                AlertDialog dialogunit = new AlertDialog.Builder(MoreActivity.this)
                        .setTitle(ToolKits.getStringbyId(MoreActivity.this, R.string.unit_settings))
                        .setView(layoutbund)
                        .setPositiveButton(ToolKits.getStringbyId(MoreActivity.this, R.string.general_ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //选择单位  添加布局 布局中放两个RadioButtom
                                        if (metric_system.isChecked()){
                                            tv_more_unit.setText(metric_system.getText().toString());
                                            //存储值到本地
                                            PreferencesToolkits.setLocalSettingUnitInfo(MoreActivity.this,ToolKits.UNIT_GONG);
                                        }else {
                                            tv_more_unit.setText(more_inch.getText().toString());
                                            //存储值到本地
                                            PreferencesToolkits.setLocalSettingUnitInfo(MoreActivity.this,ToolKits.UNIT_YING);
                                        }

                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(MoreActivity.this, R.string.general_cancel), null)
                        .create();
                dialogunit.show();
                break;
            case R.id.activity_more_Cloud_Sync:
                AlertDialog dialog = new AlertDialog.Builder(MoreActivity.this)
                        .setTitle(ToolKits.getStringbyId(MoreActivity.this, R.string.main_more_sycn_title))
                        .setMessage(ToolKits.getStringbyId(MoreActivity.this, R.string.main_more_sycn_message))
                        .setPositiveButton(ToolKits.getStringbyId(MoreActivity.this, R.string.general_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog = initDialog();
                                        progressDialog.show();
                                        DataFromClientNew dataFromClient = HttpHelper.GenerateCloudSyncParams(userEntity.getUser_id()+"", pageIndex);
                                        try {
                                            CallServer.getRequestInstance().add(MoreActivity.this,false,CommParams.HTTP_CLOUD_DATA, NoHttpRuquestFactory.creatCloud(dataFromClient),httpcallbackQuitGroup);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                        .setNegativeButton(ToolKits.getStringbyId(MoreActivity.this, R.string.general_no), null)
                        .create();
                dialog.show();
                break;
            case R.id.main_more_settings_exitent_layout:
                if(MyApplication.getInstance(MoreActivity.this).isLocalDeviceNetworkOk()){
                    new AlertDialog.Builder(MoreActivity.this)  //
                            .setTitle(getString(R.string.main_more_exit_ent))
                            .setMessage(MessageFormat.format(getString(R.string.main_more_exit_ent_message), userEntity.getEntEntity().getEnt_name()))
                            .setPositiveButton(getString(R.string.general_yes), new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //请求退出群组
                                    CallServer.getRequestInstance().add(MoreActivity.this,false,CommParams.REQUEST_GET_GROUP, NoHttpRuquestFactory.create_FindQun_QuitGroup_Request(userEntity.getUser_id()+""),httpcallbackQuitGroup);
                                }
                            }).setNegativeButton(getString(R.string.general_cancel), null)
                            .show();
                }else{
                    ToolKits.showCommonTosat(MoreActivity.this, false, getString(R.string.general_network_faild), Toast.LENGTH_SHORT);
                }
                break;

            case R.id.activity_more_relogin:
                    new AlertDialog.Builder(MoreActivity.this)  //
                            .setTitle(getString(R.string.login_form_relogin_text))
                            .setPositiveButton(getString(R.string.general_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    YouzanSDK.userLogout(MoreActivity.this);
                                    provider.clearProess();
                                    //重新登录
                                    BleService.getInstance(MoreActivity.this).releaseBLE();
                                    //设置评论内容
                                    String jString =JSON.toJSONString("");
                                    SharedPreferences sharedPreferences;
                                    try
                                    {
                                        sharedPreferences = PreferencesToolkits.getAppDefaultSharedPreferences(MoreActivity.this, true);
                                        SharedPreferences.Editor edit = sharedPreferences.edit();
                                        edit.putString(PreferencesToolkits.KEY_LOGIN_USER_INFO, jString);
                                        edit.commit();
                                    }
                                    catch (Exception e)
                                    {
                                        Log.e(TAG, e.getMessage());
                                    }
                                    MyApplication.getInstance(MoreActivity.this).setPreparedForOfflineSyncToServer(false);
                                    Intent intent = new Intent(MoreActivity.this, ThirdLoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 注意此行！
                                    startActivity(intent);
                                    AppManager.getAppManager().finishAllActivity();
                                }
                            }).setNegativeButton(getString(R.string.general_cancel), null)
                            .show();
                break;

            case R.id.activity_more_exit:
                ExitApp(MoreActivity.this);
                break;
            case R.id.about_group:
                //关于群组  挑转到群组信息的网页上
                MyLog.e(TAG,"ent_url="+userEntity.getEntEntity().getEnt_portal_url());
                startActivity(IntentFactory.createCommonWebActivityIntent(MoreActivity.this, userEntity.getEntEntity().getEnt_portal_url()));
                break;
        }
    }

    public static void ExitApp(final Context context) {
        new AlertDialog.Builder(context)  //
                .setTitle(context.getString(R.string.menu_exit))
                .setPositiveButton(context.getString(R.string.general_yes), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /**设置APP状态为退出*/
                        BleService.setEXIT_APP(true);
                        AppManager.getAppManager().finishAllActivity();
                        if(CommonUtils.isStringEmpty(MyApplication.getInstance(context).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id())
                                || !isEnabled(context)
                                || MyApplication.getInstance(context).getLocalUserInfoProvider().getDeviceEntity().getDevice_type()==MyApplication.DEVICE_BAND
                                ){
                            //未绑定
                            BleService.getInstance(context).getCurrentHandlerProvider().clearProess();
                            // 以下的注释都开启后 并且 通知的服务也关闭后 就可以完全关闭APP 否则 退出不退出服务
                            MyApplication.getInstance(context).stopBleService();
                            System.exit(0);
                        }
                    }
                }).setNegativeButton(context.getString(R.string.general_cancel), null)
                .show();
    }

    //云同步
    private HttpUtils.CallBack httpcallback = new HttpUtils.CallBack() {
        @Override
        public void onRequestComplete(String result) {

        }
    };
    private HttpCallback<String> httpcallbackQuitGroup = new HttpCallback<String>() {

        @Override
        public void onSucceed(int what, Response<String> response) {
            //根据errorCode判断是否成功退出群组 退出群组成功则 new一个空的uesrEntity放到MyApplication中
            switch (what){
                case CommParams.REQUEST_GET_GROUP:
                    DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);
                    MyLog.e(TAG,"退出群组请求结果："+response.get());
                    String value = dataFromServer.getReturnValue().toString();
                    if (dataFromServer.getErrorCode()==1&&!CommonUtils.isStringEmpty(value)) {
                        UserEntity userEntity =new Gson().fromJson(dataFromServer.getReturnValue().toString(), UserEntity.class);
                        MyApplication.getInstance(MoreActivity.this).setLocalUserInfoProvider(userEntity);
                        MyToast.show(MoreActivity.this, getString(R.string.main_more_exit_ent_success), Toast.LENGTH_SHORT);
                        finish();
                    }
                    break;
                case CommParams.HTTP_CLOUD_DATA:
                    MyLog.e(TAG, "returnValue" + response.get());
                    if(CommonUtils.isStringEmpty(response.get().toString())){
                        progressDialog.dismiss();
                        return;
                    }
                    JSONObject object = JSON.parseObject(response.get().toString());
                    String returnValue = object.getString("returnValue");
                    JSONObject object1 = JSON.parseObject(returnValue);
                    String returnRecode=object1.getString("records");
                    String returnTotal=object1.getString("totalCount");
                    MyLog.e(TAG,"returnValue="+returnValue+"===totalCount====="+returnTotal+"======returnRecode====="+returnRecode);
                    if (!CommonUtils.isStringEmpty(returnRecode)) {
                        int count = Integer.parseInt(returnTotal);
                        List<SportRecord> srs = JSON.parseArray(returnRecode, SportRecord.class);
                        for (SportRecord sp:srs) {
                            sportRecords.add(sp);
                            MyLog.e(TAG, "明细数据：" + sp.toString());
                        }

                        MyLog.e(TAG, "srs：" + srs.size()+"=======count========"+count);
                        progressindex += srs.size();
                        progressDialog.setMax(count);
                        progressDialog.setProgress(progressindex);
                        if (srs.size()>0) {
                            UserDeviceRecord.saveToSqliteAsync(MoreActivity.this, srs, userEntity.getUser_id()+"", true, null);
                            pageIndex++;
                            DataFromClientNew dataFromClient = HttpHelper.GenerateCloudSyncParams(userEntity.getUser_id()+"", pageIndex);
                            MyLog.e(TAG, "当前请求的页面：" + pageIndex);
                            try {
                                CallServer.getRequestInstance().add(MoreActivity.this,false,CommParams.HTTP_CLOUD_DATA, NoHttpRuquestFactory.creatCloud(dataFromClient),httpcallbackQuitGroup);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                           /* List<SportRecord> upList_new =  SportDataHelper.backStauffSleepState(MoreActivity.this, userEntity.getUser_id() + "", sportRecords);
                            for (SportRecord sp:upList_new) {
                                MyLog.e(TAG, "计算后的明细数据：" + sp.toString());
                            }*/

                            MyLog.e(TAG, "云同步完成srs.size()<0");
                            pageIndex = 1;
                            new AsyncTask<Object, Object, Boolean>() {
                                @Override
                                protected Boolean doInBackground(Object... params) {
                                    return null;
                                }
                                @Override
                                protected void onPostExecute(Boolean result) {
                                    if(progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    new AlertDialog.Builder(MoreActivity.this)  //
                                            .setTitle(getString(R.string.main_more_sync_end))
                                            .setPositiveButton(getString(R.string.general_yes), null)
                                            .show();
                                }
                            }.execute();
                        }
                    }else{
                        MyLog.e(TAG, "云同步完成records==null");
                        pageIndex = 1;
                        new AsyncTask<Object, Object, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Object... params) {
                                return null;
                            }
                            @Override
                            protected void onPostExecute(Boolean result) {
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                                new AlertDialog.Builder(MoreActivity.this)  //
                                        .setTitle(getString(R.string.main_more_sync_end))
                                        .setPositiveButton(getString(R.string.general_yes), null)
                                        .show();
                            }
                        }.execute();
                    }
                    break;
            }


        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            if(what==CommParams.HTTP_CLOUD_DATA){
                MyLog.e(TAG, "云同步失败了");
                if(progressDialog!=null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }

        }
    };
    @Override
    protected void initListeners() {
        aboutUs.setOnClickListener(this);
        activity_more_start.setOnClickListener(this);
        activity_more_Cloud_Sync.setOnClickListener(this);
        more_unit.setOnClickListener(this);
        relogin.setOnClickListener(this);
        exitApp.setOnClickListener(this);
    }

    private void initData() {
        tv_more_verson.setText(getAPKVersionName(this) + "(" + getAPKVersionCode(this) + ")");
        if(PreferencesToolkits.getLocalSettingUnitInfo(this)==ToolKits.UNIT_GONG){
            tv_more_unit.setText(getString(R.string.metric_system));
        }else{
            tv_more_unit.setText(getString(R.string.more_inch));
        }
    }

    /**
     * 返回本程序对应APK文件的versionCode.
     *
     * @return
     */
    public static int getAPKVersionCode(Context context) {
        initProgrammVersion(context);
        return versionCode;
    }

    /**
     * 返回本程序对应APK文件的versionName.
     *
     * @return
     */
    public static String getAPKVersionName(Context context) {
        initProgrammVersion(context);
        return versionName;
    }

    /**
     * <p>
     * 初始化版本信息.<br>
     * <p/>
     * 读取程序apk文件的AndroidManifest.xml中的versionCode和versionName属性
     * 并存储在本类同名字段中以备后用.
     * </p>
     */
    private static void initProgrammVersion(Context context) {
        if (versionCode == -1) {
            PackageInfo info;
            try {
                info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                versionCode = info.versionCode;
                versionName = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                MyLog.d(TAG, "读程序版本信息时出错," + e.getMessage());
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (provider.getBleProviderObserver() == null) {
            provider.setBleProviderObserver(bleProviderObserver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        provider.setBleProviderObserver(null);
    }


    /**
     * 蓝牙观察者实现类.
     */
    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return MoreActivity.this;
        }
    }

    private ProgressDialog initDialog() {
        //this表示该对话框是针对当前Activity的
        ProgressDialog progressDialog = new ProgressDialog(MoreActivity.this);
        //设置最大值为100
        progressDialog.setMax(100);
        //设置进度条风格STYLE_HORIZONTAL
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(getString(R.string.main_more_sycing));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;

    }

    //判断是否获取了消息通知权限
    public static boolean isEnabled(Context context)
    {
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(), NotificationCollectorService.ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
