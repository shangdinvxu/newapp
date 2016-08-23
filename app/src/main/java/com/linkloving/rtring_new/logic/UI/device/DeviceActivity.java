package com.linkloving.rtring_new.logic.UI.device;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.LepaoProtocalImpl;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.CommParams;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.http.basic.CallServer;
import com.linkloving.rtring_new.http.basic.HttpCallback;
import com.linkloving.rtring_new.http.basic.NoHttpRuquestFactory;
import com.linkloving.rtring_new.http.data.DataFromServer;
import com.linkloving.rtring_new.logic.UI.main.PortalActivity;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.notify.NotificationCollectorService;
import com.linkloving.rtring_new.prefrences.PreferencesToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.LocalInfoVO;
import com.linkloving.rtring_new.prefrences.devicebean.ModelInfo;
import com.linkloving.rtring_new.utils.CommonUtils;
import com.linkloving.rtring_new.utils.MyToast;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceActivity extends ToolBarActivity implements View.OnClickListener {
    private static final String TAG = DeviceActivity.class.getSimpleName();
    private LinearLayout activity_own_alarm, activity_own_incoming_tel, activity_own_longsit, activity_own_control,
            activity_own_power,activity_own_find, activity_own_update, activity_own_delete;
    private ImageView device_img;
    private TextView DeviceMac;
    private TextView boundDeviceVerson;
    private TextView syncDeviceTime;
    private Button mButton;
    //蓝牙提供者
    private BLEProvider provider;
    LocalInfoVO vo;
    BLEHandler.BLEProviderObserverAdapter observerAdapter = null;
    private boolean canoad = false;
    private boolean RINGING;
    private int RINGCOUNT = 10;
    private int send_index = 0;
    /**
     * 控制dialog
     */
    private boolean click_oad = false; //以前出现过切换到此页面也出现dialog的情况
    private byte[] data;
    public final static int DEVICE_TYPE_WATCH = 3;
    public final static int DEVICE_VERSION_TYPE = DEVICE_TYPE_WATCH - 1;
    public final static int DEVICE_UPDATE = 666;
    private ProgressDialog dialog_syn = null;//同步
    private String file_name_OAD;
    private ProgressDialog progressDialog;
    private ProgressDialog dialog;//网络获取版本信息
    private ProgressDialog dialog_connect;//下载进度
    private int persent;
    UserEntity userEntity;
    ModelInfo modelInfo;
    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
    }

    @Override
    protected void getIntentforActivity() {
        type =  getIntent().getIntExtra("type",0);
    }

    @Override
    protected void initView() {
        userEntity=MyApplication.getInstance(DeviceActivity.this).getLocalUserInfoProvider();
        observerAdapter = new BLEProviderObserverAdapterImpl();
        vo = PreferencesToolkits.getLocalDeviceInfo(DeviceActivity.this);
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        provider.setBleProviderObserver(observerAdapter);

        SetBarTitleText(getString(R.string.watch_setting));
        dialog_syn = new ProgressDialog(DeviceActivity.this);
        dialog_syn.setMessage(getString(R.string.bracelet_oad_syn));
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.getting_version_information));
        dialog_connect=new ProgressDialog(this);
        String v = ToolKits.getStringbyId(DeviceActivity.this, R.string.bracelet_version);
        vo = PreferencesToolkits.getLocalDeviceInfo(DeviceActivity.this);
        DeviceMac = (TextView) findViewById(R.id.tv_Mac);
        boundDeviceVerson = (TextView) findViewById(R.id.tv_verson);
        syncDeviceTime = (TextView) findViewById(R.id.tv_sync);
        DeviceMac.setText("MAC:" + MyApplication.getInstance(this).getLocalUserInfoProvider().getDeviceEntity().getLast_sync_device_id());
        if (!com.linkloving.rtring_new.utils.CommonUtils.isStringEmpty(vo.version))
            boundDeviceVerson.setText(getString(R.string.brace_version) + vo.version);
        else
            boundDeviceVerson.setText(MessageFormat.format(v, "Unknow"));
        if (vo.syncTime > 0) {
            syncDeviceTime.setText(new SimpleDateFormat(ToolKits.getStringbyId(DeviceActivity.this, R.string.bracelet_sync_format)).format(new Date(vo.syncTime)) + "");
        } else {
            syncDeviceTime.setText(ToolKits.getStringbyId(DeviceActivity.this, R.string.bracelet_sync_none));
        }
        device_img = (ImageView) findViewById(R.id.device_img);
        activity_own_alarm = (LinearLayout) findViewById(R.id.activity_own_alarm);
        activity_own_incoming_tel = (LinearLayout) findViewById(R.id.activity_own_incoming_tel);
        activity_own_longsit = (LinearLayout) findViewById(R.id.activity_own_longsit);
        activity_own_control = (LinearLayout) findViewById(R.id.activity_own_control);
        activity_own_power = (LinearLayout) findViewById(R.id.activity_own_power);
        activity_own_find = (LinearLayout) findViewById(R.id.activity_own_find);
        activity_own_update = (LinearLayout) findViewById(R.id.activity_own_update);
        activity_own_delete = (LinearLayout) findViewById(R.id.activity_own_delete);

        modelInfo = PreferencesToolkits.getInfoBymodelName(DeviceActivity.this,userEntity.getDeviceEntity().getModel_name());

        if(userEntity.getDeviceEntity().getModel_name()==null || modelInfo==null){
            activity_own_alarm.setVisibility(View.GONE);
            activity_own_incoming_tel.setVisibility(View.GONE);
            activity_own_longsit.setVisibility(View.GONE);
            activity_own_control.setVisibility(View.GONE);
            activity_own_power.setVisibility(View.GONE);
            activity_own_find.setVisibility(View.GONE);
            return;
        }

        if (userEntity.getDeviceEntity().getModel_name().startsWith("LW") || userEntity.getDeviceEntity().getModel_name().startsWith("W")) {
            device_img.setImageDrawable(getResources().getDrawable(R.mipmap.watch));
            activity_own_find.setVisibility(View.GONE);
        } else if (userEntity.getDeviceEntity().getModel_name().startsWith("B")) {
            device_img.setImageDrawable(getResources().getDrawable(R.mipmap.band_logo));
        }

        //如果是主界面提示需要更新 那就直接开始更新
        if(type == DEVICE_UPDATE){
            click_oad = true;
            dialog_syn.show();
            //会在updateFor_handleSetTime()回调中开始真正的空中升级流程
            BleService.getInstance(DeviceActivity.this).syncAllDeviceInfo(DeviceActivity.this);
        }


        if(modelInfo.getAncs()==0){ //无提醒
            activity_own_incoming_tel.setVisibility(View.GONE);
        }
        if(modelInfo.getNo_disturb()==0){
            activity_own_control.setVisibility(View.GONE);
        }
        if(modelInfo.getPower_waste()==0){
            activity_own_power.setVisibility(View.GONE);
        }
        MyLog.e(TAG, "同步时间：" + new SimpleDateFormat(ToolKits.getStringbyId(DeviceActivity.this, R.string.bracelet_sync_format)).format(new Date(0)));
    }

    @Override
    public void onResume() {
        if (provider.getBleProviderObserver() == null) {
            provider.setBleProviderObserver(observerAdapter);
        }
        super.onResume();
    }

    @Override
    protected void initListeners() {
        activity_own_alarm.setOnClickListener(this);
        activity_own_incoming_tel.setOnClickListener(this);
        activity_own_longsit.setOnClickListener(this);
        activity_own_control.setOnClickListener(this);
        activity_own_power.setOnClickListener(this);
        activity_own_find.setOnClickListener(this);
        activity_own_update.setOnClickListener(this);
        activity_own_delete.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        provider.setBleProviderObserver(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_own_alarm:
                startActivity(IntentFactory.start_AlarmActivityIntent(DeviceActivity.this));
                break;
            case R.id.activity_own_incoming_tel:
                IntentFactory.start_IncomingTel(DeviceActivity.this);
//                if(PermissionUtil.gotoPermissionSettings(DeviceActivity.this)){
//                }
                break;
            case R.id.activity_own_longsit:
                IntentFactory.start_LongSitActivityIntent(DeviceActivity.this);
                break;
            case R.id.activity_own_control:
//                勿扰模式
                IntentFactory.start_HandUpActivity(DeviceActivity.this);
                break;
            case R.id.activity_own_power:
                IntentFactory.start_PowerActivity(DeviceActivity.this);
                break;
            case R.id.activity_own_find:
                if(!RINGING){
                    RINGING = true ;
                    final Timer timer = new Timer(); // 每分钟更新一下蓝牙状态
                    timer.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            provider.SetBandRing(DeviceActivity.this);
                            send_index++;
                            if(send_index == RINGCOUNT ){
                                send_index = 0;
                                timer.cancel();
                                RINGING = false;
                            }
                        }
                    }, 0, 1000);
                }

                break;

            case R.id.activity_own_update:
                if (ToolKits.isNetworkConnected(DeviceActivity.this)) {
                    if (vo.battery < 3) {
                        MyToast.showLong(DeviceActivity.this, getString(R.string.bracelet_oad_failed_battery));
                    } else {
                        if (provider.isConnectedAndDiscovered()) {
                            click_oad = true;
                            dialog_syn.show();
                            //会在updateFor_handleSetTime()回调中开始真正的空中升级流程
                            BleService.getInstance(DeviceActivity.this).syncAllDeviceInfo(DeviceActivity.this);
                        } else {
                            //没有连接蓝牙
                            MyToast.showLong(DeviceActivity.this, getString(R.string.pay_no_connect));
                        }
                    }
                } else {

                    MyToast.showLong(DeviceActivity.this, getString(R.string.bracelet_oad_failed_msg));
                }
                break;
            case R.id.activity_own_delete:
                if (ToolKits.isNetworkConnected(DeviceActivity.this)) {
                    AlertDialog unbundDialog = new AlertDialog.Builder(DeviceActivity.this)
                            .setTitle(ToolKits.getStringbyId(DeviceActivity.this, R.string.bracelet_unbound))
                            .setMessage(ToolKits.getStringbyId(DeviceActivity.this, R.string.bracelet_unbound_msg))
                            .setPositiveButton(ToolKits.getStringbyId(DeviceActivity.this, R.string.general_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //请求解绑设备
                                    if(MyApplication.getInstance(DeviceActivity.this).isLocalDeviceNetworkOk()) {
                                        String last_sync_device_id2 = userEntity.getDeviceEntity().getLast_sync_device_id2();
                                        MyLog.e(TAG,"==last_sync_device_id2==="+last_sync_device_id2+"====userEntity.getUser_id()==="+userEntity.getUser_id());
                                        CallServer.getRequestInstance().add(DeviceActivity.this,getString(R.string.general_submitting),CommParams.HTTP_UNBUND, NoHttpRuquestFactory.UnboundMACToServer(userEntity.getUser_id() + ""), httpCallback);
                                    }else {
                                        MyToast.show(DeviceActivity.this,getString(R.string.general_network_faild),Toast.LENGTH_LONG);
                                    }
                                }
                            })
                            .setNegativeButton(ToolKits.getStringbyId(DeviceActivity.this, R.string.general_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    unbundDialog.show();
                    break;
                }
                break;

        }
    }

    /**
     * 蓝牙观察者实现类.
     */
    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {
        @Override
        protected Activity getActivity() {
            return DeviceActivity.this;
        }

        /************************
         * OAD头发送成功
         *************************/
        @Override
        public void updateFor_notifyCanOAD_D() {
            //禁止 消息提醒 和 定时器扫描  的全局变量
            BleService.getInstance(DeviceActivity.this).setCANCLE_ANCS(true);
            if (provider.isConnectedAndDiscovered()) {
                canoad = true;
                provider.OADDevice(DeviceActivity.this, data);
            }

        }
        /************************OAD头发送成功*************************/

        /************************
         * OAD失败
         *****************************/
        @Override
        public void updateFor_notifyNOTCanOAD_D() {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            handler.removeCallbacks(runnable);
            MyLog.e(TAG, "OAD 失败");
            //Toast.makeText(DeviceActivity.this, "OAD 升级失败！", Toast.LENGTH_SHORT).show();
          /*  progessWidget.msg.setText( "OAD 升级失败！");
            progessWidget.syncFinish(false);*/
            canoad = false;
            //取消禁止 消息提醒 和 定时器扫描 的全局变量
            BleService.getInstance(DeviceActivity.this).setCANCLE_ANCS(false);//取消禁止 消息提醒 和 定时器扫描 的全局变量NCS(false);
        }
        /************************OAD失败****************************/
        /************************
         * OAD成功
         ****************************/
        @SuppressWarnings("static-access")
        @Override
        public void updateFor_notifyOADSuccess_D() {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            handler.removeCallbacks(runnable);
            //取消禁止 消息提醒 和 定时器扫描 的全局变量
            BleService.getInstance(DeviceActivity.this).setCANCLE_ANCS(false);
            MyLog.e(TAG, "OAD升级成功!");
          /*  mLepaoProtocalImpl.OAD_percent=(data.length-17)/16;
            progessWidget.syncFinish(true);*/
            canoad = false;
            //成功后跳转到主界面 START
            Intent intent = new Intent();
            intent.setClass(DeviceActivity.this, PortalActivity.class);
            startActivity(intent);
            finish();
            //成功后跳转到主界面 OVER
        }
        /************************OAD成功****************************/
        /*************************
         * TEST(烧制flash)
         *******************/
        @Override
        public void updateFor_FlashHeadSucess() {
            provider.flashbody(getActivity(), data);
        }
        /*************************TEST(烧制flash)*******************/
        /**
         * 点击固件更新按钮---->需要走同步流程---->在同步完运动数据后--->设置时间
         * 所以在设置时间的回调里面开始真正的OAD流程
         */
        @Override
        public void updateFor_handleSetTime() {
            super.updateFor_handleSetTime();
            if (click_oad) {
                if (ToolKits.isNetworkConnected(DeviceActivity.this)) {
                    // MyToast.showLong(DeviceActivity.this, "同步完成开始OAD");
                    //同步完成开始OAD
                    //开始OAD的AsyncTask,请求网络.获取版本信息与当前的版本信息比较
                    beforeOAD();
                } else {
                    //没有连接网络
                    MyToast.showLong(DeviceActivity.this, getString(R.string.bracelet_oad_failed_msg));
                }
                click_oad = false;
                if (dialog_syn != null || dialog_syn.isShowing())
                    dialog_syn.dismiss();
            }
        }

        @Override
        public void updateFor_handleSendDataError() {
            super.updateFor_handleSendDataError();
            //取消禁止 消息提醒 和 定时器扫描 的全局变量
            BleService.getInstance(DeviceActivity.this).setCANCLE_ANCS(false);
            if (dialog_syn != null || dialog_syn.isShowing())
                dialog_syn.dismiss();
        }

        /**************************
         * 连接失败
         ***********************/
        //在OAD过程中的话    就直接在界面上面显示失败
        @Override
        public void updateFor_handleConnectLostMsg() {
            super.updateFor_handleConnectLostMsg();
            MyLog.e(TAG, "handleConnectLostMsg()!" + canoad);
            if (canoad) {
                provider.clearProess();
                MyToast.showLong(DeviceActivity.this, getString(R.string.bracelet_oad_reason));
              /*  progessWidget.msg.setText(ToolKits.getStringbyId(DeviceActivity.this, R.string.bracelet_oad_failed));
                progessWidget.syncFinish(false);*/
                canoad = false;
            }
        }
        /**************************连接失败***********************/
    }

    private void beforeOAD() {
        dialog.show();
        FirmwareDTO firmwareDTO = new FirmwareDTO();
        int deviceType = MyApplication.getInstance(this).getLocalUserInfoProvider().getDeviceEntity().getDevice_type();
        if(deviceType ==MyApplication.DEVICE_WATCH ){
            deviceType =  MyApplication.getInstance(this).getLocalUserInfoProvider().getDeviceEntity().getDevice_type();
        }else{
            deviceType = 1;
        }
        firmwareDTO.setDevice_type(deviceType);
        firmwareDTO.setFirmware_type(DEVICE_VERSION_TYPE);
        int version_int = ToolKits.makeShort(vo.version_byte[1], vo.version_byte[0]);
        MyLog.e(TAG,"vo.version="+vo.version);
        firmwareDTO.setVersion_int(version_int + "");
        MyLog.e(TAG,"vo.getModelname()="+vo.getModelname());
        if(!CommonUtils.isStringEmpty(vo.getModelname())){
            firmwareDTO.setModel_name(vo.getModelname());
        }
        MyLog.e(TAG, "device_type:" + DEVICE_TYPE_WATCH + "====firmware_type===:" + DEVICE_VERSION_TYPE + "===version_int===:" + version_int);
        if(MyApplication.getInstance(this).isLocalDeviceNetworkOk()){
            //请求网络
            CallServer.getRequestInstance().add(DeviceActivity.this, false, CommParams.HTTP_OAD, NoHttpRuquestFactory.create_OAD_Request(firmwareDTO), httpCallback);
        }else
            MyToast.show(this,getString(R.string.main_more_sycn_fail),Toast.LENGTH_LONG);
    }



    private HttpCallback<String> httpCallback = new HttpCallback<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            dialog.dismiss();
            DataFromServer dataFromServer = JSON.parseObject(response.get(), DataFromServer.class);

            String value = dataFromServer.getReturnValue().toString();
            MyLog.e(TAG, "respone.get()" + response.get());
            switch (what) {
                case CommParams.HTTP_OAD:
                    if(!CommonUtils.isStringEmpty(response.get())) {
                        MyLog.e(TAG, "dataFromServer.getErrorCode()：" + dataFromServer.getErrorCode());
                        if (dataFromServer.getErrorCode() == 10020) {

//                                Toast.makeText(DeviceActivity.this, getString(R.string.main_more_version_check_nofind), Toast.LENGTH_LONG).show();
                            MyToast.show(DeviceActivity.this, getString(R.string.main_more_version_check_nofind), Toast.LENGTH_LONG);
                        } else {
                            JSONObject object = JSON.parseObject(value);
                            file_name_OAD = object.getString("file_name");
                            String version_code = object.getString("version_code");
                            MyLog.e(TAG, "===value====" + value + "====" + response.get() + "===file_name==" + file_name_OAD + "==version_code==" + version_code);
                            if (!CommonUtils.isStringEmptyPrefer(file_name_OAD) && value instanceof String && !ToolKits.isJSONNullObj(file_name_OAD)) {
                                MyLog.e(TAG, "json:" + value + "===vo.version==" + vo.version);
                                if (Integer.parseInt(version_code, 16) <= Integer.parseInt(vo.version, 16)) {
//                                        Toast.makeText(DeviceActivity.this, getString(R.string.main_more_version_check_is_latest), Toast.LENGTH_LONG).show();
                                    MyToast.show(DeviceActivity.this, getString(R.string.bracelet_oad_version_top), Toast.LENGTH_LONG);
                                } else {
                                    //让用户去更新
                                    AlertDialog dialog = new AlertDialog.Builder(DeviceActivity.this)
                                            .setTitle(getString(R.string.update_firmware))
                                            .setMessage(getString(R.string.update_firmware)+"?")
                                            .setPositiveButton(ToolKits.getStringbyId(DeviceActivity.this, R.string.general_yes),
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //从服务器获取了比当前版本更高的bin,开始OAD
                                                            String url = CommParams.AVATAR_DOWNLOAD_CONTROLLER_URL_ROOT_NEW+"firmware?fileName="+file_name_OAD;
                                                            String filename = file_name_OAD;
                                                            downLoad(url, filename);
                                                        }
                                                    })
                                            .setNegativeButton(ToolKits.getStringbyId(DeviceActivity.this, R.string.general_no), null)
                                            .create();
                                    dialog.show();
                                }
                            }
                        }
                    }
                    break;
                case CommParams.HTTP_UNBUND:
                    MyLog.e(TAG, "====HTTP_UNBUND====response=" + response.get());
                    //不做处理
                    if (dataFromServer.getErrorCode() == 1) {
                        if (provider.isConnectedAndDiscovered()) {
                            //连接才去下这个指令
                            provider.unBoundDevice(DeviceActivity.this);
                        }
                        //曾经出现过modelInfo为null的情况(服务器没有存这些信息)
                        if(modelInfo!=null){
                            if(modelInfo.getAncs()!=0){ //有消息提醒
                                startActivity(new Intent(NotificationCollectorService.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                                ToolKits.showCommonTosat(DeviceActivity.this, true, ToolKits.getStringbyId(DeviceActivity.this, R.string.unbound_success_msg), Toast.LENGTH_LONG);
                            }else{
                                ToolKits.showCommonTosat(DeviceActivity.this, true, ToolKits.getStringbyId(DeviceActivity.this, R.string.unbound_success), Toast.LENGTH_LONG);
                            }
                        }
                        UserEntity userEntity = MyApplication.getInstance(DeviceActivity.this).getLocalUserInfoProvider();
                        //设备号置空
                        userEntity.getDeviceEntity().setLast_sync_device_id(null);
                        //设备类型置空
                        userEntity.getDeviceEntity().setDevice_type(0);
                        MyLog.e(TAG, "====HTTP_UNBUND====userEntity=" + userEntity.toString());
                        //*******模拟断开   不管有没有连接 先执行这个再说
                        BleService.getInstance(DeviceActivity.this).releaseBLE();
                        finish();
                    } else if (dataFromServer.getErrorCode() == 10004) {
                        MyToast.show(DeviceActivity.this, DeviceActivity.this.getString(R.string.debug_device_unbound_failed), Toast.LENGTH_LONG);
                    }
                    break;
            }
//            if (dataFromServer.getErrorCode() == 10020) {
//                MyToast.show(DeviceActivity.this, "查找不到对应的固件", Toast.LENGTH_LONG);
//            } else {
//                if (!CommonUtils.isStringEmpty(value)) {
//                    JSONObject object = JSON.parseObject(value);
//                    file_name_OAD = object.getString("file_name");
//                    String version_code = object.getString("version_code");
//                    MyLog.e(TAG, "===value====" + value + "====" + response.get() + "===file_name==" + file_name_OAD + "==version_code==" + version_code);
//                    if (!CommonUtils.isStringEmptyPrefer(file_name_OAD) && value instanceof String && !ToolKits.isJSONNullObj(file_name_OAD)) {
//                        if (what == 10) {
//                            MyLog.e(TAG, "json:" + value + "===vo.version==" + vo.version);
//                            if (Integer.parseInt(version_code, 16) <= Integer.parseInt(vo.version, 16)) {
//
////                        String url = CommParams.AVATAR_DOWNLOAD_CONTROLLER_URL_ROOT_NEW+"firmware" + file_name_OAD;
////                        String filename = file_name_OAD;
////                        downLoad(url, filename);
//                                MyToast.show(DeviceActivity.this, "当前固件版本已更新到最高版本！", Toast.LENGTH_LONG);
//                            } else {
//                                //从服务器获取了比当前版本更高的bin,开始OAD
//                                String url = CommParams.AVATAR_DOWNLOAD_CONTROLLER_URL_ROOT_NEW + "firmware" + file_name_OAD;
//                                String filename = file_name_OAD;
//                                downLoad(url, filename);
//                            }
//                        } else {
//                            MyToast.showLong(DeviceActivity.this, getString(R.string.bracelet_oad_version_top));
//                        }
//                    }
//                }
//            }
//                if (what == CommParams.HTTP_UNBUND) {
//                    MyLog.e(TAG, "====HTTP_UNBUND====response=" + response.get());
//                    //不做处理
//                    if (dataFromServer.getErrorCode() == 1) {
//                        if (provider.isConnectedAndDiscovered()) {
//                            //连接才去下这个指令
//                            provider.unBoundDevice(DeviceActivity.this);
//                        }
//
//                        UserEntity userEntity = MyApplication.getInstance(DeviceActivity.this).getLocalUserInfoProvider();
//                        userEntity.getDeviceEntity().setLast_sync_device_id(null);
//                        //*******模拟断开   不管有没有连接 先执行这个再说
//                        BleService.getInstance(DeviceActivity.this).getCurrentHandlerProvider().release();
//                        BleService.getInstance(DeviceActivity.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
//                        BleService.getInstance(DeviceActivity.this).getCurrentHandlerProvider().setmBluetoothDevice(null);
//                        MyToast.show(DeviceActivity.this, DeviceActivity.this.getString(R.string.unbound_success), Toast.LENGTH_LONG);
//                    } else if (dataFromServer.getErrorCode() == 10004) {
//                        MyToast.show(DeviceActivity.this, DeviceActivity.this.getString(R.string.debug_device_unbound_failed), Toast.LENGTH_LONG);
//                    }
//                }
//                        break;
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {
            dialog.dismiss();

        }
    };

    private void downLoad(String url, String filename) {

        DownloadRequest downloadRequest = NoHttp.createDownloadRequest(url, getOADFileSavedDir(DeviceActivity.this), filename, true, false);

        CallServer.getDownloadInstance().add(0, downloadRequest, downloadListener);
        MyLog.e(TAG, "=====url===" + url);
    }

    private DownloadListener downloadListener = new DownloadListener() {

        @Override
        public void onDownloadError(int what, Exception exception) {
            dialog_connect.dismiss();
            Toast.makeText(DeviceActivity.this, getString(R.string.bracelet_down_file_fail), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStart(int what, boolean resume, long preLenght, Headers header, long count) {
            Log.i(TAG, "下载开始");

            String title = getString(R.string.general_uploading);

            String message =getString(R.string.general_uploading);

            dialog_connect = ProgressDialog.show(DeviceActivity.this, title, message);

            dialog_connect.show();

        }

        @Override
        public void onProgress(int what, int progress, long downCount) {
            Log.i(TAG, "正在下载");

        }

        @Override
        public void onFinish(int what, String filePath) {
            dialog_connect.dismiss();
            Log.i(TAG, "下载完成" + filePath);
            //下载完成,显示开始写入固件
            File file = new File(filePath);
            showDialog(file.getName(), true, null);
        }


        @Override
        public void onCancel(int what) {
            Log.i(TAG, "下载取消");
        }

    };

    private ProgressDialog onCreateDialog() {

        //this表示该对话框是针对当前Activity的
        ProgressDialog progressDialog = new ProgressDialog(DeviceActivity.this);
        //设置最大值为100
        progressDialog.setMax(100);
        //设置进度条风格STYLE_HORIZONTAL
        progressDialog.setProgressStyle(
                ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle(getString(R.string.being_written));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;

    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0x333:
                    progressDialog.incrementProgressBy(msg.arg1 - persent);

                    persent = msg.arg1;

                    postDelayed(runnable, 2000);

                    break;
            }
        }

        ;
    };
    private LepaoProtocalImpl mLepaoProtocalImpl = new LepaoProtocalImpl();

    Runnable runnable = new Runnable() {
        @SuppressWarnings("static-access")
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 0x333;
            msg.arg1 = mLepaoProtocalImpl.getOAD_percent();
            Log.i(TAG, "progress=" + msg.arg1 + "");
            handler.sendMessage(msg);
        }
    };


    private void showDialog(final String filename, final boolean fromInternet, final byte[] data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeviceActivity.this);
        builder.setMessage(getString(R.string.start_upgrading));
        builder.setTitle("OAD");
        builder.setPositiveButton(getString(R.string.queren), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //开始执行写入
                dialog.dismiss();
                beginWrite(filename);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //获取文件下载的路径
    public static String getOADFileSavedDir(Context context)

    {
        String dir = null;
        File sysExternalStorageDirectory = Environment.getExternalStorageDirectory();
        if (sysExternalStorageDirectory != null && sysExternalStorageDirectory.exists()) {
            dir = sysExternalStorageDirectory.getAbsolutePath() + "/.rtring/OAD";
        }
        Log.i(TAG, "路径" + dir);
        return dir;
    }

    private void beginWrite(String filename) {
        try {
            String filePath = getOADFileSavedDir(DeviceActivity.this) + "/" + filename;
            MyLog.i(TAG, "写入文件的路径" + filePath);
            File file = new File(filePath);
            long fileSize = file.length();
            Log.i(TAG, "file size..." + fileSize);
            if (fileSize > Integer.MAX_VALUE)
                Log.i(TAG, "file too big...");
            FileInputStream fi = new FileInputStream(file);
            byte[] buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            //确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
            fi.close();
            if (provider.isConnectedAndDiscovered()) {
                Log.i(TAG, "buffer size..." + buffer.length);
                data = buffer;
                canoad = true;
                progressDialog = onCreateDialog();
                progressDialog.incrementProgressBy(-progressDialog.getProgress());
                progressDialog.setMax(((data.length - 17) / 16));
                progressDialog.show();
                handler.post(runnable);
                provider.OADDeviceHead(DeviceActivity.this, data);
                // 第一次传文件头过去之后的流程继续在蓝牙callback中
                handler.post(runnable);
            } else {
                //  WidgetUtils.showToastLong(OwnBraceletActivity.this, ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.pay_no_connect), ToastType.ERROR);

            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
