package com.linkloving.rtring_new.logic.UI.device.incomingtel;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_new.BleService;
import com.linkloving.rtring_new.IntentFactory;
import com.linkloving.rtring_new.MyApplication;
import com.linkloving.rtring_new.R;
import com.linkloving.rtring_new.basic.toolbar.ToolBarActivity;
import com.linkloving.rtring_new.logic.dto.UserEntity;
import com.linkloving.rtring_new.notify.NotificationCollectorService;
import com.linkloving.rtring_new.prefrences.LocalUserSettingsToolkits;
import com.linkloving.rtring_new.prefrences.devicebean.DeviceSetting;
import com.linkloving.rtring_new.utils.LanguageHelper;
import com.linkloving.rtring_new.utils.ToolKits;
import com.linkloving.rtring_new.utils.logUtils.MyLog;

public class IncomingTelActivity extends ToolBarActivity{
    /**消息提醒 -- 电话提醒*/
    public final static String REMINDER_SETTING_ANDROID_CALL_URL = "http://" + "app.linkloving.com:6080/linkloving_server-watch/" + "clause_reminder/android/calls.html";
    /**来电*/
    private LinearLayout callLayout;
    private ImageView callImg;
    private TextView callText;
    private CheckBox callSwitch;
    /**短信*/
    private LinearLayout mesgsaeLayout;
    private ImageView mesgsaeImg;
    private TextView mesgsaeText;
    private CheckBox mesgsaeSwitch;
    /**QQ*/
    private LinearLayout qQLayout;
    private ImageView qQImg;
    private TextView qQText;
    private CheckBox qQSwitch;
    /**微信*/
    private LinearLayout wXLayout;
    private ImageView wXImg;
    private TextView wXText;
    private CheckBox wXSwitch;
    /**连爱*/
    private LinearLayout linkLayout;
    private ImageView linkImg;
    private TextView linkText;
    private CheckBox linkSwitch;
    private TextView tv_help;

    View view;
    Toast toast;

    private DeviceSetting deviceSetting;
    private byte[] send_data;
    private int notif_data;
    //蓝牙观察者
    private BLEHandler.BLEProviderObserverAdapter bleProviderObserver;
    //蓝牙提供者
    private BLEProvider provider;
    UserEntity userEntity;
    //    同步提醒
    Button BLE_Button;
    private LinearLayout Synchronize_device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_notify);
        userEntity = MyApplication.getInstance(getApplicationContext()).getLocalUserInfoProvider();
        provider = BleService.getInstance(this).getCurrentHandlerProvider();
        bleProviderObserver = new BLEProviderObserverAdapterImpl();
        provider.setBleProviderObserver(bleProviderObserver);
        initData();
    }
    @Override
    protected void getIntentforActivity() {
    }

    @Override
    protected void initView() {
        SetBarTitleText(getString((R.string.xiaoxi_notify)));
        /**右上角按钮设置*/
        Button btn = getRightButton();
        ViewGroup.LayoutParams layoutParams = btn.getLayoutParams();
        layoutParams.width=100;
        layoutParams.height=200;
        btn.setLayoutParams(layoutParams);
        btn.setText(getString(R.string.general_save));
        btn.setTextColor(getResources().getColor(R.color.white));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationCollectorService.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });
        callLayout = (LinearLayout) findViewById(R.id.call_layout);
        callImg = (ImageView) findViewById(R.id.call_img);
        callText= (TextView) findViewById(R.id.call_text);
        callSwitch = (CheckBox) findViewById(R.id.call_switch);
        mesgsaeLayout = (LinearLayout) findViewById(R.id.mesgsae_layout);
        mesgsaeImg = (ImageView) findViewById(R.id.mesgsae_img);
        mesgsaeText= (TextView) findViewById(R.id.mesgsae_text);
        mesgsaeSwitch = (CheckBox) findViewById(R.id.mesgsae_switch);
        qQLayout = (LinearLayout) findViewById(R.id.QQ_layout);
        qQImg = (ImageView) findViewById(R.id.QQ_img);
        qQText= (TextView) findViewById(R.id.QQ_text);
        qQSwitch = (CheckBox) findViewById(R.id.QQ_switch);
        wXLayout = (LinearLayout) findViewById(R.id.WX_layout);
        wXImg = (ImageView) findViewById(R.id.WX_img);
        wXText= (TextView) findViewById(R.id.WX_text);
        wXSwitch = (CheckBox) findViewById(R.id.WX_switch);
        linkLayout = (LinearLayout) findViewById(R.id.Link_layout);
        linkImg = (ImageView) findViewById(R.id.Link_img);
        linkText= (TextView) findViewById(R.id.Link_text);
        linkSwitch = (CheckBox) findViewById(R.id.Link_switch);
        tv_help = (TextView) findViewById(R.id.tv_help);
        tv_help.setVisibility(View.GONE);
        tv_help.setText(Html.fromHtml("<u>"+this.getString(R.string.notifications_no_res)+"</u>"));

        if(!LanguageHelper.isChinese_SimplifiedChinese()){
            //英文下不需要qq 和 微信
            qQLayout.setVisibility(View.GONE);
            wXLayout.setVisibility(View.GONE);
        }

        BLE_Button= (Button) findViewById(R.id.incoming_btn_Synchronize_device);
        Synchronize_device= (LinearLayout) findViewById(R.id.incoming_Synchronize_device);

        view= LayoutInflater.from(this).inflate(R.layout.toast_synchronize, null);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = getResources().getDimensionPixelSize(resourceId);
        //得到Toast对象
        toast = new Toast(IncomingTelActivity.this);
        //设置Toast对象的位置，3个参数分别为位置，X轴偏移，Y轴偏
        toast.setGravity(Gravity.TOP, 0, result+50);
        //设置Toast对象的显示时间
        toast.setDuration(Toast.LENGTH_LONG);
        //设置Toast对象所要展示的视图
        toast.setView(view);

    }

    private void initData() {
        //通过userid去获取对应的DeviceSetting
        deviceSetting = LocalUserSettingsToolkits.getLocalSetting(IncomingTelActivity.this,userEntity.getUser_id()+"");
        int ANCS_value = deviceSetting.getANCS_value();
        MyLog.e("IncomingTelActivity","ANCS_value:"+ANCS_value);
        /*********数据转成应用层变量START*********/
        String Ancs_str = Integer.toBinaryString(ANCS_value);
        char[] array = { 0, 0, 0, 0, 0 };
        char[] charr = Ancs_str.toCharArray();
        System.arraycopy(charr, 0, array, 5 - charr.length, charr.length); // 将字符串转换为字符数组
        /*********数据转成应用层变量OVER*********/

        if(array[4] == '1'){
            linkSwitch.setChecked(true);
            linkText.setTextColor(getResources().getColor(R.color.orange));
            linkImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_linkloving_on));
        }
        if(array[3] == '1'){
            callSwitch.setChecked(true);
            callText.setTextColor(getResources().getColor(R.color.orange));
            callImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_phone_on));
        }
        if(array[2] == '1'){
            mesgsaeSwitch.setChecked(true);
            mesgsaeText.setTextColor(getResources().getColor(R.color.orange));
            mesgsaeImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_message_on));
        }
        if(array[1] == '1'){
            wXSwitch.setChecked(true);
            wXText.setTextColor(getResources().getColor(R.color.orange));
            wXImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_wechat_on));
        }
        if(array[0] == '1'){
            qQSwitch.setChecked(true);
            qQText.setTextColor(getResources().getColor(R.color.orange));
            qQImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_qq_on));
        }
    }

    private void setNotificition()
    {
        MyLog.e("IncomingTelActivity","执行了setNotificition");
        String notif_ = "" +(qQSwitch.isChecked() ? 1 : 0)+ (wXSwitch.isChecked() ? 1 : 0)
                + (mesgsaeSwitch.isChecked() ? 1 : 0) + (callSwitch.isChecked() ? 1 : 0)
                + (linkSwitch.isChecked() ? 1 : 0);
        notif_data = Integer.parseInt(notif_, 2);//报存到本地的
        send_data = intto2byte(notif_data); //发送到蓝牙的
        /**保存并且发送到蓝牙**/
        //报存本地
        deviceSetting.setANCS_value(notif_data);
        MyLog.e("IncomingTelActivity","notif_data:"+notif_data);
        LocalUserSettingsToolkits.updateLocalSetting(IncomingTelActivity.this,deviceSetting);
        //发送到蓝牙
        //判断蓝牙是否连接
        if (provider.isConnectedAndDiscovered()) {
            //同步到设备
            provider.setNotification(IncomingTelActivity.this, send_data);
            Synchronize_device.setVisibility(View.VISIBLE);
            BLE_Button.setText(getString(R.string.synchronized_to_device));
            toast.show();
        }else{
            android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(IncomingTelActivity.this)
                    .setTitle(ToolKits.getStringbyId(IncomingTelActivity.this, R.string.portal_main_unbound))
                    .setMessage(ToolKits.getStringbyId(IncomingTelActivity.this, R.string.portal_main_unbound_msg))
                    .setPositiveButton(ToolKits.getStringbyId(IncomingTelActivity.this, R.string.general_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
            dialog.show();
        }
        //这是发送到蓝牙的处理
    }

    @Override
    protected void initListeners() {
        //电话
        callSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callSwitch.isChecked()) {
                    callText.setTextColor(getResources().getColor(R.color.orange));
                    callImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_phone_on));
                    setNotificition();
                }else{
                    callText.setTextColor(getResources().getColor(R.color.text_gray));
                    callImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_phone_off));
                    setNotificition();
                }
            }
        });
        //短信
        mesgsaeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mesgsaeSwitch.isChecked()) {
                    mesgsaeText.setTextColor(getResources().getColor(R.color.orange));
                    mesgsaeImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_message_on));
                    setNotificition();
                }else{
                    mesgsaeText.setTextColor(getResources().getColor(R.color.text_gray));
                    mesgsaeImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_message_off));
                    setNotificition();
                }
            }
        });
        //qq
        qQSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qQSwitch.isChecked()) {
                    qQText.setTextColor(getResources().getColor(R.color.orange));
                    qQImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_qq_on));
                    setNotificition();
                }else{
                    qQText.setTextColor(getResources().getColor(R.color.text_gray));
                    qQImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_qq_off));
                    setNotificition();
                }
            }
        });
        //wx
        wXSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wXSwitch.isChecked()) {
                    wXText.setTextColor(getResources().getColor(R.color.orange));
                    wXImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_wechat_on));
                    setNotificition();
                }else{
                    wXText.setTextColor(getResources().getColor(R.color.text_gray));
                    wXImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_wechact_off));
                    setNotificition();
                }
            }
        });
        //link
        linkSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linkSwitch.isChecked()) {
                    MyLog.e("IncomingTelActivity","linkSwitch:");
                    linkText.setTextColor(getResources().getColor(R.color.orange));
                    linkImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_linkloving_on));
                    setNotificition();
                }else{
                    linkText.setTextColor(getResources().getColor(R.color.text_gray));
                    linkImg.setImageDrawable(getResources().getDrawable(R.mipmap.set_linkloving_off));
                    setNotificition();
                }
            }
        });

        tv_help.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(IntentFactory.createCommonWebActivityIntent(IncomingTelActivity.this, REMINDER_SETTING_ANDROID_CALL_URL));
            }
        });
    }

    /**int转byte*/
    public static byte[] intto2byte(int a)
    {
        byte[] m = new byte[2];
        m[0] = (byte) ((0xff & a));
        m[1] = (byte) (0xff & (a >> 8));
        return m;
    }
    /**
     * 蓝牙观察者实现类.
     */
    private class BLEProviderObserverAdapterImpl extends BLEHandler.BLEProviderObserverAdapter {

        @Override
        protected Activity getActivity() {
            return IncomingTelActivity.this;
        }

        public BLEProviderObserverAdapterImpl() {
            super();
        }

        @Override
        public void updateFor_notifyForSetClockSucess() {
            super.updateFor_notifyForSetClockSucess();

        }

        @Override
        public void updateFor_notifyForSetClockFail() {
            super.updateFor_notifyForSetClockFail();
        }
    }
}


